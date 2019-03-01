package net.iaround.ui.group;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import net.iaround.BaseApplication;
import net.iaround.conf.Common;
import net.iaround.entity.PipelineGift;
import net.iaround.entity.PipelineGift.GiftLager;
import net.iaround.model.chatbar.ChatBarSendPacketGiftBean;
import net.iaround.tools.CommonFunction;
import net.iaround.ui.view.LuxuryGiftView;
import net.iaround.ui.view.pipeline.OnAddGiftNumListener;
import net.iaround.ui.view.pipeline.OnHeadViewClickListener;
import net.iaround.ui.view.pipeline.PipelineGiftView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Class: 礼物队列管理
 * Author：gh
 * Date: 2017/7/18 15:09
 * Email：jt_gaohang@163.com
 */
public class GiftQueueHandler {
    /**
     * 单例对象实例
     */
    private static GiftQueueHandler sInstance = null;

    // 大礼物数据
    private List<GiftLager> giftLagers = null;

    // 通道队列消息
    private List<PipelineGift> giftAnimList = null;

    /**
     * 礼物定时器执行间隔
     */
    private static final int mGiftClearTimerInterval = 100;

    /**
     * 礼物定时器  用于清除和从礼物队列中取礼物
     */
    private Timer mGiftClearTimer = null;

    private WeakReference<PipelineGiftView> giftOne; //giftOne;                                         // 礼物展示通道一
    private WeakReference<PipelineGiftView> giftTwo; //giftTwo;                                        // 礼物展示通道二
    private WeakReference<PipelineGiftView> giftThree;//giftThree;                                        // 礼物展示通道三
    private WeakReference<LuxuryGiftView> giftLager; //giftLager;                                         // 礼物大动画
    private PipelineGift giftAnimOne;                                         // 当前通道一播放礼物
    private PipelineGift giftAnimTwo;                                         // 当前通道二播放礼物
    private PipelineGift giftAnimThree;                                       // 当前通道三播放礼物

    private Handler mainHandle = null;                                              // 当前Handler

    private OnHeadViewClickListener onHeadViewClickListener;                 // 点击送礼头像

    private Map<Long,PipelineGift> pipelineGiftMap = null;       //存贮动画,保证每个动画的唯一
    private Map<Long,PipelineGift> pipelineGiftMapH = null;        //存贮执行动画1记录
    private Map<Long,WeakReference<PipelineGiftView>> pipelineGiftMapView = null;        //存贮执行动画View记录\\

    private ArrayList<AddGiftRunnable> mAddGiftRunnable = null; //添加礼物时执行的 runnable
    private PlayRunnable mPlayRunnable = null; //礼物定时器执行的 runnable


    private GiftQueueHandler() {

    }

    public static GiftQueueHandler getInstance() {
        if (sInstance == null) {
            synchronized (GiftQueueHandler.class) {
                if (sInstance == null) {
                    sInstance = new GiftQueueHandler();
                }
            }
        }
        return sInstance;
    }


