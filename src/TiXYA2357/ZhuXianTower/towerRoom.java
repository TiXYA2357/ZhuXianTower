package TiXYA2357.ZhuXianTower;

import cn.nukkit.Player;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.scheduler.Task;
import com.smallaswater.littlemonster.LittleMonsterMainClass;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static TiXYA2357.ZhuXianTower.Configs.*;
import static TiXYA2357.ZhuXianTower.Main.nks;
import static TiXYA2357.ZhuXianTower.Utils.*;


public class towerRoom {

    @SuppressWarnings("all")
    public static void PlayerJoinInvadeRoom(Player p){
        if (runLevels.contains(p.getLevelName())) {
            p.sendMessage("你已经在挑战中,请退出重试");
            return;
        }
        var invRoom = new towerRoom();
            if (!getTowerVoidRooms().isEmpty()) {
                var roomName = getTowerVoidRooms().get(new Random().nextInt(getTowerVoidRooms().size()));
                var dl = getPlayerMaxLevel(p);
                var info = roomLevelInfo.getOrDefault(dl + "", new HashMap<>());
                    try {
                        invRoom.lv = dl;
                        invRoom.roomName = roomName;
                        invRoom.level = nks.getLevelByName(roomName);
                        invRoom.joinCmds = (List<String>) info.getOrDefault("进入指令", new ArrayList<>());
                        invRoom.scuessCmds = (List<String>) info.getOrDefault("通关指令", new ArrayList<>());
                        invRoom.Maxage = (int) info.getOrDefault("时长", 300);
                        invRoom.age = invRoom.getMaxage();
                        invRoom.NeedKills = (int) info.getOrDefault("进攻", 1);
                        invRoom.allSpawnPos = (List<String>) info.getOrDefault("刷怪点", new ArrayList<>());
                        invRoom.monsterName = (List<String>) info.getOrDefault("刷怪", new ArrayList<>());
                    } catch (Exception e) {
                        p.sendMessage("进入对局失败,请向管理员报告此错误awa");
                        nks.getLogger().error("初始化房间遇到错误:\n" + e);
                        return;
                    }
                    playerJoinRoom.put(p.getName(), invRoom);
                    runLevels.add(roomName);
                    p.sendMessage("进入对局成功,请耐心等待游戏开始!");
                    if (!getTowerMaps().contains(p.getLevelName())) p.teleport(nks.getLevelByName(roomName).getSpawnLocation());
            } else p.sendMessage("当前无空闲的对局,请稍后再试!");
            nks.getScheduler().scheduleDelayedTask(new Task() {
                @Override
                public void onRun(int i) {
                    var r = playerJoinRoom.getOrDefault( p.getName(),null);
                    if (r != null && !r.getJoinCmds().isEmpty()) r.getJoinCmds().forEach(cmds -> asOPCmd(p,cmds));
                }
            },20);
    }
    @Getter
    long startTimeCur = System.currentTimeMillis();

    towerRoom iRoom= this;

    @Getter
    Level level;

    @Getter
    String roomName;

    @Getter
    List<String> joinCmds = new ArrayList<>();

    @Getter
    List<String> scuessCmds = new ArrayList<>();

    @Getter
    List<String> allSpawnPos = new ArrayList<>();

    @Getter
    List<String> monsterName = new ArrayList<>();


    @Getter
    @Setter
    int age = 300;

    @Getter
    int Maxage = 300;

    @Getter
    int lv;

    @Getter
    @Setter
    int startTime = getStartCD();

    @Getter
    @Setter
    int NeedKills;

    @Getter
    boolean close = false;

    public void close(){
        if (!close){
            close = true;
            // 线程安全地置空room
            synchronized (this) {
                this.iRoom= null;
            }
        }
    }


    public Position StrToPos(String pos, String level){
        var strs = pos.split(":");
        var x = Double.parseDouble(strs[0]);
        var y = Double.parseDouble(strs[1]);
        var z = Double.parseDouble(strs[2]);
        return new Position(x,y,z,nks.getLevelByName(level));
    }

    public String getRandomMonster(){
        if (this.monsterName.isEmpty()) return "无";
        return this.monsterName.get(new Random().nextInt(monsterName.size()));
    }

    public void spawnMonster(String name,Position pos){
        LittleMonsterMainClass.getInstance().monsters.get(name).spawn(pos);
    }
}
