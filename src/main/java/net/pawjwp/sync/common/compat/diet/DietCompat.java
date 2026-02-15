package net.pawjwp.sync.common.compat.diet;

import net.minecraftforge.fml.ModList;
import net.pawjwp.sync.api.shell.ShellStateComponentFactoryRegistry;

// Registers the DietShellStateComponent if the Diet mod is loaded.
public class DietCompat {
    public static void init() {
        if (ModList.get().isLoaded("diet")) {
            ShellStateComponentFactoryRegistry.getInstance().register(
                DietShellStateComponent::new,
                player -> new DietShellStateComponent(player)
            );
        }
    }
}
