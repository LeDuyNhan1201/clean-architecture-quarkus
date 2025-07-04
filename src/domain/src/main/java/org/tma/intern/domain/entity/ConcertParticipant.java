package org.tma.intern.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "concert_participant")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConcertParticipant {

    @EmbeddedId
    EventParticipantId id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "profile_id", nullable = false, updatable = false)
    @MapsId("profileId")
    Profile profile;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "concert_id", nullable = false, updatable = false)
    @MapsId("concertId")
    Concert concert;

    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    Instant createdAt;

    @Embeddable
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class EventParticipantId {

        long profileId;

        long concertId;
    }

}
