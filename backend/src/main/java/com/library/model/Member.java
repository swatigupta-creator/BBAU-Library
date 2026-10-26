package com.library.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Document("members")
public class Member {
 @Id public String id;
 public String name;
 public String email;
 public String department;
 public String role;
 public String status;
 public Member() {}
}
