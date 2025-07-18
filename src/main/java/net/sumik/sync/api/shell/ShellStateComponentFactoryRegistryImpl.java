package net.sumik.sync.api.shell;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

class ShellStateComponentFactoryRegistryImpl implements ShellStateComponentFactoryRegistry {
    public static final ShellStateComponentFactoryRegistryImpl INSTANCE = new ShellStateComponentFactoryRegistryImpl();

    private final Set<ShellStateComponentFactory> factories = new HashSet<>(16);

    @Override
    public Collection<ShellStateComponentFactory> getValues() {
        return Collections.unmodifiableSet(this.factories);
    }

    @Override
    public ShellStateComponentFactory register(ShellStateComponentFactory factory) {
        this.factories.add(factory);
        return factory;
    }
}