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

/* 关闭视频
    * */
@TaskProperty(
//        host = STNManager.STN_HOST,
        path = "/iaround/videochat",
        cmdID = Iachat.CMD_ID_CLOSE_VIDEO_CHAT,
        longChannelSupport = true,
        shortChannelSupport = false
)
public class CloseVideoTaskWrapper extends NanoMarsTaskWrapper<Iavchat.CloseVideoChatReq,Iavchat.CloseVideoChatRsp> {

    public CloseVideoTaskWrapper(ITaskEndListener listener){
        super(new Iavchat.CloseVideoChatReq(), new Iavchat.CloseVideoChatRsp());
        mListener = new WeakReference<ITaskEndListener>(listener);
    }

    @Override
    public void onPreEncode(Iavchat.CloseVideoChatReq request) {

        request.roomid = getProperties().getLong("roomid");
        request.closeState = getProperties().getInt("close_state");
        request.authToken = TaskWrapperManager.getInstance().getAccessToken();

    }

    @Override
    public void onPostDecode(Iavchat.CloseVideoChatRsp response) {

    }

    @Override
    public void onTaskEnd(final int errType, final int errCode) {
         mHandler.post(new Runnable() {
            @Override
            public void run() {
                ITaskEndListener listener = mListener.get();
                if(listener!=null){
                    listener.onTaskEnd(CloseVideoTaskWrapper.this,errType, errCode, request, response);
                }else{
                    //log("onTaskEnd() activity null");
                }
            }
        });
    }
}
