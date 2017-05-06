package cn.studyjams.s2.sj20170131.mijack.entity;

import java.util.List;

/**
 * @author Mr.Yuan
 * @date 2017/4/17
 */
public class Folder extends Media {
    private int count;
    private List<Image> data;

    public Folder(String path) {
        super(path);
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setData(List<Image> data) {
        this.data = data;
    }

    public List<Image> getData() {
        return data;
    }
}
