package com.library.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Document("loans")
public class Loan {
 @Id public String id;
 public String bookId;
 public String memberId;
 public String issued;
 public String due;
 public String status;
 public Loan() {}
}
