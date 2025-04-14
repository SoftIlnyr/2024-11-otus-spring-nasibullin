package ru.otus.hw.services.transfiguration;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.otus.hw.domain.MaterialType;
import ru.otus.hw.domain.cleaning.CleanMaterial;
import ru.otus.hw.domain.cleaning.CleaningProperties;
import ru.otus.hw.domain.transfiguration.GoldBar;
import ru.otus.hw.domain.transfiguration.TransfigurationProperties;

import java.util.Map;

@Log4j2
@Service
public class TransfigurationServiceImpl implements TransfigurationService {

    @Autowired
    private TransfigurationStatistics statistics;

    @Override
    public GoldBar transfigure(CleanMaterial material) {
        TransfigurationProperties properties = TransfigurationProperties
                .getProperies(material.getMaterialType());

        log.info("Transfiguration service: received {}, weight {}. Start transfiguration... ",
                material.getMaterialType().name(), material.getWeight());
        try {
            Thread.sleep(properties.getTransfigurationTimeSeconds() * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        GoldBar goldBar = new GoldBar();
        goldBar.setWeight(material.getWeight() * properties.getTransfigurationCoefficient());
        goldBar.setSerialNumber(getSerialNumber(properties.getMaterialType()));

        log.info("Transfiguration service: transfiguration {} complete. Weight after transfiguration {}",
                material.getMaterialType().name(), goldBar.getWeight());

        return goldBar;
    }

    private String getSerialNumber(MaterialType materialType) {
        long transfigurationCount = statistics.getTranfigurationCount();
        long materialCount = statistics.getMaterialCount(materialType);
        return String.format("%s%02d%03d", materialType.name().subSequence(0, 1), materialCount, transfigurationCount);
    }
}
