package com.hyberbin.dubbo.client.utils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

public class ReflectUtil {

    private static final Map<String, Object> DEFAULT_VALUE_MAPPING = new HashMap();

    static {
        DEFAULT_VALUE_MAPPING.put(Integer.TYPE.getName(), 0);
        DEFAULT_VALUE_MAPPING.put(Double.TYPE.getName(), 0.0D);
        DEFAULT_VALUE_MAPPING.put(Long.TYPE.getName(), 0L);
        DEFAULT_VALUE_MAPPING.put(Short.TYPE.getName(), Short.valueOf((short) 0));
        DEFAULT_VALUE_MAPPING.put(Byte.TYPE.getName(), (byte) 0);
        DEFAULT_VALUE_MAPPING.put(Character.TYPE.getName(), '\u0000');
        DEFAULT_VALUE_MAPPING.put(Boolean.TYPE.getName(), false);
        DEFAULT_VALUE_MAPPING.put(Float.TYPE.getName(), 0.0F);
        DEFAULT_VALUE_MAPPING.put(Void.TYPE.getName(), null);
    }

    public ReflectUtil() {
    }

    public static boolean isDecorate(Class beanClass) {
        if (Integer.class.equals(beanClass)) {
            return true;
        } else if (Long.class.equals(beanClass)) {
            return true;
        } else if (Float.class.equals(beanClass)) {
            return true;
        } else if (Double.class.equals(beanClass)) {
            return true;
        } else if (Boolean.class.equals(beanClass)) {
            return true;
        } else if (Character.class.equals(beanClass)) {
            return true;
        } else {
            return Byte.class.equals(beanClass) || Short.class.equals(beanClass);
        }
    }

    public static boolean isGenericType(Field field) {
        return field != null && ParameterizedType.class
                .isAssignableFrom(field.getGenericType().getClass());
    }

    public static boolean isBooleanType(Class type) {
        return type != null && (Boolean.class.equals(type) || Boolean.TYPE.equals(type));
    }
}
