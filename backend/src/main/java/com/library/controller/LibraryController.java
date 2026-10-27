package com.library.controller;
import com.library.dto.LibraryStateRequest;
import com.library.dto.LibraryStateResponse;
import com.library.exception.ApiException;
import com.library.service.LibraryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController
@RequestMapping("/api")
public class LibraryController {
 private final LibraryService library;
 public LibraryController(LibraryService library){this.library=library;}
 @GetMapping("/health") public Map<String,String> health(){return Map.of("status","ok","database","mongodb");}
 @GetMapping("/state") public LibraryStateResponse state(HttpServletRequest request){Map<?,?> user=requireUser(request);return library.state((String)user.get("role"),(String)user.get("memberId"));}
 @PutMapping("/admin/state") public Map<String,Boolean> adminState(@RequestBody LibraryStateRequest body,HttpServletRequest request){Map<?,?> user=requireUser(request);if(!"admin".equals(user.get("role")))throw ApiException.forbidden("Administrator access is required.");library.replaceState(body);return Map.of("ok",true);}
 @PostMapping("/admin/issue") public Map<String,LibraryStateResponse> issue(@RequestBody Map<String,String> body,HttpServletRequest request){requireRole(request,"admin");return Map.of("state",library.issue(body.get("memberId"),body.get("bookId"),body.get("dueDate")));}
 @PostMapping("/admin/loans/{id}/return") public Map<String,LibraryStateResponse> returnLoan(@PathVariable String id,HttpServletRequest request){requireRole(request,"admin");return Map.of("state",library.returnLoan(id));}
 @PostMapping("/admin/reservations/{id}/fulfill") public Map<String,LibraryStateResponse> fulfillReservation(@PathVariable String id,HttpServletRequest request){requireRole(request,"admin");return Map.of("state",library.fulfillReservation(id));}
 @DeleteMapping("/admin/reservations/{id}") public Map<String,Boolean> deleteReservation(@PathVariable String id,HttpServletRequest request){requireRole(request,"admin");library.deleteReservation(id);return Map.of("ok",true);}
 @PostMapping("/student/borrow") public Map<String,LibraryStateResponse> borrow(@RequestBody Map<String,String> body,HttpServletRequest request){Map<?,?> user=requireRole(request,"student");return Map.of("state",library.borrow((String)user.get("memberId"),(String)user.get("name"),body.get("bookId")));}
 @PostMapping("/student/reservations") public Map<String,LibraryStateResponse> reserve(@RequestBody Map<String,String> body,HttpServletRequest request){Map<?,?> user=requireRole(request,"student");return Map.of("state",library.reserve((String)user.get("memberId"),(String)user.get("name"),body.get("bookId")));}
 @DeleteMapping("/student/reservations/{id}") public Map<String,LibraryStateResponse> cancel(@PathVariable String id,HttpServletRequest request){Map<?,?> user=requireRole(request,"student");return Map.of("state",library.cancelReservation((String)user.get("memberId"),id));}
 @SuppressWarnings("unchecked") private Map<?,?> requireUser(HttpServletRequest request){HttpSession s=request.getSession(false);if(s==null||!(s.getAttribute("user") instanceof Map))throw ApiException.unauthorized("Please sign in to continue.");return (Map<?,?>)s.getAttribute("user");}
 private Map<?,?> requireRole(HttpServletRequest request,String role){Map<?,?> user=requireUser(request);if(!role.equals(user.get("role")))throw ApiException.forbidden(role.equals("admin")?"Administrator access is required.":"Student access is required.");return user;}
}
