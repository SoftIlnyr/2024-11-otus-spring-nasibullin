package ru.otus.hw.models;

import lombok.Data;

import java.util.List;

@Data
public class BookSaveModel {

    private String id;

    private String title;

    private String authorId;

    private List<String> genreIds;

}
