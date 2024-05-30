package com.libraryapi.api.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LoanRequest {
	
    @NotBlank(message = "O ISBN deve ser informado.")
    private String isbn;
    
    @NotBlank(message = "O cliente deve ser informado.")
    private String customer;
    
    @NotBlank(message = "O e-mail deve ser informado.")
    @Email(message = "O e-mail deve ser válido.")
    private String email;

}
