package com.library.repository;
import com.library.model.Activity;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface ActivityRepository extends MongoRepository<Activity,String> {}
