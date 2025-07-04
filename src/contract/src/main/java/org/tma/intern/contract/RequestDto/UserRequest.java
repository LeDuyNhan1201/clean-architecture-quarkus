package org.tma.intern.contract.RequestDto;

public class UserRequest {

    public record Creation(
            String username,
            String email,
            String password
    ){};

    public record UpdateBody(
            String email,
            String password
    ){};

}
