package com.food.ordering.system.customer.service.domain;

import com.food.ordering.system.customer.service.domain.dto.create.CreateCustomerCommand;
import com.food.ordering.system.customer.service.domain.dto.create.CreateCustomerResponse;
import com.food.ordering.system.customer.service.domain.dto.track.GetCustomerQuery;
import com.food.ordering.system.customer.service.domain.dto.track.GetCustomerResponse;
import com.food.ordering.system.customer.service.domain.ports.input.CustomerApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@RequiredArgsConstructor
@Validated
@Service
public class CustomerApplicationServiceImpl implements CustomerApplicationService {
    private final CustomerCreateCommandHandler customerCreateCommandHandler;
    private final CustomerQueryHandler customerQueryHandler;

    @Override
    public CreateCustomerResponse createCustomer(CreateCustomerCommand createCustomerCommand) {
        return customerCreateCommandHandler.createCustomer(createCustomerCommand);
    }

    @Override
    public GetCustomerResponse getCustomer(GetCustomerQuery getCustomerQuery) {
        return customerQueryHandler.getCustomer(getCustomerQuery);
    }
}
