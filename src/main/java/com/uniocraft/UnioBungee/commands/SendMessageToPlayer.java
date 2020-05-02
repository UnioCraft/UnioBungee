package com.uniocraft.UnioBungee.commands;

import org.apache.commons.lang3.StringUtils;

import com.uniocraft.UnioBungee.Main;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class SendMessageToPlayer extends Command {

	public SendMessageToPlayer(String name) {
		super(name);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if ((sender instanceof ProxiedPlayer)) {
			sender.sendMessage(ChatColor.RED + "Bu komut yalnızca konsoldan kullanılabilir.");
			return;
		}

		if (args.length >= 2) {
			ProxiedPlayer oyuncu = ProxyServer.getInstance().getPlayer(args[0]);
			if (oyuncu == null) return;
			String mesaj = args.length > 0 ? StringUtils.join(args, ' ', 1, args.length) : null;
			oyuncu.sendMessage(Main.prefix + ChatColor.translateAlternateColorCodes('&', mesaj));
		}
	}
}