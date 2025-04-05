package ru.otus.hw.domain.cleaning;

import lombok.Getter;
import ru.otus.hw.domain.MaterialType;

import java.util.Arrays;

@Getter
public enum CleaningProperties {

    COPPER(MaterialType.COPPER, 0.4d, 10),
    IRON(MaterialType.IRON, 0.5d, 8),
    BRONZE(MaterialType.BRONZE, 0.6d, 6),
    SILVER(MaterialType.SILVER, 0.8d, 4),
    GOLD(MaterialType.GOLD, 0.9d, 3);

    private final MaterialType materialType; // Тип материала
    private final Double cleaningCoefficient; // Сколько веса остается после очистки
    private final Integer cleaningTimeSeconds; // Время чистки в секундах

    CleaningProperties(MaterialType materialType, Double cleaningCoefficient, Integer cleaningTimeSeconds) {
        this.materialType = materialType;
        this.cleaningCoefficient = cleaningCoefficient;
        this.cleaningTimeSeconds = cleaningTimeSeconds;
    }

    public static CleaningProperties getProperties(MaterialType materialType) {
        return Arrays.stream(CleaningProperties.values())
                .filter(prop -> prop.materialType == materialType)
                .findFirst().orElseThrow(IllegalArgumentException::new);
    }
}
