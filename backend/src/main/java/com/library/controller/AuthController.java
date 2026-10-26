package com.library.controller;
import com.library.dto.AuthResponse;
import com.library.dto.LoginRequest;
import com.library.dto.RegisterRequest;
import com.library.exception.ApiException;
import com.library.model.User;
import com.library.service.AuthService;
import com.library.service.LibraryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.LinkedHashMap;
import java.util.Map;
@RestController
@RequestMapping("/api/auth")
public class AuthController {
 private final AuthService auth; private final LibraryService library;
 public AuthController(AuthService auth,LibraryService library){this.auth=auth;this.library=library;}
 @GetMapping("/session") public Map<String,Object> session(HttpServletRequest request){HttpSession s=request.getSession(false);Map<String,Object> response=new LinkedHashMap<>();response.put("user",s==null?null:s.getAttribute("user"));return response;}
 @PostMapping("/register") public AuthResponse register(@Valid @RequestBody RegisterRequest body,HttpServletRequest request){User user=auth.register(body);Map<String,Object> view=userView(user);HttpSession old=request.getSession(false);if(old!=null)old.invalidate();HttpSession s=request.getSession(true);s.setAttribute("user",view);return new AuthResponse(view,library.state(user.role,user.memberId));}
 @PostMapping("/login") public AuthResponse login(@Valid @RequestBody LoginRequest body,HttpServletRequest request){if(!body.role().equals("admin")&&!body.role().equals("student"))throw ApiException.badRequest("Email, password, and account type are required.");User user=auth.login(body.email(),body.password(),body.role());Map<String,Object> view=userView(user);HttpSession old=request.getSession(false);if(old!=null)old.invalidate();HttpSession s=request.getSession(true);s.setAttribute("user",view);return new AuthResponse(view,library.state(user.role,user.memberId));}
 @PostMapping("/logout") public Map<String,Boolean> logout(HttpServletRequest request){HttpSession session=request.getSession(false);if(session!=null)session.invalidate();return Map.of("ok",true);}
 public static Map<String,Object> userView(User user){Map<String,Object> map=new LinkedHashMap<>();map.put("id",user.id);map.put("email",user.email);map.put("role",user.role);map.put("memberId",user.memberId);map.put("name",user.name);map.put("department",user.department);return map;}
}
