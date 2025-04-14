package ru.otus.hw.services;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Service;
import ru.otus.hw.domain.MaterialType;
import ru.otus.hw.domain.RawMaterial;
import ru.otus.hw.domain.transfiguration.GoldBar;

import java.util.concurrent.ForkJoinPool;

@Log4j2
@Service
public class AlchemyLaboratoryServiceImpl implements AlchemyLaboratoryService {

    private final AlchemyTable alchemyTable;

    public AlchemyLaboratoryServiceImpl(AlchemyTable alchemyTable) {
        this.alchemyTable = alchemyTable;
    }

    @Override
    public void makeGold() {
        ForkJoinPool pool = ForkJoinPool.commonPool();
        for (int i = 0; i < 10; i++) {
            pool.execute(() -> {
                RawMaterial rawMaterial = generateRandomMaterial();
                log.info("Laboratory: new material: {}, weight {}.", rawMaterial.getMaterialType(), rawMaterial.getWeight());
                GoldBar goldBar = alchemyTable.process(rawMaterial);
                log.info("Laboratory: received gold: serial number {}, weight {}.", goldBar.getSerialNumber(), goldBar.getWeight());
            });
            delay();
        }
    }

    private RawMaterial generateRandomMaterial() {
        RawMaterial rawMaterial = new RawMaterial();
        rawMaterial.setMaterialType(MaterialType.values()[RandomUtils.insecure().randomInt(0, MaterialType.values().length)]);
        rawMaterial.setWeight(RandomUtils.insecure().randomDouble(0, 10));
        return rawMaterial;
    }

    private void delay() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
