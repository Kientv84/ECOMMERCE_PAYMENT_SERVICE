package com.payment.kientv84.services.serviceImpls;

import com.payment.kientv84.commons.PaymentMethodStatus;
import com.payment.kientv84.dtos.requests.PaymentMethodRequest;
import com.payment.kientv84.dtos.requests.PaymentMethodUpdateRequest;
import com.payment.kientv84.dtos.responses.PaymentMethodResponse;
import com.payment.kientv84.entities.PaymentMethodEntity;
import com.payment.kientv84.exceptions.EnumError;
import com.payment.kientv84.exceptions.ServiceException;
import com.payment.kientv84.mappers.PaymentMethodMapper;
import com.payment.kientv84.repositories.PaymentMethodRepository;
import com.payment.kientv84.services.PaymentMethodService;
import com.payment.kientv84.services.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentMethodServiceImpl implements PaymentMethodService {
    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentMethodMapper paymentMethodMapper;

    @Override
    public List<PaymentMethodResponse> getAllPaymentMethod() {
        try {
            List<PaymentMethodResponse> responses = paymentMethodRepository.findAll().stream().map(method -> paymentMethodMapper.mapToPaymentMethodResponse(method)).toList();

            return responses;

        } catch (Exception e) {
            throw new ServiceException(EnumError.PAYMENT_METHOD_GET_ERROR, "payment.method.get.error");
        }
    }

    @Override
    public PaymentMethodResponse createPaymentMethod(PaymentMethodRequest request) {
        try {
            PaymentMethodEntity isExist = paymentMethodRepository.findPaymentMethodByCode(request.getCode());
            if (isExist != null) {
                throw new ServiceException(EnumError.PAYMENT_METHOD_DATA_EXISTED, "payment.method.data.exit");
            }

            PaymentMethodEntity paymentMethod = PaymentMethodEntity.builder()
                    .code(request.getCode())
                    .name(request.getName())
                    .description(request.getDescription())
                    .status(PaymentMethodStatus.ACTIVE)
                    .build();

            paymentMethodRepository.save(paymentMethod);

            return paymentMethodMapper.mapToPaymentMethodResponse(paymentMethod);
        }catch (ServiceException e) {
            throw e;
        }
        catch ( Exception e) {
            log.error("Erorr", e.toString());
            throw new ServiceException(EnumError.INTERNAL_ERROR, "sys.internal.error");
        }
    }

    @Override
    public PaymentMethodResponse getPaymentMethodById(UUID id) {
        try {
            PaymentMethodEntity paymentMethodEntity = paymentMethodRepository.findById(id).orElseThrow(() -> new ServiceException(EnumError.PAYMENT_METHOD_GET_ERROR, "payment.method.get.error"));

            return  paymentMethodMapper.mapToPaymentMethodResponse(paymentMethodEntity);
        } catch (ServiceException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ServiceException(EnumError.INTERNAL_ERROR, "sys.internal.error");
        }
    }

    @Override
    public PaymentMethodResponse updatePaymentMethodById(UUID id, PaymentMethodUpdateRequest updateRequest) {
        try {
            PaymentMethodEntity paymentMethodEntity = paymentMethodRepository.findById(id).orElseThrow(() -> new ServiceException(EnumError.PAYMENT_METHOD_GET_ERROR, "payment.method.get.error"));

            if ( updateRequest.getCode() != null) {
                paymentMethodEntity.setCode(updateRequest.getCode());
            }
            if ( updateRequest.getName() != null) {
                paymentMethodEntity.setName(updateRequest.getName());
            }
            if ( updateRequest.getStatus() != null) {
                paymentMethodEntity.setStatus(updateRequest.getStatus());
            }
            if ( updateRequest.getDescription() != null) {
                paymentMethodEntity.setDescription(updateRequest.getDescription());
            }

            paymentMethodRepository.save(paymentMethodEntity);

            return paymentMethodMapper.mapToPaymentMethodResponse(paymentMethodEntity);

        } catch (ServiceException e) {
            throw e;
        } catch ( Exception e) {
            throw new ServiceException(EnumError.INTERNAL_ERROR, "sys.internal.error");
        }
    }

    @Override
    public String deletePaymentMethod(List<UUID> ids) {
        try {
            if ( ids == null || ids.isEmpty()) {
                throw new ServiceException(EnumError.PAYMENT_METHOD_ERR_DEL_EM, "payment.method.delete.empty");
            }

            List<PaymentMethodEntity> foundIds = paymentMethodRepository.findAllById(ids);

            System.out.println("Find payment method:" + foundIds.toString());

            if ( foundIds.isEmpty()) {
                throw new ServiceException(EnumError.PAYMENT_METHOD_ERR_NOT_FOUND, "payment.method.delete.notfound");
            }

            paymentMethodRepository.deleteAllById(ids);

            return "Deleted payment methods successfully: {}" + ids;

        } catch (Exception e) {
            throw new ServiceException(EnumError.INTERNAL_ERROR, "sys.internal.error");
        }
    }
}
