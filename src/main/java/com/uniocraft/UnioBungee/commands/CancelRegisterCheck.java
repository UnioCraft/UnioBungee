package com.uniocraft.UnioBungee.commands;

import com.uniocraft.UnioBungee.Main;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CancelRegisterCheck extends Command {

    private Main plugin;

    public CancelRegisterCheck(String name, Main plugin) {
        super(name);
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if ((sender instanceof ProxiedPlayer)) {
            return;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("timeout")) {
            if (plugin.registerCheck.timeoutcheckActive) {
                plugin.registerCheck.timeoutcheckActive = false;
                sender.sendMessage("Timeout check disabled.");
            } else {
                plugin.registerCheck.timeoutcheckActive = true;
                sender.sendMessage("Timeout check activated");
            }
            return;
        }

        if (plugin.registerCheck.checkActive) {
            plugin.registerCheck.checkActive = false;
            sender.sendMessage("Register check disabled.");
        } else {
            plugin.registerCheck.checkActive = true;
            sender.sendMessage("Register check activated");
        }
    }
}