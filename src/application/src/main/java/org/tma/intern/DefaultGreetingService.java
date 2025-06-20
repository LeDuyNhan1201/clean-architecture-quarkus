package org.tma.intern;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DefaultGreetingService implements GreetingService {
    @Override
    public String greet(String name) {
        return "Hello, " + name + "! Welcome to Quarkus!";
    }
}