    /**
     * 初始化三个通道View和大动画View
     * @param giftOne
     * @param giftTwo
     * @param giftThree
     */
    public synchronized void initView(final PipelineGiftView giftOne, final PipelineGiftView giftTwo, final PipelineGiftView giftThree, LuxuryGiftView giftLager) {
        this.giftOne = new WeakReference<PipelineGiftView>(giftOne);
        this.giftTwo = new WeakReference<PipelineGiftView>(giftTwo);
        this.giftThree = new WeakReference<PipelineGiftView>(giftThree);
        this.giftLager = new WeakReference<LuxuryGiftView>(giftLager);

        giftLagers =  new ArrayList<>();
        giftAnimList = new ArrayList<>();
        pipelineGiftMap = new HashMap<>();
        pipelineGiftMapH = new HashMap<>();
        pipelineGiftMapView = new HashMap<>();

        mAddGiftRunnable = new ArrayList<>(0);

        giftOne.setOnAddGiftNumListener(new OnAddGiftNumListener() {
            @Override
            public void onAddGiftNumEnd(PipelineGift gift) {
                giftAnimOne = null;
                if (pipelineGiftMapH != null)
                    pipelineGiftMapH.remove(gift.getUser_gift_id());
                giftNext(gift);
            }
        });

        giftTwo.setOnAddGiftNumListener(new OnAddGiftNumListener() {
            @Override
            public void onAddGiftNumEnd(PipelineGift gift) {
                giftAnimTwo = null;
                if (pipelineGiftMapH != null)
                    pipelineGiftMapH.remove(gift.getUser_gift_id());
                giftNext(gift);
            }
        });

        giftThree.setOnAddGiftNumListener(new OnAddGiftNumListener() {
            @Override
            public void onAddGiftNumEnd(PipelineGift gift) {
                giftAnimThree = null;
                if (pipelineGiftMapH != null)
                    pipelineGiftMapH.remove(gift.getUser_gift_id());
                giftNext(gift);
            }
        });

    }

    /**
     * 设置Handler
     * @param mainHandle
     */
    public synchronized void setMainHandle(Handler mainHandle) {
        this.mainHandle = mainHandle;
    }


    /*添加礼物 Runnable
    * */
    class AddGiftRunnable implements Runnable{
        private ChatBarSendPacketGiftBean mBean;
        public AddGiftRunnable(ChatBarSendPacketGiftBean bean){
            mBean = bean;
        }
        @Override
        public void run() {
            if (mBean == null)return;
                List<PipelineGift> pipelineGifts = new ArrayList<>();
                pipelineGifts.clear();
                if (giftAnimList == null)
                    giftAnimList = new ArrayList<>();
                pipelineGifts.addAll(giftAnimList);
                if (pipelineGiftMap.get(mBean.getGift().getUser_gift_id()) != null) {
                    if (pipelineGifts.size() > 0) {
                        // 通道动画
                        for (PipelineGift giftAnimBean : pipelineGifts) {
                            if (giftAnimBean.getUser_gift_id() == mBean.getGift().getUser_gift_id()) {
                                if (giftAnimBean.getGiftNum() < mBean.getGift().getCombo_num()) {
                                    giftAnimBean.setGiftNum(mBean.getGift().getCombo_num());
                                }
                            }
                        }
                    }

                    // 正在播放的动画一
                    PipelineGiftView one = giftOne.get();
                    if (giftAnimOne != null && one != null)
                        if (pipelineGiftMapH.get(giftAnimOne.getUser_gift_id()) != null & pipelineGiftMapView.get(giftAnimOne.getUser_gift_id()) != null) {
                            if (giftAnimOne.getUser_gift_id() == mBean.getGift().getUser_gift_id()) {
                                one.refershViewNumber(mBean.getGift().getCombo_num());
                            }
                        }

                    // 正在播放的动画二
                    PipelineGiftView two = giftTwo.get();
                    if (giftAnimTwo != null && two != null)
                        if (pipelineGiftMapH.get(giftAnimTwo.getUser_gift_id()) != null & pipelineGiftMapView.get(giftAnimTwo.getUser_gift_id()) != null) {
                            if (giftAnimTwo.getUser_gift_id() == mBean.getGift().getUser_gift_id()) {
                                two.refershViewNumber(mBean.getGift().getCombo_num());
                            }
                        }

                    // 正在播放的动画三
                    PipelineGiftView three = giftThree.get();
                    if (giftAnimThree != null && three != null)
                        if (pipelineGiftMapH.get(giftAnimThree.getUser_gift_id()) != null & pipelineGiftMapView.get(giftAnimThree.getUser_gift_id()) != null) {
                            if (giftAnimThree.getUser_gift_id() == mBean.getGift().getUser_gift_id()) {
                                three.refershViewNumber(mBean.getGift().getCombo_num());
                            }
                        }
                } else {
                    pipelineGiftMap.put(mBean.getGift().getUser_gift_id(), toGiftBean(mBean));
                    if (mBean.getSend_user().getUserid() == Common.getInstance().loginUser.getUid()) {
                        if (giftAnimList.size() > 1) {
                            giftAnimList.add(1, toGiftBean(mBean));
                        } else {
                            giftAnimList.add(0, toGiftBean(mBean));
                        }

                    } else {
                        giftAnimList.add(toGiftBean(mBean));
                    }
                }

                if (!TextUtils.isEmpty(mBean.getGift().getCombo_animation())) {
                    if (mBean.getSend_user().getUserid() == Common.getInstance().loginUser.getUid()) {
                        if (giftLagers.size() > 0) {
                            GiftLager giftLager = giftLagers.get(0);
                            if (giftLager.isPlay()) {
                                giftLagers.add(1, new GiftLager(mBean.getGift().getUser_gift_id(), mBean.getGift().getCombo_animation(), mBean.getGift().getCombo_resource_url(), mBean.getGift().getCombo_animation() + "/" + System.currentTimeMillis(), false));
                            } else {
                                giftLagers.add(0, new GiftLager(mBean.getGift().getUser_gift_id(), mBean.getGift().getCombo_animation(), mBean.getGift().getCombo_resource_url(), mBean.getGift().getCombo_animation() + "/" + System.currentTimeMillis(), false));
                            }
                        } else {
                            giftLagers.add(new GiftLager(mBean.getGift().getUser_gift_id(), mBean.getGift().getCombo_animation(), mBean.getGift().getCombo_resource_url(), mBean.getGift().getCombo_animation() + "/" + System.currentTimeMillis(), false));
                        }

                    } else {
                        giftLagers.add(new GiftLager(mBean.getGift().getUser_gift_id(), mBean.getGift().getCombo_animation(), mBean.getGift().getCombo_resource_url(), mBean.getGift().getCombo_animation() + "/" + System.currentTimeMillis(), false));
                    }
                }
                mAddGiftRunnable.remove(this);
        }
    }

