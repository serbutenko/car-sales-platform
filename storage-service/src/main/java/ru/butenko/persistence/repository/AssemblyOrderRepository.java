package ru.butenko.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.butenko.persistence.entity.AssemblyOrderEntity;

import java.util.UUID;

public interface AssemblyOrderRepository extends JpaRepository<AssemblyOrderEntity, UUID> {
}
