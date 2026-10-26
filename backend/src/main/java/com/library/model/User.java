package com.library.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
@Document("users")
public class User {
 @Id public String id;
 @Indexed(unique = true) public String email;
 public String passwordHash;
 public String role;
 public String memberId;
 public String name;
 public String department;
 public User() {}
}
