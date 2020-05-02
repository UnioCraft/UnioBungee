package com.uniocraft.UnioBungee.listeners;

import com.uniocraft.UnioBungee.Main;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class QueueListeners implements Listener {

    private Main plugin;

    public QueueListeners(Main plugin) {
        this.plugin = plugin;
        plugin.getProxy().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onConnect(ServerConnectEvent e) {
        if (!plugin.queueManager.queueActive) return;
        e.setCancelled(plugin.queueManager.connectServer(e.getPlayer(), e.getTarget()));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDisc(PlayerDisconnectEvent e) {
        if (!plugin.queueManager.queueActive) return;
        plugin.queueManager.removePlayer(e.getPlayer());
    }
}
