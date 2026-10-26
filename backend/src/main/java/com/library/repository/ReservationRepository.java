package com.library.repository;
import com.library.model.Reservation;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface ReservationRepository extends MongoRepository<Reservation,String> {}
