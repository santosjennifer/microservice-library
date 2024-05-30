package com.libraryapi.api.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoanFilterDto {
	
    private String isbn;
    private String customer;

}
