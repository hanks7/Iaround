package net.iaround.ui.dynamic.bean;

/**
 * 重新发送未发送成功的赞评的实体类
 * @author zhengst
 *
 */
public class DynamicUnLikeOrReviewBean {

	/**1-点赞  2-取消点赞  3-评论(暂时不做)*/
	public int type;
	public long postbarid;//贴吧ID
	public long dynamicid;//动态ID
	public String content;//评论内容
	public long replyID;//回复者ID
}
