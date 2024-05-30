package com.libraryapi.api.resource;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.libraryapi.api.dto.BookDto;
import com.libraryapi.api.dto.LoanFilterDto;
import com.libraryapi.api.dto.ReturnedLoanDto;
import com.libraryapi.api.payload.LoanRequest;
import com.libraryapi.api.payload.LoanResponse;
import com.libraryapi.model.entity.Book;
import com.libraryapi.model.entity.Loan;
import com.libraryapi.service.BookService;
import com.libraryapi.service.LoanService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
@Tag(name = "Loan")
public class LoanController {

	private final LoanService service;
	private final BookService bookService;
	
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LoanResponse create(@RequestBody @Valid LoanRequest request) {
        Book book = bookService
                .getBookByIsbn(request.getIsbn())
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.BAD_REQUEST, "Livro não encontrado para o ISBN informado."));
        Loan entity = Loan.builder()
                .book(book)
                .customer(request.getCustomer())
                .customerEmail(request.getEmail())
                .loanDate(LocalDate.now())
                .returned(false)
                .build();

        entity = service.save(entity);
        
		return LoanResponse.builder()
				.loan(entity.getId())
				.customer(entity.getCustomer())
				.email(entity.getCustomerEmail())
				.book(BookDto.builder()
						.author(book.getAuthor())
						.title(book.getTitle())
						.id(book.getId())
						.isbn(book.getIsbn())
						.build())
				.build();
    }
    
    @PatchMapping("{id}")
    public void returnBook(@PathVariable Long id, @RequestBody @Valid ReturnedLoanDto dto) {
        Loan loan = service.getById(id).orElseThrow(() 
        		-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empréstimo não encontrado."));
        loan.setReturned(dto.getReturned());
        service.update(loan);
    }
    
    @GetMapping
    public List<LoanResponse> find(LoanFilterDto dto, Pageable pageRequest) {
    	if (dto.getCustomer() == null && dto.getIsbn() == null) {
    		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe o ISBN ou o cliente.");
    	}
        Page<Loan> result = service.find(dto, pageRequest);
        List<LoanResponse> loans = result.getContent()
                .stream()
                .map(entity -> {
                    Book book = entity.getBook();
                    BookDto bookDTO = BookDto.builder()
                            .id(book.getId())
                            .author(book.getAuthor())
                            .title(book.getTitle())
                            .isbn(book.getIsbn())
                            .build();

                    LoanResponse loanResponse = LoanResponse.builder()
                            .loan(entity.getId())
                            .customer(entity.getCustomer())
                            .email(entity.getCustomerEmail())
                            .returned(entity.getReturned())
                            .build();
                    loanResponse.setBook(bookDTO);
                    return loanResponse;
                }).collect(Collectors.toList());
        return loans;
    }

}
