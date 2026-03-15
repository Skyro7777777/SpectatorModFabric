package com.spectatorhud.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to InGameHud to render spectated player's HUD elements.
 * This includes health bar, hotbar, food bar, experience bar, and inventory.
 */
@Mixin(InGameHud.class)
public abstract class InGameHudMixin extends DrawableHelper {

    @Shadow @Final private MinecraftClient client;
    @Shadow @Final private static Identifier WIDGETS_TEXTURE;
    @Shadow private int scaledWidth;
    @Shadow private int scaledHeight;
    @Shadow private int ticks;

    // Custom texture location for HUD elements
    @Unique
    private static final Identifier GUI_ICONS_TEXTURE = new Identifier("textures/gui/icons.png");

    /**
     * Renders the spectated player's status bars and hotbar when in spectator mode.
     * This method injects after the normal render method to overlay the spectated player's HUD.
     */
    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
        // Check if the player is in spectator mode and spectating another player
        if (this.client.player == null || !this.client.player.isSpectator()) {
            return;
        }

        // Get the spectated entity
        Entity spectatedEntity = this.client.getCameraEntity();

        if (!(spectatedEntity instanceof PlayerEntity)) {
            return;
        }

        PlayerEntity spectatedPlayer = (PlayerEntity) spectatedEntity;

        // Don't render if spectating yourself
        if (spectatedPlayer == this.client.player) {
            return;
        }

