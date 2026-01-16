package net.sumik.sync.common.config;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.common.ForgeConfigSpec.LongValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.sumik.sync.api.shell.ShellPriority;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

@Mod.EventBusSubscriber(modid = "sync", bus = Mod.EventBusSubscriber.Bus.MOD)
public class SyncConfig {
    public static final SyncConfig.CommonConfig COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;
    private static SyncConfig INSTANCE = new SyncConfig();
    private static Map<String, Long> energyMap = null;
    private static Set<UUID> technobladeSet = null;

    public static SyncConfig getInstance() {
        return INSTANCE;
    }

    @SubscribeEvent
    static void onLoad(ModConfigEvent event) {
        energyMap = null;
        technobladeSet = null;
    }

    public boolean enableInstantShellConstruction() {
        return COMMON.enableInstantShellConstruction.get();
    }

    public boolean warnPlayerInsteadOfKilling() {
        return COMMON.warnPlayerInsteadOfKilling.get();
    }

    public boolean mustMaintainOriginalBody() {
        return COMMON.mustMaintainOriginalBody.get();
    }

    public float fingerstickDamage() {
        return COMMON.fingerstickDamage.get().floatValue();
    }

    public float hardcoreFingerstickDamage() {
        return COMMON.hardcoreFingerstickDamage.get().floatValue();
    }

    public String shellConstructionRequiredItem() {
        return COMMON.shellConstructionRequiredItem.get();
    }

    public int shellConstructionItemCount() {
        return COMMON.shellConstructionItemCount.get();
    }

    public boolean consumeItemInCreative() {
        return COMMON.consumeItemInCreative.get();
    }

    public String missingItemMessage() {
        return COMMON.missingItemMessage.get();
    }

    public long shellConstructorCapacity() {
        return COMMON.shellConstructorCapacity.get();
    }

    public long shellStorageCapacity() {
        return COMMON.shellStorageCapacity.get();
    }

    public long shellStorageConsumption() {
        return COMMON.shellStorageConsumption.get();
    }

    public boolean shellStorageAcceptsRedstone() {
        return COMMON.shellStorageAcceptsRedstone.get();
    }

    public int shellStorageMaxUnpoweredLifespan() {
        return COMMON.shellStorageMaxUnpoweredLifespan.get();
    }

