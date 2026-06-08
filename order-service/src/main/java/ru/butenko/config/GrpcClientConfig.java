package ru.butenko.config;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.ClientInterceptors;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.MethodDescriptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.butenko.proto.storage.CarServiceGrpc;

import java.util.concurrent.TimeUnit;

@Configuration
public class GrpcClientConfig {
    @Value("${storage.grpc.host:localhost}")
    private String host;

    @Value("${storage.grpc.port:9090}")
    private int port;

    @Value("${storage.grpc.timeout-ms:3000}")
    private long timeoutMs;

    @Bean(destroyMethod = "shutdown")
    public ManagedChannel grpcChannel() {
        return ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
    }

    @Bean
    public CarServiceGrpc.CarServiceBlockingStub carServiceStub(ManagedChannel channel) {
        return CarServiceGrpc.newBlockingStub(ClientInterceptors.intercept(channel, deadlineInterceptor()));
    }

    @Bean
    public ClientInterceptor deadlineInterceptor() {
        return new ClientInterceptor() {
            @Override
            public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(
                    MethodDescriptor<ReqT, RespT> method,
                    CallOptions callOptions,
                    Channel next
            ) {
                return next.newCall(method, callOptions.withDeadlineAfter(timeoutMs, TimeUnit.MILLISECONDS));
            }
        };
    }
}
