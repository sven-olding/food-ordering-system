package com.food.ordering.system.customer.service.domain.dto.create;

import lombok.Builder;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Builder
public record CreateCustomerCommand(@NotNull String username,
                                    @NotNull String firstName,
                                    @NotNull String lastName) {
}