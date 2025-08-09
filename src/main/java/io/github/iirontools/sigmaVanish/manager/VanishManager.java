package io.github.iirontools.sigmaVanish.manager;

import io.github.iirontools.sigmaVanish.SigmaVanish;
import io.github.iirontools.sigmalib.message.ComponentUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class VanishManager {

    private final SigmaVanish plugin;
    private final Set<UUID> vanished;

    private static final String PERMISSION_SEE = "dzialki.admin.vanish.see";


    public VanishManager(SigmaVanish plugin) {
        this.plugin = plugin;
        this.vanished = new HashSet<>();

        new BukkitRunnable() {
            @Override
            public void run() {
                for (var vanishedUUID: vanished) {
                    Player vanishedPlayer = plugin.getServer().getPlayer(vanishedUUID);
                    if (vanishedPlayer == null) {
                        // vanished.remove(vanishedUUID); Specjalnie nie usuwam, żeby ktoś mógł wejść na serwer jako vanished
                        continue;
                    }

                    vanishedPlayer.sendActionBar(Component.text("Jesteś niewidzialny dla innych graczy!", NamedTextColor.GREEN));
                }
            }
        }.runTaskTimerAsynchronously(plugin, 0, 5L);
    }

    public void vanish(Player target) {
        target.setHealth(target.getMaxHealth());
        target.setFoodLevel(20);
        target.setInvulnerable(true);
        target.getActivePotionEffects().forEach(effect -> target.removePotionEffect(effect.getType()));
        target.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, PotionEffect.INFINITE_DURATION, 255, true));
        target.setAllowFlight(true);
        target.setMetadata("vanished", new FixedMetadataValue(plugin, true));

        for (var viewer : plugin.getServer().getOnlinePlayers()) {
            if (viewer.equals(target)) continue;
            if (viewer.hasPermission("dzialki.admin.vanish.see")) continue;

            viewer.hidePlayer(plugin, target);
        }
        vanished.add(target.getUniqueId());
    }

    public void unvanish(Player target) {
        vanished.remove(target.getUniqueId());
        target.setMetadata("vanished", new FixedMetadataValue(plugin, false));
        target.setInvulnerable(false);
        target.getActivePotionEffects().forEach(effect -> target.removePotionEffect(effect.getType()));
        if (target.getGameMode() != GameMode.CREATIVE && target.getGameMode() != GameMode.SPECTATOR) {
            target.setAllowFlight(false);
        }
        target.sendActionBar(Component.empty());

        for (Player viewer : plugin.getServer().getOnlinePlayers()) {
            viewer.showPlayer(plugin, target);
        }
    }

    public void handleJoin(Player player) {

        if (vanished.contains(player.getUniqueId())) vanish(player);

        // For other players
        for (var otherPlayer : plugin.getServer().getOnlinePlayers()) {
            if (vanished.contains(player.getUniqueId())) {
                if (otherPlayer.hasPermission(PERMISSION_SEE)) {
                    otherPlayer.sendMessage(ComponentUtils.info(Component.text(player.getName(), NamedTextColor.GOLD))
                            .append(Component.text(" dołączył na serwer z ", NamedTextColor.GRAY))
                            .append(Component.text("vanishem", NamedTextColor.GOLD))
                            .append(Component.text(".", NamedTextColor.GRAY))
                    );
                }
            } else {
                otherPlayer.sendMessage(ComponentUtils.success(Component.text(player.getName(), NamedTextColor.GOLD))
                        .append(Component.text(" dołączył na serwer.", NamedTextColor.GRAY)));
            }
        }

        // For joined player
        if (player.hasPermission(PERMISSION_SEE)) return;

        for (UUID vanishedId : vanished) {
            Player vanishedPlayer = plugin.getServer().getPlayer(vanishedId);
            if (vanishedPlayer == null || vanishedPlayer.equals(player)) continue;

            player.hidePlayer(plugin, vanishedPlayer);
        }
    }

    public void handleQuit(Player player) {

        if (!vanished.contains(player.getUniqueId())) {
            for (var otherPlayer : plugin.getServer().getOnlinePlayers()) {
                otherPlayer.sendMessage(ComponentUtils.info(Component.text(player.getName(), NamedTextColor.GOLD))
                        .append(Component.text(" wyszedł z serwera.", NamedTextColor.GRAY)));
            }
        } else {
            for (var otherPlayer : plugin.getServer().getOnlinePlayers()) {
                if (otherPlayer.hasPermission(PERMISSION_SEE)) {
                    otherPlayer.sendMessage(ComponentUtils.info(Component.text(player.getName(), NamedTextColor.GOLD))
                            .append(Component.text(" wyszedł z serwera.", NamedTextColor.GRAY)));
                }
            }
        }
    }

    public boolean isVanished(Player player) {
        return vanished.contains(player.getUniqueId());
    }
}
