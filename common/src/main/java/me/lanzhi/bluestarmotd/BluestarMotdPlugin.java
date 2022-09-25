package me.lanzhi.bluestarmotd;

import me.lanzhi.api.Bluestar;
import me.lanzhi.api.config.YamlFile;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public final class BluestarMotdPlugin extends JavaPlugin
{
    private YamlFile config;

    @NotNull
    @Override
    public YamlFile getConfig()
    {
        return config;
    }

    @Override
    public void reloadConfig()
    {
        config.reload();
    }

    @Override
    public void saveConfig()
    {
        config.save();
    }

    @Override
    public void onEnable()
    {
        this.saveDefaultConfig();
        new File(this.getDataFolder(),"icon").mkdirs();
        config=YamlFile.loadYamlFile(new File(this.getDataFolder(),"config.yml"),
                                     true,
                                     ChatColor.GREEN+"[BluestarMotd]配置文件变更已加载");
        Bluestar.getCommandManager().registerPluginCommand("bluestarmotd",this,new MainCommand(this));
        VersionManager.versionManager.init(new InfoGetter(this));
    }

    @Override
    public void onDisable()
    {
        VersionManager.versionManager.close();
    }
}
