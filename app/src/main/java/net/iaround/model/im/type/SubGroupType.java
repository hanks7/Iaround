
package net.iaround.model.im.type;

/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-8-18 下午10:10:08
 * @ClassName SubGroupType.java
 * @Description: 聊天分组状态类
 */

public class SubGroupType {
    public static int NormalChat = 1;// 1.私聊列表
    /**
     * 7.5.0去掉搭讪，改为私聊
     */
    public static int SendAccost = 1;// 2.发出搭讪
    public static int ReceiveAccost = 1;// 3.收到搭讪

    public static int GameCenter = 4;// 4.游戏中心
    public static int PostBar = 5;// 5.贴吧消息
}
