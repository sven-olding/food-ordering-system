package com.food.ordering.system.customer.service.domain;

import com.food.ordering.system.customer.service.domain.event.CustomerCreatedEvent;
import com.food.ordering.system.customer.service.domain.exception.CustomerDomainException;
import com.food.ordering.system.domain.valueobject.CustomerId;
import com.food.ordering.system.customer.service.domain.entity.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.food.ordering.system.domain.DomainConstants.TIMEZONE_ID;

@Slf4j
@Service
@Validated
public class CustomerDomainServiceImpl implements CustomerDomainService {
    @Override
    public CustomerCreatedEvent createCustomer(Customer customer) {
        List<String> failureMessages = validateCustomer(customer);
        if(!failureMessages.isEmpty()) {
            log.error("Customer validation failed: {}", failureMessages);
            throw new CustomerDomainException("Customer validation failed: " + failureMessages);
        }
        customer.setId(new CustomerId(UUID.randomUUID()));
        return new CustomerCreatedEvent(
                customer,
                ZonedDateTime.now(ZoneId.of(TIMEZONE_ID))
        );
    }

    private List<String> validateCustomer(Customer customer) {
        return Collections.emptyList();
    }
}
