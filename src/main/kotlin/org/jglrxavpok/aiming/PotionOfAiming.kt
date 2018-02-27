package org.jglrxavpok.aiming

import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import org.apache.logging.log4j.Logger
import net.minecraft.init.PotionTypes
import net.minecraft.init.Items as MCItems
import net.minecraft.init.Blocks as MCBlocks
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.potion.*
import net.minecraft.util.ResourceLocation
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.brewing.BrewingRecipe
import net.minecraftforge.common.brewing.BrewingRecipeRegistry
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper
import org.jglrxavpok.aiming.common.*

@Mod(modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter", modid = PotionOfAiming.ModID, dependencies = "required-after:forgelin;",
        name = "Potion of Aiming", version = "1.0.0")
object PotionOfAiming {
    const val ModID = "potionofaiming"

    lateinit var logger: Logger

    @SidedProxy(clientSide = "org.jglrxavpok.aiming.client.Proxy", serverSide = "org.jglrxavpok.aiming.server.Proxy")
    lateinit var proxy: CommonProxy

    val aimingPotionType = PotionType("aiming", PotionEffect(AimingPotion, 6000)).setRegistryName(ResourceLocation(ModID, "aiming"))
    val network = SimpleNetworkWrapper(ModID)

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        MinecraftForge.EVENT_BUS.register(this)
        logger = event.modLog
        proxy.preInit()
    }

    @Mod.EventHandler
    fun init(event: FMLInitializationEvent) {
        proxy.init()
    }

    @SubscribeEvent
    fun registerItems(e: RegistryEvent.Register<Item>) {
        e.registry.registerAll(*Items.list.toTypedArray())
    }

    @SubscribeEvent
    fun registerPotions(e: RegistryEvent.Register<Potion>) {
        e.registry.registerAll(AimingPotion)
    }

    @SubscribeEvent
    fun registerPotionTypes(e: RegistryEvent.Register<PotionType>) {
        e.registry.registerAll(aimingPotionType)
        val mundanePotion = PotionUtils.addPotionToItemStack(ItemStack(MCItems.POTIONITEM), PotionTypes.MUNDANE)
        val mundanePotionSplash = PotionUtils.addPotionToItemStack(ItemStack(MCItems.SPLASH_POTION), PotionTypes.MUNDANE)
        val mundanePotionLingering = PotionUtils.addPotionToItemStack(ItemStack(MCItems.LINGERING_POTION), PotionTypes.MUNDANE)
        val aimingPotion = PotionUtils.addPotionToItemStack(ItemStack(MCItems.POTIONITEM), aimingPotionType)
        val aimingPotionSplash = PotionUtils.addPotionToItemStack(ItemStack(MCItems.SPLASH_POTION), aimingPotionType)
        val aimingPotionLingering = PotionUtils.addPotionToItemStack(ItemStack(MCItems.LINGERING_POTION), aimingPotionType)
        BrewingRecipeRegistry.addRecipe(mundanePotion, ItemStack(MCItems.SPECTRAL_ARROW), aimingPotion)
        BrewingRecipeRegistry.addRecipe(mundanePotionSplash, ItemStack(MCItems.SPECTRAL_ARROW), aimingPotionSplash)
        BrewingRecipeRegistry.addRecipe(mundanePotionLingering, ItemStack(MCItems.SPECTRAL_ARROW), aimingPotionLingering)
    }


}