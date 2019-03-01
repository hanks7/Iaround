package net.iaround.model.entity;

import net.iaround.model.im.BaseServerBean;

/**
 *
 * 获取-主播认证状态
 * Created by Administrator on 2017/12/11.
 */

public class AnchorsCertificationBean extends BaseServerBean {

    public int VerifyStatus;//0是未认证1是认证2是审核中3审核未通过
    public String Pic;
    public String Video;
    public String FirstVideoPic;
}
