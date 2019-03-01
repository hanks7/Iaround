package net.iaround.ui.skill.skillmsg;

import net.iaround.BaseApplication;
import net.iaround.conf.MessageID;
import net.iaround.connector.CallBackNetwork;
import net.iaround.connector.ConnectorManage;
import net.iaround.connector.protocol.SocketSessionProtocol;
import net.iaround.model.chat.HistoryWorldMessageBean;
import net.iaround.model.chat.WorldMessageRecord;
import net.iaround.model.im.TransportMessage;
import net.iaround.model.skill.SkillAttackResult;
import net.iaround.tools.GsonUtil;
import net.iaround.utils.SkillHandleUtils;

import java.util.ArrayList;

/**
 * Class:
 * Author：yuchao
 * Date: 2017/8/25 10:24
 * Email：15369302822@163.com
 */
public class SKillMsgListPresenter extends SkillMsgLlistContract.Presenter  implements CallBackNetwork {

    private SkillMsgLlistContract.View mView;

    public SKillMsgListPresenter(SkillMsgLlistContract.View view){
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

    @Override
    public void onSendCallBack(int e, long flag) {

    }

    @Override
    public void onConnected() {

    }

    private void parseMessageToBean(String message) {
        WorldMessageRecord messageRecord = SkillHandleUtils.parseMsg(message);
        if (null == messageRecord){
            return;
        }
        mView.updateLastSkillMessage(messageRecord.skillContent);
    }

    /**
     *
     * @param message
     */
    private void parseHistoryMessageToBean(String message) {
        ArrayList<SkillAttackResult> recordList = new ArrayList<>();
        HistoryWorldMessageBean historyBean = GsonUtil.getInstance().getServerBean(message, HistoryWorldMessageBean.class);
        if (1 == historyBean.type){//剔除世界历史消息
            return;
        }
        if (null == historyBean){
            mView.refreshCompleted();
            return;
        }
        if (null != historyBean.message && historyBean.message.size() > 0){
            for (int i = historyBean.message.size() - 1; i >= 0; i--){
                WorldMessageRecord messageRecord = SkillHandleUtils.pareseToRecord(historyBean.message.get(i));
                if (null == messageRecord){
                    continue;
                }
                SkillAttackResult result = messageRecord.skillContent;
                if (null != result){
                    recordList.add(result);
                }
            }
        }
        mView.updateHistoryMessage(recordList, historyBean.ts);
    }

    @Override
    public void setSocketCallback() {
        ConnectorManage.getInstance(BaseApplication.appContext).setmSkillMsgCallBack(this);
    }

    @Override
    public void clearSocketCallback() {
        ConnectorManage.getInstance(BaseApplication.appContext).setmSkillMsgCallBack(null);
    }

    @Override
    public void getSkillMessageHistory(String groupId, long timeStamp) {
        SocketSessionProtocol.getSkillMessageHistory(BaseApplication.appContext, groupId, timeStamp);
    }
}
