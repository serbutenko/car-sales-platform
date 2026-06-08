package ru.butenko.persistence.mapper;

import org.springframework.stereotype.Component;
import ru.butenko.domain.model.TestDriveRequest;
import ru.butenko.persistence.entity.CarEntity;
import ru.butenko.persistence.entity.TestDriveRequestEntity;

@Component
public class TestDriveRequestEntityMapper {

    public TestDriveRequest toDomain(TestDriveRequestEntity entity) {
        return new TestDriveRequest(
                entity.getId(),
                entity.getClientId(),
                entity.getCar().getId(),
                entity.getStartAt()
        );
    }

    public TestDriveRequestEntity toEntity(TestDriveRequest domain, CarEntity carEntity) {
        TestDriveRequestEntity entity = new TestDriveRequestEntity();
        entity.setId(domain.getId());
        entity.setClientId(domain.getClientId());
        entity.setCar(carEntity);
        entity.setStartAt(domain.getStartAt());
        return entity;
    }
}
