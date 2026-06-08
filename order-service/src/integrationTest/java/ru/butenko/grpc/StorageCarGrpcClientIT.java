package ru.butenko.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.butenko.integration.grpc.StorageCar;
import ru.butenko.integration.grpc.StorageCarGrpcClient;
import ru.butenko.integration.grpc.StorageCarNotFoundException;
import ru.butenko.proto.storage.CarServiceGrpc;
import ru.butenko.proto.storage.Cars;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StorageCarGrpcClientIT {

    private static final UUID CAR_ID = UUID.fromString("55555555-5555-5555-5555-555555555555");
    private static final UUID MODEL_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");

    private Server server;
    private ManagedChannel channel;
    private StorageCarGrpcClient client;

    @BeforeEach
    void setUp() throws IOException {
        server = ServerBuilder.forPort(0)
                .addService(new TestCarService())
                .build()
                .start();
        channel = ManagedChannelBuilder.forAddress("localhost", server.getPort())
                .usePlaintext()
                .build();
        client = new StorageCarGrpcClient(CarServiceGrpc.newBlockingStub(channel));
    }

    @AfterEach
    void tearDown() {
        channel.shutdownNow();
        server.shutdownNow();
    }

    @Test
    void listAvailableCars_shouldReadCarsFromRealGrpcServer() {
        List<StorageCar> cars = client.listAvailableCars();

        assertEquals(1, cars.size());
        StorageCar car = cars.get(0);
        assertEquals(CAR_ID, car.id());
        assertEquals(MODEL_ID, car.modelId());
        assertEquals("WBATEST00000000001", car.vin());
        assertEquals("AVAILABLE", car.status());
    }

    @Test
    void getAvailableCar_shouldReadCarFromRealGrpcServer() {
        StorageCar car = client.getAvailableCar(CAR_ID);

        assertEquals(CAR_ID, car.id());
        assertEquals(MODEL_ID, car.modelId());
        assertEquals("WBATEST00000000001", car.vin());
        assertEquals("AVAILABLE", car.status());
    }

    @Test
    void getAvailableCar_shouldMapNotFoundFromRealGrpcServer() {
        UUID missingId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");

        StorageCarNotFoundException ex = assertThrows(
                StorageCarNotFoundException.class,
                () -> client.getAvailableCar(missingId)
        );

        assertEquals("Car is not available", ex.getMessage());
    }

    private static class TestCarService extends CarServiceGrpc.CarServiceImplBase {

        @Override
        public void listAvailableCars(
                Cars.Empty request,
                StreamObserver<Cars.CarListResponse> responseObserver
        ) {
            responseObserver.onNext(Cars.CarListResponse.newBuilder()
                    .addCars(testCar())
                    .build());
            responseObserver.onCompleted();
        }

        @Override
        public void getCarById(
                Cars.CarRequest request,
                StreamObserver<Cars.Car> responseObserver
        ) {
            if (!CAR_ID.toString().equals(request.getId())) {
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription("Car is not available")
                        .asRuntimeException());
                return;
            }

            responseObserver.onNext(testCar());
            responseObserver.onCompleted();
        }

        private static Cars.Car testCar() {
            return Cars.Car.newBuilder()
                    .setId(CAR_ID.toString())
                    .setModelId(MODEL_ID.toString())
                    .setVin("WBATEST00000000001")
                    .setStatus("AVAILABLE")
                    .build();
        }
    }
}
