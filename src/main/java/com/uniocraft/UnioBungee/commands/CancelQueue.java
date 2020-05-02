package com.uniocraft.UnioBungee.commands;

import com.uniocraft.UnioBungee.Main;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CancelQueue extends Command {

    private Main plugin;

    public CancelQueue(String name, Main plugin) {
        super(name);
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if ((sender instanceof ProxiedPlayer)) {
            return;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("debug")) {
            sender.sendMessage("ServerInfos: ");
            for (ServerInfo serverInfo : Main.instance.queueManager.serverInfos) {
                sender.sendMessage("- " + serverInfo.getName());
            }
            sender.sendMessage(" ");
            sender.sendMessage("Joining: ");
            for (ProxiedPlayer player : Main.instance.queueManager.joining) {
                sender.sendMessage("- " + player.getName());
            }
            sender.sendMessage(" ");
            sender.sendMessage("Queue: ");
            for (ServerInfo serverInfo : Main.instance.queueManager.serverInfos) {
                sender.sendMessage(serverInfo.getName() + " Queue:");
                for (ProxiedPlayer player : Main.instance.queueManager.queue.get(serverInfo)) {
                    sender.sendMessage("- " + player.getName());
                }
                sender.sendMessage(" ");
            }
            return;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("debugtoggle")) {
            if (plugin.queueManager.debug == true) {
                plugin.queueManager.debug = false;
                sender.sendMessage("Queue disabled.");
            } else {
                plugin.queueManager.debug = true;
                sender.sendMessage("Queue activated");
            }
            return;
        }

        if (plugin.queueManager.queueActive == true) {
            plugin.queueManager.queueActive = false;
            sender.sendMessage("Queue disabled.");
        } else {
            plugin.queueManager.queueActive = true;
            sender.sendMessage("Queue activated");
        }
        return;
    }
}
