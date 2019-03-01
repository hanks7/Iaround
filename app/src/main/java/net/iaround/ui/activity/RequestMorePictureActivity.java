package net.iaround.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;

import net.iaround.R;
import net.iaround.analytics.enums.ImageEntrance;
import net.iaround.conf.Common;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.model.database.SpaceModel;
import net.iaround.model.database.SpaceModel.SpaceModelReqTypes;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.DialogUtil;
import net.iaround.ui.comon.NetImageView;
import net.iaround.ui.comon.SquaredNetImageView;
import net.iaround.ui.datamodel.Photo;
import net.iaround.ui.interfaces.PhotoDetailClickListener;

import java.util.ArrayList;
import java.util.HashMap;

import static android.nfc.tech.MifareUltralight.PAGE_SIZE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static net.iaround.ui.group.activity.SpaceMe.MSG_CLOSE_PD;
import static net.iaround.ui.group.activity.SpaceMe.MSG_REFRESH;
import static net.iaround.ui.group.activity.SpaceMe.MSG_SHOW_ERROR;

/**
 * Created by admin on 2017/5/8.
 */

public class RequestMorePictureActivity extends TitleActivity implements View.OnClickListener {
    public static final int REQ_SPACE_PHOTO = 0x00f0;
    private ImageView ivLeft;
    private TextView tvTitle;
    private PullToRefreshGridView pullGridView;

    private int mCurPage;
    private int mTotalPage;