    public List<EnergyMapEntry> energyMap() {
        if (energyMap == null) {
            energyMap = new HashMap<>();

            for (String entry : COMMON.energyMapEntries.get()) {
                String[] parts = entry.split("=");
                if (parts.length == 2) {
                    try {
                        long energy = Long.parseLong(parts[1]);
                        energyMap.put(parts[0], energy);
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }

        List<EnergyMapEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Long> entry : energyMap.entrySet()) {
            entries.add(EnergyMapEntry.of(entry.getKey(), entry.getValue()));
        }
        return entries;
    }

    public List<ShellPriorityEntry> syncPriority() {
        return List.of(new ShellPriorityEntry() {
            @Override
            public ShellPriority priority() {
                return COMMON.syncPriority.get();
            }
        });
    }

    public String wrench() {
        return COMMON.wrench.get();
    }

    public boolean updateTranslationsAutomatically() {
        return COMMON.updateTranslationsAutomatically.get();
    }

    public boolean enableShellSwitchAnimation() {
        return COMMON.enableShellSwitchAnimation.get();
    }

    public boolean enableDeathRespawnAnimation() {
        return COMMON.enableDeathRespawnAnimation.get();
    }

    public boolean enableTechnobladeEasterEgg() {
        return COMMON.enableTechnobladeEasterEgg.get();
    }

    public boolean renderTechnobladeCape() {
        return COMMON.renderTechnobladeCape.get();
    }

    public boolean allowTechnobladeAnnouncements() {
        return COMMON.allowTechnobladeAnnouncements.get();
    }

    public boolean allowTechnobladeQuotes() {
        return COMMON.allowTechnobladeQuotes.get();
    }

    public int TechnobladeQuoteDelay() {
        return COMMON.technobladeQuoteDelay.get();
    }

    public boolean isTechnoblade(UUID uuid) {
        if (technobladeSet == null) {
            technobladeSet = new HashSet<>();

            for (String uuidStr : COMMON.technobladeUuids.get()) {
                try {
                    technobladeSet.add(UUID.fromString(uuidStr));
                } catch (IllegalArgumentException ignored) {
                }
            }
        }

        return technobladeSet.contains(uuid);
    }

    public void addTechnoblade(UUID uuid) {
        List<String> current = new ArrayList<>(COMMON.technobladeUuids.get());
        String uuidStr = uuid.toString();
        if (!current.contains(uuidStr)) {
            current.add(uuidStr);
            COMMON.technobladeUuids.set(current);
            technobladeSet = null;
        }
    }

    public void removeTechnoblade(UUID uuid) {
        List<String> current = new ArrayList<>(COMMON.technobladeUuids.get());
        if (current.remove(uuid.toString())) {
            COMMON.technobladeUuids.set(current);
            technobladeSet = null;
        }
    }

    public void clearTechnobladeCache() {
        COMMON.technobladeUuids.set(new ArrayList<>());
        technobladeSet = null;
    }

    static {
        Pair<CommonConfig, ForgeConfigSpec> specPair = new Builder().configure(CommonConfig::new);
        COMMON_SPEC = specPair.getRight();
        COMMON = specPair.getLeft();
    }

    public static class CommonConfig {
        public final BooleanValue enableInstantShellConstruction;
        public final BooleanValue warnPlayerInsteadOfKilling;
        public final BooleanValue mustMaintainOriginalBody;
        public final DoubleValue fingerstickDamage;
        public final DoubleValue hardcoreFingerstickDamage;
        public final ConfigValue<String> shellConstructionRequiredItem;
        public final IntValue shellConstructionItemCount;
        public final BooleanValue consumeItemInCreative;
        public final ConfigValue<String> missingItemMessage;
        public final LongValue shellConstructorCapacity;
        public final LongValue shellStorageCapacity;
        public final LongValue shellStorageConsumption;
        public final BooleanValue shellStorageAcceptsRedstone;
        public final IntValue shellStorageMaxUnpoweredLifespan;
        public final ConfigValue<List<? extends String>> energyMapEntries;
        public final EnumValue<ShellPriority> syncPriority;
        public final ConfigValue<String> wrench;
        public final BooleanValue updateTranslationsAutomatically;
        public final BooleanValue enableTechnobladeEasterEgg;
        public final BooleanValue renderTechnobladeCape;
        public final BooleanValue allowTechnobladeAnnouncements;
        public final BooleanValue allowTechnobladeQuotes;
        public final IntValue technobladeQuoteDelay;
        public final ConfigValue<List<? extends String>> technobladeUuids;
        public final BooleanValue enableShellSwitchAnimation;
        public final BooleanValue enableDeathRespawnAnimation;

        public CommonConfig(Builder builder) {
            builder.comment("Sync Configuration").push("general");
            builder.comment("Shell Construction Settings").push("construction");
            this.enableInstantShellConstruction = builder.comment("Enable instant shell construction (creative mode-like)")
                .define("enableInstantShellConstruction", false);
            this.warnPlayerInsteadOfKilling = builder.comment("Warn player instead of killing them on sync failure").define("warnPlayerInsteadOfKilling", false);
            this.mustMaintainOriginalBody = builder.comment("If the original body must be remain alive to allow syncing").define("mustMaintainOriginalBody", false);
            this.fingerstickDamage = builder.comment("Damage dealt by fingerstick (0-100)").defineInRange("fingerstickDamage", 20.0, 0.0, 100.0);
            this.hardcoreFingerstickDamage = builder.comment("Damage dealt by fingerstick in hardcore mode (0-100)")
                .defineInRange("hardcoreFingerstickDamage", 40.0, 0.0, 100.0);
            this.shellConstructionRequiredItem = builder.comment(
                    "Item required to construct a new shell (format: 'modid:itemname', e.g., 'minecraft:ender_pearl')",
                    "Leave empty to disable item requirement"
                )
                .define("shellConstructionRequiredItem", "");
            this.shellConstructionItemCount = builder.comment("Number of items consumed when constructing a shell")
                .defineInRange("shellConstructionItemCount", 1, 1, 64);
            this.consumeItemInCreative = builder.comment("Should the required item be consumed in creative mode?").define("consumeItemInCreative", false);
            this.missingItemMessage = builder.comment("Custom error message when missing required item (use %s for item name, %d for count)")
                .define("missingItemMessage", "You need %s x%d to construct a new shell!");
            builder.pop();
            builder.comment("Shell Storage Settings").push("storage");
            this.shellConstructorCapacity = builder.comment("Energy capacity of shell constructor")
                .defineInRange("shellConstructorCapacity", 256000L, 1000L, Long.MAX_VALUE);
            this.shellStorageCapacity = builder.comment("Energy capacity of shell storage").defineInRange("shellStorageCapacity", 320L, 10L, Long.MAX_VALUE);
            this.shellStorageConsumption = builder.comment("Energy consumption per tick for shell storage")
                .defineInRange("shellStorageConsumption", 16L, 1L, 1000L);
            this.shellStorageAcceptsRedstone = builder.comment("Whether shell storage accepts redstone power").define("shellStorageAcceptsRedstone", true);
            this.shellStorageMaxUnpoweredLifespan = builder.comment("Maximum ticks shell storage can run without power")
                .defineInRange("shellStorageMaxUnpoweredLifespan", 20, 0, 1200);
            builder.pop();
            builder.comment("Energy Generation").push("energy");
            this.energyMapEntries = builder.comment("Entity energy output mapping (format: 'modid:entity=energyAmount')")
                .defineList(
                    "energyMap",
                    Arrays.asList(
                        "minecraft:chicken=2",
                        "minecraft:pig=16",
                        "minecraft:player=20",
                        "minecraft:wolf=22",
                        "minecraft:villager=25",
                        "minecraft:creeper=80",
                        "minecraft:enderman=160"
                    ),
                    obj -> obj instanceof String && ((String)obj).contains("=")
                );
            builder.pop();
            builder.comment("Gameplay Settings").push("gameplay");
            this.syncPriority = builder.comment("Priority for shell selection (NATURAL, NEAREST, or color names)")
                .defineEnum("syncPriority", ShellPriority.NATURAL);
            builder.pop();
            builder.comment("Tools").push("tools");
            this.wrench = builder.comment("Item to use as wrench (format: 'modid:item')").define("wrench", "minecraft:stick");
            builder.pop();
            builder.comment("Client Settings").push("client");
            this.updateTranslationsAutomatically = builder.comment("Automatically update translations").define("updateTranslationsAutomatically", false);
            this.enableShellSwitchAnimation = builder.comment("Enable camera animation when switching between shells").define("enableShellSwitchAnimation", true);
            this.enableDeathRespawnAnimation = builder.comment("Enable camera animation when respawning into a shell after death")
                .define("enableDeathRespawnAnimation", true);
            builder.pop();
            builder.comment("Easter Eggs").push("easter_eggs");
            this.enableTechnobladeEasterEgg = builder.comment("Enable Technoblade easter egg").define("enableTechnobladeEasterEgg", true);
            this.renderTechnobladeCape = builder.comment("Render Technoblade's cape").define("renderTechnobladeCape", false);
            this.allowTechnobladeAnnouncements = builder.comment("Allow Technoblade announcements").define("allowTechnobladeAnnouncements", true);
            this.allowTechnobladeQuotes = builder.comment("Allow Technoblade quotes").define("allowTechnobladeQuotes", true);
            this.technobladeQuoteDelay = builder.comment("Delay between Technoblade quotes (in ticks)").defineInRange("technobladeQuoteDelay", 1800, 200, 72000);
            this.technobladeUuids = builder.comment("UUIDs of players to treat as Technoblade")
                .defineList("technobladeUuids", new ArrayList<>(), obj -> obj instanceof String);
            builder.pop();
            builder.pop();
        }
    }

    public interface EnergyMapEntry {
        default String entityId() {
            return "minecraft:pig";
        }

        default long outputEnergyQuantity() {
            return 16L;
        }

        default EntityType<?> getEntityType() {
            ResourceLocation id = ResourceLocation.tryParse(this.entityId());
            return id == null ? EntityType.PIG : ForgeRegistries.ENTITY_TYPES.getValue(id);
        }

        static EnergyMapEntry of(EntityType<?> entityType, long outputEnergyQuantity) {
            return of(ForgeRegistries.ENTITY_TYPES.getKey(entityType).toString(), outputEnergyQuantity);
        }

        static EnergyMapEntry of(String id, long outputEnergyQuantity) {
            return new EnergyMapEntry() {
                @Override
                public String entityId() {
                    return id;
                }

                @Override
                public long outputEnergyQuantity() {
                    return outputEnergyQuantity;
                }
            };
        }
    }

    public interface ShellPriorityEntry {
        default ShellPriority priority() {
            return ShellPriority.NATURAL;
        }
    }
}
