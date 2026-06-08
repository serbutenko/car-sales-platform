package ru.butenko.config;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.Metadata;
import io.grpc.MethodDescriptor;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class GrpcClientConfigTest {

    @Test
    void deadlineInterceptor_shouldAddDeadlineToEveryCall() {
        GrpcClientConfig config = new GrpcClientConfig();
        ReflectionTestUtils.setField(config, "timeoutMs", 3000L);
        CapturingChannel channel = new CapturingChannel();

        ClientInterceptor interceptor = config.deadlineInterceptor();
        interceptor.interceptCall(method(), CallOptions.DEFAULT, channel);

        assertNotNull(channel.callOptions.getDeadline());
    }

    private MethodDescriptor<byte[], byte[]> method() {
        MethodDescriptor.Marshaller<byte[]> marshaller = new MethodDescriptor.Marshaller<>() {
            @Override
            public java.io.InputStream stream(byte[] value) {
                return new java.io.ByteArrayInputStream(value);
            }

            @Override
            public byte[] parse(java.io.InputStream stream) {
                return new byte[0];
            }
        };

        return MethodDescriptor.<byte[], byte[]>newBuilder()
                .setType(MethodDescriptor.MethodType.UNARY)
                .setFullMethodName("test.Service/Call")
                .setRequestMarshaller(marshaller)
                .setResponseMarshaller(marshaller)
                .build();
    }

    private static class CapturingChannel extends Channel {
        private CallOptions callOptions;

        @Override
        public <RequestT, ResponseT> ClientCall<RequestT, ResponseT> newCall(
                MethodDescriptor<RequestT, ResponseT> methodDescriptor,
                CallOptions callOptions
        ) {
            this.callOptions = callOptions;
            return new ClientCall<>() {
                @Override
                public void start(Listener<ResponseT> responseListener, Metadata headers) {
                }

                @Override
                public void request(int numMessages) {
                }

                @Override
                public void cancel(String message, Throwable cause) {
                }

                @Override
                public void halfClose() {
                }

                @Override
                public void sendMessage(RequestT message) {
                }
            };
        }

        @Override
        public String authority() {
            return "test";
        }
    }
}
