package net.iaround.model.chatbar;

/**
 * Created by Administrator on 2017/6/13.
 */

public class ChatBarEntity {

    public final static int IMAGE_TEXT = 1;//普通
    public final static int RESOURCE_BANNER = 2;//广告资源
    public final static int BANNER_NOTICE = 3;//头部Banner

    public int itemType;//类型 是图文,分享or广告资源

    public Object itemBean;
}
