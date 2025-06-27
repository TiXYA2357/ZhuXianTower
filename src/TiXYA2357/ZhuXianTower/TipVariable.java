package TiXYA2357.ZhuXianTower;

import cn.nukkit.Player;
import tip.utils.Api;
import tip.utils.variables.BaseVariable;

import static TiXYA2357.ZhuXianTower.Utils.*;
import static TiXYA2357.ZhuXianTower.Configs.*;

public class TipVariable extends BaseVariable {

    public TipVariable(Player player, String str) {
        super(player);
        this.string = str;
    }

    public static void init() {
        Api.registerVariables("ZhuXianTower",TipVariable.class);
    }

    @Override
    public void strReplace() {
        addStrReplaceString("{max-clevels}", getTowerMaps().size() + "");//房间总数量
        addStrReplaceString("{run-clevels}", runLevels.size()+"");//正在运行的房间数量
        addStrReplaceString("{player-max-clevels}", getPlayerMaxLevel(player)+"");//玩家通关层数
        var isIn = playerJoinRoom.containsKey(player.getName());
        addStrReplaceString("{in-room-cd}",isIn ? playerJoinRoom.get(player.getName()).getAge() + "" : "§f未开始");//闯关倒计时
        addStrReplaceString("{in-room-nk}",isIn ? playerJoinRoom.get(player.getName()).getNeedKills() + "" : "§f未开始");//剩余攻数

    }
}
