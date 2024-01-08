package com.food.ordering.system.order.service.domain;

import com.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.entity.Product;
import com.food.ordering.system.order.service.domain.entity.Restaurant;
import com.food.ordering.system.order.service.domain.event.OrderCancelledEvent;
import com.food.ordering.system.order.service.domain.event.OrderCreatedEvent;
import com.food.ordering.system.order.service.domain.event.OrderPaidEvent;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static com.food.ordering.system.domain.DomainConstants.TIMEZONE_ID;

@RequiredArgsConstructor
@Slf4j
public class OrderDomainServiceImpl implements OrderDomainService {

    @Override
    public OrderCreatedEvent validateAndInitiateOrder(Order order,
                                                      Restaurant restaurant) {
        validateRestaurant(restaurant);
        setOrderProductInformation(order, restaurant);
        order.validateOrder();
        order.initializeOrder();
        log.info("Order with id {} is initiated", order.getId().getValue());
        return new OrderCreatedEvent(order,
                ZonedDateTime.now(ZoneId.of(TIMEZONE_ID)));
    }

    private void setOrderProductInformation(Order order, Restaurant restaurant) {
        order.getItems().forEach(orderItem -> restaurant.getProducts().forEach(restaurantProduct -> {
            Product currentProduct = orderItem.getProduct();
            if (currentProduct.equals(restaurantProduct)) {
                currentProduct.updateWithConfirmedNameAndPrice(restaurantProduct.getName(), restaurantProduct.getPrice());
            }
        }));
    }

    private void validateRestaurant(Restaurant restaurant) {
        if (!restaurant.isActive()) {
            throw new OrderDomainException("Restaurant " + restaurant.getId().getValue() + " is not active");
        }
    }

    @Override
    public OrderPaidEvent payOrder(Order order) {
        order.pay();
        log.info("Order with id {} is paid", order.getId().getValue());
        return new OrderPaidEvent(order, ZonedDateTime.now(ZoneId.of(TIMEZONE_ID)));
    }

    @Override
    public void approveOrder(Order order) {
        order.approve();
        log.info("Order with id {} is paid", order.getId().getValue());
    }

    @Override
    public OrderCancelledEvent cancelOrderPayment(Order order,
                                                  List<String> failureMessages) {
        order.initCancel(failureMessages);
        log.info("Order payment is cancelling for order with id {}", order.getId().getValue());
        return new OrderCancelledEvent(order, ZonedDateTime.now(ZoneId.of(TIMEZONE_ID)));
    }

    @Override
    public void cancelOrder(Order order, List<String> failureMessages) {
        order.cancel(failureMessages);
    }
}
