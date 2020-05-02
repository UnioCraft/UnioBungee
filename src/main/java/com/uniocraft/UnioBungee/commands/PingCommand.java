package com.uniocraft.UnioBungee.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class PingCommand extends Command {

    public PingCommand(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            sender.sendMessage(ChatColor.RED + "Sadece oyuncular ping komutunu kullanabilir.");
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer) sender;
        if (!player.hasPermission("UnioPing.use")) {
            sender.sendMessage(ChatColor.RED + "Bunu yapabilmek için izniniz yok!");
            return;
        }
        int Ping = player.getPing();

        if (args.length < 1) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2[&bUnioCraft&2] &2Pinginiz: &6" + Ping + " &2ms."));
            return;
        }
        if (args.length == 1) {
            if (!player.hasPermission("UnioPing.usedouble")) {
                sender.sendMessage(ChatColor.RED + "Bunu yapabilmek için izniniz yok!");
                return;
            }
            ProxiedPlayer user = ProxyServer.getInstance().getPlayer(args[0]);
            int PingTarget = user.getPing();
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2[&bUnioCraft&2] &6" + user.getName() + "&2 isimli oyuncunun pingi: &6" + PingTarget + " &2ms."));
        } else {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2[&bUnioCraft&2] &cHatalı bir komut girdiniz."));
        }

    }
}