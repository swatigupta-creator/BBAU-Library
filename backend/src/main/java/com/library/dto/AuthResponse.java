package com.library.dto;
import java.util.Map;
public record AuthResponse(Map<String,Object> user, LibraryStateResponse state) {}
