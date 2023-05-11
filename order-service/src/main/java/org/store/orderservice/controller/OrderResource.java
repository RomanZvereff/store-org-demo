package org.store.orderservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.store.orderservice.model.Order;
import org.store.orderservice.repository.OrderRepository;
import org.store.orderservice.service.OrderService;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

@RestController
@RequestMapping("/api/v1")
public class OrderResource {

    private static final String ENTITY_NAME = "order";

    private final Logger log = LoggerFactory.getLogger(OrderResource.class);
    private final OrderRepository orderRepository;
    private final OrderService orderService;

    @Value("${spring.application.name}")
    private String applicationName;

    public OrderResource(OrderRepository orderRepository, OrderService orderService) {
        this.orderRepository = orderRepository;
        this.orderService = orderService;
    }

    @PostMapping("/orders")
    @Transactional
    public ResponseEntity<Order> createOrder(@Valid @RequestBody Order order) throws URISyntaxException {
        log.debug("REST request to save Order : {}", order);
        if (Objects.nonNull(order.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A new order cannot already have an ID");
        }
        final Order result = orderRepository.save(order);
        HttpHeaders httpHeaders = new HttpHeaders();
        String message = String.format("A new %s is created with identifier %s", ENTITY_NAME, result.getId());
        httpHeaders.add("X-" + applicationName + "-alert", message);
        httpHeaders.add("X-" + applicationName + "-params", result.getId());
        return ResponseEntity.created(new URI("/api/orders/" + result.getId())).headers(httpHeaders).body(result);
    }

}
