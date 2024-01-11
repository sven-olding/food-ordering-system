package com.food.ordering.system.customer.service.domain.dto.track;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public record GetCustomerQuery(@NotNull UUID customerId) {
}
