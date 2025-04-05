package ru.otus.hw.domain.cleaning;

import lombok.Data;
import ru.otus.hw.domain.MaterialType;

@Data
public class CleanMaterial {

    private MaterialType materialType;
    private Double weight;

}
