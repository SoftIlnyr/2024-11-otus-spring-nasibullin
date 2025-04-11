package ru.otus.hw.services.transfiguration;

import ru.otus.hw.domain.cleaning.CleanMaterial;
import ru.otus.hw.domain.transfiguration.GoldBar;

public interface TransfigurationService {

    GoldBar transfigure(CleanMaterial material);
}
