package TiXYA2357.ZhuXianTower;

import cn.nukkit.Player;
import cn.nukkit.command.*;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.handler.FormResponseHandler;
import cn.nukkit.form.window.FormWindowSimple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import static TiXYA2357.ZhuXianTower.Configs.*;
import static TiXYA2357.ZhuXianTower.Utils.*;
import static java.lang.Double.NaN;

public class MainCommand extends Command {
    public MainCommand(String name) {
        super(name, "§r§a诛仙塔主指令");
    }

    protected static HashMap<Player,Integer> joinToCD= new HashMap<>();
    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (sender instanceof Player p){
            var keys = new ArrayList<>(playerMaxLevel.keySet());
            var index = keys.contains(p.getUniqueId()+"") ? keys.indexOf(p.getUniqueId()+"") : keys.size();
            var out = switch (index){
                case 0 -> "§a1";
                case 1-> "§b2";
                case 2 -> "§c3";
                default -> "§f" + (index+1);
            };
            var form = new FormWindowSimple(getUiTitle(),getUiText());
            final String bn = getUiButton();
            form.addButton(new ElementButton(bn));
            if (p.isOp())
                form.addButton(new ElementButton("§f重载配置"));
            form.addButton(new ElementButton("§f闯塔排行\n§d当前排行第 §e" + out + " §d名"));

            form.addHandler(FormResponseHandler.withoutPlayer(ignored -> {
                if (form.wasClosed()) return;
                var bt = form.getResponse().getClickedButton().getText();
                if (bt.equals("§f重载配置")) {
                    if (initConfig()) p.sendMessage("§a插件配置已重载");
                    else p.sendMessage("§c插件配置重载失败");
                }
                if (bt.equals(bn)){
                    if (!roomLevelInfo.containsKey(getPlayerMaxLevel(p) + "")){
                        p.sendMessage("你已到达最高层,前方没有可挑战的关卡了");
                        return;
                    }
                    if (joinToCD.containsKey(p)){
                        p.sendMessage("正在等待中");
                        return;
                    }
                    if (playerJoinRoom.containsKey(p.getName())){
                        p.sendMessage("你已经在闯关中了");
                        return;
                    }
                    if (getTowerVoidRooms().isEmpty()) {
                        p.sendMessage("战场满载,请稍后再试吧~");
                        return;
                    }
                    joinToCD.put(p, getJoinCD());

                }
                if (bt.equals("§f闯塔排行\n§d当前排行第 §e" + out + " §d名")) {
                    var entries = new ArrayList<>(playerMaxLevel.entrySet());
                    entries.sort((entry1, entry2) -> Objects.equals(entry1.getValue(), entry2.getValue())
                            && playerUseTime.getOrDefault(entry1.getKey(),100000.0) <
                            playerUseTime.getOrDefault(entry2.getKey(),100000.0) ? -1 :
                            entry2.getValue().compareTo(entry1.getValue()));

                    playerMaxLevel.clear();
                    entries.forEach(entry -> playerMaxLevel.put(entry.getKey(), entry.getValue()));
                    entries.clear();
                    getPlayerConfig().set("玩家最大层数", playerMaxLevel);
                    getPlayerConfig().save();
                    getPlayerConfig().reload();
                    StringBuilder out2 = new StringBuilder();
                    for (var i = 0; i < playerMaxLevel.size(); i++){
                        String k = playerMaxLevel.keySet().toArray()[i] + "";
                        String v = playerMaxLevel.get(k) + "";
                        var name =new AtomicReference<>("佚名");
                        playerBindUid.forEach((uid,names) -> {
                            if (uid.equals(k)) name.set(names);
                        });
                       if (i < 30) switch (i){
                            case 0 -> out2.append("§fNo.§a§l1§r§f ").append(name).append("§r§f: §a已到达第§e ")
                                    .append(v).append(" §a层 §f(用时 §a").append(playerUseTime.getOrDefault(k,NaN)).append(" §f秒)\n");
                            case 1 -> out2.append("§fNo.§b§l2§r§f ").append(name).append("§r§f: §b已到达第§e ")
                                    .append(v).append(" §b层 §f(用时 §b").append(playerUseTime.getOrDefault(k,NaN)).append(" §f秒)\n");
                            case 2 -> out2.append("§fNo.§c§l3§r§f ").append(name).append("§r§f: §c已到达第§e ")
                                    .append(v).append(" §c层 §f(用时 §c").append(playerUseTime.getOrDefault(k,NaN)).append(" §f秒)\n");
                            default -> out2.append("§fNo.").append(i + 1).append(" ").append(name).append("§r§f: §f已到达第§e ")
                                    .append(v).append(" §f层 (用时 §6").append(playerUseTime.getOrDefault(k,NaN)).append(" §f秒)\n");
                        }
                    }
                    p.showFormWindow(new FormWindowSimple("§d闯塔排行",out2.toString().trim()));
                }
            }));

            p.showFormWindow(form);

        }else {
            if (args.length == 0) sender.sendMessage(getCmd()+" reload 重载插件配置");
            else if (args[0].equalsIgnoreCase("reload")) {
               if (initConfig()) sender.sendMessage("§a插件配置已重载");
               else sender.sendMessage("§c插件配置重载失败");
            }
        }
        return false;
    }
}
