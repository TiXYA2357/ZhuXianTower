package TiXYA2357.ZhuXianTower;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.scheduler.Task;
import com.smallaswater.littlemonster.entity.LittleNpc;
import com.smallaswater.littlemonster.entity.vanilla.VanillaNPC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static TiXYA2357.ZhuXianTower.Configs.*;
import static TiXYA2357.ZhuXianTower.Main.nks;
import static TiXYA2357.ZhuXianTower.towerRoom.PlayerJoinInvadeRoom;

public class Utils {

    public static boolean hasClazz(Class<?> clazz){
        try {
            Class.forName(clazz.getName());
            return true;
        } catch (ClassNotFoundException e) {
            return false;}
    }

    /**
     * 玩家名: 玩家的房间信息
     */
    public static HashMap<String, towerRoom> playerJoinRoom = new HashMap<>();

    /**
     * 正在运行的地图的名称的列表
     */
    public static List<String> runLevels = new ArrayList<>();

    public static int getPlayerMaxLevel(Player p){
        return playerMaxLevel.getOrDefault(p.getUniqueId()+"",0);
    }

    /**
     * @return 空的 进攻模式 房间的地图名称
     */
    public static List<String> getTowerVoidRooms(){
        var list = new ArrayList<String>();
        getTowerMaps().forEach(s -> {
            if (!runLevels.contains(s)) list.add(s);
        });
        return list;
    }

    public static void asOPCmd(Player p,String cmd){
        var flag = p.isOp(); if (!flag) p.setOp(true);
        if (!cmd.isEmpty()) nks.dispatchCommand(p, cmd
                .replace("@p", p.getName()).replaceFirst("/", ""));
        if (!flag) p.setOp(false);
    }

    public static boolean isMonsterNpc(Entity entity){
        return entity instanceof LittleNpc || entity instanceof VanillaNPC;
    }
    public static void playerQuitRoom(Player p,boolean success) {
        if (!getTowerMaps().contains(p.getLevelName())) return;
        try {
            if (p.getLevel().getPlayers().values().size() < 2) {
                for (var ent : p.getLevel().getEntities()) if (isMonsterNpc(ent)) ent.close();}//销毁NPC实体

        } catch (Exception ignore) {}

        var iRoom = playerJoinRoom.getOrDefault(p.getName(),null);

        if (iRoom != null){
            if (success) iRoom.getScuessCmds().forEach(cmds -> asOPCmd(p, cmds));
            if (p.getLevel().getPlayers().values().size() < 2) runLevels.remove(iRoom.getRoomName());
            if (success) {
                var time = (double) (System.currentTimeMillis() - iRoom.getStartTimeCur()) / 1000;
                playerUseTime.put(p.getUniqueId()+"", time);
                getPlayerConfig().set("闯塔时间", playerUseTime);
                getPlayerConfig().save();
                getPlayerConfig().reload();
                var lv = getPlayerMaxLevel(p) + 1;
                if (getPlayerMaxLevel(p) % 10 == 0 && getPlayerMaxLevel(p) > 1) nks.broadcastMessage("玩家 "
                        + p.getName() + " 成功通关第 " + getPlayerMaxLevel(p) + " 层!");
                nks.getScheduler().scheduleDelayedTask(new Task() {
                    @Override
                    public void onRun(int i) {
                        if (roomLevelInfo.containsKey(lv + "")) {
                            p.sendMessage("正在进入下一层,请稍后...");
                            PlayerJoinInvadeRoom(p);
                        } else nks.broadcastMessage("玩家 " + p.getName() + " 已通关最顶层");

                    }
                },3);
            }
        }

        playerJoinRoom.remove(p.getName());
        p.teleport(nks.getDefaultLevel().getSpawnLocation());

    }

    public static boolean worldJoinPlayerIsEmpty(String levelName){
        var ln = nks.getLevelByName(levelName);
        var list = new AtomicInteger(0);
        ln.getPlayers().values().forEach(p -> {
            if (playerJoinRoom.containsKey(p.getName())) list.set(list.get() + 1);
        });
        return list.get() < 1;
    }
}
