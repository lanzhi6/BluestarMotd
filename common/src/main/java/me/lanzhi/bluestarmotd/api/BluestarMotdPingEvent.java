package me.lanzhi.bluestarmotd.api;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.util.CachedServerIcon;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class BluestarMotdPingEvent extends Event
{
    private static final HandlerList handlers=new HandlerList();
    private final PlayerListMode mode;
    private CachedServerIcon icon;
    private String motd;
    private String serverName;
    private boolean serverVersion;
    private int numPlayer;
    private int maxPlayer;
    private List<String> players;
    private int maxLines;

    public BluestarMotdPingEvent(CachedServerIcon icon,String motd,String serverName,boolean serverVersion,int numPlayer,int maxPlayer,List<String> players,int maxLines,PlayerListMode mode)
    {
        super(true);
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

    @NotNull
    public static HandlerList getHandlerList()
    {
        return handlers;
    }

    public int getMaxLines()
    {
        return maxLines;
    }

    public void setMaxLines(int maxLines)
    {
        this.maxLines=maxLines;
    }

    public PlayerListMode getListMode()
    {
        return mode;
    }

    @NotNull
    @Override
    public HandlerList getHandlers()
    {
        return handlers;
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

    public String getVersionMessage()
    {
        return serverName;
    }

    public void setVersionMessage(String message)
    {
        this.serverName=message;
    }

    public boolean isServerVersion()
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
