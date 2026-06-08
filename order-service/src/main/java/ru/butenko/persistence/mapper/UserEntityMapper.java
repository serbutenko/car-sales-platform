package ru.butenko.persistence.mapper;

import org.springframework.stereotype.Component;
import ru.butenko.domain.model.User;
import ru.butenko.persistence.entity.UserEntity;

@Component
public class UserEntityMapper {

    public User toDomain(UserEntity entity) {
        return new User(
                entity.getId(),
                entity.getName(),
                entity.getRole()
        );
    }

    public UserEntity toEntity(User domain) {
        UserEntity entity = new UserEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setRole(domain.getRole());
        return entity;
    }
}
