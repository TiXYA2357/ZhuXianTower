package TiXYA2357.ZhuXianTower;

import cn.nukkit.Player;
import cn.nukkit.scheduler.PluginTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static TiXYA2357.ZhuXianTower.Configs.*;
import static TiXYA2357.ZhuXianTower.Main.nks;
import static TiXYA2357.ZhuXianTower.MainCommand.joinToCD;
import static TiXYA2357.ZhuXianTower.Utils.*;
import static TiXYA2357.ZhuXianTower.towerRoom.PlayerJoinInvadeRoom;

public class RepeatTask extends PluginTask<Main> {
    public RepeatTask(Main main) {
        super(main);
    }

    public static HashMap<Player,String> spawnMonsterTip= new HashMap<>();
    @Override
    public void onRun(int i) {

        joinToCD.keySet().removeIf(Objects::isNull);
        runLevels.removeIf(Utils::worldJoinPlayerIsEmpty);
        spawnMonsterTip.clear();

        if (!playerJoinRoom.isEmpty()) new ArrayList<>(playerJoinRoom.entrySet()).forEach(en -> {
            var rooms = en.getValue();
            var p = nks.getPlayer(en.getKey());
                        if (rooms.getStartTime() < 0 && rooms.getNeedKills() > 0) {
                            var count = 0;
                            for (var ent : rooms.getLevel().getEntities())
                                if (isMonsterNpc(ent) && !ent.isImmobile())
                                    count++;
                            if (count < 1) rooms.setStartTime(getStartCD());
                        }
                        if (rooms.getStartTime() > 0) {
                            rooms.setStartTime(rooms.getStartTime() - 1);
                            spawnMonsterTip.put(p,getStartTip().replace("{cd}",rooms.getStartTime() + "")
                                    .replace("{lvl}",rooms.getLv()+""));
                        } else if (rooms.getStartTime() == 0) {
                            rooms.setStartTime(-1);
                            rooms.getAllSpawnPos().forEach(s ->
                                    rooms.spawnMonster(rooms.getRandomMonster(),rooms.StrToPos(s,rooms.getRoomName())));
                        }
                        if (rooms.getAge() > 0) rooms.setAge(rooms.getAge() - 1);
                        else if (p != null) {
                            p.sendMessage("本次挑战已超时");
                            playerQuitRoom(p, false);
                        }
                        if (worldJoinPlayerIsEmpty(rooms.getRoomName())) rooms.close();
        });
        new HashMap<>(joinToCD).forEach((s, in) -> {
            if (s != null)
               s.sendActionBar(getJoinTip().replace("{cd}",in + ""));
            if (in > 0) joinToCD.put(s, in-1);
            else {
               if (s != null) PlayerJoinInvadeRoom(s);
               joinToCD.remove(s);}
        });
    }
}
