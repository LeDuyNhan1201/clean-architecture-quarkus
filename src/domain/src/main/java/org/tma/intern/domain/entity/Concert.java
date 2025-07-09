package org.tma.intern.domain.entity;

import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.time.Instant;

@MongoEntity(collection = "concerts")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Concert extends AuditCollection {

    @BsonProperty(value = "title")
    String title;

    @BsonProperty(value = "region")
    String region;

    @BsonProperty(value = "location")
    String location;

    @BsonProperty(value = "start_time")
    Instant startTime;

    @BsonProperty(value = "end_time")
    Instant endTime;

    @BsonProperty(value = "is_approved")
    @Builder.Default
    boolean isApproved = false;

}
