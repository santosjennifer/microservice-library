package com.libraryapi.model.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.libraryapi.model.entity.Book;

@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {

	@Autowired
	TestEntityManager entityManager;
	
	@Autowired
	BookRepository repository;
	
	@Test
	@DisplayName("Deve retornar verdadeiro quando existir um livro na base com o isbn informado")
	public void returnTrueWhenIsbnExists() {
		String isbn = "123";
		Book book = createNewBook(isbn);
		entityManager.persist(book);
		
		boolean exists = repository.existsByIsbn(isbn);
		
		assertThat(exists).isTrue();
	}
	
	@Test
	@DisplayName("Deve retornar false quando não existir um livro na base com o isbn informado")
	public void returnFalseWhenIsbnNotExists() {
		String isbn = "123";
		
		boolean exists = repository.existsByIsbn(isbn);
		
		assertThat(exists).isFalse();
	}
	
	
	@Test
	@DisplayName("Deve obter um livro por id")
	public void findByIdTest() {
		Book book = createNewBook("123");
		entityManager.persist(book);
		
		Optional<Book> foundBook = repository.findById(book.getId());
		
		assertThat(foundBook.isPresent()).isTrue();
	}
	
	
	@Test
	@DisplayName("Deve salvar um livro")
	public void saveBookTest() {
		Book book = createNewBook("123");
		
		Book savedBook = repository.save(book);
		
		assertThat(savedBook.getId()).isNotNull();
	}
	
	@Test
	@DisplayName("Deve excluir um livro")
	public void deleteBookTest() {
		Book book = createNewBook("123");
		entityManager.persist(book);
		
		Book foundBook = entityManager.find(Book.class, book.getId());
		
		repository.delete(foundBook);
		
		Book deletedBook = entityManager.find(Book.class, book.getId());
		assertThat(deletedBook).isNull();
	}
	
    public static Book createNewBook(String isbn) {
        return Book.builder().title("Percy Jackson").author("Rick Riordan").isbn(isbn).build();
    }
}
