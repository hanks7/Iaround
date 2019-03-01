package net.iaround.ui.datamodel;


import net.iaround.model.entity.Quote;
import net.iaround.model.entity.Review;
import net.iaround.tools.CommonFunction;
import net.iaround.ui.group.bean.Topic;
import net.iaround.ui.group.bean.Users;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 圈子话题数据处理<br>
 * * <b>另外:<b/><br>
 * --圈子消息管理见 @GroupChatListModel <br>
 * --圈子管理 @GroupModel <br>
 */
public class TopicModel extends Model {
	private static TopicModel topicModel;
	private ArrayList<Topic> topicList; // 话题列表
	/**  圈话题关于我的未读数 */
	private int topicAboutMe;//未读评论数量
	private int likeNum;//未读点赞数量

	private TopicModel() {
		topicList = new ArrayList<Topic>();
	}

	public static TopicModel getInstance() {
		if (topicModel == null) {
			topicModel = new TopicModel();
		}
		return topicModel;
	}

	public ArrayList<Topic> getTopicList() {
		return topicList;
	}

	public void setTopicList(ArrayList<Topic> topicList) {
		this.topicList = topicList;
	}

	/**
	 * 设置替换当前的topic
	 * 
	 * @param index
	 * @param topic
	 */
	public void setTopic(int index, Topic topic) {
		if (index >= 0 && topicList.size() > index) {
			// topicList.set( index , topic );
		}
	}

	public HashMap<String, Object> parseRequest(String jsonStr)
			throws JSONException {
		HashMap<String, Object> map = new HashMap<String, Object>();
		JSONObject jsonObj = new JSONObject(jsonStr);

		if (jsonObj != null) {
			int status = jsonObj.optInt("status");
			map.put("status", status);
			if (200 != status) {
				map.put("error", jsonObj.optInt("error"));
			}
		}
		return map;
	}

	/**
	 * 解析话题列表
	 * 
	 * @param message
	 * @return
	 * @throws JSONException
	 */
	public HashMap<String, Object> parseTopicList(String jsonStr)
			throws JSONException {
		HashMap<String, Object> map = new HashMap<String, Object>();
		ArrayList<Topic> topicList = new ArrayList<Topic>();
		JSONObject jsonObj = new JSONObject(jsonStr);

		map.put("amount", jsonObj.optInt("amount"));
		map.put("pageno", jsonObj.optInt("pageno"));
		map.put("pagesize", jsonObj.optInt("pagesize"));
		map.put("topics", topicList);

		JSONArray jsonArr = jsonObj.optJSONArray("topics");
		if (jsonArr != null) {
			for (int i = 0; i < jsonArr.length(); i++) {
				jsonObj = jsonArr.getJSONObject(i);
				topicList.add(paramTopic(jsonObj));
			}
		}

		return map;
	}

	/**
	 * 解析每个topic
	 * 
	 * @param jsonObj
	 * @return
	 */
	public Topic paramTopic(JSONObject jsonObj) {
		if (jsonObj == null) {
			return null;
		}
		Topic topic = new Topic();
		JSONObject userJson = jsonObj.optJSONObject("user");
		User user = parseUser(userJson, 2);
		topic.setUser(user);
		topic.setTopicId(CommonFunction.jsonOptString(jsonObj, "topicid"));
		topic.setContent(CommonFunction.jsonOptString(jsonObj, "content"));
		topic.setImageUrl(CommonFunction.jsonOptString(jsonObj, "url"));
		topic.setType(jsonObj.optInt("type"));
		topic.setPlatname(CommonFunction.jsonOptString(jsonObj, "platname"));
		topic.setLikeCount(jsonObj.optInt("likecount"));
		topic.setReviewCount(jsonObj.optInt("reviewcount"));
		topic.setPublishTime(jsonObj.optLong("publishtime"));
		topic.setCurrUserLike((CommonFunction.jsonOptString(jsonObj,
                "curruserlike")).equalsIgnoreCase("y"));
		topic.setTop((CommonFunction.jsonOptString(jsonObj, "istop"))
                .equalsIgnoreCase("y"));
		topic.setGroupId(CommonFunction.jsonOptString(jsonObj, "groupid"));
		topic.setGroupName(CommonFunction.jsonOptString(jsonObj, "groupname"));
		return topic;
	}

	/**
	 * 解析喜欢话题的用户列表
	 * 
	 * @param jsonArray
	 * @return
	 */
	public ArrayList<Users> paramLikeList(JSONArray jsonArray) {
		ArrayList<Users> users = new ArrayList<Users>();
		if (jsonArray != null && jsonArray.length() > 0) {
			for (int i = 0; i < jsonArray.length(); i++) {
				try {
					JSONObject item = jsonArray.optJSONObject(i);
					JSONObject userItem = item.optJSONObject("user");
					if (item != null) {
						User user = parseUser(userItem, 2);
						Users listItem = new Users();
						listItem.setDistance(item.optInt("distance"));
						listItem.setDatetime(item.optLong("datetime"));
						listItem.setUser(user);
						users.add(listItem);
					}
				} catch (Exception e) {
				}
			}
		}
		return users;
	}

	/**
	 * 解析话题评论列表
	 * 
	 * @param result
	 * @return
	 */
	public ArrayList<Review> paramReviewList(JSONArray jsonArray) {
		ArrayList<Review> reviews = new ArrayList<Review>();
		if (jsonArray != null && jsonArray.length() > 0) {
			for (int i = 0; i < jsonArray.length(); i++) {
				try {
					JSONObject reviewJson = jsonArray.getJSONObject(i);
					Review review = new Review();
					// 用户
					JSONObject userjson = reviewJson.getJSONObject("user");
					User user = parseUser(userjson, 1);
					review.setUser(user);
					review.setDate(reviewJson.optLong("datetime"));
					review.setContent(CommonFunction.jsonOptString(reviewJson,
							"content"));
					review.setId(CommonFunction.jsonOptString(reviewJson,
							"reviewid"));
					// 引用
					JSONObject quoteJson = reviewJson.optJSONObject("quote");
					if (quoteJson != null) {
						Quote quote = new Quote();
						quote.setUserid(quoteJson.optLong("userid"));
						quote.setNickname(CommonFunction.jsonOptString(
								quoteJson, "nickname"));
						quote.setQuoteid(CommonFunction.jsonOptString(
								quoteJson, "quoteid"));
						quote.setContent(CommonFunction.jsonOptString(
								quoteJson, "qcontent"));
						quote.setStatus(quoteJson.optInt("status"));
						review.setQuote(quote);
					}
					review.setStatus(reviewJson.optInt("status"));
					reviews.add(review);
				} catch (JSONException e) {
				}
			}
		}
		return reviews;
	}

	/**
	 * 释放TopicModel
	 */
	public void reset() {
		if(topicList != null){
			topicList.clear();
			topicList = null;
		}
		topicModel = null;
	}
	
	public int getTopicAboutMe() {
		return topicAboutMe;
	}

	public void setTopicAboutMe(int topicAboutMe) {
		this.topicAboutMe = topicAboutMe;
	}

	public int getLikeNum( )
	{
		return likeNum;
	}

	public void setLikeNum( int likeNum )
	{
		this.likeNum = likeNum;
	}
}
