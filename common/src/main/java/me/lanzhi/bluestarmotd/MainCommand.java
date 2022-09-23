package me.lanzhi.bluestarmotd;

import me.lanzhi.api.RGBColor;
import me.lanzhi.api.config.YamlFile;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public final class MainCommand implements CommandExecutor, TabExecutor
{
    private final YamlFile config;
    public MainCommand(BluestarMotdPlugin plugin)
    {
        this.config=plugin.getConfig();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender,@NotNull Command command,@NotNull String label,@NotNull String[] args)
    {
        if (args.length<1)
        {
            sender.sendMessage(new RGBColor("ff0000")+"未知命令");
            return true;
        }
        if ("reload".equals(args[0]))
        {
            config.reload();
        }
        else
        {
            sender.sendMessage(new RGBColor("ff0000")+"未知命令");
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender,@NotNull Command command,@NotNull String label,@NotNull String[] args)
    {
        return Collections.singletonList("reload");
    }
}
