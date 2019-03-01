package net.iaround.model.type;
/**
 * @author KevinSu kevinsu917@126.com
 * @version 创建时间：2014-10-21 上午10:12:03
 * @Description: 举报的类型（1：色情，2：暴力，3：骚扰，5：广告，6：欺诈，7：反动，8：政治，10：色情谩骂政治等，
 *            11：个人资料不当，12：盗用他人照片）
 * @相关业务: /system/report
 */
public class ReportType {

	public static final int SEX = 1;//色情
	public static final int DISTURB = 3;//骚扰
	public static final int ABUSE = 4;
	public static final int ADVERTISEMENT = 5;//广告
	public static final int CHEAT = 6;//欺诈
	public static final int REACTIONARY = 7;//反动
	public static final int OTHER = 9;//其他
	public static final int INFORMATION_ILLEGAL = 11;
}
