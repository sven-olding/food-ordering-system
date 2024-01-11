package com.food.ordering.system.customer.service.domain;

import com.food.ordering.system.customer.service.domain.dto.track.GetCustomerQuery;
import com.food.ordering.system.customer.service.domain.dto.track.GetCustomerResponse;
import com.food.ordering.system.customer.service.domain.exception.CustomerNotFoundException;
import com.food.ordering.system.customer.service.domain.mapper.CustomerDataMapper;
import com.food.ordering.system.customer.service.domain.ports.output.repository.CustomerRepository;
import com.food.ordering.system.order.service.domain.entity.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CustomerQueryHandler {
    private final CustomerRepository customerRepository;
    private final CustomerDataMapper customerDataMapper;

    public GetCustomerResponse getCustomer(GetCustomerQuery getCustomerQuery) {
        Optional<Customer> optionalCustomer = customerRepository.findById(getCustomerQuery.customerId());
        if(optionalCustomer.isEmpty()) {
            throw new CustomerNotFoundException("Customer with id: "
                    + getCustomerQuery.customerId() + " not found");
        }
        return customerDataMapper.customerToGetCustomerResponse(optionalCustomer.get());
    }
}
