package com.food.ordering.system.customer.service.domain;

import com.food.ordering.system.customer.service.domain.event.CustomerCreatedEvent;
import com.food.ordering.system.order.service.domain.entity.Customer;

public interface CustomerDomainService {
    CustomerCreatedEvent createCustomer(Customer customer);
}
