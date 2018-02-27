package org.jglrxavpok.aiming.common

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Gui
import net.minecraft.potion.Potion
import net.minecraft.potion.PotionEffect
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.jglrxavpok.aiming.PotionOfAiming

object AimingPotion: Potion(false, 0x0ACAFE) {

    init {
        setPotionName("effect.aiming")
        setBeneficial()
        setRegistryName(PotionOfAiming.ModID, "aiming")
    }

    private val iconTexture = ResourceLocation(PotionOfAiming.ModID, "textures/potion_icon.png")

    override fun isInstant(): Boolean {
        return false
    }

    override fun isReady(duration: Int, amplifier: Int): Boolean {
        return duration > 0
    }

    override fun shouldRenderHUD(effect: PotionEffect?): Boolean {
        return true
    }

    @SideOnly(Side.CLIENT)
    override fun renderHUDEffect(x: Int, y: Int, effect: PotionEffect, mc: Minecraft, alpha: Float) {
        mc.textureManager.bindTexture(iconTexture)
        Gui.drawModalRectWithCustomSizedTexture(x + 3, y + 4, 0f, 0f, 18, 18, 18f, 18f)
    }

    @SideOnly(Side.CLIENT)
    override fun renderInventoryEffect(x: Int, y: Int, effect: PotionEffect, mc: Minecraft) {
        if (mc.currentScreen != null) {
            mc.textureManager.bindTexture(iconTexture)
            Gui.drawModalRectWithCustomSizedTexture(x + 6, y + 8, 0f, 0f, 18, 18, 18f, 18f)
        }
    }

    override fun shouldRender(effect: PotionEffect?): Boolean {
        return true
    }

    override fun shouldRenderInvText(effect: PotionEffect?): Boolean {
        return true
    }

    override fun hasStatusIcon(): Boolean {
        return false
    }


}