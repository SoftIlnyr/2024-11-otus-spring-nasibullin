package ru.otus.hw.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.BookComment;

@RequiredArgsConstructor
@Component
public class BookCommentConverter {

    public String bookCommentToString(BookComment bookComment) {
        return "Book Title: {%s}, Comment ID: {%s}, Comment text: {%s}".formatted(bookComment.getBook().getTitle(),
                bookComment.getId(), bookComment.getText());
    }
}
