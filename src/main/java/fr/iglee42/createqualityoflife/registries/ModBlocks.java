package fr.iglee42.createqualityoflife.registries;

import com.simibubi.create.AllItems;
import com.simibubi.create.AllTags;
import com.simibubi.create.Create;
import com.simibubi.create.content.equipment.armor.BacktankBlock;
import com.simibubi.create.content.kinetics.BlockStressDefaults;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltGenerator;
import com.simibubi.create.content.kinetics.belt.BeltModel;
import com.simibubi.create.content.kinetics.saw.SawGenerator;
import com.simibubi.create.content.kinetics.saw.SawMovementBehaviour;
import com.simibubi.create.content.redstone.displayLink.source.ItemNameDisplaySource;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.BuilderTransformers;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import fr.iglee42.createqualityoflife.CreateQOL;
import fr.iglee42.createqualityoflife.blocks.*;
import fr.iglee42.createqualityoflife.registries.generators.FunneledBeltGenerator;
import fr.iglee42.createqualityoflife.registries.generators.SingleBeltGenerator;
import fr.iglee42.createqualityoflife.utils.Features;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.MapColor;

import static com.simibubi.create.AllMovementBehaviours.movementBehaviour;
import static com.simibubi.create.content.redstone.displayLink.AllDisplayBehaviours.assignDataBehaviour;
import static com.simibubi.create.foundation.data.ModelGen.customItemModel;
import static com.simibubi.create.foundation.data.TagGen.axeOrPickaxe;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;
import static fr.iglee42.createqualityoflife.CreateQOL.REGISTRATE;

public class ModBlocks {

    static {
        REGISTRATE.setCreativeTab(ModCreativeModeTabs.MAIN_TAB);
    }

    public static BlockEntry<InventoryLinkerBlock> INVENTORY_LINKER = REGISTRATE.block("inventory_linker", InventoryLinkerBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.METAL))
            .properties(BlockBehaviour.Properties::noOcclusion)
            .transform(pickaxeOnly())
            //.onRegisterAfter(Registry.ITEM_REGISTRY,v-> ItemDescription.useKey(v,"block.createqol.inventory_linker"))
            .blockstate((c, p) -> p.simpleBlock(c.getEntry(), AssetLookup.partialBaseModel(c, p)))
            .transform(BlockStressDefaults.setImpact(8.0))
            .addLayer(()-> RenderType::cutoutMipped)
            .item()
            .properties(p->p.rarity(Rarity.UNCOMMON))
            .transform(customItemModel())
            .register();

    public static final BlockEntry<ShadowRadianceBacktankBlock> SHADOW_RADIANCE_CHESTPLATE =
            REGISTRATE.block("shadow_radiance_chestplate", ShadowRadianceBacktankBlock::new)
                    .initialProperties(SharedProperties::netheriteMetal)
                    .transform(BuilderTransformers.backtank(ModItems.SHADOW_RADIANCE_CHESTPLATE::get))
                    .register();

    public static BlockEntry<FunneledBeltBlock> FUNNELED_BELT = REGISTRATE.block("funneled_belt", FunneledBeltBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(p -> p.mapColor(MapColor.METAL))
            .properties(BlockBehaviour.Properties::noOcclusion)
            .transform(pickaxeOnly())
            //.onRegisterAfter(Registry.ITEM_REGISTRY,v-> ItemDescription.useKey(v,"block.createqol.inventory_linker"))
            .blockstate(new FunneledBeltGenerator()::generate)
            .transform(BlockStressDefaults.setNoImpact())
            .addLayer(()-> RenderType::cutoutMipped)
            .item()
            .properties(p->p.rarity(Rarity.UNCOMMON))
            .transform(customItemModel())
            .register();

    public static final BlockEntry<SingleBeltBlock> SINGLE_BELT = REGISTRATE.block("single_belt", SingleBeltBlock::new)
            .initialProperties(SharedProperties::stone)
            .properties(BlockBehaviour.Properties::noOcclusion)
            .addLayer(() -> RenderType::cutoutMipped)
            .transform(axeOrPickaxe())
            .blockstate(new SingleBeltGenerator()::generate)
            .transform(BlockStressDefaults.setImpact(0))
            .onRegister(assignDataBehaviour(new ItemNameDisplaySource(), "combine_item_names"))
            .onRegister(CreateRegistrate.blockModel(() -> BeltModel::new))
            .item()
            .transform(customItemModel())
            .register();




    public static BlockEntry<ChippedSawBlock>
            ALCHEMY_SAW = createChippedSaw("alchemy"),
            BOTANIST_SAW = createChippedSaw("botanist"),
            CARPENTERS_SAW = createChippedSaw("carpenters"),
            GLASSBLOWER_SAW = createChippedSaw("glassblower"),
            LOOM_SAW = createChippedSaw("loom"),
            MASON_SAW = createChippedSaw("mason"),
            TINKERING_SAW = createChippedSaw("tinkering");


    private static BlockEntry<ChippedSawBlock> createChippedSaw(String name){
        return REGISTRATE.block(name +"_saw", ChippedSawBlock::new)
                .initialProperties(SharedProperties::stone)
                .addLayer(() -> RenderType::cutoutMipped)
                .properties(p -> p.mapColor(MapColor.PODZOL))
                .transform(axeOrPickaxe())
                //.blockstate(new SawGenerator()::generate)
                .transform(BlockStressDefaults.setImpact(4.0))
                .addLayer(() -> RenderType::cutoutMipped)
                .item()
                .tag(AllTags.AllItemTags.CONTRAPTION_CONTROLLED.tag)
                .transform(customItemModel())
                .register();
    }
    public static void register(){
    }
}
