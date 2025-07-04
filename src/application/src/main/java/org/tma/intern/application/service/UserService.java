package org.tma.intern.application.service;


import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.tma.intern.contract.RequestDto.UserRequest;

public interface UserService {

    Uni<String> create(UserRequest.Creation request);

    Uni<String> delete(String id);

    Multi<String> seedUsers(int count, String... roles);

}
