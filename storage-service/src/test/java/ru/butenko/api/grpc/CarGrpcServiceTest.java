package ru.butenko.api.grpc;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Test;
import ru.butenko.domain.storage.CarStatus;
import ru.butenko.persistence.entity.CarEntity;
import ru.butenko.persistence.repository.CarRepository;
import ru.butenko.proto.storage.Cars;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CarGrpcServiceTest {

    private static final UUID CAR_ID = UUID.fromString("55555555-5555-5555-5555-555555555555");
    private static final UUID MODEL_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");

    private final CarRepository carRepository = mock(CarRepository.class);
    private final CarGrpcService service = new CarGrpcService(carRepository);

    @Test
    void listAvailableCars_shouldReturnCarsFromRepository() {
        when(carRepository.findAllByStatusAndRemovedFalse(CarStatus.AVAILABLE)).thenReturn(List.of(car()));

        CapturingObserver<Cars.CarListResponse> observer = new CapturingObserver<>();
        service.listAvailableCars(Cars.Empty.newBuilder().build(), observer);

        Cars.CarListResponse response = observer.value();
        assertEquals(1, response.getCarsCount());
        assertEquals(CAR_ID.toString(), response.getCars(0).getId());
        assertEquals(MODEL_ID.toString(), response.getCars(0).getModelId());
        assertEquals("VIN-1", response.getCars(0).getVin());
        assertEquals("AVAILABLE", response.getCars(0).getStatus());
    }

    @Test
    void listAvailableCars_shouldReturnEmptyResponse_whenRepositoryReturnsNoCars() {
        when(carRepository.findAllByStatusAndRemovedFalse(CarStatus.AVAILABLE)).thenReturn(List.of());

        CapturingObserver<Cars.CarListResponse> observer = new CapturingObserver<>();
        service.listAvailableCars(Cars.Empty.newBuilder().build(), observer);

        assertEquals(0, observer.value().getCarsCount());
    }

    @Test
    void getCarById_shouldReturnCarFromRepository() {
        when(carRepository.findByIdAndStatusAndRemovedFalse(CAR_ID, CarStatus.AVAILABLE))
                .thenReturn(Optional.of(car()));

        CapturingObserver<Cars.Car> observer = new CapturingObserver<>();
        service.getCarById(Cars.CarRequest.newBuilder().setId(CAR_ID.toString()).build(), observer);

        Cars.Car response = observer.value();
        assertEquals(CAR_ID.toString(), response.getId());
        assertEquals(MODEL_ID.toString(), response.getModelId());
        assertEquals("VIN-1", response.getVin());
        assertEquals("AVAILABLE", response.getStatus());
    }

    @Test
    void getCarById_shouldReturnNotFound_whenRepositoryReturnsEmpty() {
        when(carRepository.findByIdAndStatusAndRemovedFalse(CAR_ID, CarStatus.AVAILABLE))
                .thenReturn(Optional.empty());

        CapturingObserver<Cars.Car> observer = new CapturingObserver<>();
        service.getCarById(Cars.CarRequest.newBuilder().setId(CAR_ID.toString()).build(), observer);

        assertEquals(Status.Code.NOT_FOUND, observer.error().getStatus().getCode());
    }

    @Test
    void getCarById_shouldReturnInvalidArgument_whenIdIsInvalid() {
        CapturingObserver<Cars.Car> observer = new CapturingObserver<>();
        service.getCarById(Cars.CarRequest.newBuilder().setId("not-a-uuid").build(), observer);

        assertEquals(Status.Code.INVALID_ARGUMENT, observer.error().getStatus().getCode());
    }

    private CarEntity car() {
        CarEntity car = new CarEntity();
        car.setId(CAR_ID);
        car.setModelId(MODEL_ID);
        car.setVin("VIN-1");
        car.setStatus(CarStatus.AVAILABLE);
        car.setCreatedAt(Instant.parse("2026-01-01T00:00:00Z"));
        car.setUpdatedAt(Instant.parse("2026-01-01T00:00:00Z"));
        car.setRemoved(false);
        return car;
    }

    private static class CapturingObserver<T> implements StreamObserver<T> {
        private T value;
        private Throwable error;
        private boolean completed;

        @Override
        public void onNext(T value) {
            this.value = value;
        }

        @Override
        public void onError(Throwable throwable) {
            this.error = throwable;
        }

        @Override
        public void onCompleted() {
            this.completed = true;
        }

        T value() {
            assertEquals(true, completed);
            return value;
        }

        StatusRuntimeException error() {
            return assertThrows(StatusRuntimeException.class, () -> {
                if (error != null) {
                    throw error;
                }
            });
        }
    }
}