    /**
     * 在消息队列里面添加新的礼物动画
     * @param bean
     */
    public synchronized void addGift(final ChatBarSendPacketGiftBean bean){
        if(mainHandle!=null) {
            AddGiftRunnable runnable = new AddGiftRunnable(bean);
            mAddGiftRunnable.add(runnable);
            mainHandle.post(runnable);
        }
        //启动定时器
        if (mGiftClearTimer == null) {
            startTimer();
        }
    }

    /*播放动画 Runnable
     */
    class PlayRunnable implements Runnable{
        @Override
        public void run() {
            synchronized (sInstance) {
                if(giftAnimList == null){
                    return;
                }
                if (giftAnimList.size() < 10 & giftAnimList.size() > 0) {
                    playGiftOne();
                    playGiftTwo();
                } else {
                    if (giftAnimList.size() >= 10) {
                        playGiftOne();
                        playGiftTwo();
                        playGiftThree();
                    }
                }
            }
        }
    }

    /**
     * 播放通道动画
     */
    static class PlayTimerTask extends TimerTask{
        private Handler mHandler;
        private PlayRunnable mRunnable;
        public PlayTimerTask(Handler handler, PlayRunnable runnable){
            mHandler = handler;
            mRunnable = runnable;
        }
        @Override
        public void run() {
            synchronized (sInstance) {
                if (sInstance.mainHandle==null || mHandler!=sInstance.mainHandle) {
                    return;
                }
                mHandler.post(mRunnable);
            }
        }
    }

    private void startTimer() {
        if(mGiftClearTimer==null) {
            mGiftClearTimer = new Timer();
        }
        if(mPlayRunnable==null) {
            mPlayRunnable = new PlayRunnable();
        }
        mGiftClearTimer.schedule(new PlayTimerTask(mainHandle,mPlayRunnable), 0,mGiftClearTimerInterval);
    }

