package net.iaround.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.example.liangmutian.mypicker.TimePickerDialog;

import net.iaround.BaseApplication;
import net.iaround.R;
import net.iaround.conf.ErrorCode;
import net.iaround.connector.HttpCallBack;
import net.iaround.connector.protocol.GameChatHttpProtocol;
import net.iaround.model.entity.BaseEntity;
import net.iaround.model.entity.OrderSetBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.ui.view.face.MyGridView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

/**
 * Created by yz on 2018/8/4.
 */

public class OrderToSetActivity extends TitleActivity implements HttpCallBack, View.OnClickListener {

    private static final int GO_SET_PICE = 1000;
    private static final int GO_SET_REPEAT_DATE = 1001;
    private MyGridView mMgvOrderSet;
    private TextView mTvBeginTime;
    private TextView mTvEndTime;
    private TextView mTvRepeat;
    private TextView mTvSave;

    private Dialog mTimeBeginDialog;
    private Dialog mTimeEndDialog;

    private OrderSetAdapter mAdapter;
    private ArrayList<OrderSetBean.OrderGameItem> mOrderGameList = new ArrayList<>();
    private String[] mDayList;
    private int mPosition;//记录跳转的位置
    private String mRepeat;//记录设置的重复日期


    private OrderSetBean mOrderSetBean;

    private long mFlagGetOrderSet;
    private long mFlagSaveOrderSet;

