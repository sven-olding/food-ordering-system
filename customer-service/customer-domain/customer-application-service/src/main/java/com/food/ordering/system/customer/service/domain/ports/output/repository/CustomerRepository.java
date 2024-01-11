package com.food.ordering.system.customer.service.domain.ports.output.repository;

import com.food.ordering.system.customer.service.domain.entity.Customer;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {
    Optional<Customer> findById(UUID id);

    Customer save(Customer customer);
}
