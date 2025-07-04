package org.tma.intern.application.service;

import io.smallrye.mutiny.Uni;
import org.tma.intern.contract.RequestDto.AuthRequest;
import org.tma.intern.contract.ResponseDto.AuthResponse;

public interface AuthService {

    Uni<AuthResponse.Tokens> getTokens(AuthRequest.SignIn request);

}
