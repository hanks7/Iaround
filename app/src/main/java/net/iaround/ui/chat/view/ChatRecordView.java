package net.iaround.ui.chat.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.model.database.FriendModel;
import net.iaround.model.im.ChatRecord;
import net.iaround.model.im.type.MessageBelongType;
import net.iaround.model.skill.SkillAttackResult;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.PicIndex;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.view.HeadPhotoView;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class ChatRecordView extends RelativeLayout {

    private Bitmap mBitmap;
    private String iconPath = "";

    protected final int textSize_Notice = 13;// 提示性的字体大小
    // 默认图片资源id
    protected final int defFace = R.drawable.default_face_map;
    protected final int defShare = R.drawable.chat_record_share_default_icon;
    protected final int defSmall = PicIndex.DEFAULT_SMALL;
    protected final int defGame = PicIndex.DEFAULT_GAME_SMALL;
    protected final int defLocation = PicIndex.DEFAULT_LOCATION_SMALL;
    protected final int defVideo = R.drawable.video_item_default_left_icon;

    //点击监听器
    protected View.OnClickListener mUserIconClickListener;//头像点击的监听器
    protected View.OnLongClickListener mUserIconLongClickListener;//头像长按的监听器
    protected View.OnClickListener mRecordClickListener;//消息体点击的监听器
    protected View.OnLongClickListener mRecordLongClickListener;//消息体长按的监听器
    protected View.OnClickListener mResendClickListener;//发送失败,重发点击监听器
    protected View.OnClickListener mCheckBoxClickListener;//多选框的点击监听器
    protected View.OnClickListener mCreditsClickListener;//积分兑换提示框的点击监听器
    protected View.OnClickListener mAcceptDelegationClickListener;//接受聊吧委托找主持人/辅导员的点击监听器

    protected OnClickListener mOrderAgreeClickListener;//订单同意点击事件
    protected OnClickListener mOrderRefuseClickListener;//订单拒绝点击事件

    protected int grouprole;

    public ChatRecordView(Context context) {
        super(context);
    }

    /**
     * 初始化的操作,只进行一次
     */
    public abstract void initRecord(Context context, ChatRecord record);

    /**
     * 显示的操作,需要根据数据更新View
     */
    public abstract void showRecord(Context context, ChatRecord record);

    public abstract void reset();

    /**
     * 是否显示CheckBox
     */
    public void showCheckBox(boolean isShow) {

    }

    /**
     * 初始化所有点击监听器
     *
     * @param lUserIconClick         用户头像点击事件
     * @param lUserIconLongClick     用户头像长按事件
     * @param lRecordClick           消息体点击事件
     * @param lRecordLongClick       消息体长按事件
     * @param lResendClick           重发点击事件
     * @param lCheckBoxClickListener 多选框点击事件
     * @param lCreditsClickListener  积分兑换提示框点击事件
     */
    public void initClickListener(OnClickListener lUserIconClick,
                                  OnLongClickListener lUserIconLongClick, OnClickListener lRecordClick,
                                  OnLongClickListener lRecordLongClick, OnClickListener lResendClick,
                                  OnClickListener lCheckBoxClickListener, OnClickListener lCreditsClickListener,
                                  OnClickListener agreeClickListener, OnClickListener refuseClickListener) {
        mUserIconClickListener = lUserIconClick;
        mUserIconLongClickListener = lUserIconLongClick;
        mRecordClickListener = lRecordClick;
        mRecordLongClickListener = lRecordLongClick;
        mResendClickListener = lResendClick;
        mCheckBoxClickListener = lCheckBoxClickListener;
        mCreditsClickListener = lCreditsClickListener;
        mOrderAgreeClickListener = agreeClickListener;
        mOrderRefuseClickListener = refuseClickListener;
    }

    /**
     * 设置用户备注名
     */
    protected void setUserNotename(Context context, ChatRecord record) {
        long id = record.getFuid();
        long muid = Common.getInstance().loginUser.getUid();
        User user = FriendModel.getInstance(context).getUser(id);
        if (muid != id && user != null) {
            record.setfNoteName(user.getNoteName(true));
        }
    }

    /**
     * 设置用户的头像
     */
    protected void setUserIcon(Context context, final ChatRecord record, final HeadPhotoView iconView) {
        // 设置: 用户头像
        int vipLevel = 0;
        int svip = 0;
        String userIcon = "";
        if (record.getSendType() == MessageBelongType.RECEIVE) {
            vipLevel = record.getfVipLevel();
            svip = record.getfSVip();
            if (record.getType() == String.valueOf(15)) {
                String content = record.getContent();
                if (content != null)
                    try {
                        JSONObject jsonObject = new JSONObject(content);
                        String result = jsonObject.getString("content");
                        if (result != null) {
                            SkillAttackResult skillAttackResult = GsonUtil.getInstance().getServerBean(result, SkillAttackResult.class);
                            if (Common.getInstance().loginUser.getUid() == skillAttackResult.user.UserID) {//当前登陆者是发送者
                                userIcon = skillAttackResult.user.ICON;
                            } else {//当前登陆者不是发送者
                                userIcon = skillAttackResult.user.ICON;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            } else {
                userIcon = record.getfIconUrl();
            }
        } else {
            vipLevel = record.getVip();
            svip = record.getSVip();
            userIcon = record.getIcon();
        }
        initIconBitmap(userIcon);
        int level = -1;
        if (record.getSendType() == MessageBelongType.RECEIVE) {
            level = record.getFLevel();
        } else {
            level = record.getLevel();
        }
        iconView.executeChat(defFace, iconPath, svip, vipLevel, level);
        iconView.setTag(record);
    }

    /**
     * 设置用户头像点击事件
     *
     * @param context
     * @param iconView
     */
    protected void setUserIconClickListener(Context context, HeadPhotoView iconView) {
        iconView.setOnClickListener(mUserIconClickListener);
        iconView.setOnLongClickListener(mUserIconLongClickListener);
    }


    /**
     * 初始化头像的bitmap
     */
    private void initIconBitmap(String iconStr) {
        iconPath = iconStr;
    }

    /**
     * 获取圆角位图的方法
     *
     * @param bitmap 需要转化成圆角的位图
     * @param pixels 圆角的度数，数值越大，圆角越大
     * @return 处理后的圆角位图
     */
    private static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }


    /**
     * 获取地理位置的预览图的URL
     *
     * @param lat 纬度
     * @param lng 经度
     * @return 地理位置的预览图的URL
     */
    protected String getLocationPreview(double lat, double lng) {
        String locationUrl = null;
        String googleUrl = "http://maps.google.com/maps/api/staticmap?center=";
        locationUrl = String.format(googleUrl + "%s" + "," + "%s" +
                "&zoom=15&size=400*200&maptype=roadmap&markers=color:red|label:A|" + "%s" + "," +
                "%s" + "&sensor=false", lat, lng, lat, lng);


        CommonFunction.log("googleUrl", googleUrl);
        return locationUrl;
    }

    /**
     * 获取高德地理位置的预览图的URL
     *
     * @param x
     * @param y
     * @return
     */
    protected String getMapUrl(double x, double y) {
        String url = "http://restapi.amap.com/v3/staticmap?location=" + y + "," + x +
                "&zoom=15&size=400*200" + "&key=" + "7edc278c6ace88576a961c8dceafa206";
        CommonFunction.log("getMapUrl", url);
        return url;
    }
}
