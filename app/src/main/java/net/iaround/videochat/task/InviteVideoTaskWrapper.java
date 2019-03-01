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

 /* 视频邀请发起
    * */
@TaskProperty(
//        host = STNManager.STN_HOST,
        path = "/iaround/videochat",
        cmdID = Iachat.CMD_ID_INVITE_VIDEO_CHAT,
        longChannelSupport = true,
        shortChannelSupport = false
)
public class InviteVideoTaskWrapper extends NanoMarsTaskWrapper<Iavchat.InviteVideoChatReq,Iavchat.InviteVideoChatRsp> {

    public InviteVideoTaskWrapper(ITaskEndListener listener){
        super(new Iavchat.InviteVideoChatReq(), new Iavchat.InviteVideoChatRsp());
        mListener = new WeakReference<ITaskEndListener>(listener);
    }

    @Override
    public void onPreEncode(Iavchat.InviteVideoChatReq request) {
        request.authToken = TaskWrapperManager.getInstance().getAccessToken();
        request.from = getProperties().getLong("from");
        request.to = getProperties().getLong("to");
        request.follow = getProperties().getInt("follow");
    }

    @Override
    public void onPostDecode(Iavchat.InviteVideoChatRsp response) {

    }

    @Override
    public void onTaskEnd(final int errType, final int errCode) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                ITaskEndListener listener = mListener.get();
                if(listener!=null){
                    listener.onTaskEnd(InviteVideoTaskWrapper.this,errType, errCode, request, response);
                }else{
                    //log("onTaskEnd() activity null");
                }
            }
        });
    }
}
