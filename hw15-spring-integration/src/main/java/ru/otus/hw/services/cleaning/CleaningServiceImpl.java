package ru.otus.hw.services.cleaning;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.otus.hw.domain.cleaning.CleanMaterial;
import ru.otus.hw.domain.RawMaterial;
import ru.otus.hw.domain.cleaning.CleaningProperties;

@Log4j2
@Service
public class CleaningServiceImpl implements CleaningService {

    @Override
    public CleanMaterial clean(RawMaterial material) {
        CleaningProperties properties = CleaningProperties
                .getProperties(material.getMaterialType());

        log.info("Cleaning service: received {}, weight {}. Start cleaning... ",
                material.getMaterialType().name(), material.getWeight());
        try {
            Thread.sleep(properties.getCleaningTimeSeconds() * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        CleanMaterial cleanMaterial = new CleanMaterial();
        cleanMaterial.setMaterialType(material.getMaterialType());
        cleanMaterial.setWeight(material.getWeight() * properties.getCleaningCoefficient());

        log.info("Cleaning service: cleaning {} complete. Weight after cleaning {}",
                material.getMaterialType().name(), cleanMaterial.getWeight());

        return cleanMaterial;
    }
}
