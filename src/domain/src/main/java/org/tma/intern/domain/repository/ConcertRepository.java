package org.tma.intern.domain.repository;

import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.tma.intern.domain.entity.Concert;

@ApplicationScoped
public class ConcertRepository implements ReactivePanacheMongoRepository<Concert> {

}
