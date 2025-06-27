package TiXYA2357.ZhuXianTower;

import cn.nukkit.utils.Config;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static TiXYA2357.ZhuXianTower.Main.*;

public class Configs {

    private static final Config config = new Config(getConfigPath()+"/config.yml", Config.YAML);
    @Getter
    private static final Config playerConfig = new Config(getConfigPath()+"/playerDate.yml", Config.YAML);
    @Getter
    private static final Config towerConfig = new Config(getConfigPath()+"/Tower.yml", Config.YAML);

    @Getter
    private static String cmd;
    @Getter
    private static String uiTitle;
    @Getter
    private static String uiText;
    @Getter
    private static String uiButton;
    @Getter
    private static String joinTip;
    @Getter
    private static String startTip;
    @Getter
    private static int startCD;

    @Getter
    private static int JoinCD;

    @Getter
    private static List<String> towerMaps = new ArrayList<>();

    /**
     * uid: int
     */
    public static LinkedHashMap<String,Integer> playerMaxLevel = new LinkedHashMap<>();
    /**
     * uid: name
     */
    public static HashMap<String,String> playerBindUid = new HashMap<>();

    /**
     * uid: time
     */
    public static HashMap<String,Double> playerUseTime= new HashMap<>();
    public static HashMap<String,HashMap<String,Object>> roomLevelInfo= new HashMap<>();

    public static boolean initConfig() {
        if (!config.exists("指令")) config.set("指令", "zxt");
        cmd = config.getString("指令");
        if (!config.exists("UI标题")) config.set("UI标题", "ui标题");
        uiTitle = config.getString("UI标题");
        if (!config.exists("UI文本")) config.set("UI文本", "ui文本");
        uiText = config.getString("UI文本");
        if (!config.exists("UI按钮")) config.set("UI按钮", "ui按钮");
        uiButton = config.getString("UI按钮");
        if (config.exists("加入提示")) joinTip = config.getString("加入提示");
        joinTip = config.getString("加入提示");
        if (config.exists("加入等待提示")) config.set("加入等待提示", "第{lvl}层将在{cd}后开始刷怪");
        startTip = config.getString("加入等待提示");
        if (!config.exists("加入冷却时间")) config.set("加入冷却时间", 5);
        JoinCD = config.getInt("加入冷却时间");

        if (!config.exists("加入等待")) config.set("加入等待", 5);
        startCD = config.getInt("加入等待");

        if (!config.exists("诛仙塔列表")) config.set("诛仙塔列表", new ArrayList<>());
        towerMaps = config.get("诛仙塔列表", new ArrayList<>());

        var map = new HashMap<>(roomLevelInfo);
        var map2 = new HashMap<String,Object>();
        map2.put("进入指令",new ArrayList<>());
        map2.put("通关指令",new ArrayList<>());
        map2.put("刷怪点",new ArrayList<>());
        map2.put("刷怪", new ArrayList<>());
        map2.put("进攻",20);
        map2.put("时长",300);
        map.put("1",map2);

        if (!towerConfig.exists("仙塔数据")) towerConfig.set("仙塔数据", map);
        roomLevelInfo = towerConfig.get("仙塔数据",map);

        if (!playerConfig.exists("玩家最大层数")) playerConfig.set("玩家最大层数", new LinkedHashMap<>());
        playerMaxLevel = playerConfig.get("玩家最大层数",new LinkedHashMap<>());
        if (!playerConfig.exists("玩家绑定")) playerConfig.set("玩家绑定", new LinkedHashMap<>());
        playerBindUid = playerConfig.get("玩家绑定",new LinkedHashMap<>());
        if (!playerConfig.exists("闯塔时间")) playerConfig.set("闯塔时间", new LinkedHashMap<>());
        playerUseTime = playerConfig.get("闯塔时间",new LinkedHashMap<>());

        config.save();
        playerConfig.save();
        towerConfig.save();
        config.reload();
        playerConfig.reload();
        towerConfig.reload();
        return true;
    }

}
