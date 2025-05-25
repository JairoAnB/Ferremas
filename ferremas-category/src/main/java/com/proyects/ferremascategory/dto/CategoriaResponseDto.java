package com.proyects.ferremascategory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoriaResponseDto {

    private Long id;
    private String message;
    private HttpStatus status;
    private LocalDateTime timestamp;

}
