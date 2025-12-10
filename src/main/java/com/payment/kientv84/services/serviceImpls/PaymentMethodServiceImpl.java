package com.payment.kientv84.services.serviceImpls;

import com.fasterxml.jackson.core.type.TypeReference;
import com.payment.kientv84.commons.Constant;
import com.payment.kientv84.commons.PaymentMethodStatus;
import com.payment.kientv84.dtos.requests.PaymentMethodRequest;
import com.payment.kientv84.dtos.requests.PaymentMethodUpdateRequest;
import com.payment.kientv84.dtos.requests.search.payment.PaymentSearchModel;
import com.payment.kientv84.dtos.requests.search.payment.PaymentSearchOption;
import com.payment.kientv84.dtos.requests.search.paymentMethod.PaymentMethodSearchModel;
import com.payment.kientv84.dtos.requests.search.paymentMethod.PaymentMethodSearchOption;
import com.payment.kientv84.dtos.requests.search.paymentMethod.PaymentMethodSearchRequest;
import com.payment.kientv84.dtos.responses.PagedResponse;
import com.payment.kientv84.dtos.responses.PaymentMethodResponse;
import com.payment.kientv84.dtos.responses.PaymentResponse;
import com.payment.kientv84.entities.PaymentEntity;
import com.payment.kientv84.entities.PaymentMethodEntity;
import com.payment.kientv84.exceptions.EnumError;
import com.payment.kientv84.exceptions.ServiceException;
import com.payment.kientv84.mappers.PaymentMethodMapper;
import com.payment.kientv84.repositories.PaymentMethodRepository;
import com.payment.kientv84.services.PaymentMethodService;
import com.payment.kientv84.services.PaymentService;
import com.payment.kientv84.services.RedisService;
import com.payment.kientv84.ultis.PageableUtils;
import com.payment.kientv84.ultis.SpecificationBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentMethodServiceImpl implements PaymentMethodService {
    private final PaymentMethodRepository paymentMethodRepository;
    private final PaymentMethodMapper paymentMethodMapper;
    private final RedisService redisService;

    @Override
    public PagedResponse<PaymentMethodResponse> getAllPaymentMethod(PaymentMethodSearchRequest request) {
        log.info("Get all payment method api calling...");
        String key = "payment-methods:list:" + request.hashKey();

        try {
            PagedResponse<PaymentMethodResponse> cached = redisService.getValue(key, new TypeReference<PagedResponse<PaymentMethodResponse>>() {
            });

            if (cached != null) {
                log.info("Redis read for key {}", key);
                return cached;
            }

            PaymentMethodSearchOption option = request.getPaymentMethodSearchOption();
            PaymentMethodSearchModel model = request.getPaymentMethodSearchModel();

            List<String> allowedFields = List.of("code", "createdDate");

            PageRequest pageRequest = PageableUtils.buildPageRequest(
                    option.getPage(),
                    option.getSize(),
                    option.getSort(),
                    allowedFields,
                    "createdDate",
                    Sort.Direction.DESC
            );

            Specification<PaymentMethodEntity> spec = new SpecificationBuilder<PaymentMethodEntity>()
                    .equal("status", model.getStatus())
                    .likeAnyFieldIgnoreCase(model.getQ(), "code")
                    .build();

            Page<PaymentMethodResponse> result = paymentMethodRepository.findAll(spec, pageRequest)
                    .map(paymentMethodMapper::mapToPaymentMethodResponse);

            PagedResponse<PaymentMethodResponse> response = new PagedResponse<>(
                    result.getNumber(),
                    result.getSize(),
                    result.getTotalElements(),
                    result.getTotalPages(),
                    result.getContent()
            );

            redisService.setValue(key, response, Constant.SEARCH_CACHE_TTL);

            log.info("Redis MISS, caching search result for key {}", key);

            return response;

        } catch (Exception e) {
            log.error("Error get all payment-methods", e);
            throw new ServiceException(EnumError.PAYMENT_METHOD_GET_ERROR, "payment.method.get.error");
        }
    }

    @Override
    public List<PaymentMethodResponse> searchPaymentMethodSuggestion(String q, int limit) {
        List<PaymentMethodEntity> paymentMethods = paymentMethodRepository.searchPaymentMethodSuggestion(q, limit);
        return paymentMethods.stream().map(pay -> paymentMethodMapper.mapToPaymentMethodResponse(pay)).toList();
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

            // redis handle
            redisService.deleteByKey("payment-methods:list:*");

            return paymentMethodMapper.mapToPaymentMethodResponse(paymentMethod);
        }catch (ServiceException e) {
            throw e;
        }
        catch ( Exception e) {
            log.error("Error: ", e.toString());
            throw new ServiceException(EnumError.INTERNAL_ERROR, "sys.internal.error");
        }
    }

    @Override
    public PaymentMethodResponse getPaymentMethodById(UUID id) {
        log.info("Calling get by id api with payment method {}", id);

        String key = "payment-methods:"+id;

        try {
            PaymentMethodEntity paymentMethodEntity = paymentMethodRepository.findById(id).orElseThrow(() -> new ServiceException(EnumError.PAYMENT_METHOD_GET_ERROR, "payment.method.get.error"));

            PaymentMethodResponse response =  paymentMethodMapper.mapToPaymentMethodResponse(paymentMethodEntity);

            redisService.setValue(key, response, Constant.CACHE_TTL);

            return  response;
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

            // Invalidate cache
            String key = "payment-method:" + id;
            redisService.deleteByKey(key);

            redisService.deleteByKeys("payment-method:" + id, "payment-methods:list:*");

            log.info("Cache invalidated for key {}", key);

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

            // Soft delete:  update status
            foundIds.forEach(pay -> pay.setStatus(PaymentMethodStatus.INACTIVE));
            paymentMethodRepository.saveAll(foundIds);

            //dete cache
            ids.forEach(uuid -> redisService.deleteByKey("payment-method:"+uuid));
            redisService.deleteByKeys("payment-methods:list:*");

            log.info("Deleted payment-methods successfully and cache invalidated: {}", ids);
            return "Deleted payment-methods successfully: " + ids;

        } catch (Exception e) {
            throw new ServiceException(EnumError.INTERNAL_ERROR, "sys.internal.error");
        }
    }
}
