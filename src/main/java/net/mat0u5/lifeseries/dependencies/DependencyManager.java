package net.mat0u5.lifeseries.dependencies;

import net.fabricmc.loader.api.FabricLoader;
import net.mat0u5.lifeseries.utils.other.TextUtils;
import net.mat0u5.lifeseries.utils.player.PlayerUtils;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class DependencyManager {

    public static boolean blockbenchImportLibraryLoaded() {
        return isModLoaded("bil");
    }

    public static boolean polymerLoaded() {
        return isModLoaded("polymer-bundled");
    }

    public static boolean voicechatLoaded() {
        return isModLoaded("voicechat");
    }

    public static boolean flashbackLoaded() {
        return isModLoaded("flashback");
    }

    public static boolean wildLifeModsLoaded() {
        return blockbenchImportLibraryLoaded() && polymerLoaded();
    }

    public static boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    public static boolean checkWildLifeDependencies() {
        if (!polymerLoaded()) {
            Text text = TextUtils.format("§cYou must install the {} §cto play Wild Life.", TextUtils.clickableText("Polymer mod", TextUtils.openURLClickEvent("https://modrinth.com/mod/polymer")));
            PlayerUtils.broadcastMessage(text);
        }
        if (!blockbenchImportLibraryLoaded()) {
            Text text = TextUtils.format("§cYou must install the {} §cto play Wild Life.", TextUtils.clickableText("Blockbench Import Library mod", TextUtils.openURLClickEvent("https://modrinth.com/mod/blockbench-import-library")));
            PlayerUtils.broadcastMessage(text);
        }
        return wildLifeModsLoaded();
    }
}
