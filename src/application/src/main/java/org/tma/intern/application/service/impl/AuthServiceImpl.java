package org.tma.intern.application.service.impl;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.tma.intern.application.exception.HttpException;
import org.tma.intern.application.exception.Error;
import org.tma.intern.application.injection.IdentityProviderClient;
import org.tma.intern.application.service.AuthService;
import org.tma.intern.contract.RequestDto.AuthRequest;
import org.tma.intern.contract.ResponseDto.AuthResponse;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthServiceImpl implements AuthService {

    IdentityProviderClient keycloakClient;

    @Override
    public Uni<AuthResponse.Tokens> getTokens(AuthRequest.SignIn request) {
        return keycloakClient.getTokens(request.username(), request.password())
            .onItem().transform(tokens -> AuthResponse.Tokens.builder()
                .accessToken(tokens.get("accessToken"))
                .refreshToken(tokens.get("refreshToken")).build())
            .onFailure().transform(throwable ->
                new HttpException(Error.INVALID_AUTH_INFO, throwable, Response.Status.BAD_REQUEST));
    }

}
