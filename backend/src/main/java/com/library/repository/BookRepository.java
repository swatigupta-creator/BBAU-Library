package com.library.repository;
import com.library.model.Book;
import org.springframework.data.mongodb.repository.MongoRepository;
public interface BookRepository extends MongoRepository<Book,String> {}
