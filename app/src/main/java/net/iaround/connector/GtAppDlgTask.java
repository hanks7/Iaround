package net.iaround.connector;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.geetest.gt3unbindsdk.Bind.GT3GeetestBindListener;
import com.geetest.gt3unbindsdk.Bind.GT3GeetestUtilsBind;

import net.iaround.conf.Config;
import net.iaround.tools.CommonFunction;
import net.iaround.ui.activity.CloseAllActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 施丰雄 on 2016/5/3.
 * email 119535453@qq.com
 */
public class GtAppDlgTask implements DialogInterface.OnKeyListener {

    public static final String captchaURL = Config.loginUrl + "/v1/system/geeinit";
    public static final String validateURL = Config.loginUrl + "/v1/system/geevalidate";
    Context context;
    private GT3GeetestUtilsBind gt3GeetestUtils;

    // 极验回调
    private GeetBackListener geetBackListener;

    private static GtAppDlgTask instance = null;

    public static GtAppDlgTask getInstance() {
        synchronized (GtAppDlgTask.class) {
            if (instance == null) {
                instance = new GtAppDlgTask();
            }
        }

        return instance;
    }

    public void setContext(Context context) {
        this.context = context;
        init();
    }

    public void init() {
        /**
         * 初始化
         * 务必放在onCreate方法里面执行
         */
        if (gt3GeetestUtils == null) {
            gt3GeetestUtils = new GT3GeetestUtilsBind(this.context == null ? CloseAllActivity.getInstance().getTopActivity() : this.context);
        }

    }

    public void show() {
        if (gt3GeetestUtils == null) return;
        if (gt3GeetestUtils.getDialog() != null && gt3GeetestUtils.getDialog().isShowing()) return;
        gt3GeetestUtils.getGeetest(this.context == null ? CloseAllActivity.getInstance().getTopActivity() : this.context, captchaURL, validateURL, null, new GT3GeetestBindListener() {
            /**
             * num 1 点击验证码的关闭按钮来关闭验证码
             * num 2 点击屏幕关闭验证码
             * num 3 点击返回键关闭验证码
             */
            @Override
            public void gt3CloseDialog(int num) {

                if (num == 1 && geetBackListener != null)
                    geetBackListener.onClose();

            }


            /**
             * 验证码加载准备完成
             * 此时弹出验证码
             */
            @Override
            public void gt3DialogReady() {
            }


            /**
             * 拿到第一个url（API1）返回的数据
             */
            @Override
            public void gt3FirstResult(JSONObject jsonObject) {
            }


            /**
             * 往API1请求中添加参数
             * 添加数据为Map集合
             * 添加的数据以get形式提交
             */
            @Override
            public Map<String, String> gt3CaptchaApi1() {
                Map<String, String> map = new HashMap<String, String>();
                return map;
            }

            /**
             * 设置是否自定义第二次验证ture为是 默认为false(不自定义)
             * 如果为false这边的的完成走gt3GetDialogResult(String result)
             * 如果为true这边的的完成走gt3GetDialogResult(boolean a, String result)
             * result为二次验证所需要的数据
             */
            @Override
            public boolean gt3SetIsCustom() {
                return false;
            }

            /**
             * 拿到二次验证需要的数据
             */
            @Override
            public void gt3GetDialogResult(String result) {

            }


            /**
             * 自定义二次验证，当gtSetIsCustom为ture时执行这里面的代码
             */
            @Override
            public void gt3GetDialogResult(boolean status, String result) {

                if (status) {
                    /**
                     *  利用异步进行解析这result进行二次验证，结果成功后调用gt3GeetestUtils.gt3TestFinish()方法调用成功后的动画，然后在gt3DialogSuccess执行成功之后的结果
                     * //                JSONObject res_json = new JSONObject(result);
                     //
                     //                Map<String, String> validateParams = new HashMap<>();
                     //
                     //                validateParams.put("geetest_challenge", res_json.getString("geetest_challenge"));
                     //
                     //                validateParams.put("geetest_validate", res_json.getString("geetest_validate"));
                     //
                     //                validateParams.put("geetest_seccode", res_json.getString("geetest_seccode"));
                     //  二次验证成功调用 gt3GeetestUtils.gt3TestFinish();
                     //  二次验证失败调用 gt3GeetestUtils.gt3TestClose();
                     */
                }

            }


            /**
             * 需要做验证统计的可以打印此处的JSON数据
             * JSON数据包含了极验每一步的运行状态和结果
             */
            @Override
            public void gt3GeetestStatisticsJson(JSONObject jsonObject) {
                CommonFunction.log(jsonObject.toString());
            }

            /**
             * 往二次验证里面put数据
             * put类型是map类型
             * 注意map的键名不能是以下三个：geetest_challenge，geetest_validate，geetest_seccode
             */
            @Override
            public Map<String, String> gt3SecondResult() {
                Map<String, String> map = new HashMap<String, String>();
//                map.put("testkey", "12315");
                return map;

            }

            /**
             * 二次验证完成的回调
             * result为验证后的数据
             * 根据二次验证返回的数据判断此次验证是否成功
             * 二次验证成功调用 gt3GeetestUtils.gt3TestFinish();
             * 二次验证失败调用 gt3GeetestUtils.gt3TestClose();
             */
            @Override
            public void gt3DialogSuccessResult(String result) {
                if (gt3GeetestUtils == null) return;
                if (!TextUtils.isEmpty(result)) {
                    try {
                        JSONObject jobj = new JSONObject(result);
                        String sta = jobj.getString("status");
                        String authKey = jobj.getString("authKey");
                        if ("success".equals(sta)) {
                            if (geetBackListener != null) {
                                geetBackListener.onSuccess(authKey);
                            }
                            gt3GeetestUtils.gt3TestFinish();
                        } else {
                            gt3GeetestUtils.gt3TestClose();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    gt3GeetestUtils.gt3TestClose();
                }
            }

            /**
             * 验证过程错误
             * 返回的错误码为判断错误类型的依据
             */

            @Override
            public void gt3DialogOnError(String error) {
                CommonFunction.log("dsd", "gt3DialogOnError");
                if (gt3GeetestUtils == null) return;
                gt3GeetestUtils.cancelAllTask();
            }
        });
        //设置是否可以点击屏幕边缘关闭验证码
        gt3GeetestUtils.setDialogTouch(false);
        if (gt3GeetestUtils.getDialog() != null)
            gt3GeetestUtils.getDialog().setOnKeyListener(this);
    }

    /**
     * 释放极验弹框
     */
    public void onDestory() {
        if (gt3GeetestUtils == null) return;
        gt3GeetestUtils.cancelUtils();
        context = null; //内存泄漏
        if (geetBackListener != null) {
            geetBackListener = null;
        }
    }

    public GT3GeetestUtilsBind getGt3GeetestUtils() {
        return gt3GeetestUtils;
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            return true;
        }
        return false;

    }

    public void setGeetBackListener(GeetBackListener geetBackListener) {
        this.geetBackListener = geetBackListener;
    }

    public GeetBackListener getGeetBackListener() {
        return this.geetBackListener;
    }

    public interface GeetBackListener {
        public void onSuccess(String authKey);

        public void onClose();
    }


}
