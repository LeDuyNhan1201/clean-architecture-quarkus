package org.tma.intern.contract.ResponseDto;

import lombok.*;
import lombok.experimental.FieldDefaults;

public class AuthResponse {

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Tokens {

        String accessToken;

        String refreshToken;

    }

}
