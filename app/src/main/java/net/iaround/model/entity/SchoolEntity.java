package net.iaround.model.entity;

/**
 * @author：liush on 2017/1/10 18:24
 * 职业因为是自己拼接数据，所以可以复用这个bean/后台导出的数据是大写的，只能跟SchEntity重复了
 */
public class SchoolEntity {

    private int SchoolID;
    private String SchoolName;

    public SchoolEntity() {}

    public SchoolEntity(int schoolID, String schoolName) {
        SchoolID = schoolID;
        SchoolName = schoolName;
    }

    public int getSchoolID() {
        return SchoolID;
    }

    public void setSchoolID(int schoolID) {
        SchoolID = schoolID;
    }

    public String getSchoolName() {
        return SchoolName;
    }

    public void setSchoolName(String schoolName) {
        SchoolName = schoolName;
    }
}
