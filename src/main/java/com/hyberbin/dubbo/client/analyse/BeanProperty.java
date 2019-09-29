package com.hyberbin.dubbo.client.analyse;

import com.alibaba.fastjson.JSON;
import com.hyberbin.dubbo.client.exception.DubboClientUException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.beanutils.BeanMap;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ClassUtils;

@Setter
@Getter
public class BeanProperty {

    private String name;
    private transient Class type;
    private transient Type genericType;
    private String typeName;
    private String value;
    private transient Object trueValue;
    private transient Field field;
    private boolean isRoot;
    private transient Parameter parameter;
    private List<BeanProperty> sub;
    private Map beanMap;
    private Object objectValue;
    private boolean isPrimitiveOrWrapper = false;
    private boolean isAbstract = false;

    public void setType(Class type) {
        this.type = type;
        isPrimitiveOrWrapper = ClassUtils.isPrimitiveOrWrapper(type) || String.class.equals(type);
        isAbstract = Modifier.isInterface(type.getModifiers()) || Modifier
                .isAbstract(type.getModifiers());
        try {
            if (!isAbstract && !isPrimitiveOrWrapper) {
                objectValue = type.newInstance();
                beanMap = new BeanMap(objectValue);
            }
        } catch (Throwable e) {
            throw new DubboClientUException(e);
        }
    }

    public void setValue(String value) {
        this.value = value;
        if (value != null) {
            if (isAbstract) {
                objectValue = JSON.parse(value);
            } else if (isPrimitiveOrWrapper) {
                objectValue = ConvertUtils.convert(value,type);
            }
        }
    }

    public Object getObjectValue() {
        if (!isAbstract && !isPrimitiveOrWrapper && !CollectionUtils.isEmpty(sub)) {
            for (BeanProperty property : sub) {
                beanMap.put(property.getName(), property.getObjectValue());
            }
        } else if (value == null) {
            objectValue = null;
        }
        return objectValue;
    }

    public String toString() {
        return this.toStringHelper(this, 0);
    }

    private String toStringHelper(BeanProperty property, int level) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < level; ++i) {
            sb.append("  ");
        }

        sb.append(property.getName()).append("  -  ").append(property.getValue()).append("  -  ")
                .append(property.getTypeName()).append("\n");
        if (!CollectionUtils.isEmpty(property.sub)) {
            Iterator var6 = property.sub.iterator();

            while (var6.hasNext()) {
                BeanProperty beanProperty = (BeanProperty) var6.next();
                sb.append(this.toStringHelper(beanProperty, level + 1));
            }
        }

        return sb.toString();
    }
}

