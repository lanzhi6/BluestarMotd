package me.lanzhi.bluestarmotd.nms;

import com.mojang.authlib.GameProfile;
import me.lanzhi.bluestarmotd.Accessor;
import me.lanzhi.bluestarmotd.InfoGetter;
import me.lanzhi.bluestarmotd.ServerInfo;
import me.lanzhi.bluestarmotd.VersionManager;
import net.minecraft.server.v1_8_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.libs.com.google.gson.*;
import org.bukkit.craftbukkit.v1_8_R1.util.CraftChatMessage;
import org.bukkit.craftbukkit.v1_8_R1.util.CraftIconCache;
import org.bukkit.util.CachedServerIcon;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.UUID;

public class v1_8_R1 extends VersionManager
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
        v1_8_R1.infoGetter=infoGetter;
        GsonBuilder gsonBuilder=new GsonBuilder();
        gsonBuilder.registerTypeAdapter(ServerPingServerData.class,new ServerDataSerializer())
                   .registerTypeAdapter(ServerPingPlayerSample.class,new PlayerSampleSerializer())
                   .registerTypeAdapter(ServerPing.class,new ServerPingSerializer())
                   .registerTypeHierarchyAdapter(IChatBaseComponent.class,new ChatSerializer())
                   .registerTypeHierarchyAdapter(ChatModifier.class,new ChatModifierSerializer())
                   .registerTypeAdapterFactory(new ChatTypeAdapterFactory())
                   .create();
        Gson gson=gsonBuilder.create();
        setGson(gson);

    }

    @Override
    public void close()
    {
        GsonBuilder gsonBuilder=new GsonBuilder();
        gsonBuilder.registerTypeAdapter(ServerPingServerData.class,new ServerDataSerializer())
                   .registerTypeAdapter(ServerPingPlayerSample.class,new PlayerSampleSerializer())
                   .registerTypeAdapter(ServerPing.class,new net.minecraft.server.v1_8_R1.ServerPingSerializer())
                   .registerTypeHierarchyAdapter(IChatBaseComponent.class,new ChatSerializer())
                   .registerTypeHierarchyAdapter(ChatModifier.class,new ChatModifierSerializer())
                   .registerTypeAdapterFactory(new ChatTypeAdapterFactory());
        Gson gson=gsonBuilder.create();
        setGson(gson);
    }

    final class ServerPingSerializer extends net.minecraft.server.v1_8_R1.ServerPingSerializer
    {
        public JsonElement serialize(Object o,Type type,JsonSerializationContext jsonSerializationContext)
        {
            ServerPingSerializer serializer=new ServerPingSerializer();
            return serializer.serialize(parseServerPing(),type,jsonSerializationContext);
        }

        public JsonElement serialize(ServerPing serverPing,Type type,JsonSerializationContext jsonSerializationContext)
        {
            ServerPingSerializer serializer=new ServerPingSerializer();
            return serializer.serialize(parseServerPing(),type,jsonSerializationContext);
        }
    }

    private static final class PlayerSampleSerializer implements JsonDeserializer<ServerPingPlayerSample>, JsonSerializer<ServerPingPlayerSample>
    {
        public ServerPingPlayerSample deserialize(JsonElement param2JsonElement,Type param2Type,JsonDeserializationContext param2JsonDeserializationContext) throws JsonParseException
        {
            JsonObject jsonObject=ChatDeserializer.l(param2JsonElement,"players");
            ServerPingPlayerSample serverPingPlayerSample=new ServerPingPlayerSample(
                    ChatDeserializer.m(jsonObject,"max"),
                    ChatDeserializer.m(jsonObject,"online"));
            if (ChatDeserializer.d(jsonObject,"sample"))
            {
                JsonArray jsonArray=ChatDeserializer.t(jsonObject,"sample");
                if (jsonArray.size()>0)
                {
                    GameProfile[] arrayOfGameProfile=new GameProfile[jsonArray.size()];
                    for (byte b=0;b<arrayOfGameProfile.length;b++)
                    {
                        JsonObject jsonObject1=ChatDeserializer.l(jsonArray.get(b),"player["+b+"]");
                        String str=ChatDeserializer.h(jsonObject1,"id");
                        arrayOfGameProfile[b]=new GameProfile(UUID.fromString(str),
                                                              ChatDeserializer.h(jsonObject1,"name"));
                    }
                    serverPingPlayerSample.a(arrayOfGameProfile);
                }
            }
            return serverPingPlayerSample;
        }

        public JsonElement serialize(ServerPingPlayerSample param2ServerPingPlayerSample,Type param2Type,JsonSerializationContext param2JsonSerializationContext)
        {
            JsonObject jsonObject=new JsonObject();
            jsonObject.addProperty("max",Integer.valueOf(param2ServerPingPlayerSample.a()));
            jsonObject.addProperty("online",Integer.valueOf(param2ServerPingPlayerSample.b()));
            if (param2ServerPingPlayerSample.c()!=null&&(param2ServerPingPlayerSample.c()).length>0)
            {
                JsonArray jsonArray=new JsonArray();
                for (byte b=0;b<(param2ServerPingPlayerSample.c()).length;b++)
                {
                    JsonObject jsonObject1=new JsonObject();
                    UUID uUID=param2ServerPingPlayerSample.c()[b].getId();
                    jsonObject1.addProperty("id",(uUID==null)?"":uUID.toString());
                    jsonObject1.addProperty("name",param2ServerPingPlayerSample.c()[b].getName());
                    jsonArray.add((JsonElement) jsonObject1);
                }
                jsonObject.add("sample",(JsonElement) jsonArray);
            }
            return (JsonElement) jsonObject;
        }
    }

    private final static class ServerDataSerializer implements JsonDeserializer<ServerPingServerData>, JsonSerializer<ServerPingServerData>
    {
        public ServerPingServerData deserialize(JsonElement param2JsonElement,Type param2Type,JsonDeserializationContext param2JsonDeserializationContext) throws JsonParseException
        {
            JsonObject jsonObject=ChatDeserializer.l(param2JsonElement,"version");
            return new ServerPingServerData(ChatDeserializer.h(jsonObject,"name"),
                                             ChatDeserializer.m(jsonObject,"protocol"));
        }

        public JsonElement serialize(ServerPingServerData param2ServerData,Type param2Type,JsonSerializationContext param2JsonSerializationContext)
        {
            JsonObject jsonObject=new JsonObject();
            jsonObject.addProperty("name",param2ServerData.a());
            jsonObject.addProperty("protocol",Integer.valueOf(param2ServerData.b()));
            return (JsonElement) jsonObject;
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
        ServerPingPlayerSample playerSample=new ServerPingPlayerSample(info.getMaxPlayer(),
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
        ping.setServerInfo(new ServerPingServerData(info.getServerName()!=null?info.getServerName():serverName,
                                                     info.getServerVersion()?-1:server.aE().c().b()));
        return ping;
    }
}
