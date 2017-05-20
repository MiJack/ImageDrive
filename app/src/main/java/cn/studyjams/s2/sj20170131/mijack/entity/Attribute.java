package cn.studyjams.s2.sj20170131.mijack.entity;

/**
 * @author Mr.Yuan
 * @date 2017/5/21
 */
public class Attribute<V> {
    private String name;
    private V value;

    public Attribute() {
    }

    public Attribute(String name, V value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }
}
