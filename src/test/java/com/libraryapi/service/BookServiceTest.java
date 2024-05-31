package com.libraryapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.libraryapi.exception.BusinessException;
import com.libraryapi.model.entity.Book;
import com.libraryapi.model.repository.BookRepository;
import com.libraryapi.service.impl.BookServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    BookService service;

    @MockBean
    BookRepository repository;

    @BeforeEach
    public void setUp() {
        this.service = new BookServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBookTest() {
        Book book = createValidBook();
        when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);
        when(repository.save(book)).thenReturn(
                Book.builder().id(1L)
                        .isbn("3344")
                        .author("Suzanne Collins")
                        .title("Jogos vorazes").build());

        Book savedBook = service.save(book);

        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getIsbn()).isEqualTo("3344");
        assertThat(savedBook.getTitle()).isEqualTo("Jogos vorazes");
        assertThat(savedBook.getAuthor()).isEqualTo("Suzanne Collins");
    }
    
	@Test
	@DisplayName("Deve lançar erro de negocio ao cadastrar um livro com isbn que já cadastrado")
	public void shouldNotSaveBookWithDuplicaeteIsbn() throws Exception {
		Book book = createValidBook();
		when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);
		
		Throwable exception = Assertions.catchThrowable(() -> service.save(book));
		
		assertThat(exception)
			.isInstanceOf(BusinessException.class)
			.hasMessage("ISBN já cadastrado.");
	}
	
	@Test
	@DisplayName("Deve obter um livro por id")
	public void getByIdTest() {
		Long id = 1l;
		Book book = createValidBook();
		book.setId(id);
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.of(book));
	
		Optional<Book> foundBook = service.getById(id);
		
		assertThat(foundBook.isPresent()).isTrue();
		assertThat(foundBook.get().getId()).isEqualTo(id);
		assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
		assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
		assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
	}
	
	@Test
	@DisplayName("Deve retornar vazio ao obtre um livro por ID quando ele não existe na base")
	public void bookNotFoundByTest() {
		Long id = 1l;

		Mockito.when(repository.findById(id)).thenReturn(Optional.empty());
	
		Optional<Book> book = service.getById(id);
		
		assertThat(book.isPresent()).isFalse();
	}
	
    @Test
    @DisplayName("Deve excluir um livro")
    public void deleteBookTest() {
        Book book = Book.builder().id(1l).build();

        assertDoesNotThrow( () -> service.delete(book) );

        Mockito.verify(repository, Mockito.times(1)).delete(book);
    }
	
    @Test
    @DisplayName("Deve ocorrer erro ao tentar excluir um livro inexistente")
    public void deleteInvalidBookTest(){
        Book book = new Book();

        assertThrows(IllegalArgumentException.class, () -> service.delete(book));

        Mockito.verify(repository, Mockito.never()).delete(book);
    }
    
    @Test
    @DisplayName("Deve atualizar um livro")
    public void updateBookTest() {
        long id = 1l;

        Book updatingBook = Book.builder().id(id).build();

        Book updatedBook = createValidBook();
        updatedBook.setId(id);
        when(repository.save(updatingBook)).thenReturn(updatedBook);

        Book book = service.update(updatingBook);

        assertThat(book.getId()).isEqualTo(updatedBook.getId());
        assertThat(book.getTitle()).isEqualTo(updatedBook.getTitle());
        assertThat(book.getIsbn()).isEqualTo(updatedBook.getIsbn());
        assertThat(book.getAuthor()).isEqualTo(updatedBook.getAuthor());
    }
    
    @Test
    @DisplayName("Deve ocorrer erro ao tentar atualizar um livro inexistente")
    public void updateInvalidBookTest(){
        Book book = Book.builder().id(1l).build();

        assertDoesNotThrow(() -> service.delete(book));

        Mockito.verify(repository, Mockito.times(1)).delete(book);
    }
	 
    @Test
    @DisplayName("Deve filtrar livros pelas propriedades")
    public void findBookTest() {
        Book book = createValidBook();

        PageRequest pageRequest = PageRequest.of(0, 10);

        List<Book> lista = Arrays.asList(book);
        Page<Book> page = new PageImpl<>(lista, pageRequest, 1);
        when(repository.findAll(Mockito.<Example<Book>>any(), Mockito.<Pageable>any()))
                .thenReturn(page);

        Page<Book> result = service.find(book, pageRequest);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(lista);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

    @Test
    @DisplayName("Deve obter um livro pelo isbn")
    public void getBookByIsbnTest() {
        String isbn = "1230";
        when(repository.findByIsbn(isbn)).thenReturn(Optional.of(Book.builder().id(1l).isbn(isbn).build()));

        Optional<Book> book = service.getBookByIsbn(isbn);

        assertThat(book.isPresent()).isTrue();
        assertThat(book.get().getId()).isEqualTo(1l);
        assertThat(book.get().getIsbn()).isEqualTo(isbn);

        verify(repository, times(1)).findByIsbn(isbn);
    }
	
    private Book createValidBook() {
        return Book.builder().isbn("123").author("Rick Riordan").title("Herois do Olimpo").build();
    }

}
