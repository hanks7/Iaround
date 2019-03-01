package net.iaround.model.entity;

//import net.iaround.model.database.Lable;

import java.util.List;

/**
 * @author：liush on 2016/12/7 15:15
 */
public class HobbyType {

    private int iconId;
    private String type;
    private int typeColor;
//    private List<Lable> details;

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getTypeColor() {
        return typeColor;
    }

    public void setTypeColor(int typeColor) {
        this.typeColor = typeColor;
    }

//    public List<Lable> getDetails() {
//        return details;
//    }
//
//    public void setDetails(List<Lable> details) {
//        this.details = details;
//    }
}