    // 播放通道一
    private void playGiftOne() {
        PipelineGiftView one = giftOne.get();
        if (one!=null && giftAnimList.size() >= 1) {
            if (!one.isShow() & !one.isShown()) {
                giftAnimOne = getGiftAnimOne();
                if (giftAnimOne == null) return;
                if (pipelineGiftMapH.get(giftAnimOne.getUser_gift_id()) == null & pipelineGiftMapView.get(giftAnimOne.getUser_gift_id()) == null) {
                    removeGift(giftAnimOne);
                    pipelineGiftMapH.put(giftAnimOne.getUser_gift_id(), giftAnimOne);
                    pipelineGiftMapView.put(giftAnimOne.getUser_gift_id(), giftOne);
                    one.setGift(giftAnimOne);
                    one.setGiftNum(giftAnimOne.getGiftNum(), giftAnimOne.getGiftArrayNmber());
                    one.setVisibility(View.VISIBLE);

                    if (onHeadViewClickListener != null) {
                        one.setOnHeadViewClickListener(onHeadViewClickListener);
                    }

                    playLager();
                }
            }
        }
    }

    /**
     * 移除当前礼物
     * @param gift
     */
    private void removeGift(PipelineGift gift) {
        ArrayList<PipelineGift> pipelineGifts = new ArrayList<>();
        pipelineGifts.clear();
        pipelineGifts.addAll(giftAnimList);
        if (pipelineGifts.size() > 0) {
            for (PipelineGift pipelineGift : pipelineGifts) {
                if (gift.getUser_gift_id() == pipelineGift.getUser_gift_id())
                    giftAnimList.remove(pipelineGift);
            }
        }
    }

    // 播放通道二
    private void playGiftTwo() {
        PipelineGiftView two = giftTwo.get();
        if (two!=null && !two.isShow() & !two.isShown()) {
            giftAnimTwo = getGiftAnimOne();
            if (giftAnimTwo == null) return;
            if (pipelineGiftMapH.get(giftAnimTwo.getUser_gift_id()) == null & pipelineGiftMapView.get(giftAnimTwo.getUser_gift_id()) == null) {
                removeGift(giftAnimTwo);
                pipelineGiftMapH.put(giftAnimTwo.getUser_gift_id(), giftAnimTwo);
                pipelineGiftMapView.put(giftAnimTwo.getUser_gift_id(), giftTwo);
                two.setGift(giftAnimTwo);
                two.setGiftNum(giftAnimTwo.getGiftNum(), giftAnimTwo.getGiftArrayNmber());
                two.setVisibility(View.VISIBLE);

                if (onHeadViewClickListener != null) {
                    two.setOnHeadViewClickListener(onHeadViewClickListener);
                }
                playLager();
            }
        }
    }

    /**
     * 下一个通道礼物
     * @param gift
     */
    private void giftNext(PipelineGift gift) {
        synchronized (gift) {

            pipelineGiftMap.remove(gift.getUser_gift_id());
            pipelineGiftMapView.remove(gift.getUser_gift_id());
            if (giftAnimList.size() < 10 & giftAnimList.size() > 0) {
                playGiftOne();
                playGiftTwo();
            } else {
                if (giftAnimList.size() >= 10) {
                    playGiftOne();
                    playGiftTwo();
                    playGiftThree();
                }
            }
        }
    }

    // 播放通道三
    private void playGiftThree() {
        PipelineGiftView three = giftThree.get();
        if (three!=null && !three.isShow() & !three.isShown()) {
            giftAnimThree = getGiftAnimOne();
            if (giftAnimThree == null) return;
            if (pipelineGiftMapH.get(giftAnimThree.getUser_gift_id()) == null & pipelineGiftMapView.get(giftAnimThree.getUser_gift_id()) == null) {
                removeGift(giftAnimThree);
                pipelineGiftMapH.put(giftAnimThree.getUser_gift_id(), giftAnimThree);
                pipelineGiftMapView.put(giftAnimThree.getUser_gift_id(), giftThree);
                three.setGift(giftAnimThree);
                three.setGiftNum(giftAnimThree.getGiftNum(), giftAnimThree.getGiftArrayNmber());
                three.setVisibility(View.VISIBLE);

                if (onHeadViewClickListener != null) {
                    three.setOnHeadViewClickListener(onHeadViewClickListener);
                }

                playLager();
            }
        }
    }

