package net.iaround.model.entity;

/**
 * @author：liush on 2016/12/15 14:30
 */
public class UploadPicEntity extends BaseEntity {
    private String attid;
    private String url;

    public String getAttid() {
        return attid;
    }

    public void setAttid(String attid) {
        this.attid = attid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
