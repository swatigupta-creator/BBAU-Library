package com.library.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
@Document("support_requests")
public class SupportRequest {
 @Id public String id;
 public String userId;
 public String message;
 public Instant createdAt = Instant.now();
 public SupportRequest() {}
}
