package org.tma.intern.adapter.exception;

import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.core.Response;
import io.quarkus.hibernate.validator.runtime.jaxrs.ResteasyReactiveViolationException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.tma.intern.adapter.component.MessagesProvider;
import org.tma.intern.contract.helper.StringHelper;

import java.util.List;

import java.util.stream.Collectors;

@Provider
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ValidationExceptionMapper implements ExceptionMapper<ResteasyReactiveViolationException> {

    MessagesProvider messages;

    @Override
    public Response toResponse(ResteasyReactiveViolationException exception) {
        List<Violation> violations = exception.getConstraintViolations()
            .stream()
            .map(this::toViolation)
            .collect(Collectors.toList());

        ValidationResponse error = new ValidationResponse(
            messages.get("Action.Fail", "Validate", "inputs"),
            violations
        );

        return Response.status(Response.Status.BAD_REQUEST)
            .entity(error)
            .build();
    }

    private Violation toViolation(jakarta.validation.ConstraintViolation<?> cv) {
        String annotationType = cv.getConstraintDescriptor()
            .getAnnotation()
            .annotationType()
            .getSimpleName();

        String field = StringHelper.uppercaseFirstChar(
            StringHelper.getLastSegment(cv.getPropertyPath().toString(), '.')
        );

        String message;

        switch (annotationType) {
            case "Size":
                String min = cv.getConstraintDescriptor().getAttributes().get("min").toString();
                String max = cv.getConstraintDescriptor().getAttributes().get("max").toString();
                message = messages.get(cv.getMessage(), field, min, max);
                break;

            default:
                message = messages.get(cv.getMessage(), field);
        }

        return new Violation(field, message);
    }

    public static class Violation {
        public String field;
        public String message;

        public Violation(String field, String message) {
            this.field = field;
            this.message = message;
        }
    }

    public static class ValidationResponse {
        public String message;
        public List<Violation> violations;

        public ValidationResponse(String message, List<Violation> violations) {
            this.message = message;
            this.violations = violations;
        }
    }
}
