package net.iaround.ui.dynamic;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import net.iaround.R;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.PublishManager;
import net.iaround.connector.protocol.DynamicHttpProtocol;
import net.iaround.model.entity.GeoData;
import net.iaround.model.type.FileUploadType;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.LocationUtil;
import net.iaround.tools.TimeFormat;
import net.iaround.ui.activity.CloseAllActivity;
import net.iaround.ui.activity.MainFragmentActivity;
import net.iaround.ui.datamodel.DynamicModel;
import net.iaround.ui.dynamic.bean.DynamicItemBean;
import net.iaround.ui.dynamic.bean.DynamicPublishBackBean;
import net.iaround.ui.dynamic.bean.DynamicPublishBean;
import net.iaround.ui.fragment.DynamicCenterFragment;
import net.iaround.ui.group.bean.PublishBaseBean;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-9-22 上午11:49:45
 * @Description: 动态发布管理类。所有的动态发布都将通过这个类，用于放在后台发布.[应该限定多长时间内，最多发多少条]
 */
public class DynamicPublishManager extends PublishManager implements
        HttpCallBack {

    public DynamicPublishManager(Context context) {
        super(context);
    }

    @Override
    protected void publishDynamic(PublishBaseBean bean) {
        DynamicPublishBean dynamicBean = (DynamicPublishBean) bean;
        String photoStr = "";// 组装
        int count = dynamicBean.getPhotoList() != null ? bean.getPhotoList()
                .size() : 0;
        for (int i = 0; i < count; i++) {

            photoStr += dynamicBean.getPhotoList().get(i)
                    + (count - i != 1 ? "," : "");
        }

        GeoData geoData = LocationUtil.getCurrentGeo(mContext);
        PUBLISH_REQUEST_FLAG = DynamicHttpProtocol.publishNewDynamic(mContext,
                dynamicBean.type, dynamicBean.getTitle(),
                dynamicBean.getContent(), photoStr, dynamicBean.getAddress(),
                dynamicBean.getShortaddress(), geoData.getLat(),
                geoData.getLng(), "", dynamicBean.getSync(),
                dynamicBean.getSyncvalue(), "", "", this);
        publishSparse.put(PUBLISH_REQUEST_FLAG, bean);
    }

    // 从分享发布动态
    public long publishDynamic(PublishBaseBean bean, HttpCallBack callBack) {
        DynamicPublishBean dynamicBean = (DynamicPublishBean) bean;
        String photoStr = "";// 组装
        int count = dynamicBean.getPhotoList() != null ? bean.getPhotoList()
                .size() : 0;
        for (int i = 0; i < count; i++) {

            photoStr += bean.getPhotoList().get(i)
                    + (count - i != 1 ? "," : "");
        }

        GeoData geoData = LocationUtil.getCurrentGeo(mContext);
        PUBLISH_REQUEST_FLAG = DynamicHttpProtocol.publishNewDynamic(mContext,
                dynamicBean.type, dynamicBean.getTitle(),
                dynamicBean.getContent(), photoStr, dynamicBean.getAddress(),
                dynamicBean.getShortaddress(), geoData.getLat(),
                geoData.getLng(), dynamicBean.getUrl(), dynamicBean.getSync(),
                dynamicBean.getSyncvalue(), dynamicBean.getSharesource(),
                dynamicBean.getSharevalue(), callBack);

        return PUBLISH_REQUEST_FLAG;
    }

    @Override
    protected int getImageUploadType() {
        return FileUploadType.PIC_DYNAMIC_PUBLISH;
    }


    @Override
    public void onGeneralSuccess(String result, long flag) {

        if (PUBLISH_REQUEST_FLAG == flag) {
            DynamicPublishBackBean bean = GsonUtil.getInstance().getServerBean(
                    result, DynamicPublishBackBean.class);

            DynamicPublishBean info = (DynamicPublishBean) publishSparse.get(flag);
            info.dynamicid = bean.dynamicid;
            publishSparse.remove(flag);
            int taskID = (int) (info.datetime & NOTIFICATION_TASK_ID_MASK);

            if (bean.isSuccess()) {
//				getSyncContent( bean.dynamicid , info , ShareType.DYNAMIC_SYNC , this );//jiqiang  分享不迁入

                // 返回成功后，写入缓存，更新状态栏
                ArrayList<DynamicItemBean> unsendSuccessList = DynamicModel.getInstent().getUnSendSuccessList();
                for (int i = 0; i < unsendSuccessList.size(); i++) {
                    DynamicItemBean itemBean = unsendSuccessList.get(i);
                    if (itemBean.getDynamicInfo().dynamicid == info.datetime) {
                        itemBean.getDynamicInfo().dynamicid = bean.dynamicid;
                        itemBean.setSendStatus(DynamicItemBean.SUCCESS);

                        DynamicModel.getInstent().addRecordToDynamicCenterList(itemBean, true);
                        DynamicModel.getInstent().addRecordToMineList(itemBean, true);
                        //动态发布成功 删除缓存的动态
                        unsendSuccessList.remove(itemBean);
                        /***
                         * 保存发送成功的id两分钟 ，在下拉刷新的时候如果没有获取到该ID，将该id的数据插入到显示列表中
                         * (只限于查看自己的个人动态)
                         */
                        DynamicModel.getInstent().addUnreviewedItem(itemBean);

                        break;
                    }
                }
                DynamicModel.getInstent().saveDynamicCenterListToFile();
                DynamicModel.getInstent().saveDynamicMineListToFile();

                updateNotification(taskID, true, info);

                Activity topActivity = CloseAllActivity.getInstance().getTopActivity();
                if (topActivity instanceof MainFragmentActivity) {
                    ((MainFragmentActivity) topActivity).freshAllCount();
                }
//				if (topActivity instanceof SpaceOther) {
//					((SpaceOther) topActivity).refreshPublishDynamic();
//				}//jiqiang  刷新数量
                CommonFunction.toastMsg(mContext, R.string.dynamic_publish_success);
            } else {
                // 发布失败
                ArrayList<DynamicItemBean> unsendSuccessList = DynamicModel.getInstent().getUnSendSuccessList();
                for (int i = 0; i < unsendSuccessList.size(); i++) {
                    DynamicItemBean itemBean = unsendSuccessList.get(i);
                    if (itemBean.getDynamicInfo().dynamicid == info.datetime) {
                        itemBean.setSendStatus(DynamicItemBean.FAIL);
                        if (bean.error == 9306) {
                            unsendSuccessList.remove(itemBean);
                            DynamicCenterFragment.sBannedPublish = true;
                        }else if (bean.error == 4002){
                            unsendSuccessList.remove(itemBean);
                            DynamicCenterFragment.sBannedPublish = true;
                        }
                        break;
                    }
                }

                Activity topActivity = CloseAllActivity.getInstance().getTopActivity();
                if (topActivity instanceof MainFragmentActivity) {
                    ((MainFragmentActivity) topActivity).freshAllCount();
                }
//				if (topActivity instanceof SpaceOther) {
//					((SpaceOther) topActivity).refreshPublishDynamic();
//				}//jiqiang   刷新数量

                if (bean.error == 9305) {//发送过于频繁
                    DynamicCenterFragment.sPublishFrequent = true;
                    CommonFunction.toastMsg(mContext, R.string.dynamic_frequently_notice);
                } else if (bean.error == 9306) {//被系统禁止发布动态
                    if (!CommonFunction.isEmptyOrNullStr(bean.errordesc)) {
                        bannedPublishDynamicBack(bean.errordesc, flag);
                    }
                } else if (bean.error == 4002) {
                    CommonFunction.toastMsg(mContext, R.string.dynamic_publish_content_is_notlegal);
                } else {
                    ErrorCode.showError(mContext, result);
                }

                updateNotification(taskID, false, info);
            }

        } else {
            handleGetSyncSuccess(flag, result);
        }

    }

    @Override
    public void onGeneralError(int e, long flag) {

        if (PUBLISH_REQUEST_FLAG == flag) {
            PublishBaseBean bean = publishSparse.get(flag);
            publishSparse.remove(flag);

            int taskID = (int) (bean.datetime & NOTIFICATION_TASK_ID_MASK);

            ArrayList<DynamicItemBean> unsendSuccessList = DynamicModel.getInstent().getUnSendSuccessList();
            for (int i = 0; i < unsendSuccessList.size(); i++) {
                DynamicItemBean itemBean = unsendSuccessList.get(i);
                if (itemBean.getDynamicInfo().dynamicid == bean.datetime) {
                    itemBean.setSendStatus(DynamicItemBean.FAIL);
                    break;
                }
            }

            Activity topActivity = CloseAllActivity.getInstance().getTopActivity();
            if (topActivity instanceof MainFragmentActivity) {
                ((MainFragmentActivity) topActivity).freshAllCount();
            }
//			if (topActivity instanceof SpaceOther) {
//				((SpaceOther) topActivity).refreshPublishDynamic();
//			}//jiqiang

            updateNotification(taskID, false, bean);
            CommonFunction.toastMsg(mContext, R.string.dynamic_publish_fail);
        } else {
            handleGetSyncContentFailure(flag);
        }

    }

    private void bannedPublishDynamicBack(String errorDesc, long flag) {
        JSONObject json;
        try {
            json = new JSONObject(errorDesc);
            long datetime = json.optLong("datetime");
            int hour = json.optInt("hour");
            String hourStr = TimeFormat.convertTimeLong2String(datetime, Calendar.MINUTE);
            String errStr = mContext.getString(R.string.banned_publish_dynamic, hourStr, ""+hour);
            CommonFunction.toastMsg(mContext, errStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // 添加提示栏
    protected void showNotification(int taskId, PublishBaseBean bean) {

        super.showNotification(taskId, bean);
    }

    /**
     * 更新Notification的状态。
     *
     * @param taskId            任务id
     * @param bIsPublishSuccess 是否发布成功
     */
    protected void updateNotification(int taskId, boolean bIsPublishSuccess,
                                      PublishBaseBean bean) {
        super.updateNotification(taskId, bIsPublishSuccess, bean);
		if (bean == null) {
			return;
		}
		if (bIsPublishSuccess) {
			NotificationManager mNotificationManager = (NotificationManager) mContext
					.getSystemService(Context.NOTIFICATION_SERVICE);
			mNotificationManager.cancel(taskId);
		} else {

			NotificationManager mNotificationManager = (NotificationManager) mContext
					.getSystemService(Context.NOTIFICATION_SERVICE);
			String contentText = mContext.getResources().getString(
					R.string.dynamic_publish_fail);
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
					mContext).setSmallIcon(R.drawable.icon)
					.setContentTitle(bean.getContent())
					.setContentText(contentText).setAutoCancel(true);

			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			intent.setClass(mContext, MainFragmentActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			mBuilder.setContentIntent(PendingIntent.getActivity(mContext, 0,
					intent, 0));
			mBuilder.build().flags = Notification.FLAG_AUTO_CANCEL;

			mNotificationManager.notify(taskId, mBuilder.build());
		}

    }

    @Override
    protected void handleUploadFail(long dynamicId) {

        ArrayList<DynamicItemBean> unsendSuccessList = DynamicModel.getInstent().getUnSendSuccessList();

        for (int i = 0; i < unsendSuccessList.size(); i++) {
            DynamicItemBean itemBean = unsendSuccessList.get(i);
            if (itemBean.getDynamicInfo().dynamicid == dynamicId) {
                itemBean.setSendStatus(DynamicItemBean.FAIL);
                break;
            }
        }
        EventBus.getDefault().post("upload_error");
//        Activity topActivity = CloseAllActivity.getInstance().getTopActivity();
//        if (topActivity instanceof MainFragmentActivity) {
//            ((MainFragmentActivity) topActivity).freshAllCount();
//        }
//		if (topActivity instanceof SpaceOther) {
//			((SpaceOther) topActivity).refreshPublishDynamic();
//		}//jiqiang
    }
}
