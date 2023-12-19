package com.food.ordering.system.payment.service.domain;

import com.food.ordering.system.domain.event.publisher.DomainEventPublisher;
import com.food.ordering.system.domain.valueobject.CustomerId;
import com.food.ordering.system.payment.service.domain.dto.PaymentRequest;
import com.food.ordering.system.payment.service.domain.entity.CreditEntry;
import com.food.ordering.system.payment.service.domain.entity.CreditHistory;
import com.food.ordering.system.payment.service.domain.entity.Payment;
import com.food.ordering.system.payment.service.domain.event.PaymentCancelledEvent;
import com.food.ordering.system.payment.service.domain.event.PaymentCompletedEvent;
import com.food.ordering.system.payment.service.domain.event.PaymentEvent;
import com.food.ordering.system.payment.service.domain.event.PaymentFailedEvent;
import com.food.ordering.system.payment.service.domain.exception.PaymentApplicationServiceException;
import com.food.ordering.system.payment.service.domain.mapper.PaymentDataMapper;
import com.food.ordering.system.payment.service.domain.ports.output.repository.CreditEntryRepository;
import com.food.ordering.system.payment.service.domain.ports.output.repository.CreditHistoryRepository;
import com.food.ordering.system.payment.service.domain.ports.output.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Component
public class PaymentRequestHelper {
    private final PaymentDomainService paymentDomainService;
    private final PaymentDataMapper paymentDataMapper;
    private final PaymentRepository paymentRepository;
    private final CreditEntryRepository creditEntryRepository;
    private final CreditHistoryRepository creditHistoryRepository;
    private final DomainEventPublisher<PaymentCompletedEvent> paymentCompletedEventDomainEventPublisher;
    private final DomainEventPublisher<PaymentCancelledEvent> paymentCancelledEventDomainEventPublisher;
    private final DomainEventPublisher<PaymentFailedEvent> paymentFailedEventDomainEventPublisher;

    @Transactional
    public PaymentEvent persistPayment(PaymentRequest paymentRequest) {
        log.info("Received payment complete event for order id: {}", paymentRequest.getOrderId());
        Payment payment = paymentDataMapper.paymentRequestModelToPayment(paymentRequest);
        CreditEntry creditEntry = getCreditEntry(payment.getCustomerId());
        List<CreditHistory> creditHistoryList = getCreditHistory(payment.getCustomerId());
        List<String> failureMessages = new ArrayList<>();
        PaymentEvent paymentEvent = paymentDomainService
                .validateAndInitiatePayment(
                        payment,
                        creditEntry,
                        creditHistoryList,
                        failureMessages,
                        paymentCompletedEventDomainEventPublisher,
                        paymentFailedEventDomainEventPublisher);
        persistDbObjects(payment, failureMessages, creditEntry, creditHistoryList);
        return paymentEvent;
    }

    @Transactional
    public PaymentEvent persistCancelPayment(PaymentRequest paymentRequest) {
        log.info("Received payment rollback event for order id: {}", paymentRequest.getOrderId());
        Optional<Payment> optionalPayment = paymentRepository
                .findByOrderId(UUID.fromString(paymentRequest.getOrderId()));
        if (optionalPayment.isEmpty()) {
            log.error("Payment not found for order id: {}", paymentRequest.getOrderId());
            throw new PaymentApplicationServiceException("Payment not found for order id: "
                    + paymentRequest.getOrderId());
        }
        Payment payment = optionalPayment.get();
        CreditEntry creditEntry = getCreditEntry(payment.getCustomerId());
        List<CreditHistory> creditHistory = getCreditHistory(payment.getCustomerId());
        List<String> failureMessages = new ArrayList<>();
        PaymentEvent paymentEvent = paymentDomainService
                .validateAndCancelPayment(
                        payment,
                        creditEntry,
                        creditHistory,
                        failureMessages,
                        paymentCancelledEventDomainEventPublisher,
                        paymentFailedEventDomainEventPublisher);
        persistDbObjects(payment, failureMessages, creditEntry, creditHistory);
        return paymentEvent;
    }

    private void persistDbObjects(Payment payment,
                                  List<String> failureMessages,
                                  CreditEntry creditEntry,
                                  List<CreditHistory> creditHistory) {
        paymentRepository.save(payment);
        if (failureMessages.isEmpty()) {
            creditEntryRepository.save(creditEntry);
            creditHistoryRepository.save(creditHistory.get(creditHistory.size() - 1));
        }
    }

    private List<CreditHistory> getCreditHistory(CustomerId customerId) {
        Optional<List<CreditHistory>> creditHistory = creditHistoryRepository.findByCustomerId(customerId);
        if (creditHistory.isEmpty()) {
            log.error("Could not find credit history for customer: {}", customerId.getValue());
            throw new PaymentApplicationServiceException("Could not find credit history for customer: "
                    + customerId.getValue());
        }
        return creditHistory.get();
    }

    private CreditEntry getCreditEntry(CustomerId customerId) {
        Optional<CreditEntry> creditEntryOptional = creditEntryRepository.findByCustomerId(customerId);
        if (creditEntryOptional.isEmpty()) {
            log.error("Could not find credit entry for customer: {}", customerId.getValue());
            throw new PaymentApplicationServiceException("Could not find credit entry for customer: "
                    + customerId.getValue());
        }
        return creditEntryOptional.get();
    }
}
