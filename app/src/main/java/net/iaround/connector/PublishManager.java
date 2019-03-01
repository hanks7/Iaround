
package net.iaround.connector;


import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.LongSparseArray;

import net.iaround.R;
import net.iaround.conf.Common;
import net.iaround.conf.Constant;
import net.iaround.connector.BatchUploadManager.BatchUploadCallBack;
import net.iaround.share.utils.AbstractShareUtils;
import net.iaround.tools.CommonFunction;
import net.iaround.ui.dynamic.DynamicPublishManager;
import net.iaround.ui.dynamic.GroupTopicPublishManager;
import net.iaround.ui.dynamic.PublishDynamicActivity;
import net.iaround.ui.group.bean.PublishBaseBean;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-11-7 下午4:28:07
 * @Description: 发布(动态, 话题, 贴吧)的管理基类
 */
public abstract class PublishManager implements BatchUploadCallBack {

    // 批量上传图片的管理类
    private BatchUploadManager batchUploadManager;
    protected Context mContext;

    // 记录动态信息的Array,Key为上传图片任务的id.为了确定上传的图片属于哪个动态
    protected LongSparseArray<PublishBaseBean> dynamicSparseTask = new LongSparseArray<PublishBaseBean>();// 动态任务Map
    // 记录可以发布动态的Array
    protected LongSparseArray<PublishBaseBean> publishSparse = new LongSparseArray<PublishBaseBean>();

    // 因为最多只能发6张图片,所以用3位就足够,[0111] 0~7,第一张图片为0；
    protected long IMAGE_TASK_MASK = Long.MAX_VALUE ^ 7;// Image对应Task的Mask
    public static int NOTIFICATION_TASK_ID_MASK = Integer.MAX_VALUE;// 用于获取notification的任务id的mask

    protected long PUBLISH_REQUEST_FLAG;// 发布请求的Flag
    protected HashMap<Long, Integer> sendHashMap;// 获取同步内容的Flag

    private PublishHandler mHandler = new PublishHandler();
    private final int FAIL_FLAG = 1000;

    public final int SYNC_TO_OTHER_PLAT_MSG = 27;
    public SyncHandler syncHandler = new SyncHandler();

