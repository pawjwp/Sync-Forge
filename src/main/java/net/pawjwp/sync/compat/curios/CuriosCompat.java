package net.pawjwp.sync.compat.curios;

import net.minecraftforge.fml.ModList;
import net.pawjwp.sync.api.shell.ShellStateComponentFactoryRegistry;

public class CuriosCompat {
    public static void init() {
        if (ModList.get().isLoaded("curios")) {
            ShellStateComponentFactoryRegistry.getInstance().register(CuriosShellStateComponent::new, CuriosShellStateComponent::fromPlayer);
        }
    }
}
