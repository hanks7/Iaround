package net.iaround.tools;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.umeng.analytics.MobclickAgent;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Constants;
import net.iaround.pay.CreditActivity;
import net.iaround.pay.FragmentPayBuyGlod;
import net.iaround.ui.activity.CloseAllActivity;
import net.iaround.ui.activity.DynamicDetailActivity;
import net.iaround.ui.activity.FaceDetailActivity;
import net.iaround.ui.activity.FaceMainActivity;
import net.iaround.ui.activity.GameListActivity;
import net.iaround.ui.activity.GeneralSubActivity;
import net.iaround.ui.activity.LovePayActivity;
import net.iaround.ui.activity.MainFragmentActivity;
import net.iaround.ui.activity.MyWalletActivity;
import net.iaround.ui.activity.OtherInfoActivity;
import net.iaround.ui.activity.PersonalDynamicActivity;
import net.iaround.ui.activity.UserInfoActivity;
import net.iaround.ui.activity.UserVIPActivity;
import net.iaround.ui.activity.UserVipOpenActivity;
import net.iaround.ui.activity.VideoDetailsActivity;
import net.iaround.ui.activity.WebViewAvtivity;
import net.iaround.ui.activity.settings.OptionFeedback;
import net.iaround.ui.activity.settings.SecretSetActivity;
import net.iaround.ui.chat.ChatPersonal;
import net.iaround.ui.datamodel.User;
import net.iaround.ui.fragment.MessageFragment;
import net.iaround.ui.fragment.VideoAnchorFragment;
import net.iaround.ui.game.GameWebViewActivity;
import net.iaround.ui.group.activity.GroupChatTopicActivity;
import net.iaround.ui.group.activity.GroupGatherDetail;
import net.iaround.ui.group.activity.GroupInfoActivity;
import net.iaround.ui.group.activity.GroupMemberViewActivity;
import net.iaround.ui.space.more.BindTelphoneForOne;
import net.iaround.ui.store.StoreGiftClassifyActivity;
import net.iaround.ui.store.StoreMainActivity;


import org.json.JSONObject;

import java.util.HashMap;


/**
 * @author tanzy
 * @Description: 基础内部跳转，不需StartActivityForResulr的内部跳转。在webview中调用要注意：当解析http的URL时是启动新的webview
 * ，注意不要启动了多个webview
 * @date 2014-10-10
 */

// 先存在的内部跳转位置为（带X的为使用本类跳转的位置）：
// SecretaryPushRecordView（X）
// AdView（X）
// DynamicShareView（X）
// WebEnterActivity（调用StartActivityForResulr）

public class InnerJump {
    //private static Context mContext;

    public static void Jump(Context context, String url) {
        Jump(context, url, null, true);
    }

    public static void Jump(Context context, String url, boolean parseHttp) {
        Jump(context, url, null, parseHttp);
    }