    /**
     * 播放大动画
     */
    private void playLager(){
        final LuxuryGiftView larger = giftLager.get();
        // 播放大动画
        if (larger!=null && giftLagers.size() > 0) {
            GiftLager giftLag = giftLagers.get(0);
            if (!giftLag.isPlay() & pipelineGiftMapView.get(giftLag.user_gift_id) != null) {
                giftLag.setPlay(true);
                CommonFunction.log("GiftQueueHandler","combo_resource_url = "+giftLag.combo_resource_url+"                    combo_animation = "+giftLag.combo_animation);
                larger.showLuxuryGift(giftLag.combo_resource_url, giftLag.combo_animation, giftLag.time);
            }
            larger.setLuxuryGiftPlayListener(new LuxuryGiftView.LuxuryGiftPlayListener() {
                @Override
                public void playEnd(String currentTime) {
                    if (giftLagers.size() > 0) {
                        ArrayList<GiftLager> lagers = new ArrayList<GiftLager>();
                        lagers.addAll(giftLagers);
                        for (GiftLager gift : lagers) {
                            if (gift.time.equals(currentTime)) {
                                giftLagers.remove(gift);
                            }
                        }

                        if (giftLagers.size() <= 0) {
                        } else {

                            GiftLager giftLag = giftLagers.get(0);
                            giftLag.setPlay(true);
                            larger.showLuxuryGift(giftLag.combo_resource_url, giftLag.combo_animation, giftLag.time);
                            //CommonFunction.log("GiftQueueHandler","combo_resource_url 11111 = "+giftLag.combo_resource_url+" combo_animation  22222222222  = "+giftLag.combo_animation);
                        }
                    }
                }

            });
        }
    }

    /**
     * 获取第一条礼物
     * @return
     */
    private PipelineGift getGiftAnimOne(){
        CommonFunction.log("GiftQueueHandler","队列里放了" + giftAnimList.size());
        synchronized (giftAnimList){
            if (giftAnimList.size() > 0 ){
                PipelineGift pipelineGift = giftAnimList.get(0);
                return pipelineGift;
            }
        }
        return null;
    }

