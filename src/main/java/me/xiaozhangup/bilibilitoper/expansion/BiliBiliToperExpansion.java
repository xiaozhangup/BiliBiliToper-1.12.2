package me.xiaozhangup.bilibilitoper.expansion;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.xiaozhangup.bilibilitoper.BiliBiliToper;
import me.xiaozhangup.bilibilitoper.data.DataMaster;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * BiliBiliToper
 * me.xiaozhangup.bilibilitoper.expansion.BiliBiliToperExpansion
 *
 * @author xiaomu
 * @since 2023/1/15 4:35 PM
 */
public class BiliBiliToperExpansion extends PlaceholderExpansion {

    private final BiliBiliToper plugin;

    public BiliBiliToperExpansion(final BiliBiliToper plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return plugin.getDescription().getName().toLowerCase();
    }

    @Override
    public @NotNull String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        String request;
        switch (params.toLowerCase()) {
            case "totalvideos":
                request = String.valueOf(DataMaster.getPostedVideos(player));
                break;
            case "nick":
                request = DataMaster.getNick(player);
                break;
            default:
                request = "";
                break;
        };
        return request;
    }
}
