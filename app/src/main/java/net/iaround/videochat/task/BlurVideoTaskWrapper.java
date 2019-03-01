package net.iaround.videochat.task;

import net.iaround.im.proto.Iachat;
import net.iaround.im.proto.Iavchat;
import net.iaround.im.task.ITaskEndListener;
import net.iaround.im.task.NanoMarsTaskWrapper;
import net.iaround.im.task.TaskProperty;

import java.lang.ref.WeakReference;

/**
 * Created by liangyuanhuan on 15/12/2017.
 */


    /* 模糊视频
   * */
@TaskProperty(
//        host = STNManager.STN_HOST,
        path = "/iaround/videochat",
        cmdID = Iachat.CMD_ID_BLUR_VIDEO_CHAT,
        longChannelSupport = true,
        shortChannelSupport = false
)
public class BlurVideoTaskWrapper extends NanoMarsTaskWrapper<Iavchat.BluerReq,Iavchat.BluerRsp> {

    public BlurVideoTaskWrapper(ITaskEndListener listener){
        super(new Iavchat.BluerReq(), new Iavchat.BluerRsp());
        mListener = new WeakReference<ITaskEndListener>(listener);
    }

    @Override
    public void onPreEncode(Iavchat.BluerReq request) {
        request.authToken = TaskWrapperManager.getInstance().getAccessToken();
        request.roomid = getProperties().getLong("roomid");
        request.open = getProperties().getLong("open");
    }

    @Override
    public void onPostDecode(Iavchat.BluerRsp response) {

    }

    @Override
    public void onTaskEnd(final int errType, final int errCode) {
         mHandler.post(new Runnable() {
            @Override
            public void run() {
                ITaskEndListener listener = mListener.get();
                if(listener!=null){
                    listener.onTaskEnd(BlurVideoTaskWrapper.this,errType, errCode, request, response);
                }else{
                    //log("onTaskEnd() activity null");
                }
            }
        });
    }
}
