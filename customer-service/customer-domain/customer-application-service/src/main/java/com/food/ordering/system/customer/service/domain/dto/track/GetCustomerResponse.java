package com.food.ordering.system.customer.service.domain.dto.track;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public record GetCustomerResponse(String id,
                                  String firstName,
                                  String lastName,
                                  String username,
                                  List<String> failureMessages) {
}
