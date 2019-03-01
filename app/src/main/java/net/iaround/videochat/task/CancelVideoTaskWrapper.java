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


    /* 取消邀请视频
   * */
@TaskProperty(
//        host = STNManager.STN_HOST,
        path = "/iaround/videochat",
        cmdID = Iachat.CMD_ID_CANCEL_VIDEO_CHAT,
        longChannelSupport = true,
        shortChannelSupport = false
)
public class CancelVideoTaskWrapper extends NanoMarsTaskWrapper<Iavchat.CancelVideoChatReq,Iavchat.CancelVideoChatRsp> {

    public CancelVideoTaskWrapper(ITaskEndListener listener){
        super(new Iavchat.CancelVideoChatReq(), new Iavchat.CancelVideoChatRsp());
        mListener = new WeakReference<ITaskEndListener>(listener);
    }

    @Override
    public void onPreEncode(Iavchat.CancelVideoChatReq request) {
            request.authToken = TaskWrapperManager.getInstance().getAccessToken();
            request.roomid = getProperties().getLong("roomid");
            request.flag = getProperties().getInt("flag", 0);
    }

    @Override
    public void onPostDecode(Iavchat.CancelVideoChatRsp response) {

    }

    @Override
    public void onTaskEnd(final int errType, final int errCode) {
         mHandler.post(new Runnable() {
            @Override
            public void run() {
                ITaskEndListener listener = mListener.get();
                if(listener!=null){
                    listener.onTaskEnd(CancelVideoTaskWrapper.this,errType, errCode, request, response);
                }else{
                    //log("onTaskEnd() activity null");
                }
            }
        });
    }
}
