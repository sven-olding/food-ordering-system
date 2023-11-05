package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.order.service.domain.dto.track.TrackOrderQuery;
import com.food.ordering.system.order.service.domain.dto.track.TrackOrderResponse;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.exception.OrderNotFoundException;
import com.food.ordering.system.order.service.domain.mapper.OrderDataMapper;
import com.food.ordering.system.order.service.domain.ports.output.respository.OrderRepository;
import com.food.ordering.system.order.service.domain.valueobject.TrackingId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderTrackCommandHandler {
    private final OrderRepository orderRepository;
    private final OrderDataMapper orderDataMapper;

    @Transactional(readOnly = true)
    public TrackOrderResponse trackOrder(TrackOrderQuery trackOrderQuery) {
        Optional<Order> optionalOrder = orderRepository.findByTrackingId(new TrackingId(trackOrderQuery.getOrderTrackingId()));
        if (optionalOrder.isEmpty()) {
            log.error("No order found with tracking id: {}", trackOrderQuery.getOrderTrackingId());
            throw new OrderNotFoundException("No order found with tracking id: " + trackOrderQuery.getOrderTrackingId());
        }

        return orderDataMapper.orderToTrackOrderResponse(optionalOrder.get());
    }
}
