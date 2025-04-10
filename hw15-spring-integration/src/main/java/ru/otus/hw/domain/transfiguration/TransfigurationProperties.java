package ru.otus.hw.domain.transfiguration;

import lombok.Getter;
import ru.otus.hw.domain.MaterialType;

import java.util.Arrays;

@Getter
public enum TransfigurationProperties {

    COPPER(MaterialType.COPPER, 2.16d, 3),
    IRON(MaterialType.IRON, 2.45d, 2),
    BRONZE(MaterialType.BRONZE, 2.2d, 2),
    SILVER(MaterialType.SILVER, 1.84d, 4),
    GOLD(MaterialType.GOLD, 1d, 1);

    private final MaterialType materialType; // Тип материала
    private final Double transfigurationCoefficient; // Во сколько раз увеличивается вес после превращения (объем остается тем же)
    private final Integer transfigurationTimeSeconds; // Время превращения

    TransfigurationProperties(MaterialType materialType, Double transfigurationCoefficient, Integer transfigurationTimeSeconds) {
        this.materialType = materialType;
        this.transfigurationCoefficient = transfigurationCoefficient;
        this.transfigurationTimeSeconds = transfigurationTimeSeconds;
    }

    public static TransfigurationProperties getProperies(MaterialType materialType) {
        return Arrays.stream(TransfigurationProperties.values())
                .filter(prop -> prop.materialType == materialType)
                .findFirst().orElseThrow(IllegalArgumentException::new);
    }

}
