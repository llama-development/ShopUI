package net.lldv.shopui.components.managers;

import cn.nukkit.form.CustomForm;
import cn.nukkit.form.Form;
import cn.nukkit.form.SimpleForm;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.player.Player;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.protocol.bedrock.packet.PlaySoundPacket;
import net.lldv.LlamaEconomy.LlamaEconomy;
import net.lldv.shopui.components.language.Language;

import java.text.NumberFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

public class ShopManager {

    public static LinkedHashMap<String, List<String>> cachedCategory = new LinkedHashMap<>();
    public static LinkedHashMap<String, String[]> cachedShopItem = new LinkedHashMap<>();

    public static void openShop(Player player) {
        SimpleForm.SimpleFormBuilder shopForm = Form.simple()
                .title(Language.getAndReplaceNP("shop-title"))
                .content(Language.getAndReplaceNP("shop-content"));
        cachedCategory.keySet().forEach(category -> shopForm.button(category, executor -> openCategory(executor, category)));
        SimpleForm finalForm = shopForm.build();
        player.showFormWindow(finalForm);
    }

    public static void openCategory(Player player, String category) {
        SimpleForm.SimpleFormBuilder shopForm = Form.simple()
                .title(category);
        cachedCategory.get(category).forEach(item -> {
            String[] s = item.split(":");
            shopForm.button(s[0], executor -> openItemShop(executor, s[0]));
        });
        SimpleForm finalForm = shopForm.build();
        player.showFormWindow(finalForm);
    }

    public static void openItemShop(Player player, String e) {
        String[] s = cachedShopItem.get(e);
        String name = s[0];
        int itemID = Integer.parseInt(s[1]);
        int itemMeta = Integer.parseInt(s[2]);
        double price = Double.parseDouble(s[3]);
        CustomForm shopForm = Form.custom()
                .title(e)
                .input(Language.getAndReplaceNP("itembuy-info", name, price), Language.getAndReplaceNP("itembuy-amount"), "1")
                .onSubmit(((executor, customFormResponse) -> {
                    try {
                        int amount = Integer.parseInt(customFormResponse.getInput(0));
                        if (amount <= 0) {
                            player.sendMessage(Language.getAndReplace("invalid-amount"));
                            playSound(player, Sound.NOTE_BASS);
                            return;
                        }
                        sellItem(executor, name, itemID, amount, itemMeta, price);
                    } catch (NumberFormatException f) {
                        player.sendMessage(Language.getAndReplace("invalid-amount"));
                        playSound(player, Sound.NOTE_BASS);
                    }
                }))
                .build();
        player.showFormWindow(shopForm);
    }

    public static void sellItem(Player player, String name, int itemID, int amount, int itemMeta, double price) {
        if (LlamaEconomy.getAPI().getMoney(player) >= price) {
            Item finalItem = Item.get(itemID, itemMeta, amount);
            if (player.getInventory().canAddItem(finalItem)) {
                player.getInventory().addItem(finalItem);
                LlamaEconomy.getAPI().reduceMoney(player, price);
                player.sendMessage(Language.getAndReplace("item-bought", name, amount, formatDouble(price)));
                playSound(player, Sound.NOTE_HARP);
            } else {
                player.sendMessage(Language.getAndReplace("inventory-full"));
                playSound(player, Sound.NOTE_BASS);
            }
        } else {
            player.sendMessage(Language.getAndReplace("no-money"));
            playSound(player, Sound.NOTE_BASS);
        }
    }

    public static String formatDouble(double d) {
        NumberFormat nf = NumberFormat.getInstance(new Locale("en", "EN"));
        return nf.format(d);
    }

    private static void playSound(Player player, Sound sound) {
        PlaySoundPacket packet = new PlaySoundPacket();
        packet.setSound(sound.getSound());
        packet.setPosition(Vector3f.from(new Double(player.getLocation().getX()).intValue(), new Double(player.getLocation().getY()).intValue(), new Double(player.getLocation().getZ()).intValue()));
        packet.setVolume(1.0F);
        packet.setPitch(1.0F);
        player.sendPacket(packet);
    }
}
