package me.lanzhi.bluestarmotd;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public abstract class VersionManager
{
    public abstract void init(InfoGetter info);

    public abstract void close();

    public static final VersionManager versionManager;

    private static final class EmptyVersionManager extends VersionManager
    {
        @Override
        public void init(InfoGetter info)
        {
        }

        @Override
        public void close()
        {
        }
    }

    static
    {
        String VersionManagerClassName=VersionManager.class.getPackage().getName()+".nms.";
        String[] packet=Bukkit.getServer().getClass().getPackage().getName().split("\\.");
        VersionManagerClassName=VersionManagerClassName+packet[packet.length-1];
        Class<?> clazz=null;
        try
        {
            clazz=Class.forName(VersionManagerClassName);
        }
        catch (ClassNotFoundException e)
        {
        }

        VersionManager manager=new EmptyVersionManager();
        if (clazz!=null)
        {
            try
            {
                Object o=clazz.getDeclaredConstructor().newInstance();
                if (o instanceof VersionManager)
                {
                    manager=(VersionManager) o;
                }
            }
            catch (Exception e)
            {
            }
        }
        versionManager=manager;
        if (versionManager instanceof EmptyVersionManager)
        {
            Bukkit.getLogger().severe(ChatColor.RED+"[BluestarMOTD]识别到未知的NMS版本: "+packet[packet.length-1]);
            Bukkit.getLogger().severe(ChatColor.RED+"[BluestarMOTD]请确认插件支持此版本,或联系开发者");
        }
        else
        {
            Bukkit.getLogger().info("[BluestarMOTD]使用NMS版本: "+packet[packet.length-1]);
        }
    }
}
