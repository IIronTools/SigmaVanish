package io.github.iirontools.sigmaVanish.listener;

import io.github.iirontools.sigmaVanish.SigmaVanish;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionListener implements Listener {

    private final SigmaVanish plugin;

    private static final String PERMISSION = "dzialki.admin.vanish.see";


    public PlayerConnectionListener(SigmaVanish plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.joinMessage(null);
        Player player = event.getPlayer();
        plugin.getVanishManager().handleJoin(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.quitMessage(null);
        Player player = event.getPlayer();
        plugin.getVanishManager().handleQuit(player);
    }
}
