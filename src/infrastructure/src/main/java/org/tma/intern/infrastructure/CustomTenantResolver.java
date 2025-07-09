package org.tma.intern.infrastructure;

import io.quarkus.oidc.TenantResolver;
import io.vertx.ext.web.RoutingContext;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class CustomTenantResolver implements TenantResolver {

    @Override
    public String resolve(RoutingContext context) {
        String path = context.request().path();
        String[] parts = path.split("/");

        log.warn("[{}] Path: {}", CustomTenantResolver.class.getName(), path);

        if (parts.length == 0) {
            return null; // mặc định
        }

        return parts[1]; // phần đầu của path làm tenant-id
    }
}
