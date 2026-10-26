package com.library.repository;
import com.library.model.Member;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface MemberRepository extends MongoRepository<Member,String> {}
