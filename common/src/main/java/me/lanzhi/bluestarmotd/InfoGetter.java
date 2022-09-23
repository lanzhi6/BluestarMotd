package me.lanzhi.bluestarmotd;

import me.clip.placeholderapi.PlaceholderAPI;
import me.lanzhi.api.Bluestar;
import me.lanzhi.api.GradientColor;
import me.lanzhi.api.RGBColor;
import me.lanzhi.bluestarmotd.api.BluestarMotdPingEvent;
import me.lanzhi.bluestarmotd.api.PlayerListMode;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.CachedServerIcon;

import java.io.File;
import java.util.*;

public final class InfoGetter
{
    private final BluestarMotdPlugin plugin;

    public InfoGetter(BluestarMotdPlugin plugin)
    {
        this.plugin=plugin;
    }

    private CachedServerIcon getIcon()
    {
        try
        {
            File iconFolder=new File(plugin.getDataFolder(),"icon");
            File[] icons=(iconFolder.exists()&&iconFolder.isDirectory())?iconFolder.listFiles():new File[0];
            if (icons!=null&&icons.length>0)
            {
                return Bukkit.loadServerIcon(icons[Bluestar.getMainManager().randomInt(icons.length)]);
            }
            return null;
        }
        catch (Exception e)
        {
            return null;
        }
    }

    private String getMotd()
    {
        List<Map<?, ?>> motds=plugin.getConfig().getMapList("motd");
        if (motds.isEmpty())
        {
            return "";
        }
        Map<?, ?> motd=motds.get(Bluestar.getMainManager().randomInt(motds.size()));
        return set(motd.get("line1"))+RGBColor.setColor("&r\n")+set(motd.get("line2"));
    }

    private int getMax()
    {
        return format(Bukkit.getMaxPlayers(),plugin.getConfig().getString("player.max",""));
    }

    private int getNum()
    {
        return format(Bukkit.getOnlinePlayers().size(),plugin.getConfig().getString("player.online"));
    }

    private int format(int cnt,String f)
    {
        if (f==null||f.isEmpty())
        {
            return cnt;
        }
        switch (f.charAt(0))
        {
            case '+':
            {
                try
                {
                    return cnt+Integer.parseInt(f.substring(1));
                }
                catch (Exception e)
                {
                    return cnt;
                }
            }
            case '-':
            {
                try
                {
                    return cnt-Integer.parseInt(f.substring(1));
                }
                catch (Exception e)
                {
                    return cnt;
                }
            }
            case '*':
            {
                try
                {
                    return cnt*Integer.parseInt(f.substring(1));
                }
                catch (Exception e)
                {
                    return cnt;
                }
            }
            case '/':
            {
                try
                {
                    return cnt/Integer.parseInt(f.substring(1));
                }
                catch (Exception e)
                {
                    return cnt;
                }
            }
            case '=':
            {
                try
                {
                    return Integer.parseInt(f.substring(1));
                }
                catch (Exception e)
                {
                    return cnt;
                }
            }
            default:
                return cnt;
        }
    }

    private String getServerName()
    {
        if (!getVersion())
        {
            return null;
        }
        List<?> list=plugin.getConfig().getList("customVersionMessage");
        if (list==null||list.isEmpty())
        {
            return "";
        }
        Object o=list.get(Bluestar.getMainManager().randomInt(list.size()));
        return set(o);
    }

    private boolean getVersion()
    {
        return plugin.getConfig().getBoolean("useCustomVersionMessage");
    }

    private PlayerListMode getListMode()
    {
        if ("info".equals(plugin.getConfig().getString("player.playerListMode","")))
        {
            return PlayerListMode.DISPLAY_INFORMATION;
        }
        return PlayerListMode.DISPLAY_PLAYERS;
    }

    private List<String> getPlayers()
    {
        List<?> list=plugin.getConfig().getList("player.playerList");
        if (list==null)
        {
            list=Collections.emptyList();
        }
        List<String> players=new ArrayList<>();
        for (Object o: list)
        {
            players.add(set(o));
        }
        if ("add".equals(plugin.getConfig().getString("player.playerListMode","")))
        {
            for (Player player: Bukkit.getOnlinePlayers())
            {
                players.add(player.getName());
            }
        }
        return players;
    }

    private int getMaxLine()
    {
        return plugin.getConfig().getInt("player.playerListMaxLines");
    }

    public ServerInfo getServerInfo()
    {
        BluestarMotdPingEvent event=Bluestar.getMainManager()
                                            .callEvent(new BluestarMotdPingEvent(getIcon(),
                                                                                 getMotd(),
                                                                                 getServerName(),
                                                                                 getVersion(),
                                                                                 getNum(),
                                                                                 getMax(),
                                                                                 getPlayers(),
                                                                                 getMaxLine(),
                                                                                 getListMode()));
        ServerInfo serverInfo=new ServerInfo(event.getIcon(),
                                             event.getMotd(),
                                             event.getVersionMessage(),
                                             event.isServerVersion(),
                                             event.getNumPlayer(),
                                             event.getMaxPlayer(),
                                             event.getPlayers(),
                                             event.getMaxLines(),
                                             event.getListMode());

        if (serverInfo.getMode()==PlayerListMode.DISPLAY_PLAYERS)
        {
            Collections.shuffle(serverInfo.getPlayers());
        }

        serverInfo.setPlayers(serverInfo.getPlayers()
                                        .subList(0,Math.min(serverInfo.getPlayers().size(),serverInfo.getMaxLines())));

        return serverInfo;
    }

    private String set(Object o)
    {
        String s=Objects.toString(o);
        s=s.replaceAll("%online%",getNum()+"");
        s=s.replaceAll("%max%",getMax()+"");
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI")!=null)
        {
            return GradientColor.setColor(PlaceholderAPI.setPlaceholders(null,s));
        }
        return GradientColor.setColor(s);
    }
}
