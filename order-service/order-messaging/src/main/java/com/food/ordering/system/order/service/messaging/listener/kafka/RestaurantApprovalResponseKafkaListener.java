package com.food.ordering.system.order.service.messaging.listener.kafka;

import com.food.ordering.system.kafka.consumer.KafkaConsumer;
import com.food.ordering.system.kafka.order.avro.model.OrderApprovalStatus;
import com.food.ordering.system.kafka.order.avro.model.RestaurantApprovalResponseAvroModel;
import com.food.ordering.system.order.service.domain.ports.input.message.listener.restaurantapproval.RestaurantApprovalResponseMessageListener;
import com.food.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestaurantApprovalResponseKafkaListener implements KafkaConsumer<RestaurantApprovalResponseAvroModel> {
    private final OrderMessagingDataMapper orderMessagingDataMapper;
    private final RestaurantApprovalResponseMessageListener restaurantApprovalResponseMessageListener;

    @Override
    @KafkaListener(id = "${kafka-consumer-config.restaurant-approval-consumer-group-id}",
            topics = "${order-service.restaurant-approval-response-topic-name}")
    public void receive(@Payload List<RestaurantApprovalResponseAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        log.info("{} number of restaurant responses received with keys: {}, partitions: {} and offsets: {}",
                messages.size(), keys, partitions, offsets);
        messages.forEach(restaurantApprovalResponseAvroModel -> {
            if (restaurantApprovalResponseAvroModel.getOrderApprovalStatus() == OrderApprovalStatus.APPROVED) {
                log.info("Processing approved order for order id: {}", restaurantApprovalResponseAvroModel.getOrderId());
                restaurantApprovalResponseMessageListener.orderApproved(orderMessagingDataMapper
                        .restaurantApprovalResponseAvroModelToRestaurantApprovalResponse(
                                restaurantApprovalResponseAvroModel));
            } else if (restaurantApprovalResponseAvroModel.getOrderApprovalStatus() == OrderApprovalStatus.REJECTED) {
                log.info("Order with id: {} was rejected with failure messages: {}",
                        restaurantApprovalResponseAvroModel.getOrderId(),
                        restaurantApprovalResponseAvroModel.getFailureMessages());
                restaurantApprovalResponseMessageListener.orderRejected(orderMessagingDataMapper
                        .restaurantApprovalResponseAvroModelToRestaurantApprovalResponse(
                                restaurantApprovalResponseAvroModel));
            }
        });
    }
}
