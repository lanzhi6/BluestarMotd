package me.lanzhi.bluestarmotd;

import me.lanzhi.bluestarmotd.api.PlayerListMode;
import org.bukkit.util.CachedServerIcon;

import java.util.List;

public final class ServerInfo
{
    private CachedServerIcon icon;
    private String motd;
    private String serverName;
    private boolean serverVersion;
    private int numPlayer;
    private int maxPlayer;
    private List<String> players;
    private int maxLines;
    private PlayerListMode mode;

    public ServerInfo(CachedServerIcon icon,String motd,String serverName,boolean serverVersion,int numPlayer,int maxPlayer,List<String> players,int maxLines,PlayerListMode mode)
    {
        this.icon=icon;
        this.motd=motd;
        this.serverName=serverName;
        this.serverVersion=serverVersion;
        this.numPlayer=numPlayer;
        this.maxPlayer=maxPlayer;
        this.players=players;
        this.maxLines=maxLines;
        this.mode=mode;
    }

    public int getMaxLines()
    {
        return maxLines;
    }

    public void setMaxLines(int maxLines)
    {
        this.maxLines=maxLines;
    }

    public PlayerListMode getMode()
    {
        return mode;
    }

    public void setMode(PlayerListMode mode)
    {
        this.mode=mode;
    }

    public CachedServerIcon getIcon()
    {
        return icon;
    }

    public void setIcon(CachedServerIcon icon)
    {
        this.icon=icon;
    }

    public String getMotd()
    {
        return motd;
    }

    public void setMotd(String motd)
    {
        this.motd=motd;
    }

    public String getServerName()
    {
        return serverName;
    }

    public void setServerName(String serverName)
    {
        this.serverName=serverName;
    }

    public boolean getServerVersion()
    {
        return serverVersion;
    }

    public void setServerVersion(boolean serverVersion)
    {
        this.serverVersion=serverVersion;
    }

    public int getNumPlayer()
    {
        return numPlayer;
    }

    public void setNumPlayer(int numPlayer)
    {
        this.numPlayer=numPlayer;
    }

    public int getMaxPlayer()
    {
        return maxPlayer;
    }

    public void setMaxPlayer(int maxPlayer)
    {
        this.maxPlayer=maxPlayer;
    }

    public List<String> getPlayers()
    {
        return players;
    }

    public void setPlayers(List<String> players)
    {
        this.players=players;
    }

    public void addPlayer(String player)
    {
        players.add(player);
    }

    public void addPlayer(int index,String player)
    {
        players.add(index,player);
    }

    public boolean removePlayer(String player)
    {
        return players.remove(player);
    }

    public boolean containsPlayer(String player)
    {
        return players.contains(player);
    }
}
