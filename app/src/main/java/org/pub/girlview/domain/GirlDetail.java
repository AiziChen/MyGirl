package org.pub.girlview.domain;

import java.io.Serializable;

public class GirlDetail implements Serializable {
    private String name;
    private String src;
    private String info;

    public GirlDetail() {
        super();
    }

    public GirlDetail(String name, String src, String info) {
        super();
        this.name = name;
        this.src = src;
        this.info = info;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    @Override
    public String toString() {
        return "GirlDetail{" +
                "name='" + name + '\'' +
                ", src='" + src + '\'' +
                ", info='" + info + '\'' +
                '}';
    }
}
