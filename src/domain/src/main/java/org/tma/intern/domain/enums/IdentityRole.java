package org.tma.intern.domain.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum IdentityRole {

    ADMIN("admin"),
    USER("user")
    ;

    public final String value;

}
