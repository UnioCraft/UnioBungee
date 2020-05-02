package com.uniocraft.UnioBungee.commands;

import com.uniocraft.UnioBungee.Main;
import com.uniocraft.UnioBungee.listeners.PartyListeners;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class RegisterPartyCommands extends Command {

    private Main plugin;

    public RegisterPartyCommands(String name, Main plugin) {
        super(name);
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if ((sender instanceof ProxiedPlayer)) {
            return;
        }

        if (ProxyServer.getInstance().getPluginManager().getPlugin("PartyAndFriends") != null) {
            new PartyListeners(plugin);
            sender.sendMessage("Party commands registered.");
        }
    }
}