package org.jglrxavpok.aiming.client

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.Tessellator
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.texture.TextureMap
import net.minecraft.client.renderer.vertex.DefaultVertexFormats
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.world.World
import net.minecraftforge.client.model.ModelLoader
import org.jglrxavpok.aiming.PotionOfAiming
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.client.event.RenderWorldLastEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.jglrxavpok.aiming.common.*
import org.lwjgl.opengl.GL11
import org.lwjgl.util.vector.Vector3f


@Mod.EventBusSubscriber(value = Side.CLIENT, modid = PotionOfAiming.ModID)
class Proxy: CommonProxy() {

    override fun preInit() {
        MinecraftForge.EVENT_BUS.register(this)
        super.preInit()
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    fun registerModels(event: ModelRegistryEvent) {
        for(item in Items.list) {
            ModelLoader.setCustomModelResourceLocation(item, 0, ModelResourceLocation(item.registryName.toString(), "inventory"))
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    fun renderAimingPathInWorld(event: RenderWorldLastEvent) {
        GL11.glPushMatrix()
        val player = Minecraft.getMinecraft().player
        val scale = 0.25f
        if(Minecraft.getMinecraft().gameSettings.thirdPersonView == 0)
            GL11.glTranslatef(0f, player.eyeHeight, 0f)

        renderRoutine(player, scale, event.partialTicks)
        GL11.glPopMatrix()
    }

    fun renderRoutine(player: EntityPlayer, scale: Float, partialTicks: Float) {
        val active = player.activePotionEffects.any { it.potion == AimingPotion }
        if(!active)
            return
        val stack = player.heldItemMainhand
        if(!ProjectileRegistry.hasDescription(stack.item))
            return
        if(!ProjectileRegistry.getDescription(stack.item)!!.inValidState(stack, player))
            return

        val time = player.ticksExisted + partialTicks
        val path = ProjectilePath(player, stack).compute(maxIterations = 1000)
        GlStateManager.enableRescaleNormal()
        val viewerPosX = Minecraft.getMinecraft().renderManager.viewerPosX
        val viewerPosY = Minecraft.getMinecraft().renderManager.viewerPosY
        val viewerPosZ = Minecraft.getMinecraft().renderManager.viewerPosZ
        GL11.glTranslated(-viewerPosX, -viewerPosY, -viewerPosZ)
        var lastPosition = Vector3f(player.positionVector.x.toFloat(), player.positionVector.y.toFloat() + player.eyeHeight, player.positionVector.z.toFloat())

        val t = time * 0.25f % 1f
        for(position in path) {
            renderPoint(lastPosition, position, t, path)
            lastPosition = position
        }

        GL11.glColor4f(1f, 1f, 1f, 1f)
        renderImpactPoint(lastPosition)
    }

    private fun renderImpactPoint(lastPosition: Vector3f) {
        GL11.glPushMatrix()

        val renderManager = Minecraft.getMinecraft().renderManager

        val posX = lastPosition.x
        val posY = lastPosition.y
        val posZ = lastPosition.z
        GlStateManager.translate(posX, posY, posZ)

        val tess = Tessellator.getInstance()
        val buffer = tess.buffer
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR)

        val radius = 0.125

        // front face
        buffer.pos(-radius, -radius, -radius).color(255, 0, 0, 255).endVertex()
        buffer.pos(+radius, -radius, -radius).color(255, 0, 0, 255).endVertex()
        buffer.pos(+radius, +radius, -radius).color(255, 0, 0, 255).endVertex()
        buffer.pos(-radius, +radius, -radius).color(255, 0, 0, 255).endVertex()

        // back face
        buffer.pos(-radius, -radius, +radius).color(255, 0, 0, 255).endVertex()
        buffer.pos(+radius, -radius, +radius).color(255, 0, 0, 255).endVertex()
        buffer.pos(+radius, +radius, +radius).color(255, 0, 0, 255).endVertex()
        buffer.pos(-radius, +radius, +radius).color(255, 0, 0, 255).endVertex()

        // top face
        buffer.pos(-radius, -radius, -radius).color(255, 0, 0, 255).endVertex()
        buffer.pos(+radius, -radius, -radius).color(255, 0, 0, 255).endVertex()
        buffer.pos(+radius, -radius, +radius).color(255, 0, 0, 255).endVertex()
        buffer.pos(-radius, -radius, +radius).color(255, 0, 0, 255).endVertex()

        // bottom face
        buffer.pos(-radius, +radius, -radius).color(255, 0, 0, 255).endVertex()
        buffer.pos(+radius, +radius, -radius).color(255, 0, 0, 255).endVertex()
        buffer.pos(+radius, +radius, +radius).color(255, 0, 0, 255).endVertex()
        buffer.pos(-radius, +radius, +radius).color(255, 0, 0, 255).endVertex()

        // left face
        buffer.pos(-radius, +radius, -radius).color(255, 0, 0, 255).endVertex()
        buffer.pos(-radius, +radius, +radius).color(255, 0, 0, 255).endVertex()
        buffer.pos(-radius, -radius, +radius).color(255, 0, 0, 255).endVertex()
        buffer.pos(-radius, -radius, -radius).color(255, 0, 0, 255).endVertex()

        // right face
        buffer.pos(+radius, +radius, -radius).color(255, 0, 0, 255).endVertex()
        buffer.pos(+radius, +radius, +radius).color(255, 0, 0, 255).endVertex()
        buffer.pos(+radius, -radius, +radius).color(255, 0, 0, 255).endVertex()
        buffer.pos(+radius, -radius, -radius).color(255, 0, 0, 255).endVertex()

        GlStateManager.disableTexture2D()
        GlStateManager.disableCull()
        GlStateManager.disableLighting()
        tess.draw()
        GlStateManager.enableLighting()
        GlStateManager.enableTexture2D()
        GlStateManager.enableCull()
        GL11.glPopMatrix()
    }

    private fun renderPoint(from: Vector3f, to: Vector3f, t: Float, path: ProjectilePath) {
        GL11.glPushMatrix()

        val mc = Minecraft.getMinecraft()
        val renderManager = mc.renderManager

        val posX = to.x * t + (1f-t) * from.x
        val posY = to.y * t + (1f-t) * from.y
        val posZ = to.z * t + (1f-t) * from.z
        GlStateManager.translate(posX, posY, posZ)
        GlStateManager.rotate(-renderManager.playerViewY, 0.0f, 1.0f, 0.0f)
        GlStateManager.rotate((if (renderManager.options.thirdPersonView == 2) -1 else 1).toFloat() * renderManager.playerViewX, 1.0f, 0.0f, 0.0f)
        GlStateManager.rotate(180.0f, 0.0f, 1.0f, 0.0f)

        path.itemDescription.render(path.projectile, Minecraft.getMinecraft().player, path)

        GL11.glPopMatrix()
    }
}