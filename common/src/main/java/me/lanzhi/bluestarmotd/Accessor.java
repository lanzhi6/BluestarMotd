package me.lanzhi.bluestarmotd;

import sun.misc.Unsafe;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public final class Accessor
{
    private static final MethodHandles.Lookup LOOKUP;
    private static final MethodType STATIC_FIELD_GETTER=MethodType.methodType(Object.class);
    private static final MethodType STATIC_FIELD_SETTER=MethodType.methodType(Void.TYPE,Object.class);
    private static final MethodType VIRTUAL_FIELD_GETTER=MethodType.methodType(Object.class,Object.class);
    private static final MethodType VIRTUAL_FIELD_SETTER=MethodType.methodType(Void.TYPE,Object.class,Object.class);

    static
    {
        MethodHandles.Lookup lookup;
        try
        {
            Class<?> unsafeClass=Class.forName("sun.misc.Unsafe");
            Field theUnsafe=unsafeClass.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            Unsafe unsafe=(Unsafe) theUnsafe.get(null);
            Field trustedLookup=MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            long offset=unsafe.staticFieldOffset(trustedLookup);
            Object baseValue=unsafe.staticFieldBase(trustedLookup);
            lookup=(MethodHandles.Lookup) unsafe.getObject(baseValue,offset);
        }
        catch (Exception e)
        {
            lookup=MethodHandles.lookup();
        }
        LOOKUP=lookup;
    }

    private final Field field;
    private final boolean staticField;
    private final MethodHandle setter;
    private final MethodHandle getter;

    private Accessor(Field field,MethodHandle setter,MethodHandle getter,boolean staticField)
    {
        this.field=field;
        this.setter=setter;
        this.getter=getter;
        this.staticField=staticField;
    }

    public static <T> T invoke(Class<?> clazz,Class<T> retuenType,Object o,Object... par) throws InvocationTargetException, IllegalAccessException
    {
        Class<?>[] parClass=new Class[par.length];
        for (int i=0;i<par.length;i++)
        {
            if (par[i]!=null)
            {
                parClass[i]=par.getClass();
            }
        }
        for (Method method: clazz.getDeclaredMethods())
        {
            if (Arrays.equals(method.getParameterTypes(),parClass)&&method.getReturnType()==retuenType)
            {
                return (T) method.invoke(o,par);
            }
        }
        return null;
    }

    public static Accessor getFieldAccessor(Field field)
    {
        try
        {
            boolean staticField=Modifier.isStatic(field.getModifiers());
            MethodHandle getter;
            MethodHandle setter;
            if (staticField)
            {
                getter=LOOKUP.findStaticGetter(field.getDeclaringClass(),field.getName(),field.getType());
                setter=LOOKUP.findStaticSetter(field.getDeclaringClass(),field.getName(),field.getType());
            }
            else
            {
                getter=LOOKUP.findGetter(field.getDeclaringClass(),field.getName(),field.getType());
                setter=LOOKUP.findSetter(field.getDeclaringClass(),field.getName(),field.getType());
            }

            if (staticField)
            {
                getter=getter.asType(STATIC_FIELD_GETTER);
                setter=setter.asType(STATIC_FIELD_SETTER);
            }
            else
            {
                getter=getter.asType(VIRTUAL_FIELD_GETTER);
                setter=setter.asType(VIRTUAL_FIELD_SETTER);
            }
            return new Accessor(field,setter,getter,staticField);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T fieldCopy(T a,String name,T b)
    {
        try
        {
            Accessor accessor=getFieldAccessor(a.getClass().getDeclaredField(name));
            accessor.set(a,accessor.get(b));
        }
        catch (Exception e)
        {
        }
        return a;
    }

    public static <T> T objectCopy(T a,T b)
    {
        if (a.getClass().isAssignableFrom(b.getClass()))
        {
            return objectCopy(a,b,a.getClass());
        }
        else if (b.getClass().isAssignableFrom(a.getClass()))
        {
            return objectCopy(a,b,b.getClass());
        }
        return a;
    }

    private static <T> T objectCopy(T a,T b,Class<?> tClass)
    {
        for (Field field: tClass.getDeclaredFields())
        {
            Accessor accessor=getFieldAccessor(field);
            if (accessor==null)
            {
                continue;
            }
            accessor.set(a,accessor.get(b));
        }
        return a;
    }

    public Object get(Object instance)
    {
        try
        {
            return this.staticField?this.getter.invokeExact():this.getter.invokeExact(instance);
        }
        catch (Throwable var3)
        {
            throw new IllegalStateException("Unable to read field value of "+this.field,var3);
        }
    }

    public void set(Object instance,Object value)
    {
        try
        {
            if (this.staticField)
            {
                this.setter.invokeExact(value);
            }
            else
            {
                this.setter.invokeExact(instance,value);
            }

        }
        catch (Throwable var4)
        {
            throw new IllegalStateException("Unable to set value of field "+this.field,var4);
        }
    }

    public Field getField()
    {
        return this.field;
    }
}
