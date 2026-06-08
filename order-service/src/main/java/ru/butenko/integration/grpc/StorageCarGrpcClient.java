package ru.butenko.integration.grpc;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.butenko.proto.storage.CarServiceGrpc;
import ru.butenko.proto.storage.Cars;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class StorageCarGrpcClient {
    private final CarServiceGrpc.CarServiceBlockingStub carServiceStub;

    public List<StorageCar> listAvailableCars() {
        try {
            log.info("gRPC client request: ListAvailableCars");
            Cars.CarListResponse response = carServiceStub.listAvailableCars(Cars.Empty.newBuilder().build());
            log.info("gRPC client response: ListAvailableCars returned {} cars", response.getCarsCount());

            return response.getCarsList().stream()
                    .map(this::toStorageCar)
                    .toList();
        } catch (StatusRuntimeException ex) {
            throw mapGrpcException(ex);
        }
    }

    public StorageCar getAvailableCar(UUID id) {
        try {
            log.info("gRPC client request: GetCarById id={}", id);
            Cars.Car response = carServiceStub.getCarById(Cars.CarRequest.newBuilder()
                    .setId(id.toString())
                    .build());
            return toStorageCar(response);
        } catch (StatusRuntimeException ex) {
            throw mapGrpcException(ex);
        }
    }

    private StorageCar toStorageCar(Cars.Car car) {
        return new StorageCar(
                UUID.fromString(car.getId()),
                UUID.fromString(car.getModelId()),
                car.getVin(),
                car.getStatus()
        );
    }

    private RuntimeException mapGrpcException(StatusRuntimeException ex) {
        Status.Code code = ex.getStatus().getCode();
        if (code == Status.Code.NOT_FOUND) {
            return new StorageCarNotFoundException(ex.getStatus().getDescription());
        }
        if (code == Status.Code.UNAVAILABLE || code == Status.Code.DEADLINE_EXCEEDED) {
            return new StorageServiceUnavailableException("StorageService is unavailable", ex);
        }
        return ex;
    }
}
