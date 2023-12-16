package com.food.ordering.system.order.service.dataaccess.order.mapper;

import com.food.ordering.system.domain.valueobject.*;
import com.food.ordering.system.order.service.dataaccess.order.entity.OrderAddressEntity;
import com.food.ordering.system.order.service.dataaccess.order.entity.OrderEntity;
import com.food.ordering.system.order.service.dataaccess.order.entity.OrderItemEntity;
import com.food.ordering.system.order.service.domain.entity.Order;
import com.food.ordering.system.order.service.domain.entity.OrderItem;
import com.food.ordering.system.order.service.domain.entity.Product;
import com.food.ordering.system.order.service.domain.valueobject.OrderItemId;
import com.food.ordering.system.order.service.domain.valueobject.StreetAddress;
import com.food.ordering.system.order.service.domain.valueobject.TrackingId;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderDataAccessMapper {
    public OrderEntity orderToOrderEntity(Order order) {
        OrderEntity orderEntity = OrderEntity.builder()
                .id(order.getId().getValue())
                .customerId(order.getCustomerId().getValue())
                .trackingId(order.getTrackingId().getValue())
                .restaurantId(order.getRestaurantId().getValue())
                .orderStatus(order.getOrderStatus())
                .price(order.getPrice().getAmount())
                .address(streetAddressToOrderAddressEntity(order.getDeliveryAddress()))
                .items(orderItemsToOrderItemEntities(order.getItems()))
                .failureMessages(order.getFailureMessages() != null ?
                        String.join(",", order.getFailureMessages()) : "")
                .build();

        orderEntity.getAddress().setOrder(orderEntity);
        orderEntity.getItems().forEach(orderItemEntity -> orderItemEntity.setOrder(orderEntity));

        return orderEntity;
    }

    public Order orderEntityToOrder(OrderEntity orderEntity) {
        return Order.builder()
                .orderId(new OrderId(orderEntity.getId()))
                .restaurantId(new RestaurantId(orderEntity.getRestaurantId()))
                .customerId(new CustomerId(orderEntity.getCustomerId()))
                .trackingId(new TrackingId(orderEntity.getTrackingId()))
                .orderStatus(orderEntity.getOrderStatus())
                .deliveryAddress(orderAddressEntityToStreetAddress(orderEntity.getAddress()))
                .failureMessages(orderEntity.getFailureMessages() != null ?
                        List.of(orderEntity.getFailureMessages().split(",")) : new ArrayList<>())
                .items(orderItemEntitiesToOrderItems(orderEntity.getItems()))
                .build();
    }

    private StreetAddress orderAddressEntityToStreetAddress(OrderAddressEntity orderAddressEntity) {
        return new StreetAddress(orderAddressEntity.getId(),
                orderAddressEntity.getStreet(),
                orderAddressEntity.getPostalCode(),
                orderAddressEntity.getCity());
    }

    private List<OrderItem> orderItemEntitiesToOrderItems(List<OrderItemEntity> orderItemEntities) {
        return orderItemEntities.stream()
                .map(this::orderItemEntityToOrderItem)
                .toList();
    }

    private OrderItem orderItemEntityToOrderItem(OrderItemEntity orderItemEntity) {
        return OrderItem.builder()
                .subTotal(new Money(orderItemEntity.getSubTotal()))
                .quantity(orderItemEntity.getQuantity())
                .price(new Money(orderItemEntity.getPrice()))
                .product(new Product(new ProductId(orderItemEntity.getProductId())))
                .orderItemId(new OrderItemId(orderItemEntity.getId()))
                .orderId(new OrderId(orderItemEntity.getOrder().getId()))
                .build();
    }

    private List<OrderItemEntity> orderItemsToOrderItemEntities(List<OrderItem> items) {
        return items.stream()
                .map(orderItem -> OrderItemEntity.builder()
                        .id(orderItem.getId().getValue())
                        .productId(orderItem.getProduct().getId().getValue())
                        .price(orderItem.getPrice().getAmount())
                        .quantity(orderItem.getQuantity())
                        .subTotal(orderItem.getSubTotal().getAmount())
                        .build())
                .toList();
    }

    private OrderAddressEntity streetAddressToOrderAddressEntity(StreetAddress streetAddress) {
        return OrderAddressEntity.builder()
                .street(streetAddress.getStreet())
                .city(streetAddress.getCity())
                .postalCode(streetAddress.getPostalCode())
                .id(streetAddress.getId())
                .build();
    }
}
