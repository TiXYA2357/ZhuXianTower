package TiXYA2357.ZhuXianTower;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDeathEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.scheduler.Task;

import static TiXYA2357.ZhuXianTower.Configs.*;
import static TiXYA2357.ZhuXianTower.Main.nks;
import static TiXYA2357.ZhuXianTower.Utils.*;

public class ListenEvent implements Listener {
    @EventHandler
    protected void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
            playerBindUid.put(player.getUniqueId() + "", player.getName());
            getPlayerConfig().set("玩家绑定", playerBindUid);
            getPlayerConfig().save(); getPlayerConfig().reload();
            if (!playerMaxLevel.containsKey(player.getUniqueId() + ""))
                playerMaxLevel.put(player.getUniqueId() + "", 0);
        playerQuitRoom(player,false);
    }

    @EventHandler
    protected void onPlayerQuit(PlayerQuitEvent e) {
        var p = e.getPlayer();
        if (runLevels.contains(p.getLevelName())) playerQuitRoom(p,false);
    }


    @EventHandler
    protected void onPlayerDeath(PlayerDeathEvent e) {
        var p = e.getEntity().getPlayer();
        if (playerJoinRoom.containsKey(p.getName())) {
                for (var ent : p.getLevel().getEntities()) if (isMonsterNpc(ent)) ent.close();
        }
    }

    @EventHandler
    protected void onPlayerTeleport(PlayerTeleportEvent e) {
        var p = e.getPlayer();
        if (runLevels.contains(p.getLevelName()) && !e.getFrom().getLevelName().equals(e.getTo().getLevelName())) playerQuitRoom(p,false);

    }
    @EventHandler
    protected void onPlayerRespawn(PlayerRespawnEvent e) {
        var p = e.getPlayer();
        if (playerJoinRoom.containsKey(p.getName()) && playerJoinRoom.get(p.getName()) != null)
            nks.getScheduler().scheduleDelayedTask(new Task() {
                @Override
                public void onRun(int i) {
                    playerQuitRoom(p, false);
                }
            },5);

    }
    @EventHandler
    protected void onEntityDeath(EntityDeathEvent e) {
        var ent = e.getEntity();
        if (ent instanceof Player)  return;
        if (runLevels.contains(ent.getLevelName()) && isMonsterNpc(ent)) {
            if (ent.isImmobile()) {
                ent.getLevel().getPlayers().values().forEach(p -> playerQuitRoom(p,false));
                return;
            }
            var de = e.getEntity().getLastDamageCause();
            if (de instanceof EntityDamageByEntityEvent) {
                if (((EntityDamageByEntityEvent) de).getDamager() instanceof Player p
                        && playerJoinRoom.containsKey(p.getName())){
                    var room = playerJoinRoom.get(p.getName());

                    room.setNeedKills(room.getNeedKills()-1);
                    if (room.getNeedKills() < 1) playerQuitRoom(p,true);

                }
            }
        }
    }
}
