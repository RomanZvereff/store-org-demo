package org.store.orderservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.store.orderservice.model.Order;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
}
