package com.libraryapi.model.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.libraryapi.model.entity.Book;
import com.libraryapi.model.entity.Loan;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long>{

    @Query(value = "SELECT case WHEN (count(l.id) > 0) THEN true else false end " +
            " FROM Loan l WHERE l.book = :book AND (l.returned is null OR l.returned is false)")
    boolean existsByBookAndNotReturned(@Param("book") Book book);

    @Query("SELECT l FROM Loan l JOIN l.book b WHERE b.isbn = :isbn OR l.customer = :customer")
    Page<Loan> findByBookIsbnOrCustomer(@Param("isbn") String isbn, @Param("customer") String customer, Pageable pageable);
    
    Page<Loan> findByBook(Book book, Pageable pageable);

    @Query("SELECT l FROM Loan l WHERE l.loanDate <= :threeDaysAgo AND (l.returned is null OR l.returned is false)")
    List<Loan> findByLoanDateLessThanAndNotReturned(@Param("threeDaysAgo") LocalDate threeDaysAgo);

}
