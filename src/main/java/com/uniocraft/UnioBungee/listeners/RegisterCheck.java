package com.uniocraft.UnioBungee.listeners;

import com.uniocraft.UnioBungee.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.event.PlayerHandshakeEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class RegisterCheck implements Listener {

    private Main plugin;
    public boolean checkActive = true;
    public boolean timeoutcheckActive = false;
    private Set<String> registered = new HashSet<>();

    private Map<String, Long> ipLastAttempts = new HashMap<>();
    private Map<String, Integer> attemptCounts = new HashMap<>();

    public RegisterCheck(Main plugin) {
        this.plugin = plugin;
        plugin.getProxy().getPluginManager().registerListener(plugin, this);

        plugin.getProxy().getScheduler().schedule(plugin, () -> {
            for (String IP : new HashSet<>(ipLastAttempts.keySet())) {
                if (System.currentTimeMillis() - ipLastAttempts.get(IP) > 120000) {
                    ipLastAttempts.remove(IP);
                    attemptCounts.remove(IP);
                }
            }
        }, 10, 10, TimeUnit.SECONDS);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onHandshake(PlayerHandshakeEvent event) {
        if (!timeoutcheckActive) return;
        if (event.getConnection().getAddress() == null) {
            return;
        }

        String IP = event.getConnection().getAddress().getAddress().getHostAddress();
        Long lastAttempt = ipLastAttempts.get(IP) != null ? ipLastAttempts.get(IP) : 0L;
        int attemptCount = attemptCounts.get(IP) != null ? attemptCounts.get(IP) : 0;

        if (System.currentTimeMillis() - lastAttempt >= 10000) {
            attemptCounts.put(IP, 1);
        } else {
            attemptCounts.put(IP, attemptCount + 1);
        }

        ipLastAttempts.put(IP, System.currentTimeMillis());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPreLoginEvent(PreLoginEvent event) {
        if (event.isCancelled()) return;

        String player = event.getConnection().getName();
        String IP = event.getConnection().getAddress().getAddress().getHostAddress();
        Long lastAttempt = ipLastAttempts.get(IP) != null ? ipLastAttempts.get(IP) : 0L;
        int attemptCount = attemptCounts.get(IP) != null ? attemptCounts.get(IP) : 0;

        if (registered.contains(player)) return;

        if (timeoutcheckActive) {
            if ((System.currentTimeMillis() - lastAttempt) < 10000 && attemptCount > 5) {
                event.setCancelReason(ChatColor.translateAlternateColorCodes('&', "&4&lÇok hızlı giriş yapmayı deniyorsunuz! Lütfen yavaşlayın!"));
                event.setCancelled(true);
                return;
            }
        }

        if (!checkActive) return;

        if (!plugin.sqlManager.isPlayerRegistered(player)) {
            event.setCancelReason(ChatColor.translateAlternateColorCodes('&', "&4&lSunucumuza giriş yapmak için kayıt olmalısınız! &9&lwww.uniocraft.com &4&ladresinden üye olabilirsiniz."));
            event.setCancelled(true);
        } else {
            registered.add(player);
        }
    }
}
