package com.food.ordering.system.order.service.messaging.listener.kafka;

import com.food.ordering.system.kafka.consumer.KafkaConsumer;
import com.food.ordering.system.kafka.order.avro.model.PaymentResponseAvroModel;
import com.food.ordering.system.kafka.order.avro.model.PaymentStatus;
import com.food.ordering.system.order.service.domain.ports.input.message.listener.payment.PaymentResponseMessageListener;
import com.food.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class PaymentResponseKafkaListener implements KafkaConsumer<PaymentResponseAvroModel> {
    private final OrderMessagingDataMapper orderMessagingDataMapper;
    private final PaymentResponseMessageListener paymentResponseMessageListener;

    @Override
    @KafkaListener(id = "${kafka-consumer-config.payment-consumer-group-id}",
            topics = "${order-service.payment-response-topic-name}")
    public void receive(@Payload List<PaymentResponseAvroModel> messages,
                        @Header List<String> keys,
                        @Header List<Integer> partitions,
                        @Header List<Long> offsets) {

        log.info("{} number of payment responses received with keys: {}, partitions: {} and offsets: {}",
                messages.size(), keys, partitions, offsets);
        messages.forEach(paymentResponseAvroModel -> {
            if (paymentResponseAvroModel.getPaymentStatus() == PaymentStatus.COMPLETED) {
                log.info("Processing successful payment for order id: {}", paymentResponseAvroModel.getOrderId());
                paymentResponseMessageListener.paymentCompleted(orderMessagingDataMapper
                        .paymentResponseAvroModelToPaymentResponse(paymentResponseAvroModel));
            } else if (paymentResponseAvroModel.getPaymentStatus() == PaymentStatus.CANCELLED ||
                    paymentResponseAvroModel.getPaymentStatus() == PaymentStatus.FAILED) {
                log.info("Cancelling payment for order id: {}", paymentResponseAvroModel.getOrderId());
                paymentResponseMessageListener.paymentCancelled(orderMessagingDataMapper
                        .paymentResponseAvroModelToPaymentResponse(paymentResponseAvroModel));
            }
        });
    }
}
