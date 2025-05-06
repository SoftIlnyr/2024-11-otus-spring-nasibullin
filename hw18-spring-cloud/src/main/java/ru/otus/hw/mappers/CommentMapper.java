package ru.otus.hw.mappers;

import org.mapstruct.Mapper;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.models.Comment;

import java.util.List;

@Mapper(componentModel = "spring", uses = {BookMapper.class})
public interface CommentMapper {

    CommentDto toDto(Comment comment);

    List<CommentDto> toDto(List<Comment> comments);

}
