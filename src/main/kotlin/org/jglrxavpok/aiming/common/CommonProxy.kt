package org.jglrxavpok.aiming.common

import net.minecraft.init.Items as MCItems

open class CommonProxy {
    open fun init() {
        with(ProjectileRegistry) {
            registerProjectileType(BasicProjectileItemDescription(MCItems.SNOWBALL, drag = 0.99f, velocity = 1.5f))
            registerProjectileType(BasicProjectileItemDescription(MCItems.ENDER_PEARL, drag = 0.99f, velocity = 1.5f))
            registerProjectileType(BasicProjectileItemDescription(MCItems.EGG, drag = 0.99f, velocity = 1.5f))
            registerProjectileType(BasicProjectileItemDescription(MCItems.EXPERIENCE_BOTTLE, gravity = 0.07f, drag = 0.99f, velocity = 0.7f, pitchOffset = -20f))
            registerProjectileType(BasicProjectileItemDescription(MCItems.SPLASH_POTION, gravity = 0.05f, drag = 0.99f, velocity = 0.5f, pitchOffset = -20f))
            registerProjectileType(BasicProjectileItemDescription(MCItems.LINGERING_POTION, gravity = 0.05f, drag = 0.99f, velocity = 0.5f, pitchOffset = -20f))

            //Bow
            registerProjectileType(BowProjectileItemDescription)
        }
    }

    open fun preInit() {

    }
}