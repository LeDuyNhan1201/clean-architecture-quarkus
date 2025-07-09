package org.tma.intern.domain.entity;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.tma.intern.domain.enums.BookingStatus;

@MongoEntity(collection = "bookings")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Booking extends AuditCollection  {

    @BsonProperty(value = "user_id")
    String userId;

    @BsonProperty(value = "seat_id")
    String seatId;

    @BsonProperty(value = "quantity")
    @Builder.Default
    int quantity = 1;

    @BsonProperty(value = "total_price")
    long totalPrice;

    @BsonProperty(value = "status")
    @Builder.Default
    String status = BookingStatus.PENDING.value;

}
