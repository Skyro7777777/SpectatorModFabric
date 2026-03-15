package com.spectatorhud.client;

/**
 * Configuration and utility class for SpectatorHUD mod.
 */
public class SpectatorHUDConfig {

    // Mod configuration settings
    private static boolean showHealthBar = true;
    private static boolean showHotbar = true;
    private static boolean showFoodBar = true;
    private static boolean showExperienceBar = true;
    private static boolean showInventory = true;
    private static boolean showArmorBar = true;
    private static boolean showAirBar = true;

    /**
     * Gets whether the health bar should be shown for spectated players.
     */
    public static boolean shouldShowHealthBar() {
        return showHealthBar;
    }

    /**
     * Gets whether the hotbar should be shown for spectated players.
     */
    public static boolean shouldShowHotbar() {
        return showHotbar;
    }

    /**
     * Gets whether the food bar should be shown for spectated players.
     */
    public static boolean shouldShowFoodBar() {
        return showFoodBar;
    }

    /**
     * Gets whether the experience bar should be shown for spectated players.
     */
    public static boolean shouldShowExperienceBar() {
        return showExperienceBar;
    }

    /**
     * Gets whether the inventory should be shown for spectated players.
     */
    public static boolean shouldShowInventory() {
        return showInventory;
    }

    /**
     * Gets whether the armor bar should be shown for spectated players.
     */
    public static boolean shouldShowArmorBar() {
        return showArmorBar;
    }

    /**
     * Gets whether the air bar should be shown for spectated players.
     */
    public static boolean shouldShowAirBar() {
        return showAirBar;
    }

    /**
     * Sets whether the health bar should be shown for spectated players.
     */
    public static void setShowHealthBar(boolean value) {
        showHealthBar = value;
    }

    /**
     * Sets whether the hotbar should be shown for spectated players.
     */
    public static void setShowHotbar(boolean value) {
        showHotbar = value;
    }

    /**
     * Sets whether the food bar should be shown for spectated players.
     */
    public static void setShowFoodBar(boolean value) {
        showFoodBar = value;
    }

    /**
     * Sets whether the experience bar should be shown for spectated players.
     */
    public static void setShowExperienceBar(boolean value) {
        showExperienceBar = value;
    }

    /**
     * Sets whether the inventory should be shown for spectated players.
     */
    public static void setShowInventory(boolean value) {
        showInventory = value;
    }

    /**
     * Sets whether the armor bar should be shown for spectated players.
     */
    public static void setShowArmorBar(boolean value) {
        showArmorBar = value;
    }

    /**
     * Sets whether the air bar should be shown for spectated players.
     */
    public static void setShowAirBar(boolean value) {
        showAirBar = value;
    }

    /**
     * Resets all configuration options to their default values.
     */
    public static void resetToDefaults() {
        showHealthBar = true;
        showHotbar = true;
        showFoodBar = true;
        showExperienceBar = true;
        showInventory = true;
        showArmorBar = true;
        showAirBar = true;
    }
}
