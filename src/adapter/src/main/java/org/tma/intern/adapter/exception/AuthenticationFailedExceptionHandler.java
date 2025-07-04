package org.tma.intern.adapter.exception;

import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

import io.quarkus.security.AuthenticationFailedException;
import io.vertx.ext.web.Router;
import org.tma.intern.application.Error;

@ApplicationScoped
public class AuthenticationFailedExceptionHandler {

    public void init(@Observes Router router) {
        router.route().failureHandler(event -> {
            if (event.failure() instanceof AuthenticationFailedException) {
                JsonObject responseBody = new JsonObject()
                    .put("code", Error.TOKEN_INVALID.getCode())
                    .put("message", Error.TOKEN_INVALID.getMessage());

                event.response()
                    .setStatusCode(401)
                    .putHeader("Content-Type", "application/json")
                    .end(responseBody.encode());
            } else {
                event.next();
            }
        });
    }
}