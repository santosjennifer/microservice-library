package com.libraryapi.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.libraryapi.api.dto.LoanFilterDto;
import com.libraryapi.model.entity.Book;
import com.libraryapi.model.entity.Loan;

public interface LoanService {
	
	Loan save(Loan loan);
    Optional<Loan> getById(Long id);
    Loan update(Loan loan);
    Page<Loan> find(LoanFilterDto filterDTO, Pageable pageable);
    Page<Loan> getLoansByBook(Book book, Pageable pageable);
    List<Loan> getAllLateLoans();

}
