package com.food.ordering.system.customer.service.dataaccess.outbox.adapter;

import com.food.ordering.system.customer.service.dataaccess.outbox.exception.CustomerOutboxNotFoundException;
import com.food.ordering.system.customer.service.dataaccess.outbox.mapper.CustomerOutboxDataAccessMapper;
import com.food.ordering.system.customer.service.dataaccess.outbox.repository.CustomerOutboxJpaRepository;
import com.food.ordering.system.customer.service.domain.outbox.model.CustomerOutboxMessage;
import com.food.ordering.system.customer.service.domain.ports.output.repository.CustomerOutboxRepository;
import com.food.ordering.system.outbox.OutboxStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CustomerOutboxRepositoryImpl implements CustomerOutboxRepository {
    private final CustomerOutboxJpaRepository customerOutboxJpaRepository;
    private final CustomerOutboxDataAccessMapper customerOutboxDataAccessMapper;

    @Override
    public CustomerOutboxMessage save(CustomerOutboxMessage customerOutboxMessage) {
        return customerOutboxDataAccessMapper
                .customerOutboxEntityToCustomerOutboxMessage(customerOutboxJpaRepository
                        .save(customerOutboxDataAccessMapper
                                .customerOutboxMessageToOutboxEntity(customerOutboxMessage)));
    }

    @Override
    public Optional<List<CustomerOutboxMessage>> findByOutboxStatus(OutboxStatus status) {
       return Optional.of(customerOutboxJpaRepository.findByOutboxStatus(status)
                .orElseThrow(() -> new CustomerOutboxNotFoundException("Customer outbox object " +
                        "cannot be found for"))
                .stream()
                .map(customerOutboxDataAccessMapper::customerOutboxEntityToCustomerOutboxMessage)
                .collect(Collectors.toList()));
    }

    @Override
    public void deleteByOutboxStatus(OutboxStatus status) {
        customerOutboxJpaRepository.deleteByOutboxStatus(status);
    }
}
