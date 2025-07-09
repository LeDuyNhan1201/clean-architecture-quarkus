package org.tma.intern.domain.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum SeatType {

    VIP("Vip", 100.0),
    STANDARD("Standard", 75.0),
    ;

    public final String value;

    public final double price;

}
