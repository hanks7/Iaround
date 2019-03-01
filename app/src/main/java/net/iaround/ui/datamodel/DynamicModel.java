package net.iaround.ui.datamodel;

import android.content.Context;

import net.iaround.conf.Common;
import net.iaround.connector.protocol.DynamicHttpProtocol;
import net.iaround.database.DatabaseFactory;
import net.iaround.database.DynamicWorker;
import net.iaround.model.im.DynamicNewNumberBean;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.GsonUtil;
import net.iaround.tools.PathUtil;
import net.iaround.tools.SharedPreferenceCache;
import net.iaround.ui.datamodel.ResourceListBean.ResourceItemBean;
import net.iaround.ui.dynamic.bean.DynamicItemBean;
import net.iaround.ui.dynamic.bean.DynamicListFileBean;
import net.iaround.ui.dynamic.bean.DynamicMineNewBean;
import net.iaround.ui.dynamic.bean.DynamicUnLikeOrReviewBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * 动态模块的业务数据处理类.主要处理：1.与数据库之间的交互 2.缓存一些常用的数据 动态分为：5.5以前的照片动态 and 5.5以后的新动态
 * 
 * @author KevinSu
 */

public class DynamicModel extends Model {
	private DynamicNewNumberBean newCommetLikeNumBean;// 最新评论的实体，保存到文件当中去
	private DynamicMineNewBean mineDynamicBean;// 我的动态Bar的数据实体
	private ArrayList<DynamicItemBean> dynamicCenterList;// 动态中心的数据
	private ArrayList<DynamicItemBean> dynamicMineList;// 我的动态的数据
	private static ArrayList<DynamicItemBean> dynamicUnSendSuccessList;//动态未发送成功列表

	private static ArrayList<DynamicItemBean> dynamicUnreviewedList = new ArrayList<DynamicItemBean>();  //动态未发送成功为审核通过的动态
	private static ArrayList<DynamicItemBean> retDynamicUnreviewedList = new ArrayList<DynamicItemBean>();

	private ArrayList<ResourceItemBean> resourcesList;//动态中心资源列表
	/** 动态点赞未发送成功列表 */
	private HashMap<String, Object> likeUnSendSuccessMap = new HashMap<String, Object>();
	private HashMap<Long, Long> likeSendFlag = new HashMap<Long, Long>();
	
	private static DynamicModel model = null;

	private DynamicModel() {
		getDynamicMyNewFromFile();
		getDynamicCenterListFromFile();
		getDynamicMineListFromFile();
	}

	public static DynamicModel getInstent() {
		if (model == null) {
			model = new DynamicModel();
		}
		return model;
	}

