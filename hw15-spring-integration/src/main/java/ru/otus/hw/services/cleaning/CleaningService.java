package ru.otus.hw.services.cleaning;

import ru.otus.hw.domain.cleaning.CleanMaterial;
import ru.otus.hw.domain.RawMaterial;

public interface CleaningService {

    CleanMaterial clean(RawMaterial material);

}
