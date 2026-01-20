package net.sumik.sync.common.compat.curios;

import net.minecraftforge.fml.ModList;
import net.sumik.sync.api.shell.ShellStateComponentFactoryRegistry;

// Registers the CuriosShellStateComponent if the Curios mod is loaded.
public class CuriosCompat {
    public static void init() {
        if (ModList.get().isLoaded("curios")) {
            ShellStateComponentFactoryRegistry.getInstance().register(
                CuriosShellStateComponent::new,
                player -> new CuriosShellStateComponent(player)
            );
        }
    }
}
