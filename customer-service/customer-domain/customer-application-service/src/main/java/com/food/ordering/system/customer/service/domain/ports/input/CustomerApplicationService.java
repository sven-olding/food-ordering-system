package com.food.ordering.system.customer.service.domain.ports.input;

import com.food.ordering.system.customer.service.domain.dto.create.CreateCustomerCommand;
import com.food.ordering.system.customer.service.domain.dto.create.CreateCustomerResponse;
import com.food.ordering.system.customer.service.domain.dto.track.GetCustomerQuery;
import com.food.ordering.system.customer.service.domain.dto.track.GetCustomerResponse;

import javax.validation.Valid;

public interface CustomerApplicationService {
    CreateCustomerResponse createCustomer(@Valid CreateCustomerCommand createCustomerCommand);

    GetCustomerResponse getCustomer(@Valid GetCustomerQuery getCustomerQuery);
}
