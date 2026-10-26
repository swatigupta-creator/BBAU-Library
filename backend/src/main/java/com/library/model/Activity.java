package com.library.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Document("activity")
public class Activity {
 @Id public String id;
 public String icon;
 public String tone;
 public String title;
 public String detail;
 public String time;
 public Activity() {}
}
