package net.iaround.videochat.task;

import android.os.Handler;
import android.os.Looper;

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

/* 视频邀请确认
    * */
@TaskProperty(
//        host = STNManager.STN_HOST,
        path = "/iaround/videochat",
        cmdID = Iachat.CMD_ID_CONFIRM_VIDEO_CHAT,
        longChannelSupport = true,
        shortChannelSupport = false
)
public class ConfirmVideoTaskWrapper extends NanoMarsTaskWrapper<Iavchat.ConfirmVideoChatReq,Iavchat.ConfirmVideoChatRsp> {
    private static final String TAG = "IAround_ConfirmVideoTaskWrapper";
    public ConfirmVideoTaskWrapper(ITaskEndListener listener){
        super(new Iavchat.ConfirmVideoChatReq(), new Iavchat.ConfirmVideoChatRsp());
        mListener = new WeakReference<ITaskEndListener>(listener);
    }

    @Override
    public void onPreEncode(Iavchat.ConfirmVideoChatReq request) {
        request.authToken = TaskWrapperManager.getInstance().getAccessToken();
        request.roomid = getProperties().getLong("roomid");
        log("request, roomid=" + request.roomid );
    }

    @Override
    public void onPostDecode(Iavchat.ConfirmVideoChatRsp response) {

    }

    @Override
    public void onTaskEnd(final int errType, final int errCode) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                ITaskEndListener listener = mListener.get();
                if(listener!=null){
                    listener.onTaskEnd(ConfirmVideoTaskWrapper.this,errType, errCode, request, response);
                }else{
                    //log("onTaskEnd() activity null");
                }
            }
        });
    }
}
