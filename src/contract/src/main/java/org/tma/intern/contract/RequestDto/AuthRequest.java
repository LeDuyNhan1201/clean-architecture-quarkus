package org.tma.intern.contract.RequestDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AuthRequest {

    public record SignUp(
            String username,
            String email,
            String password,
            String confirmationPassword
    ){};

    public record SignIn(
            @NotNull(message = "Validation.Empty")
            @NotBlank(message = "Validation.Empty")
            @Size(message = "Validation.Size", min = 4, max = 50)
            String username,

            @NotNull(message = "Validation.Empty")
            @NotBlank(message = "Validation.Empty")
            @Size(message = "Validation.Size", min = 4, max = 20)
            String password
    ){};

}
