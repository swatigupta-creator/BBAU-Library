package com.library.service;
import com.library.dto.RegisterRequest;
import com.library.exception.ApiException;
import com.library.model.Member;
import com.library.model.User;
import com.library.repository.MemberRepository;
import com.library.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Locale;
@Service
public class AuthService {
 private final UserRepository users;
 private final MemberRepository members;
 private final PasswordEncoder passwords;
 public AuthService(UserRepository users, MemberRepository members, PasswordEncoder passwords) { this.users=users; this.members=members; this.passwords=passwords; }
 public User register(RegisterRequest request) {
  String email=request.email().trim().toLowerCase(Locale.ROOT);
  if (users.findByEmailIgnoreCase(email).isPresent()) throw ApiException.conflict("An account with this email already exists.");
  User user=new User(); user.email=email; user.name=request.name().trim(); user.department=request.department().trim(); user.passwordHash=passwords.encode(request.password()); user.role="student"; user.memberId="ST-"+java.util.UUID.randomUUID().toString().substring(0,8).toUpperCase(Locale.ROOT);
  users.save(user);
  Member member=new Member(); member.id=user.memberId; member.name=user.name; member.email=user.email; member.department=user.department; member.role="Student"; member.status="Active"; members.save(member);
  return user;
 }
 public User login(String email, String password, String role) {
  User user=users.findByEmailIgnoreCase(email.trim()).orElseThrow(() -> ApiException.unauthorized("Email or password is incorrect."));
  if (!user.role.equals(role) || !passwords.matches(password,user.passwordHash)) throw ApiException.unauthorized("Email or password is incorrect.");
  return user;
 }
}
