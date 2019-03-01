package net.iaround.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.plus.model.people.Person;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Config;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.GoldHttpProtocol;
import net.iaround.model.im.BaseServerBean;
import net.iaround.pay.bean.PayGoodsBean;
import net.iaround.pay.bean.VipBuyMemberListBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.TimeFormat;
import net.iaround.tools.chat.MD5;
import net.iaround.ui.comon.NetImageView;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 咪咕开通会员的类
 */
public class UserVipNewPayActivity extends TitleActivity implements View.OnClickListener, HttpCallBack {


    public static final String KEY_HEADER_ICON = "key_header_icon";

    private LinearLayout llNewPay1;
    private LinearLayout llNewPay2;
    private LinearLayout llNewPay3;

    private TextView tvNewPay1;
    private TextView tvNewPay2;
    private TextView tvNewPay3;

    private TextView tvTitle;
    private ImageView ivLeft;

    private NetImageView vipIcon;

    private String payChannel = "";
    private int isChina = 1;
    private long mGetMemberList;
    private long mGetMiguStatus;
    private long mPostMiguWebsite; //咪咕充值接口HTTP请求标记（遇见APP客户端直接访问咪咕服务器）
    private int currentMonth;

    private VipBuyMemberListBean.Goods data;
    public ArrayList<VipBuyMemberListBean.Goods> goods;// 购买会员列表
    public ArrayList<VipBuyMemberListBean.Goods> goodsTemp;
    private VipBuyMemberListBean bean;

    // 咪咕请求的单位
    private int miguSelect;

    // 咪咕当前订购状态
    private int orderStatus;
    private String phone;
    private String webId;
    private String serId;
    //咪咕游戏H5链接
    private String mH5Url;
    private String baiYinChannelCode;//白银channelCode
    private String huangJinChannelCode;//黄金channelCode
    private String baiJinChannelCode;//白金channelCode