    private String mMinutes[] = {"00", "15", "30", "45"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle_C(R.string.order_to_set);
        setContent(R.layout.activity_order_to_set);
        mMgvOrderSet = (MyGridView) findViewById(R.id.mgv_order_set);
        mTvBeginTime = (TextView) findViewById(R.id.tv_begin_time);
        mTvEndTime = (TextView) findViewById(R.id.tv_end_time);
        mTvRepeat = (TextView) findViewById(R.id.tv_repeat);
        mTvSave = (TextView) findViewById(R.id.tv_save);
        mTvBeginTime.setOnClickListener(this);
        mTvEndTime.setOnClickListener(this);
        mTvRepeat.setOnClickListener(this);
        mTvSave.setOnClickListener(this);
        showWaitDialog();
        mDayList = getResStringArr(R.array.day_list);
        mFlagGetOrderSet = GameChatHttpProtocol.getGameOrderSet(this, this);
        mAdapter = new OrderSetAdapter(mOrderGameList);
        mMgvOrderSet.setAdapter(mAdapter);

        mMgvOrderSet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(OrderToSetActivity.this, OrderToSetOptionActivity.class);
                intent.putExtra("type", 0);
                intent.putExtra("position", position);
                mPosition = position;
                intent.putExtra("OrderSetBean", mOrderSetBean);
                startActivityForResult(intent, GO_SET_PICE);
            }
        });

    }

    private void showBeginTimePick() {

        if (mTimeBeginDialog == null) {

            TimePickerDialog.Builder builder = new TimePickerDialog.Builder(this);
            mTimeBeginDialog = builder.setMinutes(Arrays.asList(mMinutes)).setOnTimeSelectedListener(new TimePickerDialog.OnTimeSelectedListener() {
                @Override
                public void onTimeSelected(int[] times) {

                    String hour;
                    String minute;
                    if (times[0] < 10) {
                        hour = "0" + times[0];
                    } else {
                        hour = times[0] + "";
                    }
                    if (times[1] < 10) {
                        minute = "0" + times[1];
                    } else {
                        minute = times[1] + "";
                    }
                    mTvBeginTime.setText(hour + ":" + minute);

                }
            }).create();
        }

        mTimeBeginDialog.show();

    }

    private void showEndTimePick() {

        if (mTimeEndDialog == null) {

            TimePickerDialog.Builder builder = new TimePickerDialog.Builder(this);
            mTimeEndDialog = builder.setMinutes(Arrays.asList(mMinutes)).setOnTimeSelectedListener(new TimePickerDialog.OnTimeSelectedListener() {
                @Override
                public void onTimeSelected(int[] times) {

                    String hour;
                    String minute;
                    if (times[0] < 10) {
                        hour = "0" + times[0];
                    } else {
                        hour = times[0] + "";
                    }
                    if (times[1] < 10) {
                        minute = "0" + times[1];
                    } else {
                        minute = times[1] + "";
                    }
                    mTvEndTime.setText(hour + ":" + minute);

                }
            }).create();
        }

        mTimeEndDialog.show();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_begin_time:
                showBeginTimePick();
                break;
            case R.id.tv_end_time:
                showEndTimePick();
                break;
            case R.id.tv_repeat:
                Intent intent = new Intent(this, OrderToSetOptionActivity.class);
                intent.putExtra("type", 1);
                if (!TextUtils.isEmpty(mRepeat) && mRepeat.lastIndexOf(",") == mRepeat.length() - 1) {
                    mRepeat = mRepeat.substring(0, mRepeat.length() - 1);
                }
                intent.putExtra("repeat", mRepeat);
                startActivityForResult(intent, GO_SET_REPEAT_DATE);
                break;
            case R.id.tv_save:
                saveSetting();
                break;
        }
    }

    private void saveSetting() {
        String games = "";//包含游戏id价格折扣  如果没有折扣discount为0  gameId1:price1:discount1, gameId2:price2:discount1

        for (int i = 0; i < mOrderGameList.size(); i++) {
            String split;
            if (i == mOrderGameList.size() - 1) {
                split = "";
            } else {
                split = ",";
            }
            games += mOrderGameList.get(i).gameId + ":" + mOrderGameList.get(i).price + ":" + mOrderGameList.get(i).discount + split;
        }
        if (!TextUtils.isEmpty(mRepeat) && mRepeat.lastIndexOf(",") == mRepeat.length() - 1) {
            mRepeat = mRepeat.substring(0, mRepeat.length() - 1);
        }
        showWaitDialog();
        mFlagSaveOrderSet = GameChatHttpProtocol.saveGameOrderSet(this, games, mTvBeginTime.getText().toString(), mTvEndTime.getText().toString(), mRepeat, this);
    }

    @Override
    public void onGeneralSuccess(String result, long flag) {
        destroyWaitDialog();
        if (mFlagGetOrderSet == flag) {
            mOrderSetBean = GsonUtil.getInstance().getServerBean(result, OrderSetBean.class);
            if (mOrderSetBean != null && mOrderSetBean.isSuccess()) {
                if (mOrderSetBean.games != null) {
                    mOrderGameList.addAll(mOrderSetBean.games);
                    mAdapter.notifyDataSetChanged();
                }
                mTvBeginTime.setText(mOrderSetBean.beginTime);
                mTvEndTime.setText(mOrderSetBean.endTime);

                mTvRepeat.setText(handleRepeatDate(mOrderSetBean.repeat));
            }
        } else if (mFlagSaveOrderSet == flag) {
            BaseEntity baseEntity = GsonUtil.getInstance().getServerBean(result, BaseEntity.class);
            if (baseEntity.isSuccess()) {
                CommonFunction.toastMsg(BaseApplication.appContext, R.string.save_succ);
                finish();
            } else {
                CommonFunction.toastMsg(BaseApplication.appContext, R.string.save_fail);
            }
        }

    }

    @Override
    public void onGeneralError(int e, long flag) {
        destroyWaitDialog();
        ErrorCode.toastError(BaseApplication.appContext, e);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == GO_SET_PICE) {
                double price = data.getDoubleExtra("price", 0);
                int discount = data.getIntExtra("discount", 0);
                mOrderGameList.get(mPosition).discount = discount;
                if (discount == 0) {
                    mOrderGameList.get(mPosition).price = price;
                } else {
                    mOrderGameList.get(mPosition).price = price * discount / 100;
                }
                mAdapter.notifyDataSetChanged();
            } else if (requestCode == GO_SET_REPEAT_DATE) {
                mRepeat = data.getStringExtra("repeat");
                ArrayList<Integer> repeats = new ArrayList<>();
                if (!TextUtils.isEmpty(mRepeat)) {

                    if (mRepeat.contains(",")) {
                        for (int i = 0; i < mRepeat.split(",").length; i++) {
                            int repeat = Integer.parseInt(mRepeat.split(",")[i]);
                            repeats.add(repeat);
                        }
                    } else {
                        repeats.add(Integer.parseInt(mRepeat));
                    }
                }
                mTvRepeat.setText(handleRepeatDate(repeats));
            }
        }


    }

    //处理日期数据，把type转成相应的字符串， 0-周日
    private String handleRepeatDate(ArrayList<Integer> repeat) {
        String repeatStr = " ";
        mRepeat = "";

        if (repeat != null && repeat.size() > 0) {
            Collections.sort(repeat);
            for (int i = 0; i < repeat.size(); i++) {
                switch (repeat.get(i)) {
                    case 0:
                        repeatStr = repeatStr + mDayList[0] + " ";
                        mRepeat = 0 + ",";
                        break;
                    case 1:
                        repeatStr = repeatStr + mDayList[1] + " ";
                        mRepeat = mRepeat + 1 + ",";
                        break;
                    case 2:
                        repeatStr = repeatStr + mDayList[2] + " ";
                        mRepeat = mRepeat + 2 + ",";
                        break;
                    case 3:
                        repeatStr = repeatStr + mDayList[3] + " ";
                        mRepeat = mRepeat + 3 + ",";
                        break;
                    case 4:
                        repeatStr = repeatStr + mDayList[4] + " ";
                        mRepeat = mRepeat + 4 + ",";
                        break;
                    case 5:
                        repeatStr = repeatStr + mDayList[5] + " ";
                        mRepeat = mRepeat + 5 + ",";
                        break;
                    case 6:
                        repeatStr = repeatStr + mDayList[6] + " ";
                        mRepeat = mRepeat + 6;
                        break;
                }
            }
        }
        return repeatStr;
    }

    class OrderSetAdapter extends BaseAdapter {
        private ArrayList<OrderSetBean.OrderGameItem> list;

        public OrderSetAdapter(ArrayList<OrderSetBean.OrderGameItem> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            if (list != null) {
                return list.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (list != null) {
                return list.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(OrderToSetActivity.this).inflate(R.layout.item_order_to_set, null);
                viewHolder = new ViewHolder();
                viewHolder.tv_game_name = (TextView) convertView.findViewById(R.id.tv_game_name);
                viewHolder.tv_price = (TextView) convertView.findViewById(R.id.tv_price);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (list != null && list.get(position) != null) {

                viewHolder.tv_game_name.setText(list.get(position).gameName);
                viewHolder.tv_price.setText(list.get(position).price + list.get(position).unit);
            }

            return convertView;
        }

        class ViewHolder {
            public TextView tv_game_name;
            public TextView tv_price;
        }
    }
}
