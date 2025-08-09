package io.github.iirontools.sigmaVanish;

import io.github.iirontools.sigmaVanish.command.VanishCommand;
import io.github.iirontools.sigmaVanish.listener.PlayerConnectionListener;
import io.github.iirontools.sigmaVanish.listener.VanishedProtectionListener;
import io.github.iirontools.sigmaVanish.manager.VanishManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;

public final class SigmaVanish extends JavaPlugin {

    private VanishManager vanishManager;

    @Override
    public void onEnable() {

        vanishManager = new VanishManager(this);

        this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {

            commands.registrar().register(VanishCommand.registerCommand(this));
            commands.registrar().register(VanishCommand.registerVanishCommandAlias(this));
        });

        getServer().getPluginManager().registerEvents(new PlayerConnectionListener(this), this);
        getServer().getPluginManager().registerEvents(new VanishedProtectionListener(vanishManager), this);
    }

    @Override
    public void onDisable() {
    }

    public VanishManager getVanishManager() {
        return vanishManager;
    }
}
