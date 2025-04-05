package ru.otus.hw.services.transfiguration;

import org.springframework.stereotype.Component;
import ru.otus.hw.domain.MaterialType;
import ru.otus.hw.domain.RawMaterial;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class TransfigurationStatistics {

    private AtomicLong transfigurationCount = new AtomicLong(0);
    private AtomicLong copperCount = new AtomicLong(0);
    private AtomicLong ironCount = new AtomicLong(0);
    private AtomicLong bronzeCount = new AtomicLong(0);
    private AtomicLong silverCount = new AtomicLong(0);
    private AtomicLong goldCount = new AtomicLong(0);

    public long getTranfigurationCount() {
        return transfigurationCount.incrementAndGet();
    }

    public long getMaterialCount(MaterialType materialType) {
        switch (materialType) {
            case COPPER:
                return copperCount.incrementAndGet();
            case IRON:
                return ironCount.incrementAndGet();
            case BRONZE:
                return bronzeCount.incrementAndGet();
            case SILVER:
                return silverCount.incrementAndGet();
            case GOLD:
                return goldCount.incrementAndGet();
            default:
                return 0;
        }
    }

    private long getTransfigurationCount() {
        return transfigurationCount.incrementAndGet();
    }

    private long getCopperCount() {
        return copperCount.incrementAndGet();
    }

    private long getIronCount() {
        return ironCount.incrementAndGet();
    }

    private long getBronzeCount() {
        return bronzeCount.incrementAndGet();
    }

    private long getSilverCount() {
        return silverCount.incrementAndGet();
    }

    private long getGoldCount() {
        return goldCount.incrementAndGet();
    }
}
