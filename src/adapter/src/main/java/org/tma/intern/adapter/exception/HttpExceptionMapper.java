package org.tma.intern.adapter.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.tma.intern.application.injection.MessagesProvider;
import org.tma.intern.application.exception.HttpException;
import org.tma.intern.application.exception.Error;
import org.tma.intern.contract.ResponseDto.ErrorResponse;

@Provider
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class HttpExceptionMapper implements ExceptionMapper<HttpException> {

    MessagesProvider messages;

    @Override
    public Response toResponse(HttpException exception) {
        Error error = exception.getError();
        ErrorResponse response = ErrorResponse.builder()
            .code(error.getCode())
            .message((exception.getMoreInfo() != null)
                ? messages.get(error.getMessage(), exception.getMoreInfo())
                : messages.get(error.getMessage()))
            .details(exception.getCause().getMessage())
            .build();

        return Response.status(exception.getHttpStatus()).entity(response).build();
    }

}
