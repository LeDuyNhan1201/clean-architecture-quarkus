package org.tma.intern.application;

import jakarta.ws.rs.core.Response;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HttpException extends RuntimeException {

    public HttpException(Error error, Throwable throwable, Response.Status httpStatus, String... moreInfo) {
        super(error.getMessage(), throwable);
        this.httpStatus = httpStatus;
        this.moreInfo = moreInfo;
        this.error = error;
    }

    Response.Status httpStatus;
    Error error;
    Object[] moreInfo;

}