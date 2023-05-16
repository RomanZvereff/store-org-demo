package org.store.orderservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.store.orderservice.exception.BadRequestAlertException;
import org.store.orderservice.model.Order;
import org.store.orderservice.repository.OrderRepository;
import org.store.orderservice.service.OrderService;
import org.store.orderservice.util.HeaderUtil;
import org.store.orderservice.util.ResponseUtil;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

    @GetMapping("/orders")
    public List<Order> getAllOrders() {
        log.debug("REST request to get all Orders");
        return orderRepository.findAll();
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable String id) {
        log.debug("REST request to get Order : {}", id);
        Optional<Order> result = orderRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(result);
    }

    @PostMapping("/orders")
    @Transactional
    public ResponseEntity<Order> createOrder(@Valid @RequestBody Order order) throws URISyntaxException {
        log.debug("REST request to save Order : {}", order);
        if (Objects.nonNull(order.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "A new order cannot already have an ID");
        }
        final Order result = orderRepository.save(order);
        orderService.createOrder(result);
        return ResponseEntity.created(new URI("/api/orders/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, order.getId()))
                .body(result);
    }

    @PutMapping("/orders")
    @Transactional
    public ResponseEntity<Order> updateOrder(@Valid @RequestBody Order order) throws URISyntaxException {
        log.debug("REST request to update Order : {}", order);
        if (Objects.isNull(order.getId())) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        final Order result = orderRepository.save(order);
        orderService.updateOrder(result);
        return ResponseEntity.ok()
                .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, order.getId()))
                .body(result);
    }

    @DeleteMapping("/orders/{id}")
    @Transactional
    public ResponseEntity<Void> deleteOrder(@PathVariable String id) {
        log.debug("REST request to delete Order : {}", id);
        final Optional<Order> orderOptional = orderRepository.findById(id);
        orderRepository.deleteById(id);
        orderOptional.ifPresent(order -> orderService.deleteOrder(order));
        return ResponseEntity.noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id))
                .build();
    }

}
