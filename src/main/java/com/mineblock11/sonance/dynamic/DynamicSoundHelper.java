package com.mineblock11.sonance.dynamic;

import com.mineblock11.sonance.api.SoundDefinition;
import com.mineblock11.sonance.config.SonanceConfig;
import com.mojang.serialization.Codec;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.SmithingScreen;
import net.minecraft.client.gui.screen.ingame.StonecutterScreen;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.screen.*;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Pair;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;

public class DynamicSoundHelper {
    protected static HashMap<String, Codec<?>> loadDirectories = new HashMap<>();
    protected static HashMap<String, ArrayList<?>> loadedDefinitions = new HashMap<>();

    protected static void declareDefinitionsToLoad(String directory, Codec<?> codec) {
        loadDirectories.put(directory, codec);
        loadedDefinitions.put(directory, new ArrayList<>());
    }

    protected static void clearDefinitions() {
        loadedDefinitions.values().forEach(ArrayList::clear);
    }

    public static void initialize() {
        declareDefinitionsToLoad("items", SoundDefinition.getCodec(Registries.ITEM.getKey()));
        declareDefinitionsToLoad("screens", SoundDefinition.getCodec(Registries.SCREEN_HANDLER.getKey()));
    }

    @Deprecated
    public static SoundEvent getScreenSound(ScreenHandler screen) {

        if(screen instanceof CraftingScreenHandler) {
            return SoundEvents.BLOCK_WOOD_HIT;
        }
        if(screen instanceof SmithingScreenHandler) {
            return SoundEvents.ENTITY_IRON_GOLEM_STEP;
        }
        if(screen instanceof AnvilScreenHandler) {
            return SoundEvents.BLOCK_ANVIL_PLACE;
        }
        if(screen instanceof SmokerScreenHandler) {
            return SoundEvents.BLOCK_CAMPFIRE_CRACKLE;
        }
        if(screen instanceof BlastFurnaceScreenHandler) {
            return SoundEvents.BLOCK_FIRE_AMBIENT;
        }
        if(screen instanceof FurnaceScreenHandler) {
                return SoundEvents.BLOCK_FIRE_AMBIENT;
        }
        if(screen instanceof LecternScreenHandler) {
            return SoundEvents.ENTITY_VILLAGER_WORK_LIBRARIAN;
        }
        if(screen instanceof StonecutterScreenHandler) {
            return SoundEvents.ENTITY_VILLAGER_WORK_MASON;
        }
        if(screen instanceof GrindstoneScreenHandler) {
            return SoundEvents.BLOCK_STONE_PLACE;
        }
        if(screen instanceof BeaconScreenHandler) {
            return SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE;
        }
        if(screen instanceof BrewingStandScreenHandler) {
            return SoundEvents.ITEM_BOTTLE_EMPTY;
        }
        if(screen instanceof LoomScreenHandler) {
            return SoundEvents.BLOCK_TRIPWIRE_DETACH;
        }
        if(screen instanceof CartographyTableScreenHandler) {
            return SoundEvents.ENTITY_VILLAGER_WORK_CARTOGRAPHER;
        }
        if(screen instanceof EnchantmentScreenHandler) {
            return SoundEvents.BLOCK_END_PORTAL_FRAME_FILL;
        }
        return SonanceConfig.get().inventoryOpenSoundEffect.fetchSoundEvent();
    }

    @Deprecated
    public static SoundEvent getItemSound(ItemStack itemStack, SoundEvent defaultSoundEvent, BlockSoundType type) {
        var item = itemStack.getItem();

        for (SoundDefinition<Item> definition : (ArrayList<SoundDefinition<Item>>) loadedDefinitions.get("items")) {
            if(definition.keys.isValid(item)) {
                return definition.soundEvent;
            }
        }

        if (item instanceof BlockItem blockItem) {
            var block = blockItem.getBlock();
            return type.getTransformer().apply(block.getSoundGroup(block.getDefaultState()));
        }

        return defaultSoundEvent;
    }

    public enum BlockSoundType {
        PLACE(BlockSoundGroup::getPlaceSound),
        HIT(BlockSoundGroup::getHitSound),
        BREAK(BlockSoundGroup::getBreakSound),
        FALL(BlockSoundGroup::getFallSound),
        STEP(BlockSoundGroup::getStepSound);

        private final Function<BlockSoundGroup, SoundEvent> transformer;

        BlockSoundType(Function<BlockSoundGroup, SoundEvent> transformer) {
            this.transformer = transformer;
        }

        public Function<BlockSoundGroup, SoundEvent> getTransformer() {
            return transformer;
        }
    }
}
