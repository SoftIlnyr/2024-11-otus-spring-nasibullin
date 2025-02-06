package ru.otus.hw.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "books_comments")
public class Comment {

    @Id
    private String id;

    private String bookId;

    private String text;

    public Comment(Book book, String text) {
        this.bookId = book.getId();
        this.text = text;
    }
}
