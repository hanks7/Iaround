
package net.iaround.ui.activity;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import net.iaround.R;
import net.iaround.tools.CommonFunction;
import net.iaround.ui.adapter.SectionListAdapter;
import net.iaround.ui.view.SectionListView;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * @author zhonglong kylin17@foxmail.com
 * @ClassName: CountrySelectActivity
 * @Description: 选择国家的界面
 * @date 2013-11-25 下午9:23:09
 */
public class CountrySelectActivity extends TitleActivity implements OnClickListener {

    // listview section
    private StandardArrayAdapter arrayAdapter;
    private SectionListAdapter sectionAdapter;
    private SectionListView listView;


    //标题栏
    private ImageView ivLeft;
    private TextView tvTitle;
    private FrameLayout flLeft;

    EditText search;

    // sideIndex
    LinearLayout sideIndex;
    // height of side index
    private int sideIndexHeight, sideIndexSize;
    // list with items for side index
    private ArrayList<Object[]> sideIndexList = new ArrayList<Object[]>();

    // an array with countries to display in the list
    private ArrayList<String> COUNTRIES;
    static String[] COUNTRIES_ARY;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_country);
        COUNTRIES_ARY = getResources().getStringArray(R.array.select_countrys);

        initActionbar();
        search = (EditText) findViewById(R.id.search_query);
        search.addTextChangedListener(filterTextWatcher);
        listView = (SectionListView) findViewById(R.id.section_list_view);
        sideIndex = (LinearLayout) findViewById(R.id.list_index);
        sideIndex.setOnTouchListener(new Indextouch());

        if (COUNTRIES_ARY.length > 0) {
            // not forget to sort array
            Arrays.sort(COUNTRIES_ARY);
            COUNTRIES = new ArrayList<String>(Arrays.asList(COUNTRIES_ARY));
            arrayAdapter = new StandardArrayAdapter(COUNTRIES);

            // adaptor for section
            sectionAdapter = new SectionListAdapter(this.getLayoutInflater(), arrayAdapter);
            listView.setAdapter(sectionAdapter);

            PoplulateSideview();
        }
    }

    private void initActionbar() {
        ivLeft = (ImageView) findViewById(R.id.iv_left);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        flLeft = (FrameLayout) findViewById(R.id.fl_left);
        tvTitle.setText(getString(R.string.select_country_or_area));
        ivLeft.setImageResource(R.drawable.title_back);

        ivLeft.setOnClickListener(this);
        flLeft.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*
		 * WindowManager.LayoutParams attrs = getWindow().getAttributes();
		 * attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
		 * getWindow().setAttributes(attrs);
		 */
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fl_left || v.getId() == R.id.iv_left) {
            finish();
        }
    }


    private class Indextouch implements OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            if (event.getAction() == MotionEvent.ACTION_MOVE
                    || event.getAction() == MotionEvent.ACTION_DOWN) {
                //
                // sideIndex.setBackgroundDrawable(getResources().getDrawable(
                // R.drawable.rounded_rectangle_shape));

                // now you know coordinates of touch
                float sideIndexX = event.getX();
                float sideIndexY = event.getY();

                if (sideIndexX > 0 && sideIndexY > 0) {
                    // and can display a proper item it country list
                    displayListItem(sideIndexY);

                }
            } else {
                // sideIndex.setBackgroundColor(Color.TRANSPARENT);
            }

            return true;

        }

    }

    public void onWindowFocusChanged(boolean hasFocus) {
        // get height when component is poplulated in window
        sideIndexHeight = sideIndex.getHeight();
		/*
		 * getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		 * WindowManager.LayoutParams.FLAG_FULLSCREEN);
		 */
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        getWindow().setAttributes(attrs);
        super.onWindowFocusChanged(hasFocus);
    }

    private class StandardArrayAdapter extends BaseAdapter implements Filterable {

        private final ArrayList<String> items;

        public StandardArrayAdapter(ArrayList<String> args) {
            this.items = args;
        }

        @Override
        public View getView(final int position, final View convertView,
                            final ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                final LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = vi.inflate(R.layout.item_country_view, null);
            }
            final TextView textView = (TextView) view.findViewById(R.id.row_title);
            final View line = view.findViewById(R.id.line);
            if (textView != null) {
                line.setVisibility(View.VISIBLE);
                textView.setVisibility(View.VISIBLE);
                String country = items.get(position).replace("#", "");
                final String countryFull = country;
                if (country.indexOf("+") != country.lastIndexOf("+")) {
                    country = country.substring(0, country.lastIndexOf("+"));
                }
                textView.setText(country);

                view.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO 获取数据，并返回到上一个界面
                        returnResult(countryFull);
                    }
                });
            }

            return view;
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Filter getFilter() {
            Filter listfilter = new MyFilter();
            return listfilter;
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }

    public class MyFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            // NOTE: this function is *always* called from a background thread,
            // and
            // not the UI thread.
            constraint = search.getText().toString();
            FilterResults result = new FilterResults();
            if (constraint != null && constraint.toString().length() > 0) {
                // do not show side index while filter results
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        findViewById(R.id.list_index)
                                .setVisibility(View.INVISIBLE);
                    }
                });

                ArrayList<String> filt = new ArrayList<String>();
                ArrayList<String> Items = new ArrayList<String>();
                synchronized (this) {
                    Items = COUNTRIES;
                }
                for (int i = 0; i < Items.size(); i++) {
                    String item = Items.get(i);
                    if (item.toLowerCase().replace("#", "")
                            .startsWith(constraint.toString().toLowerCase())) {
                        filt.add(item);
                    }
                }

                result.count = filt.size();
                result.values = filt;
            } else {

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        findViewById(R.id.list_index)
                                .setVisibility(View.VISIBLE);
                    }
                });
                synchronized (this) {
                    result.count = COUNTRIES.size();
                    result.values = COUNTRIES;
                }

            }
            return result;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            @SuppressWarnings("unchecked")
            ArrayList<String> filtered = (ArrayList<String>) results.values;
            arrayAdapter = new StandardArrayAdapter(filtered);
            sectionAdapter = new SectionListAdapter(getLayoutInflater(), arrayAdapter);
            listView.setAdapter(sectionAdapter);

        }

    }

    private void displayListItem(float sideIndexY) {
        // compute number of pixels for every side index item
        double pixelPerIndexItem = (double) sideIndexHeight / sideIndexSize;

        // compute the item index for given event position belongs to
        int itemPosition = (int) (sideIndexY / pixelPerIndexItem);

        if (itemPosition < sideIndexList.size()) {
            // get the item (we can do it since we know item index)
            Object[] indexItem = sideIndexList.get(itemPosition);
            listView.setSelectionFromTop((Integer) indexItem[1], 0);
        }
    }

    @SuppressLint("DefaultLocale")
    private void PoplulateSideview() {

        String latter_temp, latter = "";
        int index = 0;
        sideIndex.removeAllViews();
        sideIndexList.clear();
        for (int i = 0; i < COUNTRIES.size(); i++) {
            Object[] temp = new Object[2];
            latter_temp = (COUNTRIES.get(i)).substring(0, 1).toUpperCase();
            if (!latter_temp.equals(latter)) {
                // latter with its array index
                latter = latter_temp;
                temp[0] = latter;
                temp[1] = i + index;
                index++;
                sideIndexList.add(temp);

                TextView latter_txt = new TextView(this);
                latter_txt.setText(latter);

                latter_txt.setSingleLine(true);
                latter_txt.setHorizontallyScrolling(false);
                latter_txt.setTypeface(null, Typeface.NORMAL);
                latter_txt.setTextSize(11);
                latter_txt.setBackgroundColor(getResources().getColor(R.color.transparent));
                latter_txt.setTextColor(getResColor(R.color.lable_travel_color));
                LayoutParams params = new LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1);
                params.gravity = Gravity.CENTER_HORIZONTAL;
                params.height = 40;

                latter_txt.setLayoutParams(params);
                latter_txt.setPadding(10, 0, 10, 0);

                sideIndex.addView(latter_txt);
            }
        }

        sideIndexSize = sideIndexList.size();

    }

    private TextWatcher filterTextWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
            new StandardArrayAdapter(COUNTRIES).getFilter().filter(s.toString());
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // your search logic here
        }

    };

    /**
     * @param area 区域
     * @Title: returnResult
     * @Description: 返回数据
     */
    private void returnResult(String area) {
        CommonFunction.hideInputMethod(mContext, search);
        Intent intent = new Intent();
        intent.putExtra("area", area);
        CountrySelectActivity.this.setResult(1, intent);
        CountrySelectActivity.this.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            CommonFunction.hideInputMethod(mContext, search);
            CountrySelectActivity.this.setResult(0, null);
            CountrySelectActivity.this.finish();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

}