    protected class PublishHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            if (msg.what == FAIL_FLAG) {
                CommonFunction.toastMsg(mContext, R.string.dynamic_publish_fail);
            }
        }
    }

    public class SyncHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);
            if (msg.what == SYNC_TO_OTHER_PLAT_MSG) {
                String res = String.valueOf(msg.obj);
                int plat = msg.arg1;
                if (plat != 0 && !"".equals(res)) {
//					ShareContentBean shareContentBean = ShareModel.parseShareContent( res );
//
//					syncToOtherPlatform( plat , shareContentBean.title ,
//							shareContentBean.content , shareContentBean.link ,
//							shareContentBean.thumb , shareContentBean.pic );//jiqiang 分享不迁入
                } else {
                    CommonFunction
                            .toastMsg(mContext, R.string.get_share_content_failure_msg);
                }
            }
        }
    }

    public PublishManager(Context context) {
        mContext = context;
        batchUploadManager = new BatchUploadManager(mContext);
    }

    public static PublishManager create(Context context, int type) {
        if (type == PublishDynamicActivity.DYNAMIC) {
            return new DynamicPublishManager(context);
        } else if (type == PublishDynamicActivity.GROUP_TOPIC) {
            return new GroupTopicPublishManager(context);
        }
//		else if ( type == PublishDynamicActivity.POST_BAR )
//		{
//			return new PostBarPublishManager( context );
//		}//jiqiang
        return null;
    }

    /**
     * 添加一个动态任务
     */
    public void addTask(PublishBaseBean bean) {
        final long taskFlag = bean.datetime & IMAGE_TASK_MASK;
        dynamicSparseTask.put(taskFlag, bean);


        int taskID = (int) (bean.datetime & NOTIFICATION_TASK_ID_MASK);
        showNotification(taskID, bean);

        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    handleTask(taskFlag);
                } catch (Exception e) {
                    // 图片文件不存在
                    e.printStackTrace();
                }
            }
        }).start();

    }

    /**
     * 展示Notification的状态。
     *
     * @param taskId Notification的id
     * @param bean
     */
    protected void showNotification(int taskId, PublishBaseBean bean) {
    }

    /**
     * 更新Notification的状态。
     *
     * @param taskId            任务id
     * @param bIsPublishSuccess 是否发布成功
     */
    protected void updateNotification(int taskId, boolean bIsPublishSuccess,
                                      PublishBaseBean bean) {
    }

    // 任务处理，区分纯文本和图文
    private void handleTask(long taskFlag) throws Exception {
        PublishBaseBean bean = dynamicSparseTask.get(taskFlag);
        if (bean.getPhotoList() == null || bean.getPhotoList().size() == 0)// 纯文本，直接发布
        {
            publishDynamic(bean);
        } else {
            batchUploadManager.uploadImage(taskFlag, bean.getPhotoList(),
                    getImageUploadType(), this);
        }
    }

    @Override
    public void batchUploadSuccess(long taskFlag, ArrayList<String> serverUrlList) {
        PublishBaseBean bean = dynamicSparseTask.get(taskFlag);
        bean.setPhotoList(serverUrlList);
        publishDynamic(bean);
    }

    @Override
    public void batchUploadFail(long taskFlag) {
        // 如果上传失败，状态栏显示，失败，可以重新发布。
        PublishBaseBean info = dynamicSparseTask.get(taskFlag);
        if (info != null) {

            int taskID = (int) (info.datetime & NOTIFICATION_TASK_ID_MASK);
            updateNotification(taskID, false, info);
            handleUploadFail(info.datetime);

            dynamicSparseTask.remove(taskFlag);
            mHandler.sendEmptyMessage(FAIL_FLAG);
        }
    }

    // 动态发布
    protected abstract void publishDynamic(PublishBaseBean bean);

    // 获取照片上传的类型
    protected abstract int getImageUploadType();

    protected abstract void handleUploadFail(long flag);

    /**
     * 获取同步内容
     */
    protected void getSyncContent(long targetId, PublishBaseBean bean, int shareType,
                                  HttpCallBack callBack) {
        ArrayList<Integer> list = bean.getShareList();
        if (list != null && list.size() > 0) {
            long syncFlag = 0;
            if (sendHashMap == null) {
                sendHashMap = new HashMap<Long, Integer>();
            } else {
                if (sendHashMap.size() > 0) {
                    sendHashMap.clear();
                }
            }
            for (int i = 0; i < list.size(); i++) {
//				syncFlag = ShareHttpProtocol.requestShareContent( mContext , shareType , 1 ,
//						String.valueOf( list.get( i ) ) , String.valueOf( targetId ) ,
//						callBack );
//				sendHashMap.put( syncFlag , list.get( i ) );//jiqiang
            }
        }
    }

    /**
     * 处理获取同步内容成功的返回
     */
    protected void handleGetSyncSuccess(long flag, String result) {
        if (sendHashMap != null && sendHashMap.size() > 0) {
            if (sendHashMap.containsKey(flag)) {
                if (Constant.isSuccess(result)) {
                    Message shareMessage = new Message();
                    shareMessage.what = SYNC_TO_OTHER_PLAT_MSG;
                    shareMessage.arg1 = sendHashMap.get(flag);
                    shareMessage.obj = result;
                    syncHandler.sendMessage(shareMessage);
                } else {
                    Message shareMessage = new Message();
                    shareMessage.what = SYNC_TO_OTHER_PLAT_MSG;
                    shareMessage.arg1 = 0;
                    shareMessage.obj = "";
                    syncHandler.sendMessage(shareMessage);
                }
                sendHashMap.remove(flag);
            }
        }
    }

    /**
     * 处理获取同步内容失败的返回
     */
    protected void handleGetSyncContentFailure(long flag) {
        if (sendHashMap != null && sendHashMap.size() > 0) {
            if (sendHashMap.containsKey(flag)) {
                Message shareMessage = new Message();
                shareMessage.what = SYNC_TO_OTHER_PLAT_MSG;
                shareMessage.arg1 = 0;
                shareMessage.obj = "";
                syncHandler.sendMessage(shareMessage);

                sendHashMap.remove(flag);
            }
        }

    }

    /**
     * 同步到选中的第三方平台
     */
    protected void syncToOtherPlatform(int platform, String title, String content,
                                       String link, String thumb, String pic) {
        long uid = Common.getInstance().loginUser.getUid();
        final AbstractShareUtils weibo = AbstractShareUtils.getSingleShareUtil(mContext,
                uid, platform);
        weibo.share2Weibo(((Activity) mContext), title, content, link, thumb, pic);
    }
}
