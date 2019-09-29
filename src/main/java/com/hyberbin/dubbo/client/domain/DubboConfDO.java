package com.hyberbin.dubbo.client.domain;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DubboConfDO {

    private String id;
    private String protocol;
    private String address;
    private String group;

    public String toString() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DubboConfDO that = (DubboConfDO) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(protocol, that.protocol) &&
                Objects.equals(address, that.address) &&
                Objects.equals(group, that.group);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, protocol, address, group);
    }
}
