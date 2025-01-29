package ru.otus.hw.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.Comment;

@RequiredArgsConstructor
@Component
public class CommentConverter {

    public String commentToString(Comment comment) {
        return "Book Title: {%s}, Comment ID: {%s}, Comment text: {%s}".formatted(comment.getBook().getTitle(),
                comment.getId(), comment.getText());
    }
}