    private String headerIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_vip_new_pay);
        initData();
        initView();
        initActionBar();
        showWaitDialog();
    }

    /**
     * 初始化数据
     */
    private void initData() {

        goods = new ArrayList<>();
        goodsTemp = new ArrayList<>();
        headerIcon = getIntent().getStringExtra(KEY_HEADER_ICON);
//        reqGoodsList();
    }

    private void initActionBar() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
        ivLeft = (ImageView) findViewById(R.id.iv_left);

        tvTitle.setText(getResString(R.string.vip_recharge));

        findViewById(R.id.fl_left).setOnClickListener(this);
        ivLeft.setOnClickListener(this);
    }

    private void initView() {

        llNewPay1 = (LinearLayout) findViewById(R.id.ll_new_pay1);
        llNewPay2 = (LinearLayout) findViewById(R.id.ll_new_pay2);
        llNewPay3 = (LinearLayout) findViewById(R.id.ll_new_pay3);

        tvNewPay1 = (TextView) findViewById(R.id.tv_new_pay1);
        tvNewPay2 = (TextView) findViewById(R.id.tv_new_pay2);
        tvNewPay3 = (TextView) findViewById(R.id.tv_new_pay3);
        vipIcon = (NetImageView) findViewById(R.id.iv_vip_banner);

        findViewById(R.id.tv_get_packageGift).setOnClickListener(this);
        findViewById(R.id.rl_get_packageGift).setOnClickListener(this);
        tvNewPay1.setOnClickListener(this);
        tvNewPay2.setOnClickListener(this);
        tvNewPay3.setOnClickListener(this);

        tvNewPay1.setClickable(false);
        tvNewPay2.setClickable(false);
        tvNewPay3.setClickable(false);

        llNewPay2.setVisibility(View.VISIBLE);
        llNewPay3.setVisibility(View.VISIBLE);

        if (headerIcon != null) {
            if (!TextUtils.isEmpty(headerIcon)) {
                vipIcon.execute(R.drawable.group_info_bg, headerIcon);
            }
        }

    }

    /**
     * 请求数据，获取商品列表
     */
    private void reqGoodsList() {
        /**
         * 只有中文简体的方式下有支付方式
         * isChina   1 大陆  2 国外
         */
        isChina = 1;
        mGetMemberList = GoldHttpProtocol.getBuyMemberList(mContext,
                String.valueOf(isChina), this);
    }

    private void setProductName(ArrayList<VipBuyMemberListBean.Goods> goods) {
        this.goods = goods;
        if (goods != null && goods.size() > 0) {
            tvNewPay1.setText(getResString(R.string.user_vip_open));
            tvNewPay2.setText(getResString(R.string.user_vip_open));
            tvNewPay3.setText(getResString(R.string.user_vip_open));

            vipIcon.execute(R.drawable.group_info_bg, bean.icon);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_left:
            case R.id.iv_left:
                finish();
                break;
            case R.id.tv_new_pay1:
                if (orderStatus > 0) {
                    if (orderStatus == 10) {
                        requestWebsite("705663246765");
                    }
                    return;
                }
                requestMonthlyPay_10();
                break;
            case R.id.tv_new_pay2:
                if (orderStatus > 0) {
                    if (orderStatus == 15) {
                        requestWebsite("760000143300");
                    }
                    return;
                }
                requestMonthlyPay_15();
                break;
            case R.id.tv_new_pay3:
                if (orderStatus > 0) {
                    if (orderStatus == 20) {
                        requestWebsite("760000143298");
                    }
                    return;
                }
                requestMonthlyPay_20();
                break;
            case R.id.tv_get_packageGift:
            case R.id.rl_get_packageGift:
                WebViewAvtivity.launchVerifyCode(UserVipNewPayActivity.this, 0, mH5Url);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestPayMiGu();
    }

    /**
     * 咪咕订购状态
     */
    private void requestPayMiGu() {
        mGetMiguStatus = GoldHttpProtocol.getMiguStatus(this, this);
    }

    /**
     * 请求10元充值
     */
    private void requestMonthlyPay_10() {
        String webId = "yj10" + getRadom() + Common.getInstance().loginUser.getUid();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("spCode", "783287");
//        map.put("channelCode", "43016000");
        if(TextUtils.isEmpty(baiYinChannelCode)){
            baiYinChannelCode = "43781000";
        }
        map.put("channelCode", baiYinChannelCode);
        map.put("serviceID", "705663246765");
        map.put("webId", webId);
        map.put("productDescribe", "咪咕遇见白银会员");
        map.put("monthStatus", 1);
        map.put("redirectURL", "http://www.baidu.com");
        map.put("failRedirectURL", "http://www.google.cn");
        String url = Config.sMonthlyPay + getUrlParamsByMap(map, false);
        Intent i = new Intent(mContext, WebViewAvtivity.class);
        i.putExtra(WebViewAvtivity.WEBVIEW_URL, url);
        i.putExtra(WebViewAvtivity.WEBVIEW_IGNORE_SSL_ERROR, true);
        startActivityForResult(i, 201);
    }

    /**
     * 请求15元充值
     */
    private void requestMonthlyPay_15() {
        String webId = "yj15" + getRadom() + Common.getInstance().loginUser.getUid();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("spCode", "783287");
//        map.put("channelCode", "43016000");
        if(TextUtils.isEmpty(huangJinChannelCode)){
            huangJinChannelCode = "43016002";
        }
        map.put("channelCode", huangJinChannelCode);
        map.put("serviceID", "760000143300");
        map.put("webId", webId);
        map.put("productDescribe", "咪咕遇见黄金会员");
        map.put("monthStatus", 1);
        map.put("redirectURL", "http://www.baidu.com");
        map.put("failRedirectURL", "http://www.google.cn");
        String url = Config.sMonthlyPay + getUrlParamsByMap(map, false);
        Intent i = new Intent(mContext, WebViewAvtivity.class);
        i.putExtra(WebViewAvtivity.WEBVIEW_URL, url);
        i.putExtra(WebViewAvtivity.WEBVIEW_IGNORE_SSL_ERROR, true);
        startActivityForResult(i, 201);

    }

    /**
     * 请求20元充值
     */
    private void requestMonthlyPay_20() {
        String webId = "yj20" + getRadom() + Common.getInstance().loginUser.getUid();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("spCode", "783287");
//        map.put("channelCode", "43016000");
        if(TextUtils.isEmpty(baiJinChannelCode)){
            baiJinChannelCode = "43784000";
        }
        map.put("channelCode", baiJinChannelCode);
        map.put("serviceID", "760000143298");
        map.put("webId", webId);
        map.put("productDescribe", "咪咕遇见白金会员");
        map.put("monthStatus", 1);
        map.put("redirectURL", "http://www.baidu.com");
        map.put("failRedirectURL", "http://www.google.cn");
        String url = Config.sMonthlyPay + getUrlParamsByMap(map, false);
        Intent i = new Intent(mContext, WebViewAvtivity.class);
        i.putExtra(WebViewAvtivity.WEBVIEW_URL, url);
        i.putExtra(WebViewAvtivity.WEBVIEW_IGNORE_SSL_ERROR, true);
        startActivityForResult(i, 201);

    }

    /**
     * 请求20元充值
     */
    private void requestWebsite(String serviceId) {
        mPostMiguWebsite = GoldHttpProtocol.requestWebsite(this, Config.sMiguWebsite, buildMGUnSubscribeMessage1(serviceId), this);
    }

    /*
  * 咪咕退订 XML消息
  * @param spId 企业代码
  * @param spPassword 密码
  * @param serviceId 业务代码 705663246765    咪咕遇见白银会员；760000143300    咪咕遇见黄金会员；760000143298    咪咕遇见白金会员
  * @param OA 用户伪代码-手机号
  *
  * @param userID 用户ID-手机号
  * @pram webID 遇见用户ID
  *
  *   格式
      <?xml  version="1.0"  encoding="utf-8"?>
      <soapenv:Envelope  xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/">
      <soapenv:Header>
              <RequestSOAPHeader  xmlns="http://www.huawei.com.cn/schema/common/v2_1">
                      <spId>35000001</spId>
                      <spPassword>******</spPassword>
                      <serviceId>35000001000001</serviceId>
                      <timeStamp>20170817055450</timeStamp>
                      <OA>13600000001</OA>
              </RequestSOAPHeader>
      </soapenv:Header>
      <soapenv:Body>
              <unSubscribeProductRequest  xmlns="http://www.csapi.org/schema/parlayx/subscribe/manage/v1_0/local">
                      <unSubscribeProductReq>
                              <userID  xmlns="">
                                      <ID>13600000001</ID>
                                      <type>0</type>
                              </userID>
                              <subInfo  xmlns="">
                                      <serviceId>35000001000001</serviceId>
                                      <channelID>0</channelID>
                              </subInfo>
                      </unSubscribeProductReq>
              </unSubscribeProductRequest>
      </soapenv:Body>
      </soapenv:Envelope>
  *
  * */
//    public String buildMGUnSubscribeMessage(String serviceId) {
//        String password = "783287bmeB400!"+TimeFormat.timeFormat8();
//        StringBuffer sb = new StringBuffer();
//        sb.append("<?xml  version=\"1.0\"  encoding=\"utf-8\"?>").
//                append("<SOAP-ENV:Envelope  xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">").
//                append("<SOAP-ENV:Header>").
//                append("<ns1:RequestSOAPHeader  xmlns:ns1=\"http://www.huawei.com.cn/schema/common/v2_1\">").
//                append("<ns1:spId>").append("783287").append("</ns1:spId>").
//                append("<ns1:spPassword>").append(MD5.encrypt(password,false)).append("</ns1:spPassword>").
//                append("<ns1:serviceId>").append(serviceId).append("</ns1:serviceId>").
//                append("<ns1:timeStamp>").append(TimeFormat.timeFormat8()).append("</ns1:timeStamp>").
//                append("<ns1:OA>").append(phone).append("</ns1:OA>").
//                append("<ns1:webId>").append(webId).append("</ns1:webId>").
//                append("</ns1:RequestSOAPHeader>").
//                append("</SOAP-ENV:Header>").
//                append("<SOAP-ENV:Body>").
//                append("<loc:unSubscribeProductRequest  xmlns:loc=\"http://www.csapi.org/schema/parlayx/subscribe/manage/v1_0/local\">").
//                append("<loc:unSubscribeProductReq>").
//                append("<userID>").
//                append("<ID>").append(phone).append("</ID>").append("<type>1</type>").
//                append("</userID>").
//                append("<subInfo>").
//                append("<productID>").append(serviceId).append("</productID>").
//                append("<channelID>").append("7").append("</channelID>").
//                append("</subInfo>").
//                append("</loc:unSubscribeProductReq>").
//                append("</loc:unSubscribeProductRequest>").
//                append("</SOAP-ENV:Body>").
//                append("</SOAP-ENV:Envelope>");
//
//        return sb.toString();
//    }

    public String buildMGUnSubscribeMessage1(String serviceId) {
        String spid = "783287";
        String spPasswd = "bmeB400!";
        String timeStamp = "" + System.currentTimeMillis();
        String str = spid + spPasswd + timeStamp;
        String passwd = encrypt(str);

        String xml = "<?xml version=\"1.0\" encoding=\"utf-8\" ?><SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"><SOAP-ENV:Header><ns1:RequestSOAPHeader xmlns:ns1=\"http://www.huawei.com.cn/schema/common/v2_1\"><ns1:spPassword>" +
                passwd + "</ns1:spPassword><ns1:spId>" + spid + "</ns1:spId><ns1:serviceId>" + serId + "</ns1:serviceId><ns1:timeStamp>" + timeStamp +
                "</ns1:timeStamp><ns1:OA>" + phone + "</ns1:OA><ns1:webID>" + webId +
                "</ns1:webID></ns1:RequestSOAPHeader></SOAP-ENV:Header><SOAP-ENV:Body><loc:unSubscribeProductRequest xmlns:loc=\"http://www.csapi.org/schema/parlayx/subscribe/manage/v1_0/local\"><loc:unSubscribeProductReq><userID><ID>" +
                phone + "</ID><type>1</type></userID><subInfo><productID>" + serId + "</productID><channelID>7</channelID></subInfo></loc:unSubscribeProductReq></loc:unSubscribeProductRequest></SOAP-ENV:Body></SOAP-ENV:Envelope>";

//        StringBuffer sb = new StringBuffer();
//        sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>").
//                append("<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">").
//                append("<SOAP-ENV:Header>").
//                append("<ns1:RequestSOAPHeader xmlns:ns1=\"http://www.huawei.com.cn/schema/common/v2_1\">").
//                append("<ns1:spPassword>").append(encrypt(password)).append("</ns1:spPassword>").
//                append("<ns1:spId>").append(spid).append("</ns1:spId>").
//                append("<ns1:serviceId>").append(serviceId).append("</ns1:serviceId>").
//                append("<ns1:timeStamp>").append(time).append("</ns1:timeStamp>").
//                append("<ns1:OA>").append(phone).append("</ns1:OA>").
//                append("<ns1:webID>").append(webId).append("</ns1:webID>").
//                append("</ns1:RequestSOAPHeader>").
//                append("</SOAP-ENV:Header>").
//                append("<SOAP-ENV:Body>").
//                append("<loc:unSubscribeProductRequest xmlns:loc=\"http://www.csapi.org/schema/parlayx/subscribe/manage/v1_0/local\">").
//                append("<loc:unSubscribeProductReq>").
//                append("<userID>").
//                append("<ID>").append(phone).append("</ID>").append("<type>1</type>").
//                append("</userID>").
//                append("<subInfo>").
//                append("<productID>").append(serviceId).append("</productID>").
//                append("<channelID>").append("7").append("</channelID>").
//                append("</subInfo>").
//                append("</loc:unSubscribeProductReq>").
//                append("</loc:unSubscribeProductRequest>").
//                append("</SOAP-ENV:Body>").
//                append("</SOAP-ENV:Envelope>");

        return xml;
    }

    public static String encrypt(String passwd) {
        if (passwd == null)
            return null;
        try {
            MessageDigest alg = MessageDigest.getInstance("MD5");
            alg.reset();
            alg.update(passwd.getBytes());
            byte[] hash = alg.digest();
            String str = "";
            for (byte b : hash) {
                String s = Integer.toHexString(b);
                if (s.length() == 0)
                    str += "00";
                if (s.length() == 1)
                    str += "0" + s.toUpperCase();
                else if (s.length() == 2)
                    str += s.toUpperCase();
                else
                    str += s.substring(s.length() - 2).toUpperCase();
            }
            return str;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * 生成随机数7位数的
     *
     * @return
     */
    private String getRadom() {
        String s = "";
        for (int i = 0; i < 7; i++) {
            int intCount = (new Random()).nextInt(9);
            s += intCount + "";
        }
        return s;
    }

    /**
     * 将map 转为 string
     *
     * @param map
     * @return
     */
    public String getUrlParamsByMap(Map<String, Object> map,
                                    boolean isSort) {
        if (map == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        List<String> keys = new ArrayList<String>(map.keySet());
        if (isSort) {
            Collections.sort(keys);
        }
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = map.get(key).toString();
            sb.append(key + "=" + value);
            sb.append("&");
        }
        String s = sb.toString();
        if (s.endsWith("&")) {
            s = s.substring(0, s.lastIndexOf("&"));
        }
        return s;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            String Str = String.format(getResString(R.string.vip_buy_success),
                    currentMonth);
            CommonFunction.toastMsg(mContext, Str);
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        if (flag == mGetMemberList) {
            //废弃
            BaseServerBean Basebean = GsonUtil.getInstance().getServerBean(result,
                    BaseServerBean.class);
            if (Basebean != null && Basebean.isSuccess()) {
                bean = GsonUtil.getInstance().getServerBean(result,
                        VipBuyMemberListBean.class);

                if (bean != null && bean.goods.size() > 0 && bean.goods.size() > 6) {
                    payChannel = result;
                    goods.add(bean.goods.get(6));
                    goods.add(bean.goods.get(5));
                    goods.add(bean.goods.get(4));
                }
                /**
                 * 排序
                 */
                Collections.sort(goods, new Comparator<VipBuyMemberListBean.Goods>() {

                    /*
                     * int compare(Student o1, Student o2) 返回一个基本类型的整型，
                     * 返回负数表示：o1 小于o2，
                     * 返回0 表示：o1和o2相等，
                     * 返回正数表示：o1大于o2。
                     */
                    public int compare(VipBuyMemberListBean.Goods o1, VipBuyMemberListBean.Goods o2) {

                        //按照学生的年龄进行升序排列
                        if (Float.valueOf(o1.price) > Float.valueOf(o2.price)) {
                            return 1;
                        }
                        if (Float.valueOf(o1.price) == Float.valueOf(o2.price)) {
                            return 0;
                        }
                        return -1;
                    }
                });
                setProductName(goods);
            }
        } else if (flag == mGetMiguStatus) {
            destroyWaitDialog();
            tvNewPay1.setClickable(true);
            tvNewPay2.setClickable(true);
            tvNewPay3.setClickable(true);
            //会员状态是否开通（遇见服务器接口返回）
            try {
                JSONObject obj = new JSONObject(result);
                if (obj.has("status") && obj.optLong("status") == 200) {
                    orderStatus = CommonFunction.jsonOptInt(obj, "membertype");
                    phone = CommonFunction.jsonOptString(obj, "phone");
                    webId = CommonFunction.jsonOptString(obj, "webid");
                    serId = CommonFunction.jsonOptString(obj, "serviceid");
                    mH5Url = CommonFunction.jsonOptString(obj, "url");
                    baiYinChannelCode = CommonFunction.jsonOptString(obj, "baiYinChannelCode");
                    huangJinChannelCode = CommonFunction.jsonOptString(obj, "huangJinChannelCode");
                    baiJinChannelCode = CommonFunction.jsonOptString(obj, "baiJinChannelCode");

                    if (orderStatus == 10) {
                        tvNewPay1.setBackgroundResource(R.drawable.chat_person_background_of_sendgift);
                        tvNewPay1.setText("退订");
                        tvNewPay2.setBackgroundResource(R.drawable.btn_user_vip_migu_was);
                        tvNewPay2.setText("开通会员");
                        tvNewPay3.setBackgroundResource(R.drawable.btn_user_vip_migu_was);
                        tvNewPay3.setText("开通会员");
                    } else if (orderStatus == 15) {
                        tvNewPay1.setText("开通会员");
                        tvNewPay1.setBackgroundResource(R.drawable.btn_user_vip_migu_was);
                        tvNewPay2.setText("退订");
                        tvNewPay2.setBackgroundResource(R.drawable.chat_person_background_of_sendgift);
                        tvNewPay3.setText("开通会员");
                        tvNewPay3.setBackgroundResource(R.drawable.btn_user_vip_migu_was);
                    } else if (orderStatus == 20) {
                        tvNewPay1.setBackgroundResource(R.drawable.btn_user_vip_migu_was);
                        tvNewPay1.setText("开通会员");
                        tvNewPay2.setBackgroundResource(R.drawable.btn_user_vip_migu_was);
                        tvNewPay2.setText("开通会员");
                        tvNewPay3.setBackgroundResource(R.drawable.chat_person_background_of_sendgift);
                        tvNewPay3.setText("退订");
                    } else {
                        tvNewPay1.setBackgroundResource(R.drawable.chat_person_background_of_sendgift);
                        tvNewPay1.setText("开通会员");
                        tvNewPay2.setBackgroundResource(R.drawable.chat_person_background_of_sendgift);
                        tvNewPay2.setText("开通会员");
                        tvNewPay3.setBackgroundResource(R.drawable.chat_person_background_of_sendgift);
                        tvNewPay3.setText("开通会员");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (mPostMiguWebsite == flag) {
            //咪咕服务器退订接口的返回
            XmlPullParser parser = Xml.newPullParser();
            try {
                Reader in_withcode = new StringReader(result);
                parser.setInput(in_withcode);
                int eventType = parser.getEventType();
                String resultDescription = "";
                String error = "";

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    switch (eventType) {
                        case XmlPullParser.START_TAG:
                            String name = parser.getName();
                            if (name.equalsIgnoreCase("result")) {
                                error = parser.nextText();
                            } else if (name.equalsIgnoreCase("resultDescription")) {
                                resultDescription = parser.nextText();
                            }
                            break;
                    }
                    eventType = parser.next();
                }

                if (error.equals("209999")) {
                    CommonFunction.toastMsg(this, "其他原因或者参数问题或已经被退订");
                } else if (error.equals("200002")) {
                    CommonFunction.toastMsg(this, "MD5中password不对");
                } else if (error.equals("10001211")) {
                    CommonFunction.toastMsg(this, "请求消息格式非法，检查该用消息是否已被退订。");
                }
                if (error.equals("00000000")) {
                    CommonFunction.toastMsg(this, "退订成功了");
                    requestPayMiGu();
                }
                CommonFunction.log("migu", "error  = " + error);
                CommonFunction.log("migu", "resultDescription  = " + resultDescription);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onGeneralError(int e, long flag) {
        destroyWaitDialog();
        ErrorCode.toastError(this, e);
//        if (flag == mGetMemberList) {
//        }
    }

}
