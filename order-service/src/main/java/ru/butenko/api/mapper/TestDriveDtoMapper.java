package ru.butenko.api.mapper;

import org.springframework.stereotype.Component;
import ru.butenko.api.dto.response.TestDriveRequestResponse;
import ru.butenko.domain.model.TestDriveRequest;

@Component
public class TestDriveDtoMapper {

    public TestDriveRequestResponse toResponse(TestDriveRequest request) {
        return new TestDriveRequestResponse(
                request.getId(),
                request.getClientId(),
                request.getCarId(),
                request.getStartAt()
        );
    }
}
