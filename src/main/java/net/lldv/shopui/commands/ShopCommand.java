package net.lldv.shopui.commands;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.player.Player;
import net.lldv.shopui.ShopUI;
import net.lldv.shopui.components.managers.ShopManager;
import net.lldv.shopui.components.tools.Command;

public class ShopCommand extends PluginCommand<ShopUI> {

    public ShopCommand(ShopUI owner) {
        super(owner, Command.create(ShopUI.getInstance().getConfig().getString("Command.Shop"), ShopUI.getInstance().getConfig().getString("Command.ShopDescription")));
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            ShopManager.openShop(player);
        }
        return true;
    }
}
