package com.library.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public record SupportRequestDto(@NotBlank @Size(min=5,max=2000) String message) {}
