package me.xiaozhangup.bilibilitoper;

import com.alibaba.fastjson2.JSONObject;
import me.xiaozhangup.bilibilitoper.bilibiliapi.BGetter;
import me.xiaozhangup.bilibilitoper.data.DataMaster;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import static me.xiaozhangup.bilibilitoper.BiliBiliToper.mm;
import static me.xiaozhangup.bilibilitoper.BiliBiliToper.plugin;

public class ChatInput implements Listener {

    public static final @NotNull Component nomatch = mm.deserialize("<dark_gray>[<color:#00a1d6>哔哩</color>]</dark_gray> <yellow>视频不符合要求!</yellow>");
    public static final @NotNull Component accnomatch = mm.deserialize("<dark_gray>[<color:#00a1d6>哔哩</color>]</dark_gray> <yellow>视频发布者账号和绑定账号不一致!</yellow>");
    public static final @NotNull Component cantread = mm.deserialize("<dark_gray>[<color:#00a1d6>哔哩</color>]</dark_gray> <red>服务器遇到错误无法获取数据,请重试!</red>");
    public static final @NotNull Component posted = mm.deserialize("<dark_gray>[<color:#00a1d6>哔哩</color>]</dark_gray> <yellow>你投稿过这个视频了!</yellow>");
    public static final @NotNull Component cancel = mm.deserialize("<dark_gray>[<color:#00a1d6>哔哩</color>]</dark_gray> 已取消操作");
    public static final @NotNull Component donepost = mm.deserialize("<dark_gray>[<color:#00a1d6>哔哩</color>]</dark_gray> <yellow>你的视频提交成功</yellow>");
    public static HashMap<Player, Integer> state = new HashMap<>();
    public static HashMap<Player, Long> cool = new HashMap<>();

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String message = e.getMessage();
        Audience audience = plugin.adventure().player(p);
        if (state.get(p) != null) {
            e.setCancelled(true);
            if (message.equals("cancel")) {
                state.remove(p);
                audience.sendMessage(cancel);
                return;
            }
            switch (state.get(p)) {
                case 1 : {
                    state.remove(p);
                    DataMaster.setPlayerAccount(p, message);
                    audience.sendMessage(mm.deserialize("<dark_gray>[<color:#00a1d6>哔哩</color>]</dark_gray> 已成功绑定账号 " + message));
                    cool.put(p, System.currentTimeMillis());
                    break;
                }
                case 2 : {
                    //debug
                    //debug
                    state.remove(p);
                    if (DataMaster.getPostedVideos(p).contains(message)) {
                        audience.sendMessage(posted);
                        return;
                    }
                    JSONObject jsonObject = BGetter.getBaseJson(message);
                    if (jsonObject == null) {
                        audience.sendMessage(cantread);
                        return;
                    }
                    if (!BGetter.getPoster(jsonObject).equals(DataMaster.getNick(p))) {
                        audience.sendMessage(accnomatch);
                        return;
                    }
                    JSONObject video = BGetter.getVideo(jsonObject);
                    if (!check(video)) { //皮飞
                        audience.sendMessage(nomatch);
                        return;
                    }
                    DataMaster.addPostedVideo(p, message);
                    audience.sendMessage(donepost);
                    //todo 奖赏代码/和全服广播
                    BiliBiliToper.runReward(p);
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        Audience au = plugin.adventure().player(p);
                        au.sendMessage(mm.deserialize(""));
                        au.sendMessage(mm.deserialize("<dark_gray>[<color:#00a1d6>哔哩</color>]</dark_gray> 玩家<yellow>" + p.getName() + "</yellow>成功投稿了一次视频!"));
                        au.sendMessage(mm.deserialize("<dark_gray>[<color:#00a1d6>哔哩</color>]</dark_gray> 你可以<yellow><click:open_url:'" + "https://www.bilibili.com/video/" + message + "'>点击此处</click></yellow>前往BiliBili观看他的作品!"));
                        au.sendMessage(mm.deserialize(""));
                    });
                    break;
                }
            }
        }
    }

    public static boolean check(JSONObject video) {
        if (
                        !video.getString("tname").equals(BiliBiliToper.part) ||
                        !video.getString("desc").contains(BiliBiliToper.qqgroup) ||
                        !video.getString("desc").contains(BiliBiliToper.serverip)
        ) {
            return false;
        } else {
            final boolean[] ali = {false};
            String title = video.getString("title");
            BiliBiliToper.alias.forEach(s -> {
                if (title.contains(s)) {
                    ali[0] = true;
                }
            });
            if (ali[0]) return true;
            return video.getString("title").contains(BiliBiliToper.tname);
        }
    }

    //code
    //!video.getString("title").contains(BiliBiliToper.tname) && !video.getString("title").contains("111111"))

}
