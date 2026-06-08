package ru.butenko.integration.grpc;

import io.grpc.Status;
import org.junit.jupiter.api.Test;
import ru.butenko.proto.storage.CarServiceGrpc;
import ru.butenko.proto.storage.Cars;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StorageCarGrpcClientTest {

    private static final UUID CAR_ID = UUID.fromString("55555555-5555-5555-5555-555555555555");
    private static final UUID MODEL_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");

    @Test
    void listAvailableCars_shouldMapGrpcResponse() {
        CarServiceGrpc.CarServiceBlockingStub stub = mock(CarServiceGrpc.CarServiceBlockingStub.class);
        when(stub.listAvailableCars(Cars.Empty.newBuilder().build()))
                .thenReturn(Cars.CarListResponse.newBuilder().addCars(car()).build());
        StorageCarGrpcClient client = new StorageCarGrpcClient(stub);

        List<StorageCar> cars = client.listAvailableCars();

        assertEquals(1, cars.size());
        assertEquals(new StorageCar(CAR_ID, MODEL_ID, "VIN-1", "AVAILABLE"), cars.get(0));
    }

    @Test
    void listAvailableCars_shouldReturnEmptyList_whenStorageReturnsNoCars() {
        CarServiceGrpc.CarServiceBlockingStub stub = mock(CarServiceGrpc.CarServiceBlockingStub.class);
        when(stub.listAvailableCars(Cars.Empty.newBuilder().build()))
                .thenReturn(Cars.CarListResponse.newBuilder().build());
        StorageCarGrpcClient client = new StorageCarGrpcClient(stub);

        assertEquals(List.of(), client.listAvailableCars());
    }

    @Test
    void getAvailableCar_shouldMapGrpcResponse() {
        CarServiceGrpc.CarServiceBlockingStub stub = mock(CarServiceGrpc.CarServiceBlockingStub.class);
        when(stub.getCarById(Cars.CarRequest.newBuilder().setId(CAR_ID.toString()).build()))
                .thenReturn(car());
        StorageCarGrpcClient client = new StorageCarGrpcClient(stub);

        StorageCar car = client.getAvailableCar(CAR_ID);

        assertEquals(new StorageCar(CAR_ID, MODEL_ID, "VIN-1", "AVAILABLE"), car);
    }

    @Test
    void getAvailableCar_shouldThrowNotFound_whenGrpcReturnsNotFound() {
        CarServiceGrpc.CarServiceBlockingStub stub = mock(CarServiceGrpc.CarServiceBlockingStub.class);
        when(stub.getCarById(Cars.CarRequest.newBuilder().setId(CAR_ID.toString()).build()))
                .thenThrow(Status.NOT_FOUND.withDescription("not found").asRuntimeException());
        StorageCarGrpcClient client = new StorageCarGrpcClient(stub);

        assertThrows(StorageCarNotFoundException.class, () -> client.getAvailableCar(CAR_ID));
    }

    @Test
    void listAvailableCars_shouldThrowUnavailable_whenGrpcReturnsUnavailable() {
        CarServiceGrpc.CarServiceBlockingStub stub = mock(CarServiceGrpc.CarServiceBlockingStub.class);
        when(stub.listAvailableCars(Cars.Empty.newBuilder().build()))
                .thenThrow(Status.UNAVAILABLE.asRuntimeException());
        StorageCarGrpcClient client = new StorageCarGrpcClient(stub);

        assertThrows(StorageServiceUnavailableException.class, client::listAvailableCars);
    }

    @Test
    void listAvailableCars_shouldThrowUnavailable_whenGrpcDeadlineExceeded() {
        CarServiceGrpc.CarServiceBlockingStub stub = mock(CarServiceGrpc.CarServiceBlockingStub.class);
        when(stub.listAvailableCars(Cars.Empty.newBuilder().build()))
                .thenThrow(Status.DEADLINE_EXCEEDED.asRuntimeException());
        StorageCarGrpcClient client = new StorageCarGrpcClient(stub);

        assertThrows(StorageServiceUnavailableException.class, client::listAvailableCars);
    }

    private static Cars.Car car() {
        return Cars.Car.newBuilder()
                .setId(CAR_ID.toString())
                .setModelId(MODEL_ID.toString())
                .setVin("VIN-1")
                .setStatus("AVAILABLE")
                .build();
    }
}
