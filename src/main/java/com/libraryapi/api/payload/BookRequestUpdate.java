package com.libraryapi.api.payload;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class BookRequestUpdate {

	@NotBlank(message = "O título deve ser informado.")
    private String title;
	
	@NotBlank(message = "O autor deve ser informado.")
    private String author;

}
