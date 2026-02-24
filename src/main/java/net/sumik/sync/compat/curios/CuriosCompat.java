package net.sumik.sync.compat.curios;

import net.minecraftforge.fml.ModList;
import net.sumik.sync.api.shell.ShellStateComponentFactoryRegistry;

public class CuriosCompat {
    public static void init() {
        if (ModList.get().isLoaded("curios")) {
            ShellStateComponentFactoryRegistry.getInstance().register(CuriosShellStateComponent::new, CuriosShellStateComponent::fromPlayer);
        }
    }
}
