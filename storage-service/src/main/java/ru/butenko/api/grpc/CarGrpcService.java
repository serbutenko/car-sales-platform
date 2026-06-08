package ru.butenko.api.grpc;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.butenko.domain.storage.CarStatus;
import ru.butenko.persistence.entity.CarEntity;
import ru.butenko.persistence.repository.CarRepository;
import ru.butenko.proto.storage.CarServiceGrpc;
import ru.butenko.proto.storage.Cars;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarGrpcService extends CarServiceGrpc.CarServiceImplBase {
    private final CarRepository carRepository;

    @Override
    @Transactional(readOnly = true)
    public void listAvailableCars(Cars.Empty request, StreamObserver<Cars.CarListResponse> responseObserver) {
        log.info("gRPC request: ListAvailableCars");

        Cars.CarListResponse.Builder responseBuilder = Cars.CarListResponse.newBuilder();
        carRepository.findAllByStatusAndRemovedFalse(CarStatus.AVAILABLE).stream()
                .map(this::toGrpcCar)
                .forEach(responseBuilder::addCars);

        Cars.CarListResponse response = responseBuilder.build();
        log.info("gRPC response: ListAvailableCars returned {} cars", response.getCarsCount());
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    @Transactional(readOnly = true)
    public void getCarById(Cars.CarRequest request, StreamObserver<Cars.Car> responseObserver) {
        log.info("gRPC request: GetCarById id={}", request.getId());

        UUID carId;
        try {
            carId = UUID.fromString(request.getId());
        } catch (IllegalArgumentException ex) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Invalid car id: " + request.getId())
                    .asRuntimeException());
            return;
        }

        Optional<Cars.Car> car = carRepository.findByIdAndStatusAndRemovedFalse(carId, CarStatus.AVAILABLE)
                .map(this::toGrpcCar);
        if (car.isEmpty()) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Available car not found: " + carId)
                    .asRuntimeException());
            return;
        }

        responseObserver.onNext(car.get());
        responseObserver.onCompleted();
    }

    private Cars.Car toGrpcCar(CarEntity car) {
        return Cars.Car.newBuilder()
                .setId(car.getId().toString())
                .setModelId(car.getModelId().toString())
                .setVin(car.getVin())
                .setStatus(car.getStatus().name())
                .build();
    }
}
