package net.iaround.im.task;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.MessageNano;
import com.tencent.mars.stn.StnLogic;

import net.iaround.utils.MemoryDump;

import java.lang.ref.WeakReference;

/**
 * using nano protocol buffer encoding
 *
 * Created by liangyuanhuan on 07/12/2017.
 */

public abstract class NanoMarsTaskWrapper<T extends MessageNano, R extends MessageNano> extends AbstractTaskWrapper {

    private static final String TAG = "IAround_NanoTaskWrapper";

    protected T request;
    protected R response;
    protected WeakReference<ITaskEndListener> mListener;
    protected Handler mHandler;

    public NanoMarsTaskWrapper(T req, R resp) {
        super();

        this.request = req;
        this.response = resp;
        this.mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public byte[] req2buf() {
        try {

            onPreEncode(request);

            final byte[] flatArray = new byte[request.getSerializedSize()];
            final CodedOutputByteBufferNano output = CodedOutputByteBufferNano.newInstance(flatArray);
            request.writeTo(output);

            Log.d(TAG, "encoded request to buffer, ["+ MemoryDump.dumpHex(flatArray) + "]");

            return flatArray;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new byte[0];
    }

    @Override
    public int buf2resp(byte[] buf) {
        try {
            Log.d(TAG, "decode response buffer, ["+MemoryDump.dumpHex(buf)+"]");

            response = MessageNano.mergeFrom(response, buf);
            onPostDecode(response);

            //Log.d(TAG,"req2buf() response string:"+response.toString());

            return StnLogic.RESP_FAIL_HANDLE_NORMAL;

        } catch (Exception e) {
            Log.e(TAG, "%s", e);
        }

        return StnLogic.RESP_FAIL_HANDLE_TASK_END;
    }

    public abstract void onPreEncode(T request);

    public abstract void onPostDecode(R response);
}


