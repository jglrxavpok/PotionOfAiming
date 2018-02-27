package org.jglrxavpok.aiming.common

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemBow
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11

object ProjectileRegistry {

    private val map = mutableMapOf<Item, ProjectileItemDescription>()

    fun registerProjectileType(projectileItemDescription: ProjectileItemDescription) {
        map.put(projectileItemDescription.correspondingItem, projectileItemDescription)
    }

    fun getDescription(item: Item) = map[item]

    fun hasDescription(item: Item) = item in map
}

object BowProjectileItemDescription: ProjectileItemDescription {

    val texture = ResourceLocation("minecraft:textures/particle/particles.png")
    override val correspondingItem = Items.BOW

    override fun render(stack: ItemStack, player: EntityPlayer, path: ProjectilePath) {
        val mc = Minecraft.getMinecraft()
        mc.renderEngine.bindTexture(texture)
        val tess = Tessellator.getInstance()
        val buffer = tess.buffer
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR)
        val radius = 0.25
        val alpha = 150
        val minU = 8.0 / 128.0
        val maxU = 16.0 / 128.0
        val minV = 32.0 / 128.0
        val maxV = 40.0 / 128.0
        buffer.pos(-radius, -radius, 0.0).tex(minU, minV).color(0, 0, 0, alpha).endVertex()
        buffer.pos(+radius, -radius, 0.0).tex(maxU, minV).color(0, 0, 0, alpha).endVertex()
        buffer.pos(+radius, +radius, 0.0).tex(maxU, maxV).color(0, 0, 0, alpha).endVertex()
        buffer.pos(-radius, +radius, 0.0).tex(minU, maxV).color(0, 0, 0, alpha).endVertex()

        GlStateManager.enableBlend()
        tess.draw()
        GlStateManager.disableBlend()
    }

    override fun gravity(stack: ItemStack, player: EntityPlayer): Float {
        return 0.05000000074505806f
    }

    override fun dragFactor(stack: ItemStack, player: EntityPlayer): Float {
        return 0.99f
    }

    override fun velocity(stack: ItemStack, player: EntityPlayer): Float {
        val timeLeft = player.itemInUseCount
        val charge = Items.BOW.getMaxItemUseDuration(stack) - timeLeft
        val baseVelocity = ItemBow.getArrowVelocity(charge)
        return 3f * baseVelocity
    }

    override fun inValidState(stack: ItemStack, player: EntityPlayer): Boolean {
        return player.activeItemStack == stack
    }

    override fun pitchOffset(stack: ItemStack, player: EntityPlayer): Float {
        return 0f
    }
}

open class BasicProjectileItemDescription(override val correspondingItem: Item, val gravity: Float = 0.03f, val drag: Float, val velocity: Float, val pitchOffset: Float = 0f): ProjectileItemDescription {

    val texture by lazy {
        val registryName = correspondingItem.registryName!!
        ResourceLocation(registryName.resourceDomain, "textures/items/${registryName.resourcePath}.png")
    }

    override fun render(stack: ItemStack, player: EntityPlayer, path: ProjectilePath) {
        val mc = Minecraft.getMinecraft()
        mc.renderEngine.bindTexture(texture)
        val tess = Tessellator.getInstance()
        val buffer = tess.buffer
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR)
        val radius = 0.25
        val alpha = 100
        buffer.pos(-radius, -radius, 0.0).tex(0.0, 1.0).color(255, 255, 255, alpha).endVertex()
        buffer.pos(+radius, -radius, 0.0).tex(1.0, 1.0).color(255, 255, 255, alpha).endVertex()
        buffer.pos(+radius, +radius, 0.0).tex(1.0, 0.0).color(255, 255, 255, alpha).endVertex()
        buffer.pos(-radius, +radius, 0.0).tex(0.0, 0.0).color(255, 255, 255, alpha).endVertex()

        GlStateManager.enableBlend()
        tess.draw()
        GlStateManager.disableBlend()
    }

    override fun gravity(stack: ItemStack, player: EntityPlayer) = gravity

    override fun dragFactor(stack: ItemStack, player: EntityPlayer) = drag

    override fun velocity(stack: ItemStack, player: EntityPlayer) = velocity

    override fun inValidState(stack: ItemStack, player: EntityPlayer) = true

    override fun pitchOffset(stack: ItemStack, player: EntityPlayer) = pitchOffset
}

interface ProjectileItemDescription {
    val correspondingItem: Item

    fun gravity(stack: ItemStack, player: EntityPlayer): Float
    fun dragFactor(stack: ItemStack, player: EntityPlayer): Float
    fun velocity(stack: ItemStack, player: EntityPlayer): Float
    fun inValidState(stack: ItemStack, player: EntityPlayer): Boolean
    fun pitchOffset(stack: ItemStack, player: EntityPlayer): Float

    @SideOnly(Side.CLIENT)
    fun render(stack: ItemStack, player: EntityPlayer, path: ProjectilePath)
}