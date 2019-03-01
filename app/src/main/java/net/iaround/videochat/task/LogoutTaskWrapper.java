package net.iaround.videochat.task;

import android.os.Handler;
import android.os.Looper;

import net.iaround.im.proto.Iachat;
import net.iaround.im.service.STNService;
import net.iaround.im.task.ITaskEndListener;
import net.iaround.im.task.NanoMarsTaskWrapper;
import net.iaround.im.task.TaskProperty;

import java.lang.ref.WeakReference;

/**
 * Created by liangyuanhuan on 15/12/2017.
 */

/*登出
    * */
@TaskProperty(
//        host = STNManager.STN_HOST,
        path = "/iaround/videochat",
        cmdID = Iachat.CMD_ID_LOGOUT,
        longChannelSupport = true,
        shortChannelSupport = false
)
public class LogoutTaskWrapper extends NanoMarsTaskWrapper<Iachat.LogoutReq,Iachat.LogoutRsp> {

    public LogoutTaskWrapper(ITaskEndListener listener){
        super(new Iachat.LogoutReq(), new Iachat.LogoutRsp());
        mListener = new WeakReference<ITaskEndListener>(listener);
    }

    @Override
    public void onPreEncode(Iachat.LogoutReq request) {
        request.uid = getProperties().getLong("uid");
        request.authToken = TaskWrapperManager.getInstance().getAccessToken();
    }

    @Override
    public void onPostDecode(Iachat.LogoutRsp response) {

    }

    @Override
    public void onTaskEnd(final int errType, final int errCode) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                ITaskEndListener listener =  mListener.get();
                if(listener!=null){
                    listener.onTaskEnd(LogoutTaskWrapper.this,errType, errCode, request, response);
                }else{
                    log("onTaskEnd() listener null");
                }
            }
        });
    }
}