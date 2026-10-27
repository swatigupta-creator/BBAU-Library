package com.library.service;
import com.library.dto.LibraryStateRequest;
import com.library.dto.LibraryStateResponse;
import com.library.exception.ApiException;
import com.library.model.*;
import com.library.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
@Service
public class LibraryService {
 private final BookRepository books; private final MemberRepository members; private final LoanRepository loans; private final ReservationRepository reservations; private final ActivityRepository activities; private final MongoTemplate mongo;
 public LibraryService(BookRepository books, MemberRepository members, LoanRepository loans, ReservationRepository reservations, ActivityRepository activities, MongoTemplate mongo) { this.books=books; this.members=members; this.loans=loans; this.reservations=reservations; this.activities=activities; this.mongo=mongo; }
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
  state.books().stream().filter(item->!books.existsById(item.id)).forEach(books::save);
  state.members().stream().filter(item->!members.existsById(item.id)).forEach(members::save);
  state.activity().stream().filter(item->item.id!=null&&!activities.existsById(item.id)).forEach(activities::save);
 }
 public LibraryStateResponse borrow(String memberId,String memberName,String bookId) {
  Book book=books.findById(bookId).orElseThrow(()->ApiException.conflict("This book is no longer available."));
  List<Loan> memberLoans=loans.findAll().stream().filter(x->memberId.equals(x.memberId)&&!"Returned".equals(x.status)).toList();
  if(memberLoans.size()>=3) throw ApiException.conflict("Your account reached the borrowing limit.");
  if(memberLoans.stream().anyMatch(x->bookId.equals(x.bookId))) throw ApiException.conflict("You already borrowed this book.");
  issueAvailableBook(bookId);
  Book issuedBook=books.findById(bookId).orElseThrow();
  createLoan(issuedBook,memberId,LocalDate.now().plusDays(14).toString());
  activity("↗","green","Book issued to "+memberName,issuedBook.title+" · "+issuedBook.id);
  return state("student",memberId);
 }
 public LibraryStateResponse issue(String memberId,String bookId,String dueDate) {
  Member member=members.findById(memberId).orElseThrow(()->ApiException.notFound("Member not found."));
  List<Loan> memberLoans=loans.findAll().stream().filter(x->memberId.equals(x.memberId)&&!"Returned".equals(x.status)).toList();
  if(memberLoans.size()>=3) throw ApiException.conflict("This member reached the borrowing limit.");
  if(memberLoans.stream().anyMatch(x->bookId.equals(x.bookId))) throw ApiException.conflict("This member already borrowed this book.");
  Book book=issueAvailableBook(bookId);
  createLoan(book,memberId,dueDate);
  activity("↗","green","Book issued to "+member.name,book.title+" · "+book.id);
  return state("admin",null);
 }
 public LibraryStateResponse returnLoan(String id) {
  Loan loan=loans.findById(id).orElseThrow(()->ApiException.notFound("Loan not found."));
  Loan returned=mongo.findAndModify(Query.query(Criteria.where("_id").is(id).and("status").in("Issued","Overdue")),new Update().set("status","Returned"),FindAndModifyOptions.options().returnNew(true),Loan.class);
  if(returned==null) return state("admin",null);
  mongo.updateFirst(Query.query(Criteria.where("_id").is(loan.bookId).and("available").lt(books.findById(loan.bookId).map(book->book.copies).orElse(0))),new Update().inc("available",1),Book.class);
  activity("↙","gold","Return processed",books.findById(loan.bookId).map(book->book.title).orElse("Unknown title")+" · "+loan.memberId);
  return state("admin",null);
 }
 public LibraryStateResponse fulfillReservation(String id) {
  Reservation reservation=reservations.findById(id).orElseThrow(()->ApiException.notFound("Reservation not found."));
  Member member=members.findById(reservation.memberId).orElseThrow(()->ApiException.notFound("Member not found."));
  List<Loan> memberLoans=loans.findAll().stream().filter(x->member.id.equals(x.memberId)&&!"Returned".equals(x.status)).toList();
  if(memberLoans.size()>=3) throw ApiException.conflict("This member reached the borrowing limit.");
  if(memberLoans.stream().anyMatch(x->reservation.bookId.equals(x.bookId))) throw ApiException.conflict("This member already borrowed this book.");
  Book book=issueAvailableBook(reservation.bookId);
  createLoan(book,member.id,LocalDate.now().plusDays(14).toString());
  reservations.delete(reservation);
  activity("↗","green","Reservation fulfilled for "+member.name,book.title+" · "+book.id);
  return state("admin",null);
 }
 public void deleteReservation(String id) {
  if(!reservations.existsById(id)) throw ApiException.notFound("Reservation not found.");
  reservations.deleteById(id);
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
 private Book issueAvailableBook(String bookId) {
  Book book=mongo.findAndModify(Query.query(Criteria.where("_id").is(bookId).and("available").gt(0)),new Update().inc("available",-1),FindAndModifyOptions.options().returnNew(true),Book.class);
  if(book==null) throw ApiException.conflict("This book is no longer available.");
  return book;
 }
 private void createLoan(Book book,String memberId,String dueDate) {
  Loan loan=new Loan(); loan.id=uid("LN"); loan.bookId=book.id; loan.memberId=memberId; loan.issued=LocalDate.now().toString(); loan.due=dueDate; loan.status="Issued"; loans.save(loan);
 }
 private String uid(String prefix) { return prefix+"-"+UUID.randomUUID().toString().substring(0,8).toUpperCase(); }
}
