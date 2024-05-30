package com.libraryapi.api.dto;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReturnedLoanDto {

	private Boolean returned;
}
