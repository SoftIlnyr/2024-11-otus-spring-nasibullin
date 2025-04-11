package ru.otus.hw.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookUpdateDto {

    @NotEmpty
    private String id;

    @NotEmpty(message = "title must not be null")
    private String title;

    @NotEmpty(message = "authorId must not be null")
    private String authorId;

    @NotEmpty(message = "Genres ids must not be null")
    private List<String> genreIds;

}
