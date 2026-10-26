package com.library.dto;
import com.library.model.*;
import java.util.List;
public record LibraryStateRequest(List<Book> books, List<Member> members, List<Loan> loans, List<Reservation> reservations, List<Activity> activity) {}
