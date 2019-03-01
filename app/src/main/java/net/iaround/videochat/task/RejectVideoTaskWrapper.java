package net.iaround.videochat.task;

import android.os.Handler;
import android.os.Looper;

import net.iaround.im.STNManager;
import net.iaround.im.proto.Iachat;
import net.iaround.im.proto.Iavchat;
import net.iaround.im.service.STNService;
import net.iaround.im.task.ITaskEndListener;
import net.iaround.im.task.NanoMarsTaskWrapper;
import net.iaround.im.task.TaskProperty;

import java.lang.ref.WeakReference;

/**
 * Created by liangyuanhuan on 15/12/2017.
 */

/* 拒绝邀请视频
  * */
@TaskProperty(
//        host = STNManager.STN_HOST,
        path = "/iaround/videochat",
        cmdID = Iachat.CMD_ID_REJECT_VIDEO_CHAT,
        longChannelSupport = true,
        shortChannelSupport = false
)
public class RejectVideoTaskWrapper extends NanoMarsTaskWrapper<Iavchat.RejectVideoChatReq,Iavchat.RejectVideoChatRsp> {

    public RejectVideoTaskWrapper(ITaskEndListener listener){
        super(new Iavchat.RejectVideoChatReq(), new Iavchat.RejectVideoChatRsp());
        mListener = new WeakReference<ITaskEndListener>(listener);
    }

    @Override
    public void onPreEncode(Iavchat.RejectVideoChatReq request) {
            request.authToken = TaskWrapperManager.getInstance().getAccessToken();
            request.roomid = getProperties().getLong("roomid");
    }

    @Override
    public void onPostDecode(Iavchat.RejectVideoChatRsp response) {

    }

    @Override
    public void onTaskEnd(final int errType, final int errCode) {
        log("onTaskEnd() into, errType=" + errType + "， errCode=" + errCode);
         mHandler.post(new Runnable() {
            @Override
            public void run() {
                ITaskEndListener listener = mListener.get();
                if(listener!=null){
                    listener.onTaskEnd(RejectVideoTaskWrapper.this,errType, errCode, request, response);
                }else{
                    //log("onTaskEnd() activity null");
                }
            }
        });
    }
}

