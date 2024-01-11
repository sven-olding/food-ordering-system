package com.food.ordering.system.customer.service.domain;

import com.food.ordering.system.customer.service.domain.dto.create.CreateCustomerCommand;
import com.food.ordering.system.customer.service.domain.dto.create.CreateCustomerResponse;
import com.food.ordering.system.customer.service.domain.event.CustomerCreatedEvent;
import com.food.ordering.system.customer.service.domain.exception.CustomerDomainException;
import com.food.ordering.system.customer.service.domain.mapper.CustomerDataMapper;
import com.food.ordering.system.customer.service.domain.outbox.CustomerOutboxHelper;
import com.food.ordering.system.customer.service.domain.ports.output.repository.CustomerRepository;
import com.food.ordering.system.order.service.domain.entity.Customer;
import com.food.ordering.system.outbox.OutboxStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerCreateCommandHandler {
    private final CustomerCreateHelper customerCreateHelper;
    private final CustomerDataMapper customerDataMapper;
    private final CustomerOutboxHelper customerOutboxHelper;

    @Transactional
    public CreateCustomerResponse createCustomer(CreateCustomerCommand createCustomerCommand) {
        CustomerCreatedEvent customerCreatedEvent = customerCreateHelper.persistCustomer(createCustomerCommand);

        log.info("Customer is created with id: {}",
                customerCreatedEvent.getCustomer().getId().getValue());

        CreateCustomerResponse createCustomerResponse = customerDataMapper
                .customerCreatedEventToCreateCustomerResponse(customerCreatedEvent);

        customerOutboxHelper.saveCustomerOutboxMessage(
                customerDataMapper.customerCreatedEventToCustomerCreatedEventPayload(customerCreatedEvent),
                OutboxStatus.STARTED
        );
        return createCustomerResponse;
    }

}
