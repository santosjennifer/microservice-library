package com.libraryapi.api.payload;

import com.libraryapi.api.dto.BookDto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LoanResponse {

    private Long loan;
    private String customer;
    private String email;
    private BookDto book;
    private boolean returned;
    
}
