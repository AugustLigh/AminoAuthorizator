package org.august.aminoAuthorizator.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.august.aminoAuthorizator.managers.AuthManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.HashMap;
import java.util.Map;

public class AuthListener implements Listener {

    private final AuthManager authManager;
    private final Map<Player, Integer> authTasks = new HashMap<>();

    public AuthListener(AuthManager authManager) {
        this.authManager = authManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String code = this.authManager.generateCode(player);
        player.setWalkSpeed(0.2F);
        player.setFlySpeed(0.1F);
        sendAuthMessage(player, code);
        startAuthTimeout(player, code);
    }

    private void sendAuthMessage(Player player, String code) {
        Component responseMessage = Component.text("Введите код ")
                .color(TextColor.color(255, 0, 0))
                .append(Component.text(code+" ")
                        .color(TextColor.color(255, 255, 255))
                        .decorate(TextDecoration.BOLD))
                .append(Component.text("боту")
                        .decorate(TextDecoration.UNDERLINED)
                        .color(TextColor.color(0, 255, 0))
                        .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, "http://aminoapps.com/p/w7owwi")))
                .append(Component.text(" для авторизации")
                        .color(TextColor.color(255, 0, 0)));
        player.sendMessage(responseMessage);
    }

    private void startAuthTimeout(Player player, String code) {
        BukkitScheduler scheduler = Bukkit.getScheduler();
        final long[] remainingTime = {60}; // Тайм-аут в секундах.

        int taskId = scheduler.scheduleSyncRepeatingTask(Bukkit.getPluginManager().getPlugin("AminoAuthorizator"), () -> {
            if (remainingTime[0] <= 0) {
                // Время истекло
                player.kick(Component.text("Время для авторизации истекло!").color(TextColor.color(255, 0, 0)));
                cancelAuthTask(player);
                return;
            }

            // Каждые 10 секунд отправляется напоминание
            if (remainingTime[0] % 10 == 0) {
                sendAuthMessage(player, code);
            }

            if (authManager.isPlayerAuthorized(player)) {
                cancelAuthTask(player);
            }

            remainingTime[0]--;
        }, 0L, 20L); // Запускается сразу, повторяется каждую секунду

        authTasks.put(player, taskId);
    }

    private void cancelAuthTask(Player player) {
        Integer taskId = authTasks.remove(player);
        if (taskId != null) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        cancelAuthTask(event.getPlayer());
        authManager.removePlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!authManager.isPlayerAuthorized(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!authManager.isPlayerAuthorized(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (!authManager.isPlayerAuthorized(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!authManager.isPlayerAuthorized((Player) event.getWhoClicked())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player && !authManager.isPlayerAuthorized(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (!authManager.isPlayerAuthorized(player)) {
            Component responseMessage = Component.text("Вы должны авторизоваться для выполнения этой команды!")
                    .color(TextColor.color(255, 0, 0));
            player.sendMessage(responseMessage);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!authManager.isPlayerAuthorized(player)) {
            Component responseMessage = Component.text("Вы должны авторизоваться, чтобы писать в чат!")
                    .color(TextColor.color(255, 0, 0));
            player.sendMessage(responseMessage);
            event.setCancelled(true);  // Отменить отправку сообщения
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!authManager.isPlayerAuthorized(player)) {
            event.setCancelled(true);  // Отменить взаимодействие
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (!authManager.isPlayerAuthorized(player)) {
                event.setCancelled(true);  // Отменить урон для неавторизованного игрока
            }
        }
    }
}
