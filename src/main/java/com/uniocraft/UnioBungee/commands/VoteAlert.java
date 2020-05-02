package com.uniocraft.UnioBungee.commands;

import org.apache.commons.lang3.StringUtils;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class VoteAlert extends Command {

	public VoteAlert(String name) {
		super(name);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if ((sender instanceof ProxiedPlayer)) {
			sender.sendMessage(ChatColor.RED + "Bu komut yalnızca konsoldan kullanılabilir.");
			return;
		}

		if (args.length >= 1) {
			String mesaj = StringUtils.join(args, ' ', 0, args.length);
			ProxyServer.getInstance().broadcast(ChatColor.translateAlternateColorCodes('&', mesaj));
		}
	}
}