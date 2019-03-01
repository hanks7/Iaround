package net.iaround.model.im;

import java.util.ArrayList;

/**
 * @ClassName AccostRelationshipBean.java
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-8-11 下午5:13:45
 * @Description: 81063 获取用户搭讪关系(新接口)
 */

public class AccostRelationshipBean extends BaseServerBean
{
	public int ischat;//类型（int） 是否交流（0-否，1-有）
	public ArrayList< GameQuestion > questions;

	
	public class GameQuestion
	{
		public String order;//题号
		public int userquestionid;// 问题id
		public String question;//问题内容
		public int answerway;//回答的方式 类型（int）回答方式（0-文字，1-照片，2-拍照）
		public String forecolor;//类型（String）前景色 "#FFFFFF"
		public String background ;//类型（String） 背景 "http://www.test.com/1.jpg"
		public String answer;//问题的答案、当回答方式未非文字则是URL
		public long answertime;//回答问题的时间
	}
	
	public GameQuestion init(String order , String question)
	{
		GameQuestion item = new GameQuestion( );
		item.order = order;
		item.userquestionid = 1;
		item.question = question;
		item.answerway = 1;
		
		return item;
	}
}
	
