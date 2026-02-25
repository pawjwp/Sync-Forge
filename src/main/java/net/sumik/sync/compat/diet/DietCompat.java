package net.sumik.sync.compat.diet;

import net.minecraftforge.fml.ModList;
import net.sumik.sync.api.shell.ShellStateComponentFactoryRegistry;

public class DietCompat {
    public static void init() {
        if (ModList.get().isLoaded("diet")) {
            ShellStateComponentFactoryRegistry.getInstance().register(DietShellStateComponent::new, DietShellStateComponent::fromPlayer);
        }
    }
}
