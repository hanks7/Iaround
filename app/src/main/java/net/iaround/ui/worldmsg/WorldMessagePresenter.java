package net.iaround.ui.worldmsg;

import android.util.Log;

import com.alibaba.fastjson.JSON;

import net.iaround.BaseApplication;
import net.iaround.conf.Constant;
import net.iaround.conf.ErrorCode;
import net.iaround.conf.MessageID;
import net.iaround.connector.CallBackNetwork;
import net.iaround.connector.ConnectorManage;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.SendWorldMessageProtocol;
import net.iaround.connector.protocol.SocketSessionProtocol;
import net.iaround.model.chat.HistoryWorldMessageBean;
import net.iaround.model.chat.WorldMessageRecord;
import net.iaround.model.im.TransportMessage;
import net.iaround.tools.GsonUtil;
import net.iaround.utils.SkillHandleUtils;

import java.util.ArrayList;

/**
 * 作者：zx on 2017/8/26 16:40
 */
public class WorldMessagePresenter extends WorldMessageContract.Presenter implements CallBackNetwork{

    private WorldMessageContract.View mView;

    public WorldMessagePresenter(WorldMessageContract.View view) {
        this.mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void onReceiveMessage(TransportMessage message) {
        switch (message.getMethodId()) {
            case MessageID.SESSION_GROUP_WORLD_MESSAGE:
                parseMessageToBean(message.getContentBody());
                break;
            case MessageID.WORLD_MESSAGE_HISTORY:
                parseHistoryMessageToBean(message.getContentBody());
                break;
        }
    }

    /**
     *
     * @param message
     */
    private void parseHistoryMessageToBean(String message) {
        ArrayList<WorldMessageRecord> recordList = new ArrayList<>();
        HistoryWorldMessageBean historyBean = GsonUtil.getInstance().getServerBean(message, HistoryWorldMessageBean.class);
        if (2 == historyBean.type){//剔除技能历史消息
            return;
        }
        if (null == historyBean){
            mView.refreshCompleted();
            return;
        }
        if (null != historyBean.message && historyBean.message.size() > 0){
            for (int i = historyBean.message.size() - 1; i >= 0; i--){
                com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(historyBean.message.get(i));
                String content = JSON.toJSONString(jsonObject);
                WorldMessageRecord record = SkillHandleUtils.pareseToRecord(content);
                if (null != record){
                    recordList.add(record);
                }
            }
        }
        Log.d("Other","time = "+ historyBean.ts);
        mView.updateHistoryMessage(recordList, historyBean.ts);
    }

    @Override
    public void onSendCallBack(int e, long flag) {

    }

    @Override
    public void onConnected() {

    }

    private void parseMessageToBean(String message){
        WorldMessageRecord record = SkillHandleUtils.parseMsg(message);
        if (null == record){
            return;
        }
        mView.updateLastMessage(record);
    }

    @Override
    public void setSocketCallback() {
        ConnectorManage.getInstance(BaseApplication.appContext).setmWorldMsgCallBack(this);
    }

    @Override
    public void sendWorldMessage(String groupId, String content) {
        addFlag(SendWorldMessageProtocol.getInstance().getSendWorldMessageData(BaseApplication.appContext, Integer.parseInt(groupId), content, new HttpCallBack() {
            @Override
            public void onGeneralSuccess(String result, long flag) {
                if (!Constant.isSuccess(result)){
                    ErrorCode.showError(BaseApplication.appContext, result);
                }
            }

            @Override
            public void onGeneralError(int e, long flag) {

            }
        }));
    }

    @Override
    public void clearSocketCallback() {
        ConnectorManage.getInstance(BaseApplication.appContext).setmWorldMsgCallBack(null);
    }

    @Override
    public void getWorldMessageHistory(long timeStamp) {
        SocketSessionProtocol.getWorldMessageHistory(BaseApplication.appContext, timeStamp);
    }

}
