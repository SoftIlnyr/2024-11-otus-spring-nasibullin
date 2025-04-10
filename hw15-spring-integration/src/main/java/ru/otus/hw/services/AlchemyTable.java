package ru.otus.hw.services;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import ru.otus.hw.domain.RawMaterial;
import ru.otus.hw.domain.transfiguration.GoldBar;

@MessagingGateway
public interface AlchemyTable {

    @Gateway(requestChannel = "materialChannel", replyChannel = "goldChannel")
    GoldBar process(RawMaterial material);

}
