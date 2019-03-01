package net.iaround.videochat.task;

import net.iaround.im.proto.Iachat;
import net.iaround.im.service.STNService;
import net.iaround.im.task.ITaskEndListener;
import net.iaround.im.task.NanoMarsTaskWrapper;
import net.iaround.im.task.TaskProperty;

import java.lang.ref.WeakReference;

/**
 * Created by liangyuanhuan on 15/12/2017.
 */

 /*登陆
    * */
@TaskProperty(
//        host = STNManager.STN_HOST,
        path = "/iaround/videochat",
        cmdID = Iachat.CMD_ID_LOGIN,
        longChannelSupport = true,
        shortChannelSupport = false
)
public class LoginTaskWrapper extends NanoMarsTaskWrapper<Iachat.LoginReq,Iachat.LoginRsp> {
    public static final String TAG = "IAround_LoginTask";

    public LoginTaskWrapper(ITaskEndListener listener){
        super(new Iachat.LoginReq(), new Iachat.LoginRsp());
        mListener = new WeakReference<ITaskEndListener>(listener);
    }

    @Override
    public void onPreEncode(Iachat.LoginReq request) {
        getProperties().getInt("");
        request.uid = getProperties().getLong("uid");
        request.authType = getProperties().getInt("authType");
        request.authToken = TaskWrapperManager.getInstance().getAccessToken();
        request.timestamp = System.currentTimeMillis();
    }

    @Override
    public void onPostDecode(Iachat.LoginRsp response) {

    }

    @Override
    public void onTaskEnd(final int errType, final int errCode) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                ITaskEndListener listener = mListener.get();
                if(listener!=null){
                    listener.onTaskEnd(LoginTaskWrapper.this,errType,errCode,request,response);
                }else{
                    //log("onTaskEnd() activity null");
                }
            }
        });
    }
}