    public static void Jump(Context context, String url, Intent mIntent, boolean parseHttp) {
        //mContext = context;

        if (CommonFunction.isEmptyOrNullStr(url)) {
            Log.e("secretary push", "null url");
            return;
        }

        if (parseHttp && url.contains("http://") && url.contains("gamecenter")) {// 游戏中心的url
            GameWebViewActivity.launchGameCenter(context, url);
            return;
        }

        if (parseHttp && url.contains("http://")) {// 内部浏览器跳转
            Intent i = new Intent(context, WebViewAvtivity.class);
            i.putExtra(WebViewAvtivity.WEBVIEW_URL, url);
            i.putExtra(WebViewAvtivity.WEBVIEW_NEW_SHRARE, 1);
            context.startActivity(i);
            return;
        }

        String[] info = url.split("[?]");

        if (info[0].equals("iaround://store")) { // 商店
            JumpStore(context);
        } else if (info[0].equals("iaround://goldpay")) { // 金币充值
            JumpGoldPay(context);
        } else if (info[0].equals("iaround://feedback")) { // 问题反馈
            JumpFeedBack(context);
        } else if (info[0].equals("iaround://bindphone")) { // 绑定手机
            JumpBindPhone(context);
        }
        // else if ( info[ 0 ].equals( "iaround://phonebook" ) )
        // {// 导入通讯录
        // JumpImportContact( );
        // }
        else if (info[0].equals("iaround://vipprivilege")) {// 跳到vip首页 6.0新增
            JumpVipActivity(context);
        } else if (info[0].equals("iaround://vipstore")) {// 跳到vip购买页（价格页面） 6.0新增
            JumpVipPrivilege(context);
        } else if (info[0].equals("iaround://expressionstore")) {// 跳到表情商店 6.0新增
            JumpFaceStore(context);
        } else if (info[0].equals("iaround://invisible")) {// 跳到隐私设置 6.0新增
            JumpPrivacySetting(context);
        } else if (info[0].equals("iaround://wallet")) {// 跳到我的钱包界面 6.0新增
            JumpWallet(context);
        } else if (info[0].equals("iaround://credits")) {//跳转到兑吧页面（6_2）
            JumpDuiba(context);
        } else if (info[0].equals("iaround://chatbarhall")) {//跳转到聊吧页面 6.4新增
            JumpChatbarHall();
        } else if (info[0].equals("iaround://chatbarpersoncenter")) {//跳转到聊吧个人中心 6.4新增
            JumpPersonalCenter();
        } else if (info[0].equals("iaround://videolist")) {//跳转到视频列表
            actionVideoList(context);
        } else if (info[0].equals("iaround://videopaylove")) {// 跳到爱心充值
            actionLovePay(context);
        } else if (info.length == 2) {
            if (info[0].equals("iaround://userinfo")) { // 个人资料
                JumpUserInfo(getParameter("userid", info[1]), context);
            } else if (info[0].equals("iaround://photodetail")) { // 照片详情
                JumpPhotoDetail(getParameter("photoid", info[1]));
            } else if (info[0].equals("iaround://groupdetail")) { // 圈子详情
                JumpGroupDetail(getParameter("groupid", info[1]), context);
            }
            // else if ( info[ 0 ].equals( "iaround://gamedetail" ) )
            // { // 游戏详情 5.7 去除
            // JumpGameDetail( getParameter( "gameid" , info[ 1 ] ) ,
            // getParameter( "plat" , info[ 1 ] ) );
            // }
            else if (info[0].equals("iaround://gifts")) { // 礼物列表
                JumpGiftList(getParameter("category", info[1]), context);
            } else if (info[0].equals("iaround://maps")) { // 表情贴图
                JumpFaceDetail(getParameter("mapid", info[1]), context);
            }
            // else if ( info[ 0 ].equals( "iaround://scene" ) )
            // { // 约会场景5.7去除
            // // JumpThemeDetail( getParameter( "sceneid" , info[ 1 ] ) );
            // }
            // else if ( info[ 0 ].equals( "iaround://giftdetail" ) )
            // { // 礼物详情 6.0去除
            // // JumpGiftDetail( getParameter( "giftid" , info[ 1 ] ) );
            // }
            else if (info[0].equals("iaround://groupssearch")) { // 圈子列表
                JumpGroupSearch(getParameter("id", info[1]),
                        getParameter("categoryid", info[1]));
            } else if (info[0].equals("iaround://groupparty")) {// 跳到圈聚会
                JumpGroupParty(getParameter("groupid", info[1]), getParameter("partyid", info[1]), context);
            } else if (info[0].equals("iaround://grouptopic")) {// 跳到圈话题
                JumpGroupTopic(getParameter("groupid", info[1]),
                        getParameter("topicid", info[1]));
            } else if (info[0].equals("iaround://postbartopic")) {// 跳到贴吧话题
                JumpPostbarTopic(getParameter("postbarid", info[1]),
                        getParameter("topicid", info[1]));
            } else if (info[0].equals("iaround://dynamicdetail")) {// 跳到动态详情
                JumpDynamicDetail(getParameter("dynamicid", info[1]), context);
            } else if (info[0].equals("iaround://giftbagdetail")) {// 跳到礼包详情 6.0新增
                JumpGiftBagDetail(getParameter("giftbagid", info[1]));
            } else if (info[0].equals("iaround://postbardetail")) {// 跳到侃啦详情
                JumpKanLaDetail(getParameter("postbarid", info[1]));
            } else if (info[0].equals("iaround://chatbardetail")) {// 跳到聊吧资料页
                JumpChatbarDetail(getParameter("chatbarid", info[1]));
                JumpGroupDetail(getParameter("chatbarid", info[1]), context);
            } else if (info[0].equals("iaround://chatbar")) {// 跳到聊吧面板
                JumpGroupChatTopic(getParameter("groupid", info[1]), context);
            } else if (info[0].equals("iaround://groupmembers")) {// 跳到聊吧管理
                JumpGroupMembers(getParameter("groupid", info[1]), context);
            } else if (info[0].equals("iaround://videodetails")) {// 跳到主播详情
                actionVideoDetails(getParameter("userid", info[1]), context);
            } else if (info[0].equals("iaround://personaldynamiclist")) {// 跳到个人动态列表
                JumpDynamicDetail(getParameter("userid", info[1]), context);
            } else if (info[0].equals("iaround://gamelist")) {// 跳转到陪玩分类详情列表界面
                JumpGameListActivity(getParameter("gameid", info[1]), context);
            } else { // 未知URL
                Log.e("secretary push", "unknow url ==== " + url);
            }
        } else {
            // 参数数量错误或未知URL
            Log.e("secretary push", "unknow url or wrong parameter counts  ==" + url);
        }
    }

