package ru.otus.hw.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "books_comments")
public class Comment {

    @Id
    private String id;

    @DBRef
    @Lazy
    private Book book;

    private String text;

    public Comment(Book book, String text) {
        this.book = book;
        this.text = text;
    }
}
