package com.rws.lt.lc.blueprint.web;

import com.rws.lt.lc.blueprint.security.GenericAuthorization;
import com.rws.lt.lc.blueprint.transfer.HealthStatus;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * External health check. This is a wrapper over the internal healthCheck running on the admin port.
 * It hides details that should not be exposed publicly (like the mongo connection status, or even the fact that we use mongo behind the scenes)
 */
@RestController
@RequestMapping("/v1")
public class HealthController {

    private final HealthEndpoint healthEndpoint;

    public HealthController(HealthEndpoint healthEndpoint) {
        this.healthEndpoint = healthEndpoint;
    }

    @GetMapping("/health")
    @GenericAuthorization
    public HealthStatus health() {
        Status health = healthEndpoint.health().getStatus();
        return new HealthStatus(Status.UP.equals(health));
    }

}
