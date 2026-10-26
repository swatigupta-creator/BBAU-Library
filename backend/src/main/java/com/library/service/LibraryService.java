package com.library.service;
import com.library.dto.LibraryStateRequest;
import com.library.dto.LibraryStateResponse;
import com.library.exception.ApiException;
import com.library.model.*;
import com.library.repository.*;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
@Service
public class LibraryService {
 private final BookRepository books; private final MemberRepository members; private final LoanRepository loans; private final ReservationRepository reservations; private final ActivityRepository activities;
 public LibraryService(BookRepository books, MemberRepository members, LoanRepository loans, ReservationRepository reservations, ActivityRepository activities) { this.books=books; this.members=members; this.loans=loans; this.reservations=reservations; this.activities=activities; }
 public LibraryStateResponse state(String role, String memberId) {
  List<Book> allBooks=books.findAll();
  List<Member> memberList=role.equals("admin")?members.findAll():members.findById(memberId).map(List::of).orElse(List.of());
  List<Loan> loanList=loans.findAll().stream().filter(x->role.equals("admin")||memberId.equals(x.memberId)).toList();
  List<Reservation> reservationList=reservations.findAll().stream().filter(x->role.equals("admin")||memberId.equals(x.memberId)).toList();
  List<Activity> activityList=role.equals("admin")?activities.findAll():List.of();
  return new LibraryStateResponse(allBooks,memberList,loanList,reservationList,activityList);
 }
 public void replaceState(LibraryStateRequest state) {
  if (state==null||state.books()==null||state.members()==null||state.loans()==null||state.reservations()==null||state.activity()==null) throw ApiException.badRequest("Invalid library data.");
  books.deleteAll(); books.saveAll(state.books()); members.deleteAll(); members.saveAll(state.members()); loans.deleteAll(); loans.saveAll(state.loans()); reservations.deleteAll(); reservations.saveAll(state.reservations()); activities.deleteAll(); activities.saveAll(state.activity());
 }
 public LibraryStateResponse borrow(String memberId,String memberName,String bookId) {
  Book book=books.findById(bookId).orElseThrow(()->ApiException.conflict("This book is no longer available."));
  List<Loan> memberLoans=loans.findAll().stream().filter(x->memberId.equals(x.memberId)&&!"Returned".equals(x.status)).toList();
  if(book.available<1) throw ApiException.conflict("This book is no longer available.");
  if(memberLoans.size()>=3) throw ApiException.conflict("Your account reached the borrowing limit.");
  if(memberLoans.stream().anyMatch(x->bookId.equals(x.bookId))) throw ApiException.conflict("You already borrowed this book.");
  book.available--; books.save(book);
  Loan loan=new Loan(); loan.id=uid("LN"); loan.bookId=book.id; loan.memberId=memberId; loan.issued=LocalDate.now().toString(); loan.due=LocalDate.now().plusDays(14).toString(); loan.status="Issued"; loans.save(loan);
  activity("↗","green","Book issued to "+memberName,book.title+" · "+book.id);
  return state("student",memberId);
 }
 public LibraryStateResponse reserve(String memberId,String memberName,String bookId) {
  Book book=books.findById(bookId).orElseThrow(()->ApiException.notFound("Book not found."));
  if(book.available>0) throw ApiException.conflict("This title is available to borrow.");
  if(reservations.findAll().stream().anyMatch(x->bookId.equals(x.bookId)&&memberId.equals(x.memberId)&&"Pending".equals(x.status))) throw ApiException.conflict("You already reserved this title.");
  Reservation reservation=new Reservation(); reservation.id=uid("RS"); reservation.bookId=bookId; reservation.memberId=memberId; reservation.date=LocalDate.now().toString(); reservation.status="Pending"; reservations.save(reservation);
  activity("◷","gold","Reservation placed",book.title+" · "+memberName);
  return state("student",memberId);
 }
 public LibraryStateResponse cancelReservation(String memberId,String id) {
  Reservation reservation=reservations.findById(id).orElseThrow(()->ApiException.notFound("Reservation not found."));
  if(!memberId.equals(reservation.memberId)) throw ApiException.notFound("Reservation not found.");
  reservations.delete(reservation); return state("student",memberId);
 }
 public void activity(String icon,String tone,String title,String detail) {
  Activity item=new Activity(); item.icon=icon; item.tone=tone; item.title=title; item.detail=detail; item.time="Just now"; activities.save(item);
 }
 private String uid(String prefix) { return prefix+"-"+UUID.randomUUID().toString().substring(0,8).toUpperCase(); }
}
