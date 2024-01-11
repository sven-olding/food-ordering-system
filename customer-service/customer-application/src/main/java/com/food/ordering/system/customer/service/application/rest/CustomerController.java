package com.food.ordering.system.customer.service.application.rest;

import com.food.ordering.system.customer.service.domain.dto.create.CreateCustomerCommand;
import com.food.ordering.system.customer.service.domain.dto.create.CreateCustomerResponse;
import com.food.ordering.system.customer.service.domain.dto.track.GetCustomerQuery;
import com.food.ordering.system.customer.service.domain.dto.track.GetCustomerResponse;
import com.food.ordering.system.customer.service.domain.ports.input.CustomerApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/customers", produces = "application/vnd.api.v1+json")
public class CustomerController {
    private final CustomerApplicationService customerApplicationService;

    @PostMapping
    ResponseEntity<CreateCustomerResponse> createCustomer(@RequestBody CreateCustomerCommand createCustomerCommand) {
        CreateCustomerResponse createCustomerResponse = customerApplicationService
                .createCustomer(createCustomerCommand);
        log.info("Customer created with id: {}, message: {}",
                createCustomerResponse.getCustomerId(),
                createCustomerResponse.getMessage());
        return ResponseEntity.ok(createCustomerResponse);
    }

    @GetMapping("/{customerId}")
    ResponseEntity<GetCustomerResponse> getCustomer(@PathVariable UUID customerId) {
        GetCustomerResponse getCustomerResponse = customerApplicationService
                .getCustomer(new GetCustomerQuery(customerId));
        log.info("GetCustomerResponse id: {}", getCustomerResponse.id());
        if(!getCustomerResponse.failureMessages().isEmpty()) {
            log.error("Failure messages: {}", getCustomerResponse.failureMessages());
        }
        return ResponseEntity.ok(getCustomerResponse);
    }
}
