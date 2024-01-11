package com.food.ordering.system.customer.service.domain;

import com.food.ordering.system.customer.service.domain.dto.create.CreateCustomerCommand;
import com.food.ordering.system.customer.service.domain.event.CustomerCreatedEvent;
import com.food.ordering.system.customer.service.domain.exception.CustomerDomainException;
import com.food.ordering.system.customer.service.domain.mapper.CustomerDataMapper;
import com.food.ordering.system.customer.service.domain.ports.output.repository.CustomerRepository;
import com.food.ordering.system.customer.service.domain.entity.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Component
public class CustomerCreateHelper {
    private final CustomerDomainService customerDomainService;
    private final CustomerRepository customerRepository;
    private final CustomerDataMapper customerDataMapper;

    @Transactional
    public CustomerCreatedEvent persistCustomer(CreateCustomerCommand createCustomerCommand) {
        Customer customer = customerDataMapper.createCustomerCommandToCustomer(createCustomerCommand);
        CustomerCreatedEvent customerCreatedEvent = customerDomainService.createCustomer(customer);
        customer = customerRepository.save(customerCreatedEvent.getCustomer());
        if (customer == null) {
            log.error("Could not save customer: {}", createCustomerCommand.username());
            throw new CustomerDomainException("Could not save customer: "
                    + createCustomerCommand.username());
        }
        return customerCreatedEvent;
    }
}
