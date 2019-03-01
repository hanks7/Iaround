package net.iaround.ui.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiItemDetail;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amap.api.services.poisearch.PoiSearch.Query;
import com.amap.api.services.poisearch.PoiSearch.SearchBound;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import net.iaround.R;
import net.iaround.model.entity.GeoData;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.LocationUtil;
import net.iaround.tools.SharedPreferenceUtil;
import net.iaround.ui.adapter.AddressListAdapter;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 使用大众点评或者高德地图的api获取周边数据
 */
public class MapSearchIaroundActivity extends BaseFragmentActivity implements
		OnPoiSearchListener, OnClickListener{

	private final String TAG = this.getClass().getName();

	/********** 地址数据请求的参数 *********/
	private final String apiUrl = "http://api.dianping.com/v1/business/find_businesses";
	private final String appKey = "88715318"; // 请替换为自己的 App Key 和 App secret
	private final String secret = "67215dadd62d4f1fb3cc29cbff006de3";
	private final String[] categorys = { "生活服务", "美食", "购物", "汽车服务", "旅行社",
			"酒店", "运动健身", "休闲娱乐" };

	private int subCategory;// 请求的地址类型,对应categorys的某一项

	private ArrayList<PoiInfo> addresses = new ArrayList<PoiInfo>();
	private PoiInfo dontShowAddressInfo = new PoiInfo();// 不显示地址的Item信息
	private AddressListAdapter addAdapter;

	private PullToRefreshListView mIaroundListView;
	private View dzdpView;// 大众点评标识View
	private View rlSearchView;//搜索栏
	private EditText etSearch;//输入栏
	
	private ProgressBar mProgressBar;

	private boolean hasNextPage = true;
	private int pageNo = 1;
	private int totleDataGet = 0;// 已经获取了多少条数据

	private PoiResult poiResult;
	private List<PoiItem> poiItems;// poi数据
	private boolean isSwitchDianpin = false;

//	private final int DZDP_FEEDBACK_FLAG = 0;// 大众点评数据请求返回Flag
	private final int NO_MORE_DATA_FLAG = 1;// 无更多数据Flag
	private final int AMAP_FEETBACK_FLAG = 2;// 高德地图数据请求返回Flag

	private Double latitude;
	private Double longitude;

	private String nameSelected = "";
	public static final String ADDRESS_NAME_KEY = "selected_name";

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {

			stopPulling();

			switch (msg.what) {

//			case DZDP_FEEDBACK_FLAG:
//				JSONObject jsonObject;
//				try {
//					jsonObject = new JSONObject(String.valueOf(msg.obj));
//					JSONArray busineesses = (JSONArray) jsonObject
//							.get("businesses");
//					int total = jsonObject.optInt("total_count");
//					int count = jsonObject.optInt("count");
//
//					totleDataGet += count;
//
//					if (totleDataGet >= total) {
//						subCategory++;
//						if (subCategory >= categorys.length) {
//							hasNextPage = false;
//						} else {
//							pageNo = 0;
//							totleDataGet = 0;
//						}
//					}
//
//					if (pageNo == 1 && subCategory == 0) {
//						if (addresses.size() > 1) {
//							addresses.clear();
//							addresses.add(dontShowAddressInfo);
//						}
//
//						if (busineesses != null && busineesses.length() > 0) {
//							PoiInfo addInfo = new PoiInfo();
//
//							addInfo.name = busineesses.getJSONObject(0)
//									.optString("city");
//
//							addInfo.lat = latitude;
//							addInfo.lng = longitude;
//							addInfo.address = addInfo.name;
//							addInfo.type = 1;
//							addresses.add(addInfo);
//
//							addAdapter.setStrCity(addInfo.name);
//						}
//					}
//
//					for (int i = 0, iMax = busineesses.length(); i < iMax; i++) {
//						PoiInfo addInfo = new PoiInfo();
//
//						JSONObject obj = busineesses.getJSONObject(i);
//						addInfo.address = obj.optString("address");
//						addInfo.name = obj.optString("name");
//						addInfo.lat = obj.optDouble("latitude");
//						addInfo.lng = obj.optDouble("longitude");
//						addInfo.type = 1;
//						addresses.add(addInfo);
//
//					}
//
//					if (addAdapter == null) {
//						addAdapter = new AddressListAdapter(
//								MapSearchIaroundActivity.this, addresses, nameSelected);
//						addAdapter.setmOriginalValues(addresses);
//						mIaroundListView.setAdapter(addAdapter);
//					} else {
//						addAdapter.setmOriginalValues(addresses);
//						addAdapter.notifyDataSetChanged();
//					}
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//				break;

			case NO_MORE_DATA_FLAG:
				CommonFunction.toastMsg(MapSearchIaroundActivity.this,
						R.string.no_more_data);
				stopPulling();
				break;
			case AMAP_FEETBACK_FLAG:
				if (poiItems != null && poiItems.size() > 0) {
					if (pageNo == 1 && subCategory == 0) {
						if (addresses.size() > 1) {
							addresses.clear();
							addresses.add(dontShowAddressInfo);
						}

						if (poiItems != null && poiItems.size() > 0) {
							PoiInfo addInfo = new PoiInfo();

							addInfo.name = poiItems.get(0).getCityName();
							addInfo.lat = latitude;
							addInfo.lng = longitude;
							addInfo.address = addInfo.name;
							addInfo.type = 1;
							addresses.add(addInfo);

							addAdapter.setStrCity(addInfo.name);
						}
					}

					for (int i = 0, iMax = poiItems.size(); i < iMax; i++) {
						PoiInfo addInfo = new PoiInfo();
						PoiItem poi = poiItems.get(i);
						addInfo.address = poiItems.get(i).getSnippet();

						addInfo.name = poiItems.get(i).toString();
						addInfo.lat = poiItems.get(i).getLatLonPoint()
								.getLatitude();
						addInfo.lng = poiItems.get(i).getLatLonPoint()
								.getLongitude();
						addInfo.type = 1;
						addresses.add(addInfo);

					}
					if (addAdapter == null) {
						addAdapter = new AddressListAdapter(
								MapSearchIaroundActivity.this, addresses, nameSelected);
						addAdapter.setmOriginalValues(addresses);
						mIaroundListView.setAdapter(addAdapter);
					} else {
						addAdapter.setmOriginalValues(addresses);
						addAdapter.notifyDataSetChanged();
					}
				}
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_poisearch);

		GeoData geo = LocationUtil.getCurrentGeo(MapSearchIaroundActivity.this);
		latitude = geo.getLat() * 1.0 / 1E6;
		longitude = geo.getLng() * 1.0 / 1E6;

		nameSelected = getIntent().getStringExtra(ADDRESS_NAME_KEY);
		if (nameSelected == null) nameSelected = "";

		initView();

		// 初始化不显示地址的item
		dontShowAddressInfo.name = getResources().getString(
				R.string.map_select_no_visibility);
		dontShowAddressInfo.type = 0;

		addresses.add(dontShowAddressInfo);

		SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance(MapSearchIaroundActivity.this);
		if (sp.has(SharedPreferenceUtil.SWITCH)) {
			String currentItemStatus = sp
					.getString(SharedPreferenceUtil.SWITCH);
			if (currentItemStatus.length() >= 1) {
				isSwitchDianpin = currentItemStatus.charAt(0) == '1';
			}
		}

		// 如果是用大众点评的api,需要展示大众点评的标识View
		if (isSwitchDianpin) {
			View dzdpView = View.inflate(mContext,
					R.layout.l_address_list_dazongdianpin_view, null);
			mIaroundListView.getRefreshableView().addHeaderView(dzdpView);
		}

		if (addAdapter == null) {
			addAdapter = new AddressListAdapter(MapSearchIaroundActivity.this,
					addresses, nameSelected);
			mIaroundListView.setAdapter(addAdapter);
		}

		performPulling();
	}

	/**
	 * 设置页面监听
	 */
	private void initView() {
		findViewById(R.id.iv_left).setOnClickListener(this);
		findViewById(R.id.fl_left).setOnClickListener(this);
		findViewById(R.id.fl_back).setOnClickListener(this);
		((TextView) findViewById(R.id.tv_title))
				.setText(R.string.map_select_address_title);

		mIaroundListView = (PullToRefreshListView) findViewById(R.id.lvPoints);

//		PauseOnScrollListener listener = new PauseOnScrollListener(
//				ImageViewUtil.getDefault().getImageLoader(), true, true);

		mIaroundListView.getRefreshableView().setDividerHeight(0);
//		mIaroundListView.setOnScrollListener(listener);

		mIaroundListView.getRefreshableView().setSelector(
				R.drawable.transparent);
		mIaroundListView.setPullToRefreshOverScrollEnabled(false);// 禁止滚动过头
		mIaroundListView.getRefreshableView().setFastScrollEnabled(false);
		mIaroundListView.setMode(Mode.BOTH);

		mIaroundListView
				.setOnRefreshListener(new OnRefreshListener2<ListView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						subCategory = 0;
						pageNo = 1;
						hasNextPage = true;
						totleDataGet = 0;
						try {
							if (isSwitchDianpin) {
								request(pageNo);
							} else {
								doSearchIaround(pageNo);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						if (hasNextPage) {
							pageNo++;
							try {
								if (isSwitchDianpin) {
									request(pageNo);
								} else {
									doSearchIaround(pageNo);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						} else {
							mHandler.sendEmptyMessage(NO_MORE_DATA_FLAG);
						}
					}
				});

		mIaroundListView.getRefreshableView().setTextFilterEnabled(true);

		rlSearchView = View.inflate(mContext, R.layout.l_address_search_bar_view, null);
		etSearch = (EditText) rlSearchView.findViewById(R.id.etSearch);
		etSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

				if (TextUtils.isEmpty(s)) {
					mIaroundListView.getRefreshableView().clearTextFilter();
					mIaroundListView.setMode(Mode.BOTH);
				} else {
					mIaroundListView.getRefreshableView().setFilterText(s.toString());
					mIaroundListView.setMode(Mode.DISABLED);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});

		mIaroundListView.getRefreshableView().addHeaderView(rlSearchView);
	}

	/**
	 * 大众点评的地址请求<br>
	 * <b>请求参数:</b><br>
	 * category=小区& latitude=23.11928013000488& longitude=113.37419622802734
	 * &sort=1 &limit=20 &offset_type=1 &out_offset_type=1 &platform=2
	 */
	public void request(int page) throws Exception {

		final int pagenum = page;

//		new Thread() {
//			public void run() {
//				try {
//					Map<String, String> paramMap = getParaMap(pagenum);
//					// 对参数名进行字典排序
//					String[] keyArray = paramMap.keySet()
//							.toArray(new String[0]);
//					Arrays.sort(keyArray);
//
//					StringBuilder stringBuilder = new StringBuilder();
//					// 拼接有序的参数名-值串
//					stringBuilder.append(appKey);
//					for (String key : keyArray) {
//						stringBuilder.append(key).append(paramMap.get(key));
//					}
//
//					String codes = stringBuilder.append(secret).toString();
//					// SHA-1编码，
//					// 这里使用的是Apache-codec，即可获得签名(shaHex()会首先将中文转换为UTF8编码然后进行sha1计算，使用其他的工具包请注意UTF8编码转换)
//					Locale defloc = Locale.getDefault();
//					String sign = org.apache.commons.codec.digest.DigestUtils
//							.shaHex(codes).toUpperCase(defloc);
//
//					// 添加签名
//					stringBuilder = new StringBuilder();
//					stringBuilder.append("appkey=").append(appKey)
//							.append("&sign=").append(sign);
//					for (Entry<String, String> entry : paramMap.entrySet()) {
//						stringBuilder.append('&').append(entry.getKey())
//								.append('=').append(entry.getValue());
//					}
//					String queryString = stringBuilder.toString();
//
//					StringBuffer response = new StringBuffer();
//					HttpClientParams httpConnectionParams = new HttpClientParams();
//					httpConnectionParams.setConnectionManagerTimeout(1000);
//					HttpClient client = new HttpClient(httpConnectionParams);
//					HttpMethod method = new GetMethod(apiUrl);
//
//					BufferedReader reader = null;
//					String encodeQuery = URIUtil.encodeQuery(queryString,
//							"UTF-8"); // UTF-8
//					// 请求
//					method.setQueryString(encodeQuery);
//					client.executeMethod(method);
//					reader = new BufferedReader(new InputStreamReader(
//							method.getResponseBodyAsStream(), "UTF-8"));
//					String line = null;
//					while ((line = reader.readLine()) != null) {
//						response.append(line).append(
//								System.getProperty("line.separator"));
//					}
//					reader.close();
//					method.releaseConnection();
//
//					Message msg = new Message();
//					msg.what = DZDP_FEEDBACK_FLAG;
//					msg.obj = response.toString();
//					mHandler.sendMessage(msg);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		}.start();
	}

	// 获取大众点评需要的请求的参数map
	private Map<String, String> getParaMap(int pagenum) {
		Map<String, String> paramMap = new HashMap<String, String>();

		String category = categorys[subCategory];

		paramMap.put("latitude", latitude + "");
		paramMap.put("longitude", longitude + "");
		paramMap.put("category", category);
		paramMap.put("limit", "20");

		paramMap.put("offset_type", "1");
		paramMap.put("platform", "2");
		paramMap.put("out_offset_type", "1");
		paramMap.put("sort", "7");
		paramMap.put("format", "json");
		paramMap.put("page", pagenum + "");
		return paramMap;
	}

	@Override
	public void onPoiItemDetailSearched(PoiItemDetail arg0, int arg1) {
		CommonFunction.log(TAG, "PoiItemDetail resutl =" + arg0);
	}

	@Override
	public void onPoiSearched(PoiResult result, int arg1) {
		poiResult = result; // poi返回的结果

		// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
		if (poiResult != null) {
			poiItems = poiResult.getPois();
		}

		mHandler.sendEmptyMessage(AMAP_FEETBACK_FLAG);
	}

	/**
	 * 通过高德地图地址请求<br>
	 *
	 * @param page
	 */
	protected void doSearchIaround(int page) {
		// "餐馆|酒店|景点 |小区|商务楼|学校"
		Query query = new PoiSearch.Query("", "写字楼|小区|学校|餐饮|酒店|景点|影院", "");//
		// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）

		query.setPageSize(20);
		query.setPageNum(page);

		SearchBound bound = new PoiSearch.SearchBound(new LatLonPoint(latitude,
				longitude), 2000);

		PoiSearch poiSearch = new PoiSearch(this, query);
		poiSearch.setBound(bound);

		poiSearch.setOnPoiSearchListener(this);
		poiSearch.searchPOIAsyn();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.fl_left:
			case R.id.iv_left:
			case R.id.fl_back:
				finish();
				break;
		}
	}

	public void setProgressBarVisible(boolean isvisible) {
		if (isvisible && mProgressBar == null) {
			// 滚动条
			mProgressBar = new ProgressBar(this);
			int dp_24 = (int) (getResources().getDimension(R.dimen.dp_1) * 24);
			RelativeLayout.LayoutParams lpPb = new RelativeLayout.LayoutParams(
					dp_24, dp_24);
			lpPb.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
			mProgressBar.setIndeterminate(true);
			mProgressBar.setIndeterminateDrawable(getResources().getDrawable(
					R.drawable.pull_ref_pb));
			mProgressBar.setLayoutParams(lpPb);
			((ViewGroup) dzdpView).addView(mProgressBar);
		}

		if (mProgressBar != null) {
			mProgressBar.setVisibility(isvisible ? View.VISIBLE : View.GONE);
		}
	}

	public void performPulling() {
		mIaroundListView.setRefreshing();
	}

	public void stopPulling() {
		mIaroundListView.onRefreshComplete();
	}

	/** 地理位置搜索返回的实体 */
	public class PoiInfo {
		public String name;// 地理位置的名字,例如:御发商务大厦
		public String address;// 具体的地理位置,例如:黄埔大道中336号
		public double lat;// 纬度
		public double lng;// 经度
		public int type;// 类型,0-[不显示地址],1-[显示地址]
	}
}
