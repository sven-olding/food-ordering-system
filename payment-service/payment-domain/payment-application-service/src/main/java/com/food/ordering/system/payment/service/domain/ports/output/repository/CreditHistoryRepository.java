package com.food.ordering.system.payment.service.domain.ports.output.repository;

import com.food.ordering.system.payment.service.domain.entity.CreditHistory;

import java.util.Optional;
import java.util.UUID;

public interface CreditHistoryRepository {
    CreditHistory save(CreditHistory creditHistory);

    Optional<CreditHistory> findByCustomerId(UUID customerId);
}
