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

 /*
    * 被叫定时扣费上报
    * */
@TaskProperty(
//        host = STNManager.STN_HOST,
        path = "/iaround/videochat",
        cmdID = Iachat.CMD_ID_PAY,
        longChannelSupport = true,
        shortChannelSupport = false
)
public class PayVerifyTaskWrapper extends NanoMarsTaskWrapper<Iavchat.PayVerifyReq,Iavchat.PayVerifyRsp> {

    public PayVerifyTaskWrapper(ITaskEndListener activity){
        super(new Iavchat.PayVerifyReq(), new Iavchat.PayVerifyRsp());
        mListener = new WeakReference<ITaskEndListener>(activity);
    }

    @Override
    public void onPreEncode(Iavchat.PayVerifyReq request) {

            request.authToken = TaskWrapperManager.getInstance().getAccessToken();
            request.roomid = getProperties().getLong("roomid");
            request.ts = System.currentTimeMillis();

    }

    @Override
    public void onPostDecode(Iavchat.PayVerifyRsp response) {

    }

    @Override
    public void onTaskEnd(final int errType, final int errCode) {
        log("onTaskEnd() into, errType=" + errType + "， errCode=" + errCode);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                ITaskEndListener listener = mListener.get();
                if(listener!=null){
                    listener.onTaskEnd(PayVerifyTaskWrapper.this,errType, errCode, request, response);
                }else{
                    //log("onTaskEnd() activity null");
                }
            }
        });
    }
}