package ru.butenko.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.butenko.application.abstractions.*;
import ru.butenko.application.components.*;
import ru.butenko.application.service.*;
import ru.butenko.security.CurrentUserService;

import java.util.List;

@Configuration
public class ApplicationConfig {

    @Bean
    public ConfiguratorValidator configuratorValidator() {
        return new ConfiguratorValidator();
    }

    @Bean
    public PriceCalculator priceCalculator() {
        return new PriceCalculator();
    }

    @Bean
    public DetailFactoryRegistry detailFactoryRegistry(ComponentOptionRepository componentOptionRepository) {
        return new DetailFactoryRegistry(List.of(
                new WheelsDetailCreator(componentOptionRepository),
                new TransmissionDetailCreator(componentOptionRepository),
                new SteeringWheelDetailCreator(componentOptionRepository),
                new InteriorDetailCreator(componentOptionRepository)
        ));
    }

    @Bean
    public ConfiguratorService configuratorService(
            CarModelRepository carModelRepository,
            DetailFactoryRegistry detailFactoryRegistry,
            ConfiguratorValidator configuratorValidator,
            PriceCalculator priceCalculator
    ) {
        return new ConfiguratorService(
                carModelRepository,
                detailFactoryRegistry,
                configuratorValidator,
                priceCalculator
        );
    }

    @Bean
    public CatalogService catalogService(CarRepository carRepository) {
        return new CatalogService(carRepository);
    }

    @Bean
    public TestDriveService testDriveService(
            CarRepository carRepository,
            TestDriveRepository testDriveRepository,
            TestDriveCarRepository testDriveCarRepository
    ) {
        return new TestDriveService(
                carRepository,
                testDriveRepository,
                testDriveCarRepository
        );
    }

    @Bean
    public OrderService orderService(
            CarRepository carRepository,
            UserRepository userRepository,
            StockOrderRepository stockOrderRepository,
            CustomOrderRepository customOrderRepository,
            ConfiguratorService configuratorService,
            CurrentUserService currentUserService,
            OrderApprovalEventPublisher orderApprovalEventPublisher
    ) {
        return new OrderService(
                carRepository,
                userRepository,
                stockOrderRepository,
                customOrderRepository,
                configuratorService,
                currentUserService,
                orderApprovalEventPublisher
        );
    }
}
