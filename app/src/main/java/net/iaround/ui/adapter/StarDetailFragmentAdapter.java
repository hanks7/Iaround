package net.iaround.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.model.entity.WalletDetailListItemData;
import net.iaround.tools.CommonFunction;
import net.iaround.ui.adapter.poweradapter.PowerAdapter;
import net.iaround.ui.adapter.poweradapter.PowerHolder;

public class StarDetailFragmentAdapter extends PowerAdapter<WalletDetailListItemData> {

    private Context mContext;

    private LayoutInflater inflater;

    public StarDetailFragmentAdapter(Context context) {
        super();
        mContext = context;
        inflater = LayoutInflater.from(mContext);
    }


    @Override
    public PowerHolder<WalletDetailListItemData> onViewHolderCreate(@NonNull ViewGroup parent, int viewType) {
        View inflate = inflater.inflate(R.layout.star_detail_fragment_item_adapter, parent, false);
        return new StarDetailFragmentHolder(inflate);
    }

    @Override
    public void onViewHolderBind(@NonNull PowerHolder<WalletDetailListItemData> holder, int position) {
        holder.onBind(list.get(position));
    }

    private class StarDetailFragmentHolder extends PowerHolder<WalletDetailListItemData> {

        private TextView mStarNum;
        private TextView mPayType;
        private TextView mPayTime;

        public StarDetailFragmentHolder(View itemView) {
            super(itemView);
            mStarNum = (TextView) itemView.findViewById(R.id.star_num);
            mPayType = (TextView) itemView.findViewById(R.id.pay_type);
            mPayTime = (TextView) itemView.findViewById(R.id.pay_time);
        }

        @Override
        public void onBind(WalletDetailListItemData walletDetailListItemData) {
            super.onBind(walletDetailListItemData);
            mStarNum.setText(walletDetailListItemData.num);
            mPayType.setText(CommonFunction.getLangText(walletDetailListItemData.consumetype));
            mPayTime.setText(walletDetailListItemData.datetime);
        }
    }
}
