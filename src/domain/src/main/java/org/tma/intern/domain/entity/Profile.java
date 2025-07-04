package org.tma.intern.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.tma.intern.domain.enums.Gender;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "profiles")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Profile extends AuditEntity<Long> {

    @Column(name = "user_id", nullable = false, updatable = false, unique = true)
    String userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, length = 100)
    Gender gender;

    @Temporal(TemporalType.DATE)
    @Column(name = "day_of_birth")
    Date dayOfBirth;

}
