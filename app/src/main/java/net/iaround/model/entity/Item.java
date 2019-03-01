package net.iaround.model.entity;

import java.io.Serializable;

public class Item implements Serializable {
    private static final long serialVersionUID = 9080737532307304908L;
    public String key;
    public int value;

    public Item(String key, int value) {
        this.key = key;
        this.value = value;
    }

    public Item() {
    }
}