        // Render the spectated player's HUD elements
        renderSpectatedPlayerHUD(matrices, spectatedPlayer, tickDelta);
    }

    /**
     * Renders all HUD elements for the spectated player.
     */
    @Unique
    private void renderSpectatedPlayerHUD(MatrixStack matrices, PlayerEntity spectatedPlayer, float tickDelta) {
        // Render hotbar with items
        renderSpectatedHotbar(matrices, spectatedPlayer);

        // Render health bar
        renderSpectatedHealth(matrices, spectatedPlayer);

        // Render food/hunger bar
        renderSpectatedFood(matrices, spectatedPlayer);

        // Render experience bar
        renderSpectatedExperience(matrices, spectatedPlayer);

        // Render mount health if applicable (for horses, etc.)
        renderSpectatedMountHealth(matrices, spectatedPlayer);

        // Render status effects
        renderSpectatedStatusBars(matrices, spectatedPlayer);
    }

    /**
     * Renders the hotbar of the spectated player.
     */
    @Unique
    private void renderSpectatedHotbar(MatrixStack matrices, PlayerEntity spectatedPlayer) {
        PlayerInventory inventory = spectatedPlayer.getInventory();

        // Position calculations
        int x = this.scaledWidth / 2 - 91;
        int y = this.scaledHeight - 22;

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, WIDGETS_TEXTURE);

        // Draw the hotbar background
        this.drawTexture(matrices, x, y, 0, 0, 182, 22);

        // Draw the selected slot highlight
        int selectedSlot = inventory.selectedSlot;
        this.drawTexture(matrices, x - 1 + selectedSlot * 20, y - 1, 0, 22, 24, 22);

        // Render the items in the hotbar
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        for (int slot = 0; slot < 9; ++slot) {
            ItemStack itemStack = inventory.getStack(slot);
            int itemX = x + 3 + slot * 20;
            int itemY = y + 3;

            // Render the item
            this.client.getItemRenderer().renderInGuiWithOverrides(itemStack, itemX, itemY);

            // Render item count
            this.client.getItemRenderer().renderGuiItemOverlay(this.client.textRenderer, itemStack, itemX, itemY, null);
        }

        RenderSystem.disableBlend();
    }

    /**
     * Renders the health bar of the spectated player.
     */
    @Unique
    private void renderSpectatedHealth(MatrixStack matrices, PlayerEntity spectatedPlayer) {
        float health = spectatedPlayer.getHealth();
        float maxHealth = spectatedPlayer.getMaxHealth();
        float absorptionAmount = spectatedPlayer.getAbsorptionAmount();

        // Position calculations
        int x = this.scaledWidth / 2 - 91;
        int y = this.scaledHeight - 39;

        RenderSystem.setShaderTexture(0, GUI_ICONS_TEXTURE);
        RenderSystem.enableBlend();

        // Calculate hearts
        int hearts = (int) Math.ceil(maxHealth / 2.0F);
        int healthHearts = (int) Math.ceil(health / 2.0F);

        // Render hearts
        for (int heart = 0; heart < hearts; ++heart) {
            int heartX = x + heart * 8;
            int heartY = y;

            // Background heart
            this.drawTexture(matrices, heartX, heartY, 16, 0, 9, 9);

            // Current health heart
            if (heart * 2 < health) {
                if (heart * 2 + 1 < health) {
                    // Full heart
                    this.drawTexture(matrices, heartX, heartY, 52, 0, 9, 9);
                } else {
                    // Half heart
                    this.drawTexture(matrices, heartX, heartY, 61, 0, 9, 9);
                }
            }
        }

        // Render absorption hearts if any
        if (absorptionAmount > 0) {
            int absorptionHearts = (int) Math.ceil(absorptionAmount / 2.0F);
            for (int heart = 0; heart < absorptionHearts; ++heart) {
                int heartX = x + (hearts + heart) * 8;
                int heartY = y;

                // Absorption hearts (yellow)
                this.drawTexture(matrices, heartX, heartY, 160, 0, 9, 9);

                if (heart * 2 + 1 < absorptionAmount) {
                    this.drawTexture(matrices, heartX, heartY, 169, 0, 9, 9);
                } else {
                    this.drawTexture(matrices, heartX, heartY, 178, 0, 9, 9);
                }
            }
        }

        RenderSystem.disableBlend();
    }

    /**
     * Renders the food/hunger bar of the spectated player.
     */
    @Unique
    private void renderSpectatedFood(MatrixStack matrices, PlayerEntity spectatedPlayer) {
        int foodLevel = spectatedPlayer.getHungerManager().getFoodLevel();
        float saturation = spectatedPlayer.getHungerManager().getSaturationLevel();

        // Position calculations (right side of the screen)
        int x = this.scaledWidth / 2 + 91 - 9;
        int y = this.scaledHeight - 39;

        RenderSystem.setShaderTexture(0, GUI_ICONS_TEXTURE);
        RenderSystem.enableBlend();

        // Render food icons
        for (int food = 0; food < 10; ++food) {
            int foodX = x - food * 8;
            int foodY = y;

            // Background
            this.drawTexture(matrices, foodX, foodY, 16, 27, 9, 9);

            // Current food level
            if (food * 2 + 1 < foodLevel) {
                this.drawTexture(matrices, foodX, foodY, 52, 27, 9, 9);
            } else if (food * 2 + 1 == foodLevel) {
                this.drawTexture(matrices, foodX, foodY, 61, 27, 9, 9);
            }
        }

        RenderSystem.disableBlend();
    }

    /**
     * Renders the experience bar of the spectated player.
     */
    @Unique
    private void renderSpectatedExperience(MatrixStack matrices, PlayerEntity spectatedPlayer) {
        int xpLevel = spectatedPlayer.experienceLevel;
        float xpProgress = spectatedPlayer.experienceProgress;

        if (xpLevel > 0 || xpProgress > 0) {
            // Position calculations
            int x = this.scaledWidth / 2 - 91;
            int y = this.scaledHeight - 32;

            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, GUI_ICONS_TEXTURE);

            // Render experience bar background
            this.drawTexture(matrices, x, y, 0, 64, 182, 5);

            // Render experience bar progress
            int progressWidth = (int) (xpProgress * 182.0F);
            if (progressWidth > 0) {
                this.drawTexture(matrices, x, y, 0, 69, progressWidth, 5);
            }

            // Render experience level text
            if (xpLevel > 0) {
                String levelText = Integer.toString(xpLevel);
                int textX = (this.scaledWidth - this.client.textRenderer.getWidth(levelText)) / 2;
                int textY = this.scaledHeight - 31 - 4;

                // Render text with shadow
                this.client.textRenderer.draw(matrices, levelText, textX + 1, textY, 0);
                this.client.textRenderer.draw(matrices, levelText, textX - 1, textY, 0);
                this.client.textRenderer.draw(matrices, levelText, textX, textY + 1, 0);
                this.client.textRenderer.draw(matrices, levelText, textX, textY - 1, 0);
                this.client.textRenderer.draw(matrices, levelText, textX, textY, 8453920);
            }
        }
    }

    /**
     * Renders the mount health bar (for horses, etc.) of the spectated player.
     */
    @Unique
    private void renderSpectatedMountHealth(MatrixStack matrices, PlayerEntity spectatedPlayer) {
        Entity vehicle = spectatedPlayer.getVehicle();

        if (vehicle != null && vehicle.isLiving()) {
            net.minecraft.entity.LivingEntity livingVehicle = (net.minecraft.entity.LivingEntity) vehicle;
            float vehicleHealth = livingVehicle.getHealth();
            float vehicleMaxHealth = livingVehicle.getMaxHealth();

            // Position calculations
            int x = this.scaledWidth / 2 + 91;
            int y = this.scaledHeight - 39;

            RenderSystem.setShaderTexture(0, GUI_ICONS_TEXTURE);
            RenderSystem.enableBlend();

            // Render hearts for the mount
            int hearts = (int) Math.ceil(vehicleMaxHealth / 2.0F);
            for (int heart = 0; heart < hearts && heart < 10; ++heart) {
                int heartX = x - heart * 8;
                int heartY = y;

                // Background
                this.drawTexture(matrices, heartX, heartY, 52, 9, 9, 9);

                // Current health
                if (heart * 2 < vehicleHealth) {
                    if (heart * 2 + 1 < vehicleHealth) {
                        this.drawTexture(matrices, heartX, heartY, 88, 9, 9, 9);
                    } else {
                        this.drawTexture(matrices, heartX, heartY, 97, 9, 9, 9);
                    }
                }
            }

            RenderSystem.disableBlend();
        }
    }

    /**
     * Renders status bars (air, armor) for the spectated player.
     */
    @Unique
    private void renderSpectatedStatusBars(MatrixStack matrices, PlayerEntity spectatedPlayer) {
        // Render armor bar
        int armor = spectatedPlayer.getArmor();
        if (armor > 0) {
            int x = this.scaledWidth / 2 - 91;
            int y = this.scaledHeight - 51;

            RenderSystem.setShaderTexture(0, GUI_ICONS_TEXTURE);
            RenderSystem.enableBlend();

            for (int armorIcon = 0; armorIcon < 10; ++armorIcon) {
                int armorX = x + armorIcon * 8;
                int armorY = y;

                if (armorIcon * 2 < armor) {
                    if (armorIcon * 2 + 1 < armor) {
                        this.drawTexture(matrices, armorX, armorY, 34, 9, 9, 9);
                    } else {
                        this.drawTexture(matrices, armorX, armorY, 25, 9, 9, 9);
                    }
                }
            }

            RenderSystem.disableBlend();
        }

        // Render air bar if underwater
        int air = spectatedPlayer.getAir();
        int maxAir = spectatedPlayer.getMaxAir();
        if (spectatedPlayer.isSubmergedInWater() || air < maxAir) {
            int x = this.scaledWidth / 2 + 91 - 9;
            int y = this.scaledHeight - 51;

            RenderSystem.setShaderTexture(0, GUI_ICONS_TEXTURE);
            RenderSystem.enableBlend();

            int airBubbles = (int) Math.ceil(air / 30.0F);
            for (int bubble = 0; bubble < 10; ++bubble) {
                int bubbleX = x - bubble * 8;
                int bubbleY = y;

                if (bubble < airBubbles) {
                    this.drawTexture(matrices, bubbleX, bubbleY, 16, 18, 9, 9);
                } else {
                    this.drawTexture(matrices, bubbleX, bubbleY, 25, 18, 9, 9);
                }
            }

            RenderSystem.disableBlend();
        }
    }
}
