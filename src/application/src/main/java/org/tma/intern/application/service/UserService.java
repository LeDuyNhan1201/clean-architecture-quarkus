package org.tma.intern.application.service;

import io.smallrye.mutiny.Uni;
import org.tma.intern.contract.RequestDto.UserRequest;

import java.util.List;

public interface UserService {

    Uni<String> create(UserRequest.Creation request);

    Uni<String> delete(String id);

    Uni<List<String>> seedUsers(int count, String... roles);

}
