package ru.butenko.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.butenko.BaseIT;
import ru.butenko.proto.storage.CarServiceGrpc;
import ru.butenko.proto.storage.Cars;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class CarGrpcServiceIT extends BaseIT {

    private static final UUID AVAILABLE_CAR_ID = UUID.fromString("55555555-5555-5555-5555-555555555555");
    private static final UUID MODEL_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");
    private static final UUID RESERVED_CAR_ID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

    @Autowired
    private Server grpcServer;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private ManagedChannel channel;
    private CarServiceGrpc.CarServiceBlockingStub stub;

    @BeforeEach
    void setUp() {
        channel = ManagedChannelBuilder.forAddress("localhost", grpcServer.getPort())
                .usePlaintext()
                .build();
        stub = CarServiceGrpc.newBlockingStub(channel);
    }

    @AfterEach
    void tearDown() {
        channel.shutdownNow();
        jdbcTemplate.update("delete from storage_cars where id = ?", RESERVED_CAR_ID);
    }

    @Test
    void listAvailableCars_shouldReturnOnlyAvailableNotRemovedCars() {
        insertCar(RESERVED_CAR_ID, "VIN-RESERVED", "RESERVED", false);

        Cars.CarListResponse response = stub.listAvailableCars(Cars.Empty.newBuilder().build());

        assertEquals(1, response.getCarsCount());
        Cars.Car car = response.getCars(0);
        assertEquals(AVAILABLE_CAR_ID.toString(), car.getId());
        assertEquals(MODEL_ID.toString(), car.getModelId());
        assertEquals("WBATEST00000000001", car.getVin());
        assertEquals("AVAILABLE", car.getStatus());
    }

    @Test
    void getCarById_shouldReturnAvailableCar() {
        Cars.Car car = stub.getCarById(Cars.CarRequest.newBuilder()
                .setId(AVAILABLE_CAR_ID.toString())
                .build());

        assertEquals(AVAILABLE_CAR_ID.toString(), car.getId());
        assertEquals(MODEL_ID.toString(), car.getModelId());
        assertEquals("WBATEST00000000001", car.getVin());
        assertEquals("AVAILABLE", car.getStatus());
    }

    @Test
    void getCarById_shouldReturnNotFound_whenCarIsNotAvailable() {
        insertCar(RESERVED_CAR_ID, "VIN-RESERVED", "RESERVED", false);

        StatusRuntimeException ex = assertThrows(StatusRuntimeException.class, () ->
                stub.getCarById(Cars.CarRequest.newBuilder()
                        .setId(RESERVED_CAR_ID.toString())
                        .build()));
        assertEquals(Status.Code.NOT_FOUND, ex.getStatus().getCode());
    }

    @Test
    void getCarById_shouldReturnInvalidArgument_whenIdIsInvalid() {
        StatusRuntimeException ex = assertThrows(StatusRuntimeException.class, () ->
                stub.getCarById(Cars.CarRequest.newBuilder()
                        .setId("not-a-uuid")
                        .build()));
        assertEquals(Status.Code.INVALID_ARGUMENT, ex.getStatus().getCode());
    }

    private void insertCar(UUID id, String vin, String status, boolean removed) {
        jdbcTemplate.update(
                """
                        insert into storage_cars(id, model_id, vin, status, created_at, updated_at, removed)
                        values (?, ?, ?, ?, ?, ?, ?)
                        on conflict (id) do update
                            set status = excluded.status,
                                removed = excluded.removed
                        """,
                id,
                MODEL_ID,
                vin,
                status,
                Timestamp.from(Instant.parse("2026-01-01T00:00:00Z")),
                Timestamp.from(Instant.parse("2026-01-01T00:00:00Z")),
                removed
        );
    }
}
