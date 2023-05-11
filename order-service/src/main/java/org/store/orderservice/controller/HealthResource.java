package org.store.orderservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.store.orderservice.model.Health;
import org.store.orderservice.model.HealthStatus;

@RestController
@RequestMapping("/api/v1/")
public class HealthResource {

    private final Logger log = LoggerFactory.getLogger(HealthResource.class);

    @GetMapping(
            value = "/health",
            produces = "application/json")
    public ResponseEntity<Health> getHealth() {
        log.debug("REST request to get the Health Status");
        final Health health = new Health();
        health.setStatus(HealthStatus.UP);
        return ResponseEntity.ok().body(health);
    }

}
