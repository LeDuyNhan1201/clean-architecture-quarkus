package org.tma.intern.domain.repository;

import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.tma.intern.domain.entity.Seat;

@ApplicationScoped
public class SeatRepository implements ReactivePanacheMongoRepository<Seat> {
}
