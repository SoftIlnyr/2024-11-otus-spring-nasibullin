package ru.otus.hw.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.hw.config.AppRunner;
import ru.otus.hw.domain.MaterialType;
import ru.otus.hw.domain.RawMaterial;
import ru.otus.hw.domain.transfiguration.GoldBar;

@SpringBootTest
@DirtiesContext
class AlchemyTableTest {

    @Autowired
    private AlchemyTable alchemyTable;

    @MockitoBean
    private AppRunner appRunner;

    @Test
    void process() {
        RawMaterial rawMaterial = new RawMaterial();
        rawMaterial.setMaterialType(MaterialType.COPPER);
        Double weight = 10d;
        rawMaterial.setWeight(weight);
        GoldBar goldBar = alchemyTable.process(rawMaterial);
        Assertions.assertNotNull(goldBar);
        Assertions.assertEquals("C01001", goldBar.getSerialNumber());
    }
}