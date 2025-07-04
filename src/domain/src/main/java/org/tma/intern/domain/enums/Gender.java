package org.tma.intern.domain.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Gender {

    MALE("male"),
    FEMALE("female"),
    OTHER("other")
    ;

    public final String value;

}
