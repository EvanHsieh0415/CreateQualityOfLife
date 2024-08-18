package fr.iglee42.createqualityoflife;

import com.jozufozu.flywheel.core.PartialModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.equipment.armor.BacktankUtil;
import com.simibubi.create.foundation.particle.AirParticleData;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import com.simibubi.create.foundation.utility.AnimationTickHolder;
import fr.iglee42.createqualityoflife.client.GoggleArmorLayer;
import fr.iglee42.createqualityoflife.client.ShadowRadianceFirstPersonRenderer;
import fr.iglee42.createqualityoflife.items.ShadowRadianceChestplate;
import fr.iglee42.createqualityoflife.registries.ModPartialModels;
import fr.iglee42.createqualityoflife.registries.ModSprites;
import fr.iglee42.createqualityoflife.utils.CommonKeysHandler;
import fr.iglee42.createqualityoflife.utils.KeyBindManager;
import fr.iglee42.createqualityoflife.utils.Pos3D;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.Random;

public class CreateQOLClient {

    public static void onCtorClient(IEventBus modEventBus, IEventBus forgeEventBus) {
        ModPartialModels.init();
        //if (CreateCasing.isExtendedCogsLoaded())CreateExtendedCogwheelsPartials.init();

        modEventBus.addListener(CreateQOLClient::clientInit);
        modEventBus.addListener(CreateQOLClient::addEntityRendererLayers);
        modEventBus.addListener(CreateQOLClient::registerKeys);

        forgeEventBus.addListener(CreateQOLClient::onClientTick);

    }
    public static void registerKeys(RegisterKeyMappingsEvent event){
        event.register(KeyBindManager.FANS_KEY);
        event.register(KeyBindManager.HOVER_KEY);
    }

    public static void clientInit(final FMLClientSetupEvent event) {
        new ModSprites();
        //MinecraftForge.EVENT_BUS.register(new KeyBindManager());
        //ModPonderTags.register();
        //PonderIndex.register();

    }


    public static void showPropellers(BlockState renderedState, int light, PoseStack ms, MultiBufferSource buffer, RenderType renderType, LevelAccessor level) {
        PartialModel partial = (AnimationTickHolder.getRenderTime(level)) % 10 >= 5 ? ModPartialModels.SHADOW_RADIANCE_CHESTPLATE_PROPELLERS : ModPartialModels.SHADOW_RADIANCE_CHESTPLATE_PROPELLERS_ALT;
        SuperByteBuffer propellers = CachedBufferer.partial(partial,renderedState);
        propellers
                .forEntityRender()
                .light(light)
                .renderInto(ms, buffer.getBuffer(renderType));
    }

    public static void addEntityRendererLayers(EntityRenderersEvent.AddLayers event){
        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();

        GoggleArmorLayer.registerOnAll(dispatcher);
    }

    public static void onClientTick(TickEvent.ClientTickEvent event){
        ShadowRadianceFirstPersonRenderer.clientTick();
        Minecraft minecraft = Minecraft.getInstance();
        if (event.phase == TickEvent.Phase.END) {
            if (minecraft.player != null && minecraft.level != null) {
                if (!minecraft.isPaused() && !minecraft.player.isSpectator()) {
                    ItemStack chest = minecraft.player.getItemBySlot(EquipmentSlot.CHEST);
                    Item item = chest.getItem();
                    if ((!chest.isEmpty() && item instanceof ShadowRadianceChestplate && isFlying(minecraft.player) && !minecraft.player.isCreative())) {
                        if (minecraft.options.particles().get() != ParticleStatus.MINIMAL) {
                            showJetpackParticles(minecraft);
                        }
                        // Play sounds:
                        //if (SimplyJetpacksConfig.enableJetpackSounds.get() && !JetpackSound.playing(minecraft.player.getId())) {
                        //    minecraft.getSoundManager().play(new JetpackSound(minecraft.player));
                        //}
                    }
                }
            }
        }
    }
    private static void showJetpackParticles(Minecraft minecraft) {
        Player player = minecraft.player;
        Random rand = new Random();
        float random = (rand.nextFloat() - 0.5F) * 0.1F;
        double[] sneakBonus = player.isCrouching() ? new double[]{-0.30, -0.10} : new double[]{0, 0};
        Pos3D playerPos = new Pos3D(player).translate(0, 1.5, 0);
        Pos3D vCenter = new Pos3D((rand.nextFloat() - 0.5F) * 0.25F, -0.90 + sneakBonus[1], -0.5 + sneakBonus[0]).rotate(player.yBodyRot, 0);
        Pos3D v = playerPos.translate(vCenter).translate(new Pos3D(player.getDeltaMovement()));
        ParticleOptions particle = player.isUnderWater() ? ParticleTypes.BUBBLE : new AirParticleData(0,0.002F);
        minecraft.particleEngine.createParticle(particle, v.x, v.y, v.z, random, -0.2D, random);
    }

    public static boolean isFlying(Player player) {
        ItemStack stack = player.getItemBySlot(EquipmentSlot.CHEST);
        if (!stack.isEmpty()) {
            Item item = stack.getItem();
            if (item instanceof ShadowRadianceChestplate) {
                if (ShadowRadianceChestplate.isFansEnable(stack) && !BacktankUtil.getAllWithAir(player).isEmpty()) {
                    if (ShadowRadianceChestplate.isHoverEnable(stack)) {
                        return !player.onGround();
                    } else {
                        return CommonKeysHandler.isHoldingUp(player);
                    }
                }
            }
        }
        return false;
    }
}