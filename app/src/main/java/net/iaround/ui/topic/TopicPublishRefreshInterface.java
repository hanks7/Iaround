package net.iaround.ui.topic;
/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-10-17 下午4:28:50
 * @Description: 发布动态成功后刷新的接口
 */
public interface TopicPublishRefreshInterface {

	void refreshTopicListPage(long datetime, long topicId);
}
