package me.lanzhi.bluestarmotd.nms;

import com.google.gson.*;
import com.mojang.authlib.GameProfile;
import me.lanzhi.bluestarmotd.Accessor;
import me.lanzhi.bluestarmotd.InfoGetter;
import me.lanzhi.bluestarmotd.ServerInfo;
import me.lanzhi.bluestarmotd.VersionManager;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_13_R2.util.CraftChatMessage;
import org.bukkit.craftbukkit.v1_13_R2.util.CraftIconCache;
import org.bukkit.util.CachedServerIcon;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.UUID;

public class v1_13_R2 extends VersionManager
{
    private static InfoGetter infoGetter;

    private void setGson(Gson gson)
    {
        Field field;
        try
        {
            field=PacketStatusOutServerInfo.class.getDeclaredField("a");
            field.setAccessible(true);
            Accessor.getFieldAccessor(field).set(null,gson);
            field.setAccessible(false);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void init(InfoGetter infoGetter)
    {
        v1_13_R2.infoGetter=infoGetter;
        GsonBuilder gsonBuilder=new GsonBuilder();
        gsonBuilder.registerTypeAdapter(ServerPing.ServerData.class,new ServerPing.ServerData.Serializer())
                   .registerTypeAdapter(ServerPing.ServerPingPlayerSample.class,
                                        new ServerPing.ServerPingPlayerSample.Serializer())
                   .registerTypeAdapter(ServerPing.class,new ServerPingSerializer())
                   .registerTypeHierarchyAdapter(IChatBaseComponent.class,new IChatBaseComponent.ChatSerializer())
                   .registerTypeHierarchyAdapter(ChatModifier.class,new ChatModifier.ChatModifierSerializer())
                   .registerTypeAdapterFactory(new ChatTypeAdapterFactory());
        Gson gson=gsonBuilder.create();
        setGson(gson);

    }

    @Override
    public void close()
    {
        GsonBuilder gsonBuilder=new GsonBuilder();
        gsonBuilder.registerTypeAdapter(ServerPing.ServerData.class,new ServerPing.ServerData.Serializer())
                   .registerTypeAdapter(ServerPing.ServerPingPlayerSample.class,
                                        new ServerPing.ServerPingPlayerSample.Serializer())
                   .registerTypeAdapter(ServerPing.class,new ServerPing.Serializer())
                   .registerTypeHierarchyAdapter(IChatBaseComponent.class,new IChatBaseComponent.ChatSerializer())
                   .registerTypeHierarchyAdapter(ChatModifier.class,new ChatModifier.ChatModifierSerializer())
                   .registerTypeAdapterFactory(new ChatTypeAdapterFactory());
        Gson gson=gsonBuilder.create();
        setGson(gson);
    }

    final class ServerPingSerializer extends ServerPing.Serializer
    {
        @Override
        public JsonElement serialize(ServerPing serverPing,Type type,JsonSerializationContext jsonSerializationContext)
        {
            JsonSerializer<ServerPing> serializer=new ServerPing.Serializer();
            return serializer.serialize(parseServerPing(),type,jsonSerializationContext);
        }
    }

    private ServerPing parseServerPing()
    {
        ServerInfo info=infoGetter.getServerInfo();
        ServerPing ping=new ServerPing();
        ping.setMOTD(CraftChatMessage.fromString(info.getMotd(),true)[0]);
        CachedServerIcon icon=info.getIcon();
        if (icon instanceof CraftIconCache)
        {
            ping.setFavicon(((CraftIconCache) icon).value);
        }
        else if (Bukkit.getServerIcon() instanceof CraftIconCache)
        {
            ping.setFavicon(((CraftIconCache) Bukkit.getServerIcon()).value);
        }
        ServerPing.ServerPingPlayerSample playerSample=new ServerPing.ServerPingPlayerSample(info.getMaxPlayer(),
                                                                                             info.getNumPlayer());
        GameProfile[] profiles=new GameProfile[info.getPlayers().size()];
        for (int i=0;i<info.getPlayers().size();i++)
        {
            profiles[i]=new GameProfile(new UUID(0,0),info.getPlayers().get(i));
        }
        playerSample.a(profiles);
        ping.setPlayerSample(playerSample);
        MinecraftServer server=MinecraftServer.getServer();
        String serverName=server.getServerModName()+" "+server.getVersion();
        ping.setServerInfo(new ServerPing.ServerData(info.getServerName()!=null?info.getServerName():serverName,
                                                     info.getServerVersion()?-1:server.getServerPing()
                                                                                      .getServerData()
                                                                                      .getProtocolVersion()));
        return ping;
    }
}
