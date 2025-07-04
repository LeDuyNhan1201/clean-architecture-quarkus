package org.tma.intern.adapter.component;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class RequestContext {
    private String correlationId;

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }
}
