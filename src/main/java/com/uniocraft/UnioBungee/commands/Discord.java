package com.uniocraft.UnioBungee.commands;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class Discord extends Command {

    public Discord(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ProxiedPlayer oyuncu = (ProxiedPlayer) sender;
        oyuncu.sendMessage("§2-------------------------------------------------------------------------------");
        oyuncu.sendMessage("§2[§bUnioCraft§2] §aDiscord Adresimiz: §bhttps://discordapp.com/invite/3BdHDv5");
        oyuncu.sendMessage("§2-------------------------------------------------------------------------------");
    }
}