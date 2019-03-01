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
    * 主叫定时扣费上报
    * */
@TaskProperty(
//        host = STNManager.STN_HOST,
        path = "/iaround/videochat",
        cmdID = Iachat.CMD_ID_PAY,
        longChannelSupport = true,
        shortChannelSupport = false
)
public class PayTaskWrapper extends NanoMarsTaskWrapper<Iavchat.PayReq,Iavchat.PayRsp> {

    public PayTaskWrapper(ITaskEndListener activity){
        super(new Iavchat.PayReq(), new Iavchat.PayRsp());
        mListener = new WeakReference<ITaskEndListener>(activity);
    }

    @Override
    public void onPreEncode(Iavchat.PayReq request) {
            request.authToken = TaskWrapperManager.getInstance().getAccessToken();
            request.roomid = getProperties().getLong("roomid");

    }

    @Override
    public void onPostDecode(Iavchat.PayRsp response) {

    }

    @Override
    public void onTaskEnd(final int errType, final int errCode) {
        log("onTaskEnd() into, errType=" + errType + "， errCode=" + errCode);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                ITaskEndListener listener = mListener.get();
                if(listener!=null){
                    listener.onTaskEnd(PayTaskWrapper.this,errType, errCode, request, response);
                }else{
                    //log("onTaskEnd() activity null");
                }
            }
        });
    }
}
