package com.hyberbin.dubbo.client.analyse;

import com.hyberbin.dubbo.client.utils.ReflectUtil;
import com.google.inject.Inject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import sun.reflect.generics.reflectiveObjects.TypeVariableImpl;

@Slf4j
public class JavaParameterAnalyser {

    @Inject
    private GenericParameterAnalyser genericParameterAnalyser;

    public BeanProperty analyse(Class clazz, Method method, Parameter parameter) {
        BeanProperty rootProperty = new BeanProperty();
        Set<Class> analysedSet = new HashSet();
        Type[] genericTypes = null;
        Class parameterType = parameter.getType();
        if (parameter.getParameterizedType() instanceof ParameterizedType) {
            rootProperty.setGenericType(parameter.getParameterizedType());
            genericTypes = ((ParameterizedType) parameter.getParameterizedType())
                    .getActualTypeArguments();
        } else if (parameter.getParameterizedType() instanceof TypeVariable) {
            TypeVariable typeVariable = (TypeVariable) parameter.getParameterizedType();
            Class classGenericType = genericParameterAnalyser
                    .getClassGenericType(clazz, method.getDeclaringClass(), typeVariable.getName());
            parameterType = classGenericType;
        }
        List<BeanProperty> subPropertyList = analysisRecur(clazz, parameterType, analysedSet,
                genericTypes);
        rootProperty.setType(parameterType);
        rootProperty.setTypeName(parameterType.getName());
        rootProperty.setName(parameter.getName());
        rootProperty.setSub(subPropertyList);
        rootProperty.setRoot(true);
        return rootProperty;
    }

    private List<BeanProperty> analysisRecur(Class outerClass, Class beanClass,
            Set<Class> analysedSet,
            Type[] genericTypes) {
        if (this.notNeedAnalyse(beanClass)) {
            return null;
        } else if (analysedSet.contains(beanClass)) {
            return null;
        } else {
            List<BeanProperty> propertyList = new ArrayList();
            analysedSet.add(beanClass);
            getJavaBeanField(beanClass).forEach((field) -> {
                BeanProperty property = new BeanProperty();
                property.setName(field.getName());
                property.setRoot(false);
                if (field.getGenericType() instanceof TypeVariableImpl) {
                    if (genericTypes == null) {
                        Class classGenericType = genericParameterAnalyser
                                .getClassGenericType(outerClass, beanClass, field,
                                        ((TypeVariableImpl) field.getGenericType()).getName());
                        property.setType(classGenericType);
                    } else {
                        property.setType((Class) genericTypes[0]);
                    }
                } else {
                    property.setType(field.getType());
                }

                if (ReflectUtil.isGenericType(field)) {
                    property.setTypeName(field.getGenericType().getTypeName());
                    property.setGenericType(field.getGenericType());
                } else {
                    property.setTypeName(property.getType().getTypeName());
                }

                property.setSub(this.analysisRecur(outerClass, property.getType(), analysedSet,
                        (Type[]) null));
                property.setField(field);
                propertyList.add(property);
            });
            analysedSet.remove(beanClass);
            return propertyList;
        }
    }

    private boolean notNeedAnalyse(Class beanClass) {
        if (beanClass == null || beanClass.isPrimitive()) {
            return true;
        } else if (ReflectUtil.isDecorate(beanClass)) {
            return true;
        } else if (String.class.equals(beanClass)) {
            return true;
        } else if (Collection.class.isAssignableFrom(beanClass)) {
            return true;
        } else if (Map.class.isAssignableFrom(beanClass)) {
            return true;
        } else if (Date.class.isAssignableFrom(beanClass)) {
            return true;
        } else {
            return beanClass.isEnum() ? true : beanClass.isArray();
        }
    }

    private List<Field> getJavaBeanField(Class beanClass) {
        Map<String, Method> methodDic = new HashMap(32);
        List<Field> javaBeanField = new ArrayList();

        for (Class clazz = beanClass; clazz != Object.class && clazz != null;
                clazz = clazz.getSuperclass()) {
            for (Method method:clazz.getMethods()) {
                methodDic.put(method.getName(), method);
            }
            for (Field field:clazz.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers()) && this
                        .existGetterMethod(field, methodDic) && this
                        .existSetterMethod(field, methodDic)) {
                    javaBeanField.add(field);
                }
            }
        }

        return javaBeanField;
    }

    private boolean existSetterMethod(Field field, Map<String, Method> methodDic) {
        if (methodDic.containsKey(this.buildMethodName("set", field.getName()))) {
            return true;
        } else {
            return (field.getType() == Boolean.TYPE || field.getType() == Boolean.class)
                    && StringUtils.startsWith(field.getName(), "is") && methodDic
                    .containsKey(this.buildMethodName("set", field.getName().substring(2)));
        }
    }

    private boolean existGetterMethod(Field field, Map<String, Method> methodDic) {
        if (field.getType() == Boolean.TYPE || field.getType() == Boolean.class) {
            if (StringUtils.startsWith(field.getName(), "is") && methodDic
                    .containsKey(field.getName())) {
                return true;
            }

            if (methodDic.containsKey(this.buildMethodName("is", field.getName()))) {
                return true;
            }
        }

        return methodDic.containsKey(this.buildMethodName("get", field.getName()));
    }

    private String buildMethodName(String prefix, String body) {
        return prefix + Character.toUpperCase(body.charAt(0)) + body.substring(1);
    }
}