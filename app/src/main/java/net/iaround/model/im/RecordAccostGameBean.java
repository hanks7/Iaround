
package net.iaround.model.im;

/**
 * @ClassName RecordAccostGameBean.java
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-8-10 下午4:14:08
 * @Description: 聊天记录中真心话实体
 */

public class RecordAccostGameBean
{
	public String order;//问题的题号
	public int userquestionid;// 问题id
	public String question;//问题内容
	public int answerway;//回答的方式 类型（int）回答方式（0-文字，1-照片，2-拍照）
	public String answer;//问题的答案、当回答方式未非文字则是URL
	
	
	public boolean bIsTextAnswer()
	{
		return answerway == 0;
	}
}
