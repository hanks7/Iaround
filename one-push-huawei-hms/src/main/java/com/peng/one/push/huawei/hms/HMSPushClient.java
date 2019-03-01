package com.peng.one.push.huawei.hms;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import com.huawei.hms.api.ConnectionResult;
import com.huawei.hms.api.HuaweiApiClient;
import com.huawei.hms.support.api.client.ResultCallback;
import com.huawei.hms.support.api.push.HuaweiPush;
import com.huawei.hms.support.api.push.TokenResult;
import com.peng.one.push.OnePush;
import com.peng.one.push.OneRepeater;
import com.peng.one.push.cache.OnePushCache;
import com.peng.one.push.core.IPushClient;
import com.peng.one.push.log.OneLog;
import java.util.Collections;

/**
 * HMS推送客户端
 * Created by pyt on 2017/5/15.
 */

public class HMSPushClient implements IPushClient {

  private static final String TAG = "HMSPushClient";

  private Context context;
  private HuaweiApiClient huaweiApiClient;

  @Override
  public void initContext(final Context context) {
    this.context = context.getApplicationContext();
    huaweiApiClient = new HuaweiApiClient.Builder(context).addApi(HuaweiPush.PUSH_API)
            .addConnectionCallbacks(new HuaweiApiClient.ConnectionCallbacks() {
              @Override
              public void onConnected() {
                //华为移动服务client连接成功，在这边处理业务自己的事件
                OneLog.i("HMS connect success!");

                String tokenStr = OnePushCache.getToken(HMSPushClient.this.context);

                String token = "";

                OneLog.i("111111111     token  = " + tokenStr);
                if (!TextUtils.isEmpty(tokenStr) && tokenStr.contains("_")){
                  String[] tokens = tokenStr.split("_");
                  long time = 0;
                  if (!TextUtils.isEmpty(tokens[0]))
                    time = Long.valueOf(tokens[0]);
                  if (tokens.length > 1)
                    token = tokens[1];
                  if(System.currentTimeMillis() - time > 1000 * 60 * 60 * 24){
                    OneLog.i("333333333333");
                    getToken();
                    new Thread(new Runnable() {
                      @Override
                      public void run() {
                        HuaweiPush.HuaweiPushApi.enableReceiveNormalMsg(huaweiApiClient, true);
                      }
                    }).start();
                  }else{
//                    OneRepeater.transmitCommandResult(HMSPushClient.this.context, OnePush.TYPE_REGISTER,
//                            OnePush.RESULT_OK, token, null, null);
                    OneRepeater.transmitCommandResult(context, OnePush.TYPE_REGISTER,OnePush.RESULT_OK,token,null,null);
                  }
                }else{
                  OneLog.i("22222222222");
                  getToken();
                  new Thread(new Runnable() {
                    @Override
                    public void run() {
                      HuaweiPush.HuaweiPushApi.enableReceiveNormalMsg(huaweiApiClient, true);
                    }
                  }).start();
                }


//                new Thread(new Runnable() {
//                  @Override
//                  public void run() {
//                    HuaweiPush.HuaweiPushApi.enableReceiveNotifyMsg(huaweiApiClient, true);
//                  }
//                }).start();

              }

              @Override
              public void onConnectionSuspended(int i) {
                if (huaweiApiClient != null) {
                  huaweiApiClient.connect();
                }
                OneLog.i("HMS disconnect,retry.");
              }
            })
            .addOnConnectionFailedListener(new HuaweiApiClient.OnConnectionFailedListener() {
              @Override
              public void onConnectionFailed(ConnectionResult connectionResult) {
                OneRepeater.transmitCommandResult(HMSPushClient.this.context, OnePush.TYPE_REGISTER,
                        OnePush.RESULT_ERROR, null, String.valueOf(connectionResult.getErrorCode()), "huawei-hms register error code : "+connectionResult.getErrorCode());
              }
            })
            .build();
  }

  private void getToken() {
    if(!huaweiApiClient.isConnected()) {
      OneLog.i("获取TOKEN失败，原因：HuaweiApiClient未连接");
      return;
    }
    HuaweiPush.HuaweiPushApi.getToken(huaweiApiClient)
            .setResultCallback(new ResultCallback<TokenResult>() {
              @Override
              public void onResult(TokenResult tokenResult) {
                OneLog.i("token " + tokenResult.getTokenRes());
                if (tokenResult.getTokenRes() != null && !TextUtils.isEmpty(
                        tokenResult.getTokenRes().getToken())) {

                  OneRepeater.transmitCommandResult(HMSPushClient.this.context, OnePush.TYPE_REGISTER,
                          OnePush.RESULT_OK, tokenResult.getTokenRes().getToken(), null, null);
                }
              }
            });
  }

  @Override
  public void register() {
    if (!huaweiApiClient.isConnected()) {
      huaweiApiClient.connect();
    }
//    getToken();
  }

  @Override
  public void unRegister() {
    //        huaweiApiClient.disconnect();
    OneLog.i("unRegister");
    if(!huaweiApiClient.isConnected()) {
      OneLog.i("注销TOKEN失败，原因：HuaweiApiClient未连接");
      return;
    }
    final String token = OnePushCache.getToken(context);
    if (!TextUtils.isEmpty(token)) {
      new Thread() {
        @Override
        public void run() {
          super.run();
          HuaweiPush.HuaweiPushApi.deleteToken(huaweiApiClient, token);
          HuaweiPush.HuaweiPushApi.enableReceiveNormalMsg(huaweiApiClient, false);
          HuaweiPush.HuaweiPushApi.enableReceiveNotifyMsg(huaweiApiClient, false);
        }
      }.start();
    }
  }

  @Override
  public void bindAlias(String alias) {
    //hua wei push is not support bind account
  }

  @Override
  public void unBindAlias(String alias) {
    //hua wei push is not support unbind account
  }

  @Override
  public void addTag(String tag) {
    if (TextUtils.isEmpty(tag)) {
      return;
    }
    HuaweiPush.HuaweiPushApi.setTags(huaweiApiClient, Collections.singletonMap(tag, tag));
  }

  @Override
  public void deleteTag(String tag) {
    if (TextUtils.isEmpty(tag)) {
      return;
    }
    HuaweiPush.HuaweiPushApi.deleteTags(huaweiApiClient, Collections.singletonList(tag));
  }

}