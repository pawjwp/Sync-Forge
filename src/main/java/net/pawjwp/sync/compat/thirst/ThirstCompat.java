package net.pawjwp.sync.compat.thirst;

import net.minecraftforge.fml.ModList;
import net.pawjwp.sync.api.shell.ShellStateComponentFactoryRegistry;

public class ThirstCompat {
    public static void init() {
        if (ModList.get().isLoaded("thirst")) {
            ShellStateComponentFactoryRegistry.getInstance().register(ThirstShellStateComponent::new, ThirstShellStateComponent::fromPlayer);
        }
    }
}
