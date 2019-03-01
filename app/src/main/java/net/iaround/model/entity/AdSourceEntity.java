package net.iaround.model.entity;

import net.iaround.model.im.BaseServerBean;

/**
 * Class: 广告数据
 * Author：gh
 * Date: 2017/8/26 20:33
 * Email：jt_gaohang@163.com
 */
public class AdSourceEntity extends BaseServerBean{

    public AdSouce indexAd;

    public class AdSouce{
        public String adPath;  //广告地址
        public String adJumpPath;  //跳转地址
        public int adScale;  //1大图 0小图

        public String getAdPath() {
            if (adPath == null)
                adPath = "";
            return adPath;
        }

        public String getAdJumpPath() {
            return adJumpPath;
        }

        public int getAdScale() {
            return adScale;
        }
    }
}
