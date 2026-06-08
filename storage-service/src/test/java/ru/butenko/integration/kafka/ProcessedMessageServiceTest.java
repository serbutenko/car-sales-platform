package ru.butenko.integration.kafka;

import org.junit.jupiter.api.Test;
import ru.butenko.persistence.repository.ProcessedMessageRepository;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProcessedMessageServiceTest {

    private final ProcessedMessageRepository repository = mock(ProcessedMessageRepository.class);
    private final ProcessedMessageService service = new ProcessedMessageService(repository);

    @Test
    void processIfNew_shouldRunHandlerAndSaveMessage_whenMessageWasNotProcessed() {
        UUID eventId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        String consumerName = "storage-service-order-events";
        AtomicBoolean handled = new AtomicBoolean(false);
        when(repository.existsByEventIdAndConsumerName(eventId, consumerName)).thenReturn(false);

        service.processIfNew(eventId, "OrderSentForApproval", consumerName, () -> handled.set(true));

        assertTrue(handled.get());
        verify(repository).save(any());
    }

    @Test
    void processIfNew_shouldSkipHandler_whenMessageWasAlreadyProcessed() {
        UUID eventId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        String consumerName = "storage-service-order-events";
        AtomicBoolean handled = new AtomicBoolean(false);
        when(repository.existsByEventIdAndConsumerName(eventId, consumerName)).thenReturn(true);

        service.processIfNew(eventId, "OrderSentForApproval", consumerName, () -> handled.set(true));

        assertFalse(handled.get());
        verify(repository, never()).save(any());
    }
}
