package net.lldv.shopui;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.registry.CommandRegistry;
import net.lldv.shopui.commands.ShopCommand;
import net.lldv.shopui.components.language.Language;
import net.lldv.shopui.components.managers.ShopManager;

import java.util.ArrayList;
import java.util.List;

public class ShopUI extends PluginBase {

    private static ShopUI instance;

    @Override
    public void onLoad() {
        instance = this;
        saveDefaultConfig();
        registerCommands();
    }

    @Override
    public void onEnable() {
        Language.init();
        loadPlugin();
    }

    private void loadPlugin() {
        getConfig().getSection("ShopForm").getAll().getKeys(false).forEach(s -> {
            List<String> list = new ArrayList<>();
            ShopManager.cachedCategory.put(s, list);
            getConfig().getStringList("ShopForm." + s).forEach(e -> {
                String[] f = e.split(":");
                ShopManager.cachedShopItem.put(f[0], f);
                list.add(e);
            });
        });
    }

    private void registerCommands() {
        CommandRegistry registry = getServer().getCommandRegistry();
        registry.register(this, new ShopCommand(this));
    }

    public static ShopUI getInstance() {
        return instance;
    }
}
