package com.spectatorhud.client;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpectatorHUDClient implements ClientModInitializer {
    public static final String MOD_ID = "spectatorhud";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("SpectatorHUD initialized! Now you can see spectated player's HUD.");
    }
}