    private IARAdapter mAdapter;
    private ArrayList<Photo> mPhotos;
    private ArrayList<String> mIds;
    private ArrayList<String> mSmalls;
    private int amount;
    private boolean isMyself = false;
    private long mUid;
    private boolean isVip = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUid = getIntent().getLongExtra("uid", -1);
        setContentView(R.layout.space_topic);
        initViews();
        initData();
        initListeners();
    }

    private void initData() {
        isMyself = mUid == Common.getInstance().loginUser.getUid();
    }


    private void initViews() {
        ivLeft = (ImageView) findViewById(R.id.iv_left);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        pullGridView = (PullToRefreshGridView) findViewById(R.id.all_gridView);

        int dp_5 = CommonFunction.dipToPx(RequestMorePictureActivity.this, 5);
        pullGridView.setPullToRefreshOverScrollEnabled(false);// 禁止滚动过头
        pullGridView.getRefreshableView().setVerticalSpacing(dp_5);
        pullGridView.getRefreshableView().setHorizontalSpacing(dp_5);
        pullGridView.setMode(PullToRefreshBase.Mode.BOTH);
        pullGridView.getRefreshableView().setNumColumns(4);
        pullGridView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<GridView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
                mCurPage = 1;
                reqUserData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
                if (mCurPage < mTotalPage) {
                    mCurPage = mCurPage + 1;
                    reqUserData();
                } else {
                    refreshView.onRefreshComplete();
                }
            }
        });

    }

    private void initListeners() {
        ivLeft.setOnClickListener(this);
        findViewById(R.id.fl_left).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_left:
            case R.id.iv_left:
                finish();
                break;


        }
    }

    private void reqUserData() {
        try {
            SpaceModel netwrokInterface = SpaceModel.getInstance(RequestMorePictureActivity.this);
            netwrokInterface.photoListReq(mUid, mCurPage, PAGE_SIZE, new HttpCallBack() {
                @Override
                public void onGeneralSuccess(String result, long flag) {
                    SpaceModel netwrokInterface = SpaceModel.getInstance(RequestMorePictureActivity.this);
                    HashMap<String, Object> res = null;
                    try {
                        res = netwrokInterface.getRes(result, flag);
                    } catch (Throwable e) {
                        e.printStackTrace();
                        Message msge = new Message();
                        msge.what = MSG_SHOW_ERROR;
//                        mMainHandler.sendMessage( msge );
                    }
                    if (res != null) {
                        if ((Integer) res.get("status") != 200) {
                            Message msge = new Message();
                            msge.what = MSG_SHOW_ERROR;
                            msge.obj = result;
//                            mMainHandler.sendMessage( msge );
                        } else {
                            SpaceModelReqTypes type = (SpaceModelReqTypes) res.get("reqType");
                            if (
                                    (type == SpaceModelReqTypes.PHOTO_LIST)) {
                                Message msgref = new Message();
                                msgref.what = MSG_REFRESH;
                                Object pageno = res.get("pageno");
                                Object amount = res.get("amount");
                                msgref.arg1 = pageno == null ? 1 : (Integer) pageno;
                                msgref.arg2 = amount == null ? 1 : (Integer) amount;
                                msgref.obj = res.get("photos");
                                mMainHandler.sendMessage(msgref);
                                setResult(Activity.RESULT_OK);
                            }
                        }
                    }
                }

                @Override
                public void onGeneralError(int e, long flag) {

                }
            });

        } catch (Exception e) {
            pullGridView.onRefreshComplete();
            Toast.makeText(RequestMorePictureActivity.this, R.string.network_req_failed,
                    Toast.LENGTH_SHORT).show();
        }

    }

    private Handler mMainHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_CLOSE_PD: {
                    pullGridView.onRefreshComplete();
                }
                break;
                case MSG_SHOW_ERROR: {
                    if (msg.obj != null
                            && !CommonFunction.isEmptyOrNullStr(msg.obj.toString())) {
                        ErrorCode.showError(RequestMorePictureActivity.this, String.valueOf(msg.obj));
                    }
                }
                break;
                case MSG_REFRESH: {
                    refreshData(msg);
                }
                break;
            }
        }
    };

    private void refreshData(Message msg) {
        mCurPage = msg.arg1;
        amount = msg.arg2;
        mTotalPage = amount / PAGE_SIZE;
        if (amount % PAGE_SIZE > 0) {
            mTotalPage++;
        }

//        ((TextView) mActionBar.findViewById(R.id.title_name)).setText(
//                getString(mType == 0 ? R.string.like : R.string.all_photo)
//                        + "("
//                        + amount
//                        + ")");//jiqiang

        @SuppressWarnings("unchecked")
        ArrayList<Photo> array = (ArrayList<Photo>) msg.obj;
        if (array == null) {
            array = new ArrayList<Photo>();
        }
        if (mCurPage <= 1) {
            mPhotos.clear();
        }
        mPhotos.addAll(array);

        if (isMyself) {
            Common.getInstance().loginUser.setPhotos(mPhotos);
        }

        mIds = new ArrayList<String>();
        mSmalls = new ArrayList<String>();
        for (Photo ph : mPhotos) {
            mIds.add(ph.getId());

            mSmalls.add(CommonFunction.thumPicture(ph.getUri()));
        }

        if (mAdapter == null) {
            mAdapter = new IARAdapter();
            pullGridView.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private class IARAdapter extends BaseAdapter implements View.OnClickListener {

        @Override
        public int getCount() {
            return mPhotos == null ? 0 : mPhotos.size();
        }

        @Override
        public Photo getItem(int position) {
            return mPhotos.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                // convertView = new SquaredNetImageView( getAttachActivity( )
                // );
                LayoutInflater inflater = (LayoutInflater) parent.getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.z_space_photo_gridadapter, null);
            }
            Photo photo = getItem(position);

            SquaredNetImageView ivLockView = (SquaredNetImageView) convertView
                    .findViewById(R.id.lock_icon);
            boolean isLock = false;
            if (!isVip & !isMyself) {
                if (position >= 8) {
                    ivLockView.setVisibility(VISIBLE);
                    // ivLockView.setScaleType( ScaleType.FIT_CENTER );

                    ivLockView.getImageView().setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    ivLockView.getImageView().setImageResource(
                            R.drawable.z_space_photo_lock_src);


                    isLock = true;
                } else {
                    ivLockView.setVisibility(INVISIBLE);
                }

            } else {
                ivLockView.setVisibility(INVISIBLE);
            }


            ImageView delIcon = (ImageView) convertView.findViewById(R.id.del_icon);
            delIcon.setTag(photo);
            delIcon.setTag(R.id.adapter_item, position);
//            if (isShowDelect) {
//                delIcon.setVisibility(VISIBLE);
//                delIcon.setOnClickListener(new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View arg0) {
//                        // TODO Auto-generated method stub
//
//                        final int point = (Integer) arg0.getTag(R.id.adapter_item);
//                        final Photo pto = (Photo) arg0.getTag();
//
//                        if (isTips) {
//                            String title = getResources().getString(
//                                    R.string.space_modify_photo_delect_title);
//                            String message = getContext().getResources().getString(
//                                    R.string.space_modify_photo_delect_checkbox);
//                            String ok = getContext().getResources().getString(
//                                    R.string.space_modify_photo_delect_ok);
//                            String cancel = getContext().getResources().getString(
//                                    R.string.space_modify_photo_delect_cancel);
//
//                            CustomDialog dialog = DialogUtil.showCheckDialog(getContext(), title, message,
//                                    R.layout.z_space_check_box_dialog_layout, ok,
//                                    new View.OnClickListener() {
//
//                                        @Override
//                                        public void onClick(View v) {
//                                            // TODO Auto-generated method stub
//
//                                            mPhotos.remove(point);
//                                            mIds.remove(point);
//                                            mSmalls.remove(point);
//                                            notifyDataSetChanged();
//                                            delPhoto(pto.getId());
//                                            isTips = !checkBox.isChecked();
//
//                                            ((Activity) getContext()).setResult(Activity.RESULT_OK);
//
//                                            SharedPreferenceUtil.getInstance(getContext()).putBoolean(DEL_PIC_TIPS, isTips);
//                                            // 删除照片
//                                        }
//                                    }, cancel);
//                            checkBox = (CheckBox) dialog.getDialogView().findViewById(R.id.checkBox1);
//                        } else {
//                            mPhotos.remove(point);
//                            mIds.remove(point);
//                            mSmalls.remove(point);
//                            notifyDataSetChanged();
//                            delPhoto(pto.getId());
//
//                            Common.getInstance().loginUser.setPhotos(mPhotos);
//
//                            ((Activity) getContext()).setResult(Activity.RESULT_OK);
//                        }
//                    }
//                });
//            } else {
//                delIcon.setVisibility(INVISIBLE);
//            }//jiqiang

            delIcon.setVisibility(INVISIBLE);

            SquaredNetImageView icon = (SquaredNetImageView) convertView
                    .findViewById(R.id.photo_view);

            icon.setProgressBarVisible(false);
            icon.getImageView().setScaleType(ImageView.ScaleType.FIT_XY);
            icon.getImageView().setAdjustViewBounds(true);
            icon.executeFadeIn(NetImageView.DEFAULT_SMALL,
                    CommonFunction.thumPicture(photo.getUri()));
//			int requestCode = mType == 0 ? REQ_SPACE_TOPIC : REQ_SPACE_PHOTO;
            int requestCode = REQ_SPACE_PHOTO;
            if (!isLock) {
                icon.setOnClickListener(new PhotoDetailClickListener(RequestMorePictureActivity.this,
                        mUid, requestCode, photo.getId(), mIds, mSmalls,
                        ImageEntrance.ALBUM));
                icon.setTag(photo);
            } else {
                icon.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        DialogUtil.showTobeVipDialog(RequestMorePictureActivity.this, R.string.space_photo_dialog_title,
                                R.string.only_vip_can_view_all_pic);
                    }
                });
            }
//            icon.setOnLongClickListener(new View.OnLongClickListener() {
//
//                @Override
//                public boolean onLongClick(View v) {
//                    // TODO Auto-generated method stub
//                    if (isMyself) {
//                        isShowDelect = true;
//                        ivModifyView.setImageResource(R.drawable.z_space_modify_cancel);
//                        notifyDataSetChanged();
//                        return true;
//                    }
//                    return false;
//                }
//            });


            return convertView;
        }

        public void onClick(View v) {
            Photo photo = (Photo) v.getTag();
            if (photo.isDirty()) {
//                warningDirty();//jiqiang
            }
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }

}
