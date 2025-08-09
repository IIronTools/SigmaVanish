package io.github.iirontools.sigmaVanish.listener;

import io.github.iirontools.sigmaVanish.manager.VanishManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;

public class VanishedProtectionListener implements Listener {

    private final VanishManager vanishManager;

    public VanishedProtectionListener(VanishManager vanishManager) {
        this.vanishManager = vanishManager;
    }

    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        if (vanishManager.isVanished(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPotionEffectApply(EntityPotionEffectEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (vanishManager.isVanished(player)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        if (vanishManager.isVanished(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        if (vanishManager.isVanished(player)) {
            event.setCancelled(true);
        }
    }
}
