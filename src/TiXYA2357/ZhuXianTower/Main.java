package TiXYA2357.ZhuXianTower;

import cn.nukkit.Server;
import cn.nukkit.event.Listener;
import cn.nukkit.plugin.*;
import lombok.Getter;

import static TiXYA2357.ZhuXianTower.Configs.*;
import static TiXYA2357.ZhuXianTower.Utils.hasClazz;

public final class Main extends PluginBase implements Listener {

    @Getter
    private static String ConfigPath;
    public final static Server nks = Server.getInstance();
    @Getter
    private static Plugin plugin;
    @Getter
    private static Main main;


    @Override
    public void onEnable() {
        this.getServer().getLogger().info("ZhuXianTower is loading...\n (This is a free plugin) Author: TiXYA2357");
        this.getServer().getScheduler().scheduleRepeatingTask(this, new RepeatTask(this),20,true);
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getServer().getPluginManager().registerEvents(new ListenEvent(), this);
        this.getServer().getCommandMap().register(getCmd(), new MainCommand(getCmd()));
        if (hasClazz(TipVariable.class)) TipVariable.init();
    }

    @Override
    public void onLoad() {
        plugin = this; main = this;
        ConfigPath = this.getDataFolder().getPath();
        initConfig();
    }
    @Override
    public void onDisable() {//插件关闭时取消所有任务
        this.getServer().getScheduler().cancelAllTasks();
    }

}