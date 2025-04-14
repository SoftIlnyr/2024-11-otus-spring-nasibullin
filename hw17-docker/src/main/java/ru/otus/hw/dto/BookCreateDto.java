package ru.otus.hw.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookCreateDto {

    @NotEmpty
    private String title;

    @NotEmpty
    private String authorId;

    @NotEmpty(message = "Genres ids must not be null")
    private List<String> genreIds;

}
