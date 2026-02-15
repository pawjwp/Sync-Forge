package net.pawjwp.sync.common.compat.thirst;

import net.minecraftforge.fml.ModList;
import net.pawjwp.sync.api.shell.ShellStateComponentFactoryRegistry;

// Registers the ThirstShellStateComponent if the Diet mod is loaded.
public class ThirstCompat {
    public static void init() {
        if (ModList.get().isLoaded("thirst")) {
            ShellStateComponentFactoryRegistry.getInstance().register(
                ThirstShellStateComponent::new,
                player -> new ThirstShellStateComponent(player)
            );
        }
    }
}
