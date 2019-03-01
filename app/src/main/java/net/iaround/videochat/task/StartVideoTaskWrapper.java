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

 /* 客户端视频推拉流结果上报
   * */
@TaskProperty(
//        host = STNManager.STN_HOST,
        path = "/iaround/videochat",
        cmdID = Iachat.CMD_ID_START_VIDEO_CHAT,
        longChannelSupport = true,
        shortChannelSupport = false
)
public class StartVideoTaskWrapper extends NanoMarsTaskWrapper<Iavchat.StartVideoChatReq,Iavchat.StartVideoChatRsp> {

    public StartVideoTaskWrapper(ITaskEndListener listener){
        super(new Iavchat.StartVideoChatReq(), new Iavchat.StartVideoChatRsp());
        mListener = new WeakReference<ITaskEndListener>(listener);
    }

    @Override
    public void onPreEncode(Iavchat.StartVideoChatReq request) {
        request.authToken = TaskWrapperManager.getInstance().getAccessToken();
        request.roomid = getProperties().getLong("roomid");
        request.ready = getProperties().getInt("ready");
        request.ts = System.currentTimeMillis();
    }

    @Override
    public void onPostDecode(Iavchat.StartVideoChatRsp response) {

    }

    @Override
    public void onTaskEnd(final int errType, final int errCode) {
        log("onTaskEnd() into, errType=" + errType + "， errCode=" + errCode);
         mHandler.post(new Runnable() {
            @Override
            public void run() {
                ITaskEndListener listener = mListener.get();
                if(listener!=null){
                    listener.onTaskEnd(StartVideoTaskWrapper.this,errType, errCode, request, response);
                }else{
                    //log("onTaskEnd() activity null");
                }
            }
        });
    }
}