	/**
	 * 插入一条记录，如果该记录已存在，则未读数加num
	 * */
	public void insertOrUpdateOneRecord(Context context, JSONObject obj) {
		int num = obj.optInt( "num" );
		int sorttype = obj.optInt("sorttype");
		int subtype = obj.optInt("subtype");
		String user = CommonFunction.jsonOptString(obj, "user");
		JSONObject userObj;
		try {
			userObj = new JSONObject(user);
			String fuid = CommonFunction.jsonOptString(userObj, "userid");
			String muid = Common.getInstance().loginUser.getUid() + "";

			DynamicWorker db = DatabaseFactory.getDynamicWorker(context);
			int oneUserUnread = db.countOneUserUnread(muid, fuid);
			if (oneUserUnread == 0) {
				db.insertOneRecord(muid, fuid, sorttype, subtype, num, user);
			} else {
				db.updateUnread(muid, fuid, user, subtype, oneUserUnread + num);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除某用户所有动态记录
	 * */
	public void deleteAllRecord(Context context, String muid) {
		DynamicWorker db = DatabaseFactory.getDynamicWorker(context);
		db.deleteAll(muid);
	}

	/**
	 * 查看多少人发来“关于我的”动态
	 * */
	public int countMyDynamicSender(Context context, String muid) {
		DynamicWorker db = DatabaseFactory.getDynamicWorker(context);

		return db.countMyDynamicSender( muid );
	}

	/**
	 * 查看总共未读的“关于我的”动态数
	 * */
	public int countMyDynamicUnread(Context context, String muid) {
		DynamicWorker db = DatabaseFactory.getDynamicWorker(context);

		return db.countAllUnread(muid);
	}

	/**
	 * 查看私聊发送人和动态发送人的并集的数量
	 * */
	public int countUnionChatDynamicSender(Context context, String muid) {
		int count = 0;
		count = DatabaseFactory.getDynamicWorker(context)
				.countUnionChatMyDynamicSender(muid);
		return count;
	}

	/************************** 对缓存操作 *********************************/

	/**
	 * 获取最新动态未读数量实体
	 * 
	 * @return
	 */
	public DynamicNewNumberBean getNewNumBean() {
		if(newCommetLikeNumBean == null){
			newCommetLikeNumBean = new DynamicNewNumberBean();
		}
		return newCommetLikeNumBean;
	}

	/**
	 * 设置最新动态未读数量实体，如果返回null，表示没有相关最新动态
	 * 
	 * @param newCommetBean
	 */
	public void setNewNumBean(DynamicNewNumberBean newCommetBean) {
		this.newCommetLikeNumBean = newCommetBean;
	}

	/**
	 * 我的最新动态，先从数据库取，获取到最新的写回数据库
	 * 
	 * @return
	 */
	public DynamicMineNewBean getMineDynamicBean() {
		if (mineDynamicBean == null) {
			mineDynamicBean = new DynamicMineNewBean();
		}
		return mineDynamicBean;
	}

	/**
	 * 设置我的最新动态
	 * 
	 * @param mineDynamicBean
	 */
	public void setMineDynamicBean(DynamicMineNewBean mineDynamicBean) {
		this.mineDynamicBean = mineDynamicBean;
	}

	/**
	 * 获取动态中心的数据
	 */
	public ArrayList<DynamicItemBean> getDynamicCenterList() {
		if (dynamicCenterList == null) {
			dynamicCenterList = new ArrayList<DynamicItemBean>();
		}
		return dynamicCenterList;
	}

	/**
	 * 设置动态中心的数据
	 * 
	 * @param list
	 */
	public void setDynamicCenterList(ArrayList<DynamicItemBean> list) {
		if (dynamicCenterList == null) {
			dynamicCenterList = new ArrayList<DynamicItemBean>();
		}
		dynamicCenterList.clear();
		dynamicCenterList.addAll(list);
	}

	/**
	 * 添加一条我的动态的记录
	 * 
	 * @param item
	 * @param addHead
	 *            是否在最开始的地方添加; 否就加到最后面
	 */
	public void addRecordToDynamicCenterList(DynamicItemBean item,
			boolean addHead) {
		if (dynamicCenterList == null) {
			dynamicCenterList = new ArrayList<DynamicItemBean>();
		}

		if (addHead) {
			dynamicCenterList.add(0, item);
		} else {
			dynamicCenterList.add(item);
		}

	}

	/** 更新动态的状态 */
	public boolean updateDynamicCenterListStatus(long dynamicId, byte status) {
		boolean result = false;// 是否有修改状态

		if (dynamicCenterList == null) {
			dynamicCenterList = new ArrayList<DynamicItemBean>();
		}

		for (int i = 0; i < dynamicCenterList.size(); i++) {
			DynamicItemBean itemBean = dynamicCenterList.get(i);
			if (itemBean.getDynamicInfo().dynamicid == dynamicId) {
				itemBean.setSendStatus(status);
				result = true;
				break;
			}
		}
		return result;
	}

	/**
	 * 获取我的动态的数据
	 */
	public ArrayList<DynamicItemBean> getDynamicMineList() {
		if (dynamicMineList == null) {
			dynamicMineList = new ArrayList<DynamicItemBean>();
		}
		return dynamicMineList;
	}

	/**
	 * 设置我的动态的数据
	 * 
	 * @param dynamicMineList
	 */
	public void setDynamicMineList(ArrayList<DynamicItemBean> list) {
		if (dynamicMineList == null) {
			dynamicMineList = new ArrayList<DynamicItemBean>();
		}
		dynamicMineList.clear();
		dynamicMineList.addAll(list);
	}

	/**
	 * 添加一条我的动态的记录
	 * 
	 * @param item
	 * @param addHead
	 *            是否在最开始的地方添加; 否就加到最后面
	 */
	public void addRecordToMineList(DynamicItemBean item, boolean addHead) {
		if (dynamicMineList == null) {
			dynamicMineList = new ArrayList<DynamicItemBean>();
		}

		if (addHead) {
			dynamicMineList.add(0, item);
		} else {
			dynamicMineList.add(item);
		}

	}

	/** 更新我的动态的状态 */
	public boolean updateDynamicMineListStatus(long dynamicId, byte status) {
		boolean result = false;// 是否有修改状态

		if (dynamicMineList == null) {
			dynamicMineList = new ArrayList<DynamicItemBean>();
		}

		for (int i = 0; i < dynamicMineList.size(); i++) {
			DynamicItemBean itemBean = dynamicMineList.get(i);
			if (itemBean.getDynamicInfo().dynamicid == dynamicId) {
				itemBean.setSendStatus(status);
				result = true;
				break;
			}
		}
		return result;
	}
	
	
	/**
	 * 获取动态未发送成功的列表
	 * @return
	 */
	public ArrayList<DynamicItemBean> getUnSendSuccessList() {
		if(dynamicUnSendSuccessList == null)
		{
			dynamicUnSendSuccessList = new ArrayList<DynamicItemBean>();
		}
		return dynamicUnSendSuccessList;
	}

	/**
	 * 添加一条动态到未发送成功动态列表
	 * @param itemBean
	 */
	public void addUnSendSuccessList(DynamicItemBean itemBean) {
		if(dynamicUnSendSuccessList == null)
		{
			dynamicUnSendSuccessList = new ArrayList<DynamicItemBean>();
		}
		dynamicUnSendSuccessList.add(itemBean);
	}
	
	
	/**
	 * 获取动态中心的资源列表
	 * @return
	 */
	public ArrayList<ResourceItemBean> getResourcesList() {
		if(resourcesList == null)
		{
			resourcesList = new ArrayList<ResourceItemBean>();
		}
		return resourcesList;
	}

	/**
	 * 设置动态中心的资源列表
	 * @param resourcesList
	 */
	public void setResourcesList(ArrayList<ResourceItemBean> resourcesList) {
		if(resourcesList == null)
		{
			resourcesList = new ArrayList<ResourceItemBean>();
		}
		resourcesList.clear();
		resourcesList.addAll(resourcesList);
	}

	public void reset() {
		if (newCommetLikeNumBean != null)
			newCommetLikeNumBean = null;
		if (mineDynamicBean != null)
			mineDynamicBean = null;
		if (dynamicCenterList != null)
			dynamicCenterList.clear();
		if (dynamicMineList != null)
			dynamicMineList.clear();
		if (dynamicUnSendSuccessList != null)
			dynamicUnSendSuccessList.clear();
		if (likeUnSendSuccessMap != null)
			likeUnSendSuccessMap.clear();
		if (likeSendFlag != null)
			likeSendFlag.clear();
		model = null;
	}

	/************************** 对数据库&文件操作 *********************************/

	/**
	 * 获取我的最新动态实体
	 */
	public void getDynamicMyNewFromFile() {
		mineDynamicBean = (DynamicMineNewBean) getBufferFromFile(PathUtil
				.getDynamicMyNewFilePath());
	}

	/**
	 * 保存我的最新动态实体
	 */
	public void saveDynamicMyNewToFile() {
		saveBufferToFile( PathUtil.getDynamicMyNewFilePath( ), mineDynamicBean );
	}

	/**
	 * 获取动态中心的数据
	 */
	public void getDynamicCenterListFromFile() {
		DynamicListFileBean bean = (DynamicListFileBean) getBufferFromFile(PathUtil
				.getDynamicCenterFilePath());
		if (dynamicCenterList == null) {
			dynamicCenterList = new ArrayList<DynamicItemBean>();
		}
		dynamicCenterList.clear( );
		if (bean != null) {
			dynamicCenterList.addAll(bean.dynamicList);
		}
	}

	/**
	 * 把动态中心的数据保存成文件
	 */
	public void saveDynamicCenterListToFile() {
		DynamicListFileBean bean = new DynamicListFileBean();
		bean.dynamicList.addAll(getDynamicCenterList());
		saveBufferToFile( PathUtil.getDynamicCenterFilePath( ), bean );
	}

	/**
	 * 获我的动态的数据
	 */
	public void getDynamicMineListFromFile() {
		DynamicListFileBean bean = (DynamicListFileBean) getBufferFromFile(PathUtil
				.getDynamicMineFilePath());
		if (dynamicMineList == null) {
			dynamicMineList = new ArrayList<DynamicItemBean>();
		}
		dynamicMineList.clear( );
		if (bean != null) {
			dynamicMineList.addAll(bean.dynamicList);
		}
	}

	/**
	 * 把我的动态数据保存成文件
	 */
	public void saveDynamicMineListToFile() {
		DynamicListFileBean bean = new DynamicListFileBean();
		if (dynamicMineList != null) {
			bean.dynamicList.addAll(dynamicMineList);
		}
		saveBufferToFile(PathUtil.getDynamicMineFilePath(), bean);
	}
	
	/**
	 * 获取动态点赞或评论未发送成功的flag  </br>
	 * ke  为  flag  </br>
	 * value  为  dynamicId
	 * @return
	 */
	public HashMap<Long, Long> getLikeSendFlagMap() {
		if (likeSendFlag == null) {
			likeSendFlag = new HashMap<Long, Long>();
		}
		return likeSendFlag;
	}
	
	/**
	 * 移除一条点赞或评论到未发送成功的flag
	 * 
	 */
	public void removeLikeSendFlagMap(long key ) {
		if (likeSendFlag == null) {
			return;
		}
		
		if (likeSendFlag.containsKey(key)) {
			likeSendFlag.remove(key);
		}
	}
	
	/**
	 * 获取动态点赞或评论未发送成功的数据
	 * @return
	 */
	public HashMap<String, Object> getLikeUnSendSuccessMap() {
		if (likeUnSendSuccessMap == null) {
			likeUnSendSuccessMap = new HashMap<String, Object>();
		}
		return likeUnSendSuccessMap;
	}
	
	/**
	 * 添加一条点赞未发送成功的数据
	 * 
	 */
	public void addLikeUnSendSuccessMap(String key , Object value) {
		if (likeUnSendSuccessMap == null) {
			likeUnSendSuccessMap = new HashMap<String, Object>();
		}
		likeUnSendSuccessMap.put(key, value);
	}
	
	/**
	 * 移除一条未发送成功的点赞或评论列表
	 * 
	 */
	public void removeLikeUnSendSuccessMap(String key ) {
		if (likeUnSendSuccessMap == null) {
			return;
		}
		if (likeUnSendSuccessMap.containsKey(key)) {
			likeUnSendSuccessMap.remove( key );
		}
	}
	
	/**
	 * 保存未发送成功点赞的数据
	 */
	public void saveLikeUnSendSuccessToCache(Context context) {
		HashMap<String, Object> map = getLikeUnSendSuccessMap( );
		if (map.size() > 0) {
			ArrayList<String> list = new ArrayList<String>();
			Iterator<String> keys = map.keySet().iterator();
			while (keys.hasNext()) {
				String key = keys.next();
				DynamicUnLikeOrReviewBean value = (DynamicUnLikeOrReviewBean) map.get(key);
				String str = GsonUtil.getInstance().getStringFromJsonObject(value);
				list.add(str);
			}
			String UserId = String.valueOf(Common.getInstance().loginUser.getUid());
			String KEY = SharedPreferenceCache.DYNAMIC_LIKE_FAIL + UserId;
			SharedPreferenceCache.getInstance(context).putString( KEY, list.toString( ) );
		}
	}
	
	/**
	 * 获取未发送成功点赞的数据
	 */
	public String getLikeUnSendSuccessFromCache(Context context) {
		String UserId = String.valueOf(Common.getInstance().loginUser.getUid());
		String KEY = SharedPreferenceCache.DYNAMIC_LIKE_FAIL + UserId;
		String cacheStr = SharedPreferenceCache.getInstance(context).getString( KEY );
		if (CommonFunction.isEmptyOrNullStr( cacheStr )) {
			return "";
		}
		return cacheStr;
	}
	
	/**
	 * 将缓存的文件转换成list
	 * @param mContext
	 * @return  list
	 */
	public ArrayList<DynamicUnLikeOrReviewBean> cacheDataConvertList(Context mContext){
		String cacheStr = getLikeUnSendSuccessFromCache( mContext );
		if (CommonFunction.isEmptyOrNullStr(cacheStr)) {
			return new ArrayList<DynamicUnLikeOrReviewBean>();
		}
		ArrayList<DynamicUnLikeOrReviewBean> mList = new ArrayList<DynamicUnLikeOrReviewBean>();
		cacheStr = cacheStr.replaceAll("\\[", "").replaceAll("\\]", "");
		if (cacheStr.contains( ", " )) {
			String[] array = cacheStr.split(", ");
			for (String string : array) {
				DynamicUnLikeOrReviewBean cacheBean = GsonUtil.getInstance()
						.getServerBean(string, DynamicUnLikeOrReviewBean.class);
				mList.add(cacheBean);
			}
		}else {
			DynamicUnLikeOrReviewBean cacheBean = GsonUtil.getInstance()
					.getServerBean(cacheStr, DynamicUnLikeOrReviewBean.class);
			mList.add(cacheBean);
		}
		
		return mList;
	}
	
	
	/**
	 * 登录后将缓存的所有数据赋给likeUnSendSuccessMap ， 清空缓存
	 */
	public void updataCacheData(Context mContext){
		ArrayList<DynamicUnLikeOrReviewBean> mList = cacheDataConvertList( mContext );
		
		for (DynamicUnLikeOrReviewBean bean : mList) {
			getLikeUnSendSuccessMap().put(String.valueOf(bean.dynamicid), bean);
		}
		
		String UserId = String.valueOf(Common.getInstance().loginUser.getUid());
		String KEY = SharedPreferenceCache.DYNAMIC_LIKE_FAIL + UserId;
		SharedPreferenceCache.getInstance(mContext).putString(KEY , "");
	}
	
	/**
	 * 重新发送未发送成功的赞评
	 */
	public void ResendSendLike(Context mContext) {
		Map<String, Object> map = getLikeUnSendSuccessMap();
		if (map.size() > 0) {
			Iterator<String> keys = map.keySet().iterator();
			while (keys.hasNext()) {
				String key = keys.next();
				DynamicUnLikeOrReviewBean value = (DynamicUnLikeOrReviewBean) map.get(key);

				long flag = 0;
				if (value.type == 1) {
					if (value.postbarid == -1) {
						CommonFunction.log("", "--->重新发送动态点赞" + flag + "**id == "
								+ value.dynamicid + "***postbarid==" + value.postbarid);
						flag = DynamicHttpProtocol.greetUserDynamic(mContext,value.dynamicid, null);
					} else {
//						CommonFunction.log("", "--->重新发送贴吧点赞" + flag + "**id == "
//								+ value.dynamicid + "***postbarid==" + value.postbarid);
//						flag = PostbarHttpProtocol.addPostbarTopicLike(
//								mContext, value.postbarid, value.dynamicid,null);//jiqiang
					}
					getLikeSendFlagMap().put(flag, value.dynamicid);
					

				} else if (value.type == 2) {
					if (value.postbarid == -1) {
						CommonFunction.log("", "--->重新发送动态取消点赞" + flag + "**id == "
								+ value.dynamicid + "***postbarid==" + value.postbarid);
						flag = DynamicHttpProtocol.greetCancelUserDynamic(
								mContext, value.dynamicid, null);
					} else {
//						CommonFunction.log("", "--->重新发送贴吧取消点赞" + flag + "**id == "
//								+ value.dynamicid + "***postbarid==" + value.postbarid);
//						flag = PostbarHttpProtocol.cancelPostbarTopicLike(
//								mContext, value.postbarid, value.dynamicid,null);//jiqiang
					}
					getLikeSendFlagMap().put(flag, value.dynamicid);
				}
			}
	 }}


	/*
	保存发送成功的id两分钟
	 */
	public void addUnreviewedItem(DynamicItemBean item )
	{
		if(item!=null)
		{
			item.curSendSucessTime = System.currentTimeMillis();
			item.dynamicUserid = Common.getInstance().loginUser.getUid( );
			dynamicUnreviewedList.add(item);
		}
	}

	public ArrayList<DynamicItemBean> getUnreviewedItem(ArrayList<DynamicItemBean> list )
	{
		long uid = Common.getInstance( ).loginUser.getUid( );
		long curTime = System.currentTimeMillis( );
		final long ReserverTime = 11*60*1000; //保留11分钟

		Iterator< DynamicItemBean > iterator = dynamicUnreviewedList.iterator( );

		while ( iterator.hasNext( ) )
		{
			DynamicItemBean item = iterator.next( );
			if ( item.dynamicUserid == uid && curTime - item.curSendSucessTime > ReserverTime )
			{
				//删除超过ReserverTime时间的发布项;
				CommonFunction
					.log( "shifengxiong", "getUnreviewedItem ====发布时间==" + item.curSendSucessTime );
				iterator.remove( );

			}
		}

		removeUnreviewedMultipleItem(list);


		retDynamicUnreviewedList.clear();
		iterator = dynamicUnreviewedList.iterator( );
		while ( iterator.hasNext( ) )
		{
			DynamicItemBean item = iterator.next( );
			if(item.dynamicUserid == Common.getInstance().loginUser.getUid( ))
			{
				retDynamicUnreviewedList.add(item  );
			}
		}


		return retDynamicUnreviewedList;
	}

	public void removeUnreviewedMultipleItem(ArrayList<DynamicItemBean> list)
	{
		if(dynamicUnreviewedList.size() ==0)return;
		if(list!=null&&list.size()>0)
		{
			for ( DynamicItemBean dynamicItem : list )
			{
				Iterator< DynamicItemBean > iterator = dynamicUnreviewedList.iterator( );
				while ( iterator.hasNext( ) )
				{
					DynamicItemBean item = iterator.next( );
					if ( item.getDynamicInfo().dynamicid == dynamicItem.getDynamicInfo().dynamicid )
					{
						//删除超过ReserverTime时间的发布项;
						CommonFunction.log( "shifengxiong",
							"列表中有相同的动态" + dynamicItem.getDynamicInfo().dynamicid  );
						iterator.remove( );
						break;

					}
				}
			}
		}
	}

}
