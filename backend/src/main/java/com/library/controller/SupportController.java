package com.library.controller;
import com.library.dto.SupportRequestDto;
import com.library.exception.ApiException;
import com.library.model.SupportRequest;
import com.library.repository.SupportRequestRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.Map;
@RestController
@RequestMapping("/api/support")
public class SupportController {
 private final SupportRequestRepository requests;
 public SupportController(SupportRequestRepository requests){this.requests=requests;}
 @PostMapping public Map<String,Boolean> submit(@Valid @RequestBody SupportRequestDto body,HttpServletRequest request){HttpSession session=request.getSession(false);if(session==null||!(session.getAttribute("user") instanceof Map<?,?> user))throw ApiException.unauthorized("Please sign in to continue.");SupportRequest item=new SupportRequest();item.userId=String.valueOf(user.get("id"));item.message=body.message().trim();item.createdAt=Instant.now();requests.save(item);return Map.of("ok",true);}
}
