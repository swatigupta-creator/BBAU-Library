package com.library.repository;
import com.library.model.Loan;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface LoanRepository extends MongoRepository<Loan,String> {}
