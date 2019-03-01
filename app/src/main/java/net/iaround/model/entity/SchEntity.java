package net.iaround.model.entity;

import java.io.Serializable;

/**
 * @author：liush on 2017/1/10 18:24
 * 职业因为是自己拼接数据，所以可以否用这个bean
 */
public class SchEntity {

    private int schoolID;
    private String schoolName;

    public SchEntity() {}


    public SchEntity(int schoolID, String schoolName) {
        this.schoolID = schoolID;
        this.schoolName = schoolName;
    }

    public int getSchoolID() {
        return schoolID;
    }

    public void setSchoolID(int schoolID) {
        this.schoolID = schoolID;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }
}
