package org.jglrxavpok.aiming.common

import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemBow
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.Vec3d
import org.lwjgl.util.vector.Vector3f

class ProjectilePath(val player: EntityPlayer, val projectile: ItemStack) {

    val positions = mutableListOf<Vector3f>()
    private val world = player.world
    val itemDescription = ProjectileRegistry.getDescription(projectile.item)!!

    operator fun iterator(): Iterator<Vector3f> {
        return positions.iterator()
    }

    fun compute(maxIterations: Int): ProjectilePath {
        val yaw = player.rotationYawHead
        val pitch = player.rotationPitch
        val pitchWithOffset = player.rotationPitch + itemDescription.pitchOffset(projectile, player)
        val initialVelocity = itemDescription.velocity(projectile, player)
        var motionX = -MathHelper.sin(yaw * 0.017453292f) * MathHelper.cos(pitch * 0.017453292f)
        var motionY = -MathHelper.sin(pitchWithOffset * 0.017453292f)
        var motionZ = MathHelper.cos(yaw * 0.017453292f) * MathHelper.cos(pitch * 0.017453292f)
        val length = Math.sqrt((motionX*motionX+motionY*motionY+motionZ*motionZ).toDouble()).toFloat()
        motionX /= length
        motionY /= length
        motionZ /= length
        motionX *= initialVelocity
        motionY *= initialVelocity
        motionZ *= initialVelocity

        if(player.onGround) {
            motionY += player.motionY.toFloat()
        }
        motionX += player.motionX.toFloat()
        motionZ += player.motionZ.toFloat()
        var x = player.posX.toFloat()
        var y = (player.posY+player.getEyeHeight().toDouble()-0.10000000149011612).toFloat()
        var z = player.posZ.toFloat()
        positions.clear()
        val pos = BlockPos.PooledMutableBlockPos.retain()

        val gravity = itemDescription.gravity(projectile, player)
        val drag = itemDescription.dragFactor(projectile, player)
        for(i in 0..maxIterations) {
            pos.setPos(x.toDouble(), y.toDouble(), z.toDouble())

            positions.add(Vector3f(x, y, z))

            val lastX = x
            val lastY = y
            val lastZ = z

            motionY -= gravity

            motionX *= drag
            motionZ *= drag

            x += motionX
            y += motionY
            z += motionZ

            val iblockstate = world.getBlockState(pos)

            if (iblockstate.getMaterial() !== Material.AIR) {
                val axisalignedbb = iblockstate.getCollisionBoundingBox(this.world, pos)

                if (axisalignedbb !== Block.NULL_AABB && axisalignedbb!!.offset(pos).contains(Vec3d(lastX.toDouble(), lastY.toDouble(), lastZ.toDouble()))) {
                    break // found intersection
                }
            }
        }

        positions.add(Vector3f(x, y, z))

        pos.release()
        return this
    }
}