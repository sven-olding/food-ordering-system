package com.food.ordering.system.customer.service.messaging.publisher.kafka;

import com.food.ordering.system.customer.service.domain.config.CustomerServiceConfigData;
import com.food.ordering.system.customer.service.domain.outbox.model.CustomerCreatedEventPayload;
import com.food.ordering.system.customer.service.domain.outbox.model.CustomerOutboxMessage;
import com.food.ordering.system.customer.service.domain.ports.output.message.publisher.CustomerCreatedEventMessagePublisher;
import com.food.ordering.system.customer.service.messaging.mapper.CustomerMessagingDataMapper;
import com.food.ordering.system.kafka.order.avro.model.CustomerAvroModel;
import com.food.ordering.system.kafka.producer.KafkaMessageHelper;
import com.food.ordering.system.kafka.producer.service.KafkaProducer;
import com.food.ordering.system.outbox.OutboxStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@RequiredArgsConstructor
@Slf4j
@Component
public class CustomerCreatedEventKafkaPublisher implements CustomerCreatedEventMessagePublisher {
    private final KafkaProducer<String, CustomerAvroModel> kafkaProducer;
    private final CustomerMessagingDataMapper customerMessagingDataMapper;
    private final CustomerServiceConfigData customerServiceConfigData;
    private final KafkaMessageHelper kafkaMessageHelper;

    @Override
    public void publish(CustomerOutboxMessage customerOutboxMessage,
                        BiConsumer<CustomerOutboxMessage, OutboxStatus> outboxCallback) {
        CustomerCreatedEventPayload customerCreatedEventPayload = kafkaMessageHelper
                .getPayload(customerOutboxMessage.getPayload(), CustomerCreatedEventPayload.class);

        log.info("Received CustomerOutboxMessage for customer id: {}",
                customerCreatedEventPayload.getCustomerId());

        try {
            CustomerAvroModel customerAvroModel = customerMessagingDataMapper
                    .customerCreatedEventPayloadToCustomerAvroModel(customerCreatedEventPayload);

            kafkaProducer.send(customerServiceConfigData.getCustomerTopicName(),
                    customerCreatedEventPayload.getCustomerId(),
                    customerAvroModel,
                    kafkaMessageHelper.getKafkaCallback(customerServiceConfigData.getCustomerTopicName(),
                            customerAvroModel,
                            customerOutboxMessage,
                            outboxCallback,
                            customerCreatedEventPayload.getCustomerId(),
                            "CustomerAvroModel"));

            log.info("CustomerAvroModel sent to kafka for customer id: {}",
                    customerCreatedEventPayload.getCustomerId());
        } catch (Exception e) {
            log.error("Error while sending CustomerCreatedEvent message" +
                            " to kafka with customer id: {}, error: {}",
                    customerCreatedEventPayload.getCustomerId(), e.getMessage(), e);
        }
    }
}
