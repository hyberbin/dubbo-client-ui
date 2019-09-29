package com.hyberbin.dubbo.client.analyse;

import com.hyberbin.dubbo.client.exception.DubboClientUException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GenericParameterAnalyser {

    private static final Map<Class, GenericAnalyser> genericAnalyserMap = new HashMap<>();

    public Class getClassGenericType(Class outerClass, Class innerClass, String typeName) {
        GenericAnalyser genericAnalyser = genericAnalyserMap.get(outerClass);
        if (genericAnalyser == null) {
            synchronized (genericAnalyserMap) {
                if (!genericAnalyserMap.containsKey(outerClass)) {
                    genericAnalyser = new GenericAnalyser();
                    genericAnalyser.instanceActualTypeArguments(outerClass);
                    genericAnalyserMap.put(outerClass, genericAnalyser);
                }
            }
        }
        return genericAnalyser.getClassGenericType(innerClass, typeName);
    }

    public Class getClassGenericType(Class outerClass, Class innerClass, Field field,
            String typeName) {
        try {
            getClassGenericType(outerClass, innerClass, typeName);
        } catch (Throwable e) {
        }
        GenericAnalyser genericAnalyser = genericAnalyserMap.get(outerClass);
        return genericAnalyser.getClassGenericType(field.getDeclaringClass(), typeName);
    }

    static class GenericAnalyser {

        private final Map<Type, Type[]> genericTypeMap = new HashMap<>();
        private final Map<Type, String[]> genericNameMap = new HashMap<>();
        private final Map<Type, Map<String, Class>> genericNameTypeMap = new HashMap<>();
        private final Set<Type> resolvedTypes = new HashSet<>();

        private void instanceActualTypeArguments(Class type) {
            if (resolvedTypes.contains(type)) {
                return;
            }
            resolvedTypes.add(type);
            Type[] genericInterfaces = type.getGenericInterfaces();
            for (Type genericInterface : genericInterfaces) {
                if (genericInterface instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) genericInterface;
                    Type rawType = parameterizedType.getRawType();
                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                    genericTypeMap.put(rawType, actualTypeArguments);
                    if (rawType instanceof Class) {
                        TypeVariable[] typeParameters = ((Class) rawType).getTypeParameters();
                        String[] names = new String[typeParameters.length];
                        Map<String, Class> map = new HashMap();
                        for (int i = 0; i < typeParameters.length; i++) {
                            names[i] = typeParameters[i].getName();
                            if (actualTypeArguments[i] instanceof Class) {
                                map.put(names[i], (Class) actualTypeArguments[i]);
                            } else if (actualTypeArguments[i] instanceof TypeVariable) {
                                TypeVariable typeVariable = (TypeVariable) actualTypeArguments[i];
                                Class aClass = genericNameTypeMap.get(type)
                                        .get(typeVariable.getName());
                                map.put(names[i], aClass);
                            }
                        }
                        genericNameMap.put(rawType, names);
                        genericNameTypeMap.put(rawType, map);
                        instanceActualTypeArguments((Class) rawType);
                    }
                }
            }
            Type genericSuperclass = type.getGenericSuperclass();
            if (genericSuperclass instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
                Type rawType = parameterizedType.getRawType();
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                genericTypeMap.put(rawType, actualTypeArguments);
                if (rawType instanceof Class) {
                    TypeVariable[] typeParameters = ((Class) rawType).getTypeParameters();
                    String[] names = new String[typeParameters.length];
                    Map<String, Class> map = new HashMap();
                    for (int i = 0; i < typeParameters.length; i++) {
                        names[i] = typeParameters[i].getName();
                        if (actualTypeArguments[i] instanceof Class) {
                            map.put(names[i], (Class) actualTypeArguments[i]);
                        } else if (actualTypeArguments[i] instanceof TypeVariable) {
                            TypeVariable typeVariable = (TypeVariable) actualTypeArguments[i];
                            Class aClass = genericNameTypeMap.get(type)
                                    .get(typeVariable.getName());
                            map.put(names[i], aClass);
                        }
                    }
                    genericNameMap.put(rawType, names);
                    genericNameTypeMap.put(rawType, map);
                    instanceActualTypeArguments((Class) rawType);
                }
            }
            for (Class clazz : type.getInterfaces()) {
                instanceActualTypeArguments(clazz);
            }
        }

        public Class getClassGenericType(Class innerClass, String typeName) {
            Map<String, Class> stringClassMap = genericNameTypeMap.get(innerClass);
            if (stringClassMap == null) {
                instanceActualTypeArguments(innerClass);
                stringClassMap = genericNameTypeMap.get(innerClass);
            }
            Class aClass = stringClassMap.get(typeName);
            if (aClass == null) {
                throw new DubboClientUException("找不到" + aClass.getName() + "中的变量参数:" + typeName);
            }
            return aClass;
        }
    }


}