    /**
     * 根据字段名获取下发路径的参数
     */
    public static String getParameter(String name, String js) {
        String p = null;
        try {
            @SuppressWarnings("deprecation")
            // 因为下发下来的参数被转成16进制符号，所以要解码
                    JSONObject jo = new JSONObject(java.net.URLDecoder.decode(js));
            p = CommonFunction.jsonOptString(jo, name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return p;
    }

    /**
     * 跳转至商店
     */
    private static void JumpStore(Context context) {
        // 6.0跳转至商店首页 zhengst
        StoreMainActivity.launcherStoreMainActivity(context);
    }

    /**
     * 跳转至金币兑换
     */
    private static void JumpGoldPay(Context context) {
        FragmentPayBuyGlod.jumpPayBuyGlodActivity(context);
    }

    /**
     * 跳转至意见反馈
     */
    private static void JumpFeedBack(Context context) {
//		Intent intent = new Intent( mContext, FeedBackActivity.class );
//		mContext.startActivity( intent );//gh
        Intent intent = new Intent();
        intent.setClass(context, OptionFeedback.class);
        context.startActivity(intent);
    }

    /**
     * 跳转至绑定手机
     */
    private static void JumpBindPhone(Context context) {
        if (Common.getInstance().loginUser.getHasPwd() == 1) {
            // 有密码
            Intent intent = new Intent(context, BindTelphoneForOne.class);
            intent.putExtra("type", 1);// 需输入密码
            context.startActivity(intent);
        } else {
            // 无密码
            Intent intent = new Intent(context, BindTelphoneForOne.class);
            intent.putExtra("type", 0);// 需设置密码
            context.startActivity(intent);
        }
    }

    // /**
    // * 跳转至导入通讯录
    // * */
    // private static void JumpImportContact( )
    // {
    // Intent intent = new Intent( );
    // intent.putExtra( "initiative" , 1 );
    // intent.setClass( mContext , AgainImportContact.class );
    // mContext.startActivity( intent );
    // }

    /**
     * 跳转至个人空间
     */
    private static void JumpUserInfo(String data, Context context) {
        if (CommonFunction.isEmptyOrNullStr(data))
            return;
//		SpaceOther.launch( mContext, Long.parseLong( data ), null );//gh
        if (Common.getInstance().loginUser.getUid() == Long.parseLong(data)) {
            // TODO: 2017/5/18 跳转到个人页面
            Intent intent = new Intent(context, UserInfoActivity.class);
            context.startActivity(intent);
        } else {
            Intent intent = new Intent(context, OtherInfoActivity.class);
            intent.putExtra(Constants.UID, Long.parseLong(data));
            context.startActivity(intent);
        }
    }

    // }

    /**
     * 跳转至圈子详情
     */
    private static void JumpGroupDetail(String data, Context context) {
        if (CommonFunction.isEmptyOrNullStr(data))
            return;
        Intent intent = new Intent(context, GroupInfoActivity.class);
        intent.putExtra(GroupInfoActivity.GROUPID, data);
        context.startActivity(intent);
    }

    // /** 跳转至游戏详情 */
    // private static void JumpGameDetail( String data , String plat )
    // {
    // if ( CommonFunction.isEmptyOrNullStr( data ) )
    // return;
    // if ( "0".equals( plat ) || "1".equals( plat ) )
    // {
    // // GameDetailActivity.lauch( mContext , Long.parseLong( data ) , -1
    // // );
    // }
    // else
    // CommonFunction.toastMsg( mContext , R.string.no_this_game );
    // }

    /**
     * 跳转至礼物列表
     */
    private static void JumpGiftList(String data, Context context) {
        if (CommonFunction.isEmptyOrNullStr(data))
            return;
        StoreGiftClassifyActivity.launcherGiftClassify(context, Integer.parseInt(data));
        // );
    }

    /**
     * 跳转至表情详情
     */
    private static void JumpFaceDetail(String data, Context context) {
        if (CommonFunction.isEmptyOrNullStr(data))
            return;
        FaceDetailActivity.launch((Activity) context, Integer.parseInt(data));
    }

    /**
     * 跳转至主题详情
     */
    @Deprecated
    private static void JumpThemeDetail(String data) {
        // if ( CommonFunction.isEmptyOrNullStr( data ) )
        // return;
        // ChatThemeDetailActivity.defalutLuanch( mContext , Integer.parseInt(
        // data ) , -1 ,
        // ChatThemeDetailActivity.LUANCH_TYPE_NOT_CHANGE );
    }

    /**
     * 跳转至照片详情
     */
    private static void JumpPhotoDetail(String data) {
//		SpaceOnePicActivity.launch( mContext, data );//gh
    }

    /**
     * 跳转至特定类型圈子列表
     */
    private static void JumpGroupSearch(String id, String categoryid) {
//		if ( CommonFunction.isEmptyOrNullStr( id ) ||
//			CommonFunction.isEmptyOrNullStr( categoryid ) )
//			return;
//		Intent intent = new Intent( mContext, GroupSearchResultActivity.class );
//		intent.putExtra( "keyword", id );
//		intent.putExtra( "categoryId", categoryid );
//		mContext.startActivity( intent );

    }

    /**
     * 跳转至特定圈聚会
     */
    private static void JumpGroupParty(String groupID, String partyID, Context context) {
        if (CommonFunction.isEmptyOrNullStr(groupID) ||
                CommonFunction.isEmptyOrNullStr(partyID))
            return;
        GroupGatherDetail.launch(context, groupID, Integer.parseInt(partyID));
    }

    /**
     * 跳转至特定圈话题
     */
    private static void JumpGroupTopic(String groupID, String topicID) {
        if (CommonFunction.isEmptyOrNullStr(groupID) ||
                CommonFunction.isEmptyOrNullStr(topicID))
            return;
//		TopicDetailActivity.skipToTopicDetail( mContext, Long.parseLong( topicID ), groupID );
    }

    /**
     * 跳转至特定贴吧话题
     */
    private static void JumpPostbarTopic(String postbarID, String topicID) {
        if (CommonFunction.isEmptyOrNullStr(postbarID) ||
                CommonFunction.isEmptyOrNullStr(topicID))
            return;
//		PostbarTopicDetailActivity
//			.launch( mContext, Long.parseLong( postbarID ), "", Long.parseLong( topicID ) );//gh
    }

    /**
     * 跳转至特定动态详情
     */
    private static void JumpDynamicDetail(String dynamicID, Context context) {
        if (CommonFunction.isEmptyOrNullStr(dynamicID))
            return;
        DynamicDetailActivity.skipToDynamicDetail(context, dynamicID, String.valueOf(Common.getInstance().loginUser.getUid()), false);
//		DynamicDetailActivity.skipToDynamicDetail( mContext, Long.parseLong( dynamicID ) );//gh
    }

    /**
     * 跳转到个人动态列表
     *
     * @param context
     * @param uid
     */
    private static void JumpPersonalDynamicList(long uid, Context context) {
        PersonalDynamicActivity.skipToPersonalDynamicActivity(context, (Activity) context, uid, 0);
    }

    /**
     * 跳转至表情商店首页
     */
    private static void JumpFaceStore(Context context) {
        FaceMainActivity.launch(context);
    }

    /**
     * 跳至隐私设置
     */
    private static void JumpPrivacySetting(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, SecretSetActivity.class);//gh
        context.startActivity(intent);
    }

    /**
     * 跳转至礼包详情
     */
    private static void JumpGiftBagDetail(String giftbagId) {
        if (CommonFunction.isEmptyOrNullStr(giftbagId))
            return;
//		StoreGiftPacksActivity.launcherGiftPacksToBuy( mContext, Integer.parseInt( giftbagId ) );//gh
    }

    /**
     * 跳转至Vip首页
     */
    private static void JumpVipActivity(Context context) {
//		VipPrivilegeActivity.lanchVipPrivilegeActivity( mContext );//gh
        UserVIPActivity.startAction(context, Common.getInstance().loginUser.getIcon(), Common.getInstance().loginUser.getSVip());
    }

    /**
     * 跳转至Vip首页
     */
    private static void JumpVipPrivilege(Context context) {
//		VipBuyMemberActivity.lanchVipBuyMemberActivity( mContext, DataTag.UNKONW );//gh
        Intent intent = new Intent(context, UserVipOpenActivity.class);
        context.startActivity(intent);
    }

    /**
     * 跳转至我的钱包首页
     */
    private static void JumpWallet(Context context) {
//		PayMainActivity.lancuPayMainActivity( mContext );//gh
        Intent intent = new Intent();
        intent.setClass(context, MyWalletActivity.class);
        intent.putExtra(Constants.USER_WALLET_PAGE_TYPE, Constants.USER_WALLET_PAGE_WALLET);
        context.startActivity(intent);
    }

    /**
     * 跳转至侃啦详情
     */
    private static void JumpKanLaDetail(String kanlaID) {
//		ThemeSquareActivity.launch( mContext, Long.parseLong( kanlaID ), "" );//gh
    }

    private static void JumpChatbarDetail(String chatbarid) {

    }

    /**
     * 跳转聊吧
     *
     * @param groupId
     */
    private static void JumpGroupChatTopic(String groupId, Context context) {
        if (context == null) {
            context = BaseApplication.appContext;
        }
        GroupChatTopicActivity old = (GroupChatTopicActivity) CloseAllActivity.getInstance().getTargetActivityOne(GroupChatTopicActivity.class);
        if (old != null) {
            old.isGroupIn = true;
            old.finish();
        }
        Intent intent = new Intent(context, GroupChatTopicActivity.class);
        intent.putExtra("id", groupId);
//        context.startActivity(intent);
        GroupChatTopicActivity.ToGroupChatTopicActivity(context, intent);
    }

    /**
     * 跳转吧管理列表
     *
     * @param groupId
     */
    private static void JumpGroupMembers(String groupId, Context context) {
        if (context == null)
            context = BaseApplication.appContext;
        Intent intent = new Intent(context, GroupMemberViewActivity.class);
        intent.putExtra("groupId", groupId);
        context.startActivity(intent);
    }


    /**
     * 跳转至聊吧大厅
     */
    private static void JumpChatbarHall() {

    }

    private static void JumpPersonalCenter() {

    }

    /**
     * 跳转视频列表
     */
    private static void actionVideoList(Context context) {
        Intent intent = new Intent(context, GeneralSubActivity.class);
        intent.putExtra(GeneralSubActivity.KEY_CLASS_NAME, VideoAnchorFragment.class.getName());
        intent.putExtra(GeneralSubActivity.KEY_ACTIVITY_TITLE, BaseApplication.appContext.getString(R.string.video));
        context.startActivity(intent);
    }

    /**
     * 跳转视频详情
     */
    private static void actionVideoDetails(String uid, Context context) {
        Intent intent = new Intent(context, VideoDetailsActivity.class);
        intent.putExtra(VideoDetailsActivity.KEY_VIDEO_UID, Long.valueOf(uid));
        context.startActivity(intent);
    }

    /**
     * 跳转爱心充值
     */
    private static void actionLovePay(Context context) {
        Intent intent = new Intent(context, LovePayActivity.class);
        context.startActivity(intent);
    }

    /**
     * 跳转至兑吧页面
     */
    private static void JumpDuiba(Context context) {
        String key = SharedPreferenceUtil.SHOWCREDITS_AVAILABLE;//兑吧是否开启
        int isShowDuiba = SharedPreferenceUtil.getInstance(BaseApplication.appContext).getInt(key);
        if (isShowDuiba == 1) {
            Intent intent = new Intent();
            intent.setClass(context, CreditActivity.class);
            //配置自动登陆地址，每次需服务端动态生成。
            //该跳转为进入页面再请求url，其他入口请求到url再进入页面
            intent.putExtra("url", "");
            context.startActivity(intent);
        }
    }

    /**
     * 跳转到游戏主播详情页H5
     *
     * @param context
     * @param gameId
     * @param targetUid
     */
    public static void JumpGamerDetail(Context context, long gameId, long targetUid) {
        //统计每个分类分别查看人数
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("uid", Common.getInstance().getUid());
        MobclickAgent.onEvent(BaseApplication.appContext, "watch_home_anchor_person_num", map);
        Jump(context, "http://notice.iaround.com/gamechat_userdetail/details.html?game_id=" + gameId + "&target_uid=" + targetUid);

    }


    /**
     * 跳转到H5下单界面
     *
     * @param context
     * @param anchorid 主播ID
     */
    public static void JumpMakeGameOrder(Context context, long anchorid) {
        InnerJump.Jump(context, "http://notice.iaround.com/gamchat_orders/orderFrom.html?userid=" + anchorid);
    }

    /**
     * 跳转到评价界面
     *
     * @param context
     * @param orderid
     */
    public static void JumpCommentGameOrder(Context context, long orderid) {
        InnerJump.Jump(context, "http://notice.iaround.com/gamchat_orders/comment.html?orderid=" + orderid);
    }

    /**
     * 去支付/订单详情界面
     * 添加当前用户的uid
     *
     * @param context
     * @param orderid
     */
    public static void JumpGameOrderDetail(Context context, long orderid) {
        InnerJump.Jump(context, "http://notice.iaround.com/gamchat_orders/orderDetails.html?orderid=" + orderid + "&userid=" + Common.getInstance().loginUser.getUid());
    }

    /**
     * 跳转到陪玩分类详情列表界面
     *
     * @param context
     * @param gameId
     */
    public static void JumpGameListActivity(String gameId, Context context) {
        Intent intent = new Intent(context, GameListActivity.class);
        intent.putExtra("GameId", Long.parseLong(gameId));
        context.startActivity(intent);
    }

}
