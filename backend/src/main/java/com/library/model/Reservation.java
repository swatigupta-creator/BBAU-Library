package com.library.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Document("reservations")
public class Reservation {
 @Id public String id;
 public String bookId;
 public String memberId;
 public String date;
 public String status;
 public Reservation() {}
}
