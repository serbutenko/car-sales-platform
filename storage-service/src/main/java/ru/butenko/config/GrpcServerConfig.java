package ru.butenko.config;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.butenko.api.grpc.CarGrpcService;

@Configuration
public class GrpcServerConfig {

    @Bean(initMethod = "start", destroyMethod = "shutdown")
    public Server grpcServer(
            CarGrpcService carGrpcService,
            @Value("${spring.grpc.server.port:9090}") int port
    ) {
        return ServerBuilder.forPort(port)
                .addService(carGrpcService)
                .build();
    }
}
