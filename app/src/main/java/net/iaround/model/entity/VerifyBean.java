package net.iaround.model.entity;

import net.iaround.model.im.BaseServerBean;

/**
 * 发送注册手机验证码实体类
 * Created by yz on 2018/4/24.
 */

public class VerifyBean extends BaseServerBean {
    public int sms_send_type;//表示是否是语音验证码1:短信；2:语音
    public String qu_hao;//区号
}
