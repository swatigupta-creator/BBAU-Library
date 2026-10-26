package com.library.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Document("books")
public class Book {
 @Id public String id;
 public String title;
 public String author;
 public String category;
 public int year;
 public int copies;
 public int available;
 public int color;
 public Book() {}
}
