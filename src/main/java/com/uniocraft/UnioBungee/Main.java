package com.uniocraft.UnioBungee;

import com.github.games647.changeskin.bungee.ChangeSkinBungee;
import com.google.common.io.ByteStreams;
import com.uniocraft.UnioBungee.commands.*;
import com.uniocraft.UnioBungee.listeners.MainListeners;
import com.uniocraft.UnioBungee.listeners.QueueListeners;
import com.uniocraft.UnioBungee.listeners.RegisterCheck;
import com.uniocraft.UnioBungee.listeners.SkinListeners;
import com.uniocraft.UnioBungee.managers.QueueManager;
import com.uniocraft.UnioBungee.managers.SQLManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;

public class Main extends Plugin {

	public static String prefix = ChatColor.AQUA + "" + ChatColor.BOLD + "UNIOCRAFT " + ChatColor.DARK_GREEN + "->" + ChatColor.GREEN + " ";
	public Configuration config;
	public SQLManager sqlManager;
	public RegisterCheck registerCheck;
	public ChangeSkinBungee changeSkinPlugin;
	public QueueManager queueManager;
	public static Main instance;

	public void onEnable() {
		instance = this;
		setupConfig();

		sqlManager = new SQLManager(this);
		queueManager = new QueueManager(this);
		new QueueListeners(this);
		getProxy().getPluginManager().registerCommand(this, new Discord("discord"));
		getProxy().getPluginManager().registerCommand(this, new SendMessageToPlayer("sendmessagetoplayer"));
		getProxy().getPluginManager().registerCommand(this, new PingCommand("ping"));
		getProxy().getPluginManager().registerCommand(this, new UpdateAdmin("updateadmin", this));
		getProxy().getPluginManager().registerCommand(this, new VoteAlert("votealert"));

		// Debug and temporary things
		getProxy().getPluginManager().registerCommand(this, new CancelRegisterCheck("cancelregistercheck", this));
		getProxy().getPluginManager().registerCommand(this, new RegisterPartyCommands("registerpartycommands", this));
		getProxy().getPluginManager().registerCommand(this, new CancelQueue("cancelqueue", this));

		if (getProxy().getPluginManager().getPlugin("ChangeSkin") != null) {
			this.changeSkinPlugin = (ChangeSkinBungee) ProxyServer.getInstance().getPluginManager().getPlugin("ChangeSkin");
			ProxyServer.getInstance().getPluginManager().registerCommand(this, new UnioSetSkin("uniosetskin", this));
			ProxyServer.getInstance().getPluginManager().registerCommand(this, new SkinGuncelle("skinguncelle", this));
			new SkinListeners(this);
		}

		new MainListeners(this);

		this.registerCheck = new RegisterCheck(this);

		getProxy().registerChannel("Return");
		getProxy().registerChannel("WDL|INIT");
		getProxy().registerChannel("PERMISSIONREPL");
		getProxy().registerChannel("schematica");
	}

	public void onDisable() {
		getProxy().unregisterChannel("Return");
		getProxy().unregisterChannel("WDL|INIT");
		getProxy().unregisterChannel("PERMISSIONREPL");
		getProxy().unregisterChannel("schematica");
		sqlManager.onDisable();
	}

	private void setupConfig() {
		if (!getDataFolder().exists()) {
			getDataFolder().mkdir();
		}

		File configFile = new File(getDataFolder(), "config.yml");
		if (!configFile.exists()) {
			try {
				configFile.createNewFile();
				try (InputStream is = getResourceAsStream("config.yml");
						OutputStream os = new FileOutputStream(configFile)) {
					ByteStreams.copy(is, os);
				}
			} catch (IOException e) {
				throw new RuntimeException("Unable to create configuration file", e);
			}
		}
		try {
			config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
		} catch (IOException e) {
			throw new RuntimeException("Couldn't load config", e);
		}
	}

	public void saveConfig() {
		try {
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, new File(getDataFolder(), "config.yml"));
		} catch (IOException e) {
			throw new RuntimeException("Couldn't save config", e);
		}
	}
}