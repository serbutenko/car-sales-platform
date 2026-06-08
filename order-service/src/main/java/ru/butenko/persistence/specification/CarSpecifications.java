package ru.butenko.persistence.specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import ru.butenko.domain.model.CarFilter;
import ru.butenko.persistence.entity.CarEntity;
import ru.butenko.persistence.entity.ComponentOptionEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class CarSpecifications {

    private CarSpecifications() {}

    public static Specification<CarEntity> byFilter(CarFilter filter) {
        CarFilter f = filter == null ? new CarFilter() : filter;

        return (root, query, cb) -> {
            if (query != null) {
                query.distinct(true);
            }

            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.isFalse(root.get("removed")));
            addRangePredicate(predicates, cb, root.get("price"), f.getMinPrice(), f.getMaxPrice());
            addRangePredicate(predicates, cb, root.get("engineVolume"), f.getMinEngineVolume(), f.getMaxEngineVolume());

            if (f.getMinEnginePowerHp() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("enginePowerHp"), f.getMinEnginePowerHp().intValue()));
            }
            if (f.getMaxEnginePowerHp() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("enginePowerHp"), f.getMaxEnginePowerHp().intValue()));
            }
            if (hasText(f.getBrand())) {
                predicates.add(cb.equal(cb.lower(root.get("brand")), f.getBrand().toLowerCase()));
            }
            if (hasText(f.getModelName())) {
                predicates.add(cb.equal(cb.lower(root.get("modelName")), f.getModelName().toLowerCase()));
            }
            if (f.getBodyType() != null) {
                predicates.add(cb.equal(root.get("bodyType"), f.getBodyType()));
            }
            if (f.getFuelType() != null) {
                predicates.add(cb.equal(root.get("fuelType"), f.getFuelType()));
            }
            if (f.getTransmissionType() != null) {
                predicates.add(cb.equal(root.get("transmissionType"), f.getTransmissionType()));
            }
            if (f.getDriveType() != null) {
                predicates.add(cb.equal(root.get("driveType"), f.getDriveType()));
            }
            if (f.getColor() != null) {
                predicates.add(cb.equal(root.get("color"), f.getColor()));
            }

            if (f.getComponentOptionIds() != null && !f.getComponentOptionIds().isEmpty()) {
                for (UUID optionId : f.getComponentOptionIds()) {
                    var subquery = query.subquery(UUID.class);
                    var optionRoot = subquery.from(ComponentOptionEntity.class);
                    Join<ComponentOptionEntity, UUID> compatibleModels = optionRoot.join("compatibleModelIds");

                    subquery.select(optionRoot.get("id"))
                            .where(
                                    cb.equal(optionRoot.get("id"), optionId),
                                    cb.isFalse(optionRoot.get("removed")),
                                    cb.equal(compatibleModels, root.get("model").get("id"))
                            );

                    predicates.add(cb.exists(subquery));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static <T extends Comparable<? super T>> void addRangePredicate(
            List<Predicate> predicates,
            jakarta.persistence.criteria.CriteriaBuilder cb,
            jakarta.persistence.criteria.Path<T> path,
            T min,
            T max
    ) {
        if (min != null) {
            predicates.add(cb.greaterThanOrEqualTo(path, min));
        }
        if (max != null) {
            predicates.add(cb.lessThanOrEqualTo(path, max));
        }
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
