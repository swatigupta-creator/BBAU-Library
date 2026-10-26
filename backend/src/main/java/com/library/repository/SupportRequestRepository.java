package com.library.repository;
import com.library.model.SupportRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface SupportRequestRepository extends MongoRepository<SupportRequest,String> {}
