package com.uniocraft.UnioBungee.commands;

import com.uniocraft.UnioBungee.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.TimeUnit;

public class UpdateAdmin extends Command {

	private Main plugin;

	public UpdateAdmin(String name, Main plugin) {
		super(name);
		this.plugin = plugin;
	}

	@Override
	public void execute(CommandSender sender, final String[] args) {
		if ((sender instanceof ProxiedPlayer)) {
			sender.sendMessage(ChatColor.RED + "Bu komut yalnızca konsoldan kullanılabilir.");
			return;
		}

		if ( args.length < 1 )
		{
			sender.sendMessage("Lütfen argüman giriniz.");
			return;
		}
		if ( args.length > 1 ){
			ProxyServer.getInstance().getScheduler().schedule(plugin, () -> {
				String mesaj = args.length > 0 ? StringUtils.join(args, ' ', 0, args.length) : null;
				ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), mesaj);
			}, 2, TimeUnit.SECONDS);

		}else{
			sender.sendMessage("Lütfen düzgün argüman giriniz.");
		}

	}
}