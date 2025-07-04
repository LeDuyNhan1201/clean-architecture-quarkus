package org.tma.intern.application.service;


import org.tma.intern.contract.RequestDto.UserRequest;

public interface UserService {

    String create(UserRequest.Creation request);

    String delete(String id);

    long seedUsers(int count, String... roles);

}
