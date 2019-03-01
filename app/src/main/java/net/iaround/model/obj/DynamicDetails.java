package net.iaround.model.obj;

/**
 * Class: 动态详情
 * Author：gh
 * Date: 2017/1/3 22:14
 * Email：jt_gaohang@163.com
 */
public class DynamicDetails {

    public final static int IMAGE_TEXT = 0;//图文
    public final static int TEXT_COMMENT = 1;//动态评论
    public final static int TEXT_LOVE = 2;//动态点赞

    public int itemType;//类型 是图文,分享or广告资源
    /**
     * 根据类型可以强转成不同的类<br>
     * 图文和分享的实体是:@DynamicItemBean<br>
     * 广告资源的实体是:@ResourceItemBean<br>
     */
    public Object itemBean;
}
