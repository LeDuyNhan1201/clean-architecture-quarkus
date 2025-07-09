package org.tma.intern.domain.entity;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;
import org.tma.intern.domain.enums.SeatType;

@MongoEntity(collection = "seats")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Seat extends AuditCollection {

    @BsonProperty(value = "concert_id")
    ObjectId concertId;

    @BsonProperty(value = "name")
    String name;

    @BsonProperty(value = "type")
    @Builder.Default
    String type = SeatType.STANDARD.value;

    @BsonProperty(value = "price")
    @Builder.Default
    double price = SeatType.STANDARD.price;

    @BsonProperty(value = "available")
    int available;

}
