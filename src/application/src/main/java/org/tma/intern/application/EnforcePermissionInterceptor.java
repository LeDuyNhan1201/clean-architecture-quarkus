package org.tma.intern.application;

import io.smallrye.mutiny.Uni;
import jakarta.annotation.Priority;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import org.tma.intern.application.exception.Error;
import org.tma.intern.application.exception.HttpException;
import org.tma.intern.application.injection.AuthorizationService;
import org.tma.intern.application.injection.IdentityContext;

import java.lang.reflect.Parameter;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@EnforcePermission
@Interceptor
@Priority(Interceptor.Priority.APPLICATION)
public class EnforcePermissionInterceptor {

    AuthorizationService authorizationService;

    IdentityContext identityContext;

    @AroundInvoke
    public Object enforce(InvocationContext ctx) throws Exception {
        EnforcePermission annotation = getAnnotation(ctx);
        if (annotation == null) {
            // No annotation -> proceed as is
            return ctx.proceed();
        }

        String resourceIdParam = annotation.resourceIdParam();
        String scope = annotation.scope();

        String resourceIdValue = getParameterValue(ctx, resourceIdParam);
        if (resourceIdValue == null || resourceIdValue.isBlank()) {
            throw new BadRequestException("Missing or blank resource ID parameter: " + resourceIdParam);
        }

        if (identityContext.getAccessToken() == null) {
            throw new HttpException(Error.TOKEN_MISSING, null, Response.Status.UNAUTHORIZED);
        }

        Object result = ctx.proceed();

        if (result instanceof Uni<?> originalUni) {
            // Reactive method returning Uni -> chain authorization before emitting result
            return authorizationService.enforcePermission(
                    identityContext.getAccessToken(),
                    resourceIdValue,
                    scope)
                .chain(() -> originalUni);
        } else {
            // Not a Uni (e.g. synchronous method)
            // Enforce authorization first
            authorizationService
                .enforcePermission(identityContext.getAccessToken(), resourceIdValue, scope)
                .await().indefinitely();
            return result;
        }
    }

    // Utility to get method/class annotation
    private EnforcePermission getAnnotation(InvocationContext ctx) {
        EnforcePermission annotation = ctx.getMethod().getAnnotation(EnforcePermission.class);
        if (annotation == null) {
            annotation = ctx.getTarget().getClass().getAnnotation(EnforcePermission.class);
        }
        return annotation;
    }

    // Utility to safely call proceed() in non-blocking style
    private CompletionStage<Object> invoke(InvocationContext ctx) {
        try {
            return CompletableFuture.completedFuture(ctx.proceed());
        } catch (Exception e) {
            CompletableFuture<Object> failed = new CompletableFuture<>();
            failed.completeExceptionally(e);
            return failed;
        }
    }


    private String getParameterValue(InvocationContext ctx, String paramName) {
        Parameter[] params = ctx.getMethod().getParameters();
        Object[] values = ctx.getParameters();

        for (int i = 0; i < params.length; i++) {
            PathParam pathParam = params[i].getAnnotation(PathParam.class);
            if (pathParam != null && pathParam.value().equals(paramName)) {
                return values[i].toString();
            }
        }
        return null;
    }

}
