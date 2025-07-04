package org.tma.intern.adapter.component;

import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CorrelationIdFilter implements ContainerRequestFilter {

    @Inject
    RequestContext requestContext;

    @Override
    public void filter(ContainerRequestContext requestContextContainer) {
        String headerValue = requestContextContainer.getHeaderString("X-Correlation-Id");
        requestContext.setCorrelationId(headerValue);
    }
}
