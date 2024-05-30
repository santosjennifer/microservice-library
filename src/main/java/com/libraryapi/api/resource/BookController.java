package com.libraryapi.api.resource;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.libraryapi.api.dto.BookDto;
import com.libraryapi.api.payload.BookRequestCreate;
import com.libraryapi.api.payload.BookRequestUpdate;
import com.libraryapi.api.payload.LoanResponse;
import com.libraryapi.model.entity.Book;
import com.libraryapi.model.entity.Loan;
import com.libraryapi.service.BookService;
import com.libraryapi.service.LoanService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/books")
@Slf4j
@Tag(name = "Book")
public class BookController {
	
	private BookService service;
	private LoanService loanService;
	
	public BookController(BookService service, LoanService loanService) {
		this.service = service;
		this.loanService = loanService;
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public BookDto create(@RequestBody @Valid BookRequestCreate request) {
		log.info("Criando um livro para isbn: {}", request.getIsbn());
		Book entity = Book.builder()
				.author(request.getAuthor())
				.title(request.getTitle())
				.isbn(request.getIsbn())
				.build();
		
		entity = service.save(entity);
		
		return BookDto.builder()
				.id(entity.getId())
				.author(entity.getAuthor())
				.title(entity.getTitle())
				.isbn(entity.getIsbn())
				.build();
	}
	
	@GetMapping("{id}")
	public BookDto get(@PathVariable Long id) {
		log.info("Obtendo detalhes do livro pelo id: {}", id);
		return service.getById(id).map( book -> 
						BookDto.builder()
							.id(book.getId())
							.author(book.getAuthor())
							.title(book.getTitle())
							.isbn(book.getIsbn())
							.build())
								.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Livro não encontrado."));
	}
	
	@DeleteMapping("{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		log.info("Excluindo livro do id: {}", id);
		Book book = service.getById(id).orElseThrow(() 
				-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Livro não encontrado."));
		service.delete(book);
	}
	
	@PutMapping("{id}")
	public BookDto update(@PathVariable Long id, @RequestBody @Valid BookRequestUpdate request) {
		return service.getById(id).map(book -> {
			book.setAuthor(request.getAuthor());
			book.setTitle(request.getTitle());
			book = service.update(book);
			
			return BookDto.builder()
					.id(book.getId())
					.author(book.getAuthor())
					.title(book.getTitle())
					.isbn(book.getIsbn())
					.build();
		}).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Livro não encontrado."));
	}
	
	@GetMapping
	public List<BookDto> find(BookDto dto, Pageable pageRequest) {
	   Book filter = Book.builder()
	   					.author(dto.getAuthor())
	   					.title(dto.getTitle())
	   					.isbn(dto.getIsbn())
	   					.build();
	   Page<Book> books = service.find(filter, pageRequest);
	   List<BookDto> bookList = books.getContent()
			   					.stream()
			   					.map(entity -> BookDto.builder()
										.id(entity.getId())
										.author(entity.getAuthor())
										.title(entity.getTitle())
										.isbn(entity.getIsbn())
										.build())
			   					.collect(Collectors.toList());
	   return bookList;
	}
	
	@GetMapping("{id}/loans")
	public List<LoanResponse> loansByBook(@PathVariable Long id, Pageable pageable) {
		Book book = service.getById(id).orElseThrow(() 
				-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Livro não encontrado."));
		Page<Loan> result = loanService.getLoansByBook(book, pageable);
		 List<LoanResponse> loanList = result.getContent()
				.stream()
				.map(loan -> {
					Book loanBook = loan.getBook();
					BookDto bookDTO = BookDto.builder()
											.id(loanBook.getId())
											.author(loanBook.getAuthor())
											.title(loanBook.getTitle())
											.isbn(loanBook.getIsbn())
											.build();
					LoanResponse loanResponse = LoanResponse.builder()
											.loan(loan.getId())
											.customer(loan.getCustomer())
											.email(loan.getCustomerEmail())
											.returned(loan.getReturned())
											.build();
					loanResponse.setBook(bookDTO);
					return loanResponse;
				}).collect(Collectors.toList());
        return loanList;
	}

}
