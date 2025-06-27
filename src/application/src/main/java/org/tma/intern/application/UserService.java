package org.tma.intern.application;


import io.smallrye.mutiny.Uni;
import org.tma.intern.common.RequestDto;

public interface UserService {

    Uni<Boolean> create(RequestDto.CreateUser request);

}