    /**
     * 收到的礼物数据转换成队列存储礼物数据
     * @param bean
     * @return
     */
    private PipelineGift toGiftBean(ChatBarSendPacketGiftBean bean){
        PipelineGift giftBean = new PipelineGift();
        giftBean.setSendId(bean.getSend_user().getUserid());

        String sendUser = bean.getSend_user().getNoteName();
        if (sendUser == null || TextUtils.isEmpty(sendUser) || "".equals(sendUser) || "null".equals(sendUser)){
            sendUser =  bean.getSend_user().getNickname( );

            if (CommonFunction.isEmptyOrNullStr( sendUser ) | sendUser.equals("null")){
                sendUser =  ""+bean.getSend_user().userid;
            }
        }else if (CommonFunction.isEmptyOrNullStr( sendUser ) | sendUser.equals("null"))
        {
            sendUser =  bean.getSend_user(). getNickname( );

            if (CommonFunction.isEmptyOrNullStr( sendUser ) | sendUser.equals("null")){
                sendUser =  ""+bean.getSend_user().userid;
            }
        }
        giftBean.setSendUser(sendUser);
        giftBean.setSendIcon(bean.getSend_user().getIcon());
        giftBean.setGiftNum(bean.getGift().getCombo_num());
        giftBean.setCurrentGiftPosition(bean.getGift().getCombo_num() - 1);
        giftBean.setGiftArrayNmber(bean.getGift().getCombo_value());
        giftBean.setGiftType(bean.getGift().getCombo_type());
        giftBean.setGiftId(bean.getGift().getGift_id());
        giftBean.setGiftImgUrl(bean.getGift().getGift_icon());

        String receiveUser = bean.getReceive_user().getNoteName();
        if (receiveUser == null || TextUtils.isEmpty(receiveUser) || "".equals(receiveUser) || "null".equals(receiveUser)){
            receiveUser =  bean.getReceive_user().getNickname( );

            if (CommonFunction.isEmptyOrNullStr( receiveUser ) | receiveUser.equals("null")){
                receiveUser =  String.valueOf(bean.getReceive_user().getUserid());
            }
        }else if (CommonFunction.isEmptyOrNullStr( receiveUser ) | receiveUser.equals("null"))
        {
            receiveUser =  bean.getReceive_user(). getNickname( );

            if (CommonFunction.isEmptyOrNullStr( receiveUser ) | receiveUser.equals("null")){
                receiveUser = String.valueOf(bean.getReceive_user().getUserid());
            }
        }
        giftBean.setReceiveUser(receiveUser);
        giftBean.setUser_gift_id(bean.getGift().getUser_gift_id());
        giftBean.setSvip(bean.getSend_user().getVip());
        giftBean.setViplevel(bean.getSend_user().getViplevel());
        giftBean.setPlay(false);
        return giftBean;
    }

    public synchronized void setOnHeadViewClickListener(OnHeadViewClickListener onHeadViewClickListener) {
        this.onHeadViewClickListener = onHeadViewClickListener;
    }

    /**
     * 释放资源，必须调用。
     */
    public synchronized void release() {
        if (mGiftClearTimer != null) {
            mGiftClearTimer.cancel();
            mGiftClearTimer = null;
        }

        if(mAddGiftRunnable!=null) {
            if(mainHandle!=null) {
                for (Runnable runnable : mAddGiftRunnable) {
                    mainHandle.removeCallbacks(runnable);
                }
            }
            mAddGiftRunnable.clear();
            mAddGiftRunnable = null;
        }

        if(mPlayRunnable!=null) {
            if(mainHandle!=null) {
                mainHandle.removeCallbacks(mPlayRunnable);
            }
            mPlayRunnable = null;
        }

        if(giftLagers!=null) {
            giftLagers.clear();
            giftLagers = null;
        }
        if(giftAnimList!=null) {
            giftAnimList.clear();
            giftAnimList = null;
        }
        if(pipelineGiftMap!=null) {
            pipelineGiftMap.clear();
            pipelineGiftMap = null;
        }
        if(pipelineGiftMapH!=null) {
            pipelineGiftMapH.clear();
            pipelineGiftMapH = null;
        }
        if(pipelineGiftMapView!=null) {
            pipelineGiftMapView.clear();
            pipelineGiftMapView = null;
        }

        if(giftOne!=null) {
            PipelineGiftView one = giftOne.get();
            if (one != null) {
                one.releaseResource();
            }
            giftOne = null;
        }
        if(giftTwo!=null) {
            PipelineGiftView two = giftTwo.get();
            if (two != null) {
                two.releaseResource();
            }
            giftTwo = null;
        }
        if(giftThree!=null) {
            PipelineGiftView three = giftThree.get();
            if (three != null) {
                three.releaseResource();
            }
            giftThree = null;
        }
        if(giftLager!=null) {
            LuxuryGiftView larger = giftLager.get();
            if (larger != null) {
                larger.setLuxuryGiftPlayListener(null);
                larger.clearAnimation();
            }
            giftLager = null;
        }

        giftAnimOne = null;
        giftAnimTwo = null;
        giftAnimThree = null;

        onHeadViewClickListener = null;

        mainHandle = null;

    }
}
