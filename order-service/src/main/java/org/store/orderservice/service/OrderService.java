package org.store.orderservice.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.store.orderservice.exception.CustomerOrderException;
import org.store.orderservice.model.Order;

import java.net.URI;
import java.util.Objects;

@Slf4j
@Service
public class OrderService {

    private RestTemplate restTemplate;

    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${spring.application.microservice-customer.url}")
    private String customerBaseUrl;

    private static final String CUSTOMER_ORDER_URL = "customerOrders/";

    public void createOrder(Order order) {
        URI url = URI.create(customerBaseUrl + CUSTOMER_ORDER_URL + order.getCustomerId());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        log.info("Order Request URL: {}", url);
        try {
            HttpEntity<Order> request = new HttpEntity<>(order, headers);
            ResponseEntity<Order> responseEntity = restTemplate.postForEntity(url, request, Order.class);
            if (responseEntity.getStatusCode().isError()) {
                log.error("For Order ID: {}, error response: {} is received to create Order in Customer Microservice", order.getId(), responseEntity.getStatusCode().getReasonPhrase());
                throw new CustomerOrderException(order.getId(), responseEntity.getStatusCodeValue());
            }
            if (responseEntity.hasBody()) {
                log.error("Order From Response: {}", Objects.requireNonNull(responseEntity.getBody()).getId());
            }
        } catch (Exception e) {
            log.error("For Order ID: {}, cannot create Order in Customer Microservice for reason: {}", order.getId(), ExceptionUtils.getRootCauseMessage(e));
            throw new CustomerOrderException(order.getId(), ExceptionUtils.getRootCauseMessage(e));
        }
    }

    public void updateOrder(Order order) {
        final var url = customerBaseUrl + CUSTOMER_ORDER_URL + order.getCustomerId();
        final var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        log.info("Order Request URL: {}", url);
        try {
            final var request = new HttpEntity<>(order, headers);
            restTemplate.put(url, request);
        } catch (Exception e) {
            log.error("For Order ID: {}, cannot create Order in Customer Microservice for reason: {}", order.getId(), ExceptionUtils.getRootCauseMessage(e));
            throw new CustomerOrderException(order.getId(), ExceptionUtils.getRootCauseMessage(e));
        }
    }


    public void deleteOrder(Order order) {
        URI url = URI.create(customerBaseUrl + CUSTOMER_ORDER_URL + order.getCustomerId() + "/" + order.getId());
        log.info("Order Request URL: {}", url);
        try {
            restTemplate.delete(url);
        } catch (Exception e) {
            log.error("For Order ID: {}, cannot create Order in Customer Microservice for reason: {}", order.getId(), ExceptionUtils.getRootCauseMessage(e));
            throw new CustomerOrderException(order.getId(), ExceptionUtils.getRootCauseMessage(e));
        }
    }

}
