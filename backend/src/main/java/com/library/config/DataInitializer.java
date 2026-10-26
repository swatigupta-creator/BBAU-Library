package com.library.config;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.model.*;
import com.library.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.io.InputStream;
import java.util.List;
@Component
public class DataInitializer implements CommandLineRunner {
 private final ObjectMapper mapper; private final BookRepository books; private final MemberRepository members; private final LoanRepository loans; private final ReservationRepository reservations; private final ActivityRepository activities; private final UserRepository users; private final PasswordEncoder passwords;
 @Value("${app.admin.email:admin@bbau.ac.in}") private String adminEmail;
 @Value("${app.admin.password:}") private String adminPassword;
 public DataInitializer(ObjectMapper mapper,BookRepository books,MemberRepository members,LoanRepository loans,ReservationRepository reservations,ActivityRepository activities,UserRepository users,PasswordEncoder passwords){this.mapper=mapper;this.books=books;this.members=members;this.loans=loans;this.reservations=reservations;this.activities=activities;this.users=users;this.passwords=passwords;}
 @Override public void run(String... args) throws Exception {
  if(books.count()==0){
   try(InputStream input=new ClassPathResource("seed-data.json").getInputStream()){
    JsonNode data=mapper.readTree(input);
    books.saveAll(mapper.convertValue(data.get("books"),new TypeReference<List<Book>>(){}));
    members.saveAll(mapper.convertValue(data.get("members"),new TypeReference<List<Member>>(){}));
    loans.saveAll(mapper.convertValue(data.get("loans"),new TypeReference<List<Loan>>(){}));
    reservations.saveAll(mapper.convertValue(data.get("reservations"),new TypeReference<List<Reservation>>(){}));
    activities.saveAll(mapper.convertValue(data.get("activity"),new TypeReference<List<Activity>>(){}));
   }
  }
  String email=adminEmail.trim().toLowerCase();
  if(!adminPassword.isBlank()){
   User admin=users.findByEmailIgnoreCase(email).orElseGet(User::new);
   if(admin.role!=null&&!admin.role.equals("admin")) throw new IllegalStateException("ADMIN_EMAIL is already used by a student account.");
   admin.email=email;admin.name=admin.name==null?"Library Admin":admin.name;admin.role="admin";admin.passwordHash=passwords.encode(adminPassword);users.save(admin);
  }
 }
}
