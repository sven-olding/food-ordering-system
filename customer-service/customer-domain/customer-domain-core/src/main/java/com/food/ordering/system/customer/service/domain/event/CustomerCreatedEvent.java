package com.food.ordering.system.customer.service.domain.event;

import com.food.ordering.system.domain.event.DomainEvent;
import com.food.ordering.system.order.service.domain.entity.Customer;

import java.time.ZonedDateTime;
import java.util.List;

public class CustomerCreatedEvent implements DomainEvent<Customer> {
    private final Customer customer;
    private final ZonedDateTime createdAt;
    private final List<String> failureMessages;

    public CustomerCreatedEvent(Customer customer, ZonedDateTime createdAt, List<String> failureMessages) {
        this.customer = customer;
        this.createdAt = createdAt;
        this.failureMessages = failureMessages;
    }

    public Customer getCustomer() {
        return customer;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public List<String> getFailureMessages() {
        return failureMessages;
    }
}
