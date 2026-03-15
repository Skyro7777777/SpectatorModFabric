package com.spectatorhud.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.ingame.ScreenHandlerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to InventoryScreen to show the spectated player's inventory.
 * When a spectator opens their inventory while spectating another player,
 * this will show the spectated player's inventory instead.
 */
@Mixin(InventoryScreen.class)
public abstract class GuiInventoryMixin extends net.minecraft.client.gui.screen.ingame.HandledScreen<net.minecraft.screen.PlayerScreenHandler> {

    @Shadow @Final private boolean narrow;

    // Texture for the inventory background
    @Unique
    private static final Identifier INVENTORY_TEXTURE = new Identifier("textures/gui/container/inventory.png");

    // Reference to the spectated player (if any)
    @Unique
    private PlayerEntity spectatedPlayer = null;

    public GuiInventoryMixin(PlayerScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    /**
     * Called when the screen is initialized. Sets up the spectated player reference.
     */
    @Inject(method = "init", at = @At("HEAD"))
    private void onInit(CallbackInfo ci) {
        if (this.client != null && this.client.player != null && this.client.player.isSpectator()) {
            Entity cameraEntity = this.client.getCameraEntity();
            if (cameraEntity instanceof PlayerEntity && cameraEntity != this.client.player) {
                this.spectatedPlayer = (PlayerEntity) cameraEntity;
            }
        }
    }

    /**
     * Renders the inventory screen. If spectating another player, shows their inventory.
     */
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onRender(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (this.spectatedPlayer != null && this.client.player != null && this.client.player.isSpectator()) {
            // Render the spectated player's inventory view
            renderSpectatedInventory(matrices, mouseX, mouseY, delta);
        }
    }

    /**
     * Renders the spectated player's inventory.
     */
    @Unique
    private void renderSpectatedInventory(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        // Draw the background
        this.renderBackground(matrices);

        // Set up rendering
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, INVENTORY_TEXTURE);

        int x = this.x;
        int y = this.y;

        // Draw the inventory background
        this.drawTexture(matrices, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);

        // Draw the player model in the inventory
        drawSpectatedPlayerModel(matrices, x + 51, y + 75, 30, (float)(x + 51) - mouseX, (float)(y + 75 - 50) - mouseY, this.spectatedPlayer);

        // Render the items in the inventory
        renderInventoryItems(matrices, mouseX, mouseY);

        // Draw the title
        this.textRenderer.draw(matrices, this.title, (float) x + 97 - this.textRenderer.getWidth(this.title) / 2, (float) y + 8, 0x404040);

        // Show spectating message
        Text spectatingText = new LiteralText("Viewing: " + this.spectatedPlayer.getName().getString());
        this.textRenderer.draw(matrices, spectatingText, (float) x + 97 - this.textRenderer.getWidth(spectatingText) / 2, (float) y + this.backgroundHeight + 5, 0xAAAAAA);

        // Render tooltips if hovering over items
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    /**
     * Draws the spectated player's model in the inventory screen.
     */
    @Unique
    private void drawSpectatedPlayerModel(MatrixStack matrices, int x, int y, int size, float mouseX, float mouseY, PlayerEntity player) {
        // Use the vanilla inventory screen method to draw the player model
        InventoryScreen.drawEntity(x, y, size, mouseX, mouseY, player);
    }

    /**
     * Renders the items in the spectated player's inventory.
     */
    @Unique
    private void renderInventoryItems(MatrixStack matrices, int mouseX, int mouseY) {
        if (this.spectatedPlayer == null) return;

        PlayerInventory inventory = this.spectatedPlayer.getInventory();

        // Set up item rendering
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // Render main inventory (3x9 grid)
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                int slotIndex = 9 + row * 9 + col;
                ItemStack stack = inventory.getStack(slotIndex);
                int itemX = this.x + 8 + col * 18;
                int itemY = this.y + 84 + row * 18;

                this.client.getItemRenderer().renderInGuiWithOverrides(stack, itemX, itemY);
                this.client.getItemRenderer().renderGuiItemOverlay(this.client.textRenderer, stack, itemX, itemY, null);
            }
        }

        // Render hotbar
        for (int slot = 0; slot < 9; ++slot) {
            ItemStack stack = inventory.getStack(slot);
            int itemX = this.x + 8 + slot * 18;
            int itemY = this.y + 142;

            this.client.getItemRenderer().renderInGuiWithOverrides(stack, itemX, itemY);
            this.client.getItemRenderer().renderGuiItemOverlay(this.client.textRenderer, stack, itemX, itemY, null);
        }

        // Render armor and offhand
        // Helmet (slot 39)
        ItemStack helmet = inventory.getStack(39);
        this.client.getItemRenderer().renderInGuiWithOverrides(helmet, this.x + 8, this.y + 8);
        this.client.getItemRenderer().renderGuiItemOverlay(this.client.textRenderer, helmet, this.x + 8, this.y + 8, null);

        // Chestplate (slot 38)
        ItemStack chestplate = inventory.getStack(38);
        this.client.getItemRenderer().renderInGuiWithOverrides(chestplate, this.x + 8, this.y + 8 + 18);
        this.client.getItemRenderer().renderGuiItemOverlay(this.client.textRenderer, chestplate, this.x + 8, this.y + 8 + 18, null);

        // Leggings (slot 37)
        ItemStack leggings = inventory.getStack(37);
        this.client.getItemRenderer().renderInGuiWithOverrides(leggings, this.x + 8, this.y + 8 + 18 * 2);
        this.client.getItemRenderer().renderGuiItemOverlay(this.client.textRenderer, leggings, this.x + 8, this.y + 8 + 18 * 2, null);

        // Boots (slot 36)
        ItemStack boots = inventory.getStack(36);
        this.client.getItemRenderer().renderInGuiWithOverrides(boots, this.x + 8, this.y + 8 + 18 * 3);
        this.client.getItemRenderer().renderGuiItemOverlay(this.client.textRenderer, boots, this.x + 8, this.y + 8 + 18 * 3, null);

        // Offhand (slot 40)
        ItemStack offhand = inventory.getStack(40);
        this.client.getItemRenderer().renderInGuiWithOverrides(offhand, this.x + 152, this.y + 8 + 18 * 3);
        this.client.getItemRenderer().renderGuiItemOverlay(this.client.textRenderer, offhand, this.x + 152, this.y + 8 + 18 * 3, null);

        // Render held item highlight
        int selectedSlot = inventory.selectedSlot;
        int highlightX = this.x + 8 + selectedSlot * 18;
        int highlightY = this.y + 142;

        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        this.fillGradient(matrices, highlightX, highlightY, highlightX + 16, highlightY + 16, 0x80FFFFFF, 0x80FFFFFF);
        RenderSystem.enableBlend();
        RenderSystem.enableDepthTest();

        RenderSystem.disableBlend();
    }

    /**
     * Prevents the spectator from interacting with the spectated player's inventory.
     */
    @Override
    protected void onMouseClick(Slot slot, int slotId, int button, SlotActionType actionType) {
        // Don't allow any clicks when viewing spectated player's inventory
        if (this.spectatedPlayer != null && this.client.player != null && this.client.player.isSpectator()) {
            return;
        }
        super.onMouseClick(slot, slotId, button, actionType);
    }
}
