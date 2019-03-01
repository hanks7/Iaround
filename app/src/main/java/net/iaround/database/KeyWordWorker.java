
package net.iaround.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import net.iaround.conf.Config;
import net.iaround.conf.KeyWord;
import net.iaround.tools.CommonFunction;
import net.iaround.tools.FileService;
import net.iaround.tools.SharedPreferenceUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


/**
 * 关键字表
 * 
 * @author Administrator
 * 
 */
public class KeyWordWorker extends ITableWorker
{
	public static final String TB_NAME = "tb_keyword"; // 表名
	public static final String ID = "id"; // 本地id 0
	public static final String K_KID = "kid"; // 关键字id
	public static final String K_KEYWORD = "keyword"; // 关键字
	public static final String K_KEYWORD_LEVEL = "level"; // 关键字等级
	public static final String[ ] selectors =
		{ ID , K_KID , K_KEYWORD ,K_KEYWORD_LEVEL };
	
	/** 关键字表创建sql **/
	public static final String tableSql = "CREATE TABLE IF NOT EXISTS " + TB_NAME + " ( " + ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + K_KID + " INTEGER, " + K_KEYWORD
			+ " VERCHAR(100), " + K_KEYWORD_LEVEL + " INTEGER );";
	public static final String deleteAll = "DELETE FROM TB_NAME"; // 清楚本地数据
	
	protected KeyWordWorker(Context context )
	{
		super( context , ID , TB_NAME );
	}
	
	// 批量导入数据
	public void insertAll( Context context )
	{
		onDeleteAll( ); // 将所有数据删除
		SQLiteDatabase db = getDb( );
		db.beginTransaction( );

		String filename =  "Keyword.txt";
		String filedata = "";
		try
		{
			filedata = FileService.readAssetsFile( context, filename );

		}catch ( Exception e )
		{
			e.printStackTrace();
		}

		if(! TextUtils.isEmpty( filedata ))
		{
			String data[] = filedata.split( "\r\n" );
			for(int i=0;i<data.length;i++)
			{
				String[] subData = 	data[i].split( "\t" );
				if(subData.length!=4)continue;
				try
				{
					int key = Integer.parseInt( subData[0] );
					String keyword = subData[1];
					int rank = Integer.parseInt( subData[3] );
					if ( !TextUtils.isEmpty( keyword ))
					{
						ContentValues values = new ContentValues( );
						values.put( K_KID, key );
						values.put( K_KEYWORD, keyword );
						values.put( K_KEYWORD_LEVEL, rank );
						db.insert( TB_NAME, null, values );
					}
					else
					{
						CommonFunction.log( "shifengxiong", "getKey ===" + subData[0] );
					}


				}
				catch ( Exception e )
				{
					e.printStackTrace();
				}
			}
		}


//		Map< Integer , String > map = new HashMap< Integer , String >( );
//		addKey1( map );
//		addKey(map ,filedata);
//		for ( Entry< Integer , String > entry : map.entrySet( ) )
//		{
//			if(!TextUtils.isEmpty( entry.getValue( ) ))
//			{
//				ContentValues values = new ContentValues( );
//				values.put( K_KID, entry.getKey( ) );
//				values.put( K_KEYWORD, entry.getValue( ) );
//				db.insert( TB_NAME, null, values );
//			}
//			else
//			{
//				CommonFunction.log( "shifengxiong","getKey ==="+entry.getKey( )  );
//			}
//		}
		db.setTransactionSuccessful( );
		db.endTransaction( );
		// 更新本地数据库的版本信息
		SharedPreferenceUtil sp = SharedPreferenceUtil.getInstance( context );
		if(!sp.has( SharedPreferenceUtil.KEYWORD_VERSION ))
		{
			sp.putLong( SharedPreferenceUtil.KEYWORD_VERSION , Config.CREATE_DATETIME );
		}
	}

	private void addKey(Map< Integer , String > map , String source)
	{
		if(! TextUtils.isEmpty( source ))
		{
			String data[] = source.split( "\r\n" );
			for(int i=0;i<data.length;i++)
			{
				String[] subData = 	data[i].split( " " );
				try
				{
					int key = Integer.parseInt( subData[0] );
					map.put( key , subData[1]  );
				}
				catch ( Exception e )
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	private void addKey1( Map< Integer , String > map )
	{
		map.put( 90 , "李洪志" );
		map.put( 418 , "六四运动" );
		map.put( 435 , "法轮功" );
		map.put( 436 , "法轮大法" );
		map.put( 800 , "转法轮" );
		map.put( 435 , "法轮" );
		map.put( 445 , "天安门事件" );
		map.put( 489 , "九•十•三运动" );
		map.put( 689 , "天安门录影" );
		map.put( 690 , "天安门屠杀" );
		map.put( 691 , "天安门镇压" );
		map.put( 1504 , "臭机八" );
		map.put( 1505 , "臭鸡巴" );
		map.put( 1508 , "催情药" );
		map.put( 1509 , "肛交" );
		map.put( 1510 , "肛门" );
		map.put( 1511 , "龟头" );
		map.put( 1513 , "机八" );
		map.put( 1514 , "机巴" );
		map.put( 1515 , "鸡八" );
		map.put( 1516 , "鸡巴" );
		map.put( 1517 , "机掰" );
		map.put( 1518 , "鸡叭" );
		map.put( 1519 , "鸡鸡" );
		map.put( 1520 , "鸡掰" );
		map.put( 1521 , "鸡奸" );
		map.put( 1522 , "妓女" );
		map.put( 1523 , "精液" );
		map.put( 1524 , "精子" );
		map.put( 1525 , "口交" );
		map.put( 1526 , "滥交" );
		map.put( 1527 , "乱交" );
		map.put( 1528 , "屁眼" );
		map.put( 1529 , "嫖娼" );
		map.put( 1530 , "强奸犯" );
		map.put( 1531 , "肉棒" );
		map.put( 1532 , "乳房" );
		map.put( 1533 , "乳峰" );
		map.put( 1534 , "乳交" );
		map.put( 1535 , "乳头" );
		map.put( 1536 , "乳晕" );
		map.put( 1537 , "三陪" );
		map.put( 1538 , "色情" );
		map.put( 1539 , "射精" );
		map.put( 1540 , "手淫" );
		map.put( 1541 , "威而钢" );
		map.put( 1542 , "威而柔" );
		map.put( 1543 , "伟哥" );
		map.put( 1544 , "性高潮" );
		map.put( 1545 , "性交" );
		map.put( 1546 , "性虐" );
		map.put( 1547 , "性欲" );
		map.put( 1548 , "颜射" );
		map.put( 1549 , "阳物" );
		map.put( 1550 , "一夜情" );
		map.put( 1551 , "阴部" );
		map.put( 1552 , "阴唇" );
		map.put( 1553 , "阴道" );
		map.put( 1554 , "阴蒂" );
		map.put( 1555 , "阴核" );
		map.put( 1556 , "阴户" );
		map.put( 1557 , "阴茎" );
		map.put( 1558 , "阴门" );
		map.put( 1559 , "淫秽" );
		map.put( 1560 , "淫乱" );
		map.put( 1561 , "淫水" );
		map.put( 1562 , "淫娃" );
		map.put( 1563 , "淫液" );
		map.put( 1564 , "淫汁" );
		map.put( 1565 , "淫穴" );
		map.put( 1566 , "淫洞" );
		map.put( 1567 , "援交" );
		map.put( 1568 , "做爱" );
		map.put( 1569 , "梦遗" );
		map.put( 1570 , "阳痿" );
		map.put( 1571 , "早泄" );
		map.put( 1572 , "奸淫" );
		map.put( 1573 , "鸡吧" );
		map.put( 1574 , "鸡把" );
		map.put( 1575 , "雞巴" );
		map.put( 1576 , "几吧" );
		map.put( 1577 , "激情淫乱" );
		map.put( 1578 , "奸幼" );
		map.put( 1579 , "姦污" );
		map.put( 1580 , "姦淫" );
		map.put( 1581 , "口淫" );
		map.put( 1582 , "近親相姦" );
		map.put( 1583 , "禁网禁片" );
		map.put( 1584 , "内射" );
		map.put( 1585 , "嫩逼" );
		map.put( 1586 , "女伟哥" );
		map.put( 1587 , "女阴" );
		map.put( 1588 , "强插" );
		map.put( 1589 , "肉洞" );
		map.put( 1590 , "肉棍" );
		map.put( 1591 , "肉穴" );
		map.put( 1592 , "色拉裤" );
		map.put( 1593 , "色情电影" );
		map.put( 1594 , "色情片" );
		map.put( 1595 , "少妇骚屄" );
		map.put( 1596 , "射程" );
		map.put( 1597 , "射频" );
		map.put( 1598 , "淫虫" );
		map.put( 1599 , "淫蕩" );
		map.put( 1600 , "淫乱小说" );
		map.put( 1601 , "淫漫畫" );
		map.put( 1602 , "淫妹妹" );
		map.put( 1603 , "淫糜" );
		map.put( 1604 , "淫女" );
		map.put( 1605 , "淫妻群交" );
		map.put( 1606 , "淫兽" );
		map.put( 1607 , "淫獸" );
		map.put( 1608 , "淫书" );
		map.put( 1609 , "淫網" );
		map.put( 1610 , "淫香" );
		map.put( 1611 , "淫语" );
		map.put( 1612 , "兽交" );
		map.put( 1613 , "獸交" );
		map.put( 1614 , "五出三进" );
		map.put( 1615 , "缩阴" );
		map.put( 1617 , "无线射频" );
		map.put( 1618 , "湿穴" );
		map.put( 1619 , "性福" );
		map.put( 1620 , "性奴集中营" );
		map.put( 1621 , "性趣" );
		map.put( 1622 , "胸推" );
		map.put( 1623 , "小姐上门" );
		map.put( 1624 , "メ入" );
		map.put( 1625 , "做-爱" );
		map.put( 1626 , "幼交" );
		map.put( 1627 , "援交妹" );
		map.put( 1628 , "援助交际" );
		map.put( 1629 , "K他命" );
		map.put( 1633 , "婊子" );
		map.put( 1636 , "操你" );
		map.put( 1634 , "操她妈" );
		map.put( 1635 , "操妳妈" );
		map.put( 1637 , "操你妈" );
		map.put( 1638 , "操他妈" );
		map.put( 1639 , "草你" );
		map.put( 1640 , "册那" );
		map.put( 1641 , "侧那" );
		map.put( 1642 , "测拿" );
		map.put( 1644 , "荡妇" );
		map.put( 1645 , "发骚" );
		map.put( 1647 , "干她妈" );
		map.put( 1648 , "干妳" );
		map.put( 1649 , "干妳娘" );
		map.put( 1650 , "干你" );
		map.put( 1651 , "干你妈" );
		map.put( 1652 , "干你妈B" );
		map.put( 1653 , "干你妈逼" );
		map.put( 1654 , "干你娘" );
		map.put( 1655 , "干他妈" );
		map.put( 1656 , "狗娘养的" );
		map.put( 1657 , "贱货" );
		map.put( 1658 , "贱人" );
		map.put( 1659 , "烂人" );
		map.put( 1660 , "老母" );
		map.put( 1662 , "妈比" );
		map.put( 1665 , "妳老母的" );
		map.put( 1666 , "妳娘的" );
		map.put( 1667 , "你妈逼" );
		map.put( 1668 , "破鞋" );
		map.put( 1669 , "仆街" );
		map.put( 1670 , "去她妈" );
		map.put( 1671 , "去妳的" );
		map.put( 1672 , "去妳妈" );
		map.put( 1673 , "去你的" );
		map.put( 1674 , "去你妈" );
		map.put( 1676 , "去他妈" );
		map.put( 1677 , "日你" );
		map.put( 1678 , "赛她娘" );
		map.put( 1679 , "赛妳娘" );
		map.put( 1680 , "赛你娘" );
		map.put( 1681 , "赛他娘" );
		map.put( 1682 , "骚货" );
		map.put( 1683 , "傻B" );
		map.put( 1684 , "傻比" );
		map.put( 1685 , "傻子" );
		map.put( 1686 , "上妳" );
		map.put( 1689 , "屎妳娘" );
		map.put( 1690 , "屎你娘" );
		map.put( 1691 , "他妈的" );
		map.put( 1693 , "我操" );
		map.put( 1694 , "我日" );
		map.put( 1696 , "猪猡" );
		map.put( 1697 , "骑你" );
		map.put( 1699 , "操他" );
		map.put( 1700 , "操她" );
		map.put( 1701 , "骑他" );
		map.put( 1702 , "骑她" );
		map.put( 1703 , "欠骑" );
		map.put( 1704 , "欠人骑" );
		map.put( 1705 , "来爽我" );
		map.put( 1706 , "来插我" );
		map.put( 1707 , "干他" );
		map.put( 1708 , "干她" );
		map.put( 1709 , "干死" );
		map.put( 1710 , "干爆" );
		map.put( 1711 , "干机" );
		map.put( 1712 , "机叭" );
		map.put( 1713 , "臭鸡" );
		map.put( 1714 , "臭机" );
		map.put( 1715 , "烂鸟" );
		map.put( 1716 , "览叫" );
		map.put( 1717 , "阳具" );
		map.put( 1718 , "肉壶" );
		map.put( 1719 , "奶子" );
		map.put( 1720 , "摸咪咪" );
		map.put( 1721 , "干鸡" );
		map.put( 1722 , "干入" );
		map.put( 1723 , "小穴" );
		map.put( 1724 , "插你" );
		map.put( 1725 , "爽你" );
		map.put( 1726 , "干干" );
		map.put( 1727 , "干X" );
		map.put( 1728 , "他干" );
		map.put( 1729 , "干它" );
		map.put( 1730 , "干牠" );
		map.put( 1731 , "干您" );
		map.put( 1732 , "干汝" );
		map.put( 1733 , "干林" );
		map.put( 1734 , "操林" );
		map.put( 1735 , "干尼" );
		map.put( 1736 , "操尼" );
		map.put( 1737 , "我咧干" );
		map.put( 1738 , "干勒" );
		map.put( 1739 , "干我" );
		map.put( 1740 , "干到" );
		map.put( 1741 , "干啦" );
		map.put( 1742 , "干爽" );
		map.put( 1743 , "欠干" );
		map.put( 1744 , "狗干" );
		map.put( 1747 , "轮干" );
		map.put( 1748 , "轮流干" );
		map.put( 1749 , "干一干" );
		map.put( 1750 , "奸暴" );
		map.put( 1751 , "再奸" );
		map.put( 1752 , "我奸" );
		map.put( 1753 , "奸你" );
		map.put( 1754 , "奸他" );
		map.put( 1755 , "奸她" );
		map.put( 1756 , "奸一奸" );
		map.put( 1757 , "淫湿" );
		map.put( 1758 , "鸡歪" );
		map.put( 1759 , "扑街" );
		map.put( 1760 , "臭西" );
		map.put( 1761 , "遗精" );
		map.put( 1762 , "烂逼" );
		map.put( 1763 , "大血比" );
		map.put( 1764 , "叼你妈" );
		map.put( 1765 , "靠你妈" );
		map.put( 1766 , "戳你" );
		map.put( 1767 , "逼你老母" );
		map.put( 1768 , "挨球" );
		map.put( 1769 , "我日你" );
		map.put( 1770 , "草拟妈" );
		map.put( 1771 , "卖逼" );
		map.put( 1772 , "狗操卖逼" );
		map.put( 1773 , "日死" );
		map.put( 1774 , "奶娘" );
		map.put( 1775 , "他娘" );
		map.put( 1776 , "她娘" );
		map.put( 1777 , "骚B" );
		map.put( 1778 , "你妈了妹" );
		map.put( 1779 , "逼毛" );
		map.put( 1780 , "插你妈" );
		map.put( 1781 , "叼你" );
		map.put( 1782 , "渣波波" );
		map.put( 1783 , "嫩b" );
		map.put( 1784 , "卖逼妈了个逼" );
		map.put( 1785 , "你妈比" );
		map.put( 1786 , "欠操日她" );
		map.put( 1787 , "日你妈" );
		map.put( 1788 , "日死她" );
		map.put( 1789 , "日死你" );
		map.put( 1790 , "骚逼" );
		map.put( 1791 , "骚水" );
		map.put( 1792 , "骚穴" );
		map.put( 1793 , "傻逼" );
		map.put( 1794 , "生孩子没屁眼" );
		map.put( 1798 , "小逼" );
		map.put( 1799 , "小来子" );
		map.put( 1808 , "曰你" );
		map.put( 1809 , "x你" );
		map.put( 1810 , "滚你妈" );
		map.put( 1811 , "你妈b" );
		map.put( 1812 , "你妈的b" );
		map.put( 1819 , "曰货" );
		map.put( 1820 , "女干" );
		map.put( 1841 , "勃起" );
		map.put( 1842 , "射了" );
		map.put( 1880 , "宏志" );
		map.put( 1881 , "洪志" );
		map.put( 1882 , "fuck" );
		map.put( 1931 , "cao" );
		map.put( 1932 , "NMD" );
		map.put( 1933 , "NND" );
		map.put( 1934 , "sb" );
		map.put( 1939 , "屄" );
		map.put( 1940 , "肏" );
		map.put( 1942 , "屙" );
		map.put( 1943 , "掯" );
		map.put( 1944 , "屌" );
		map.put( 2009 , "生殖器" );
		map.put( 2180 , "news@epochtimes" );
		map.put( 2199 , "news@epochtimes.com" );
		map.put( 2262 , "cntv.us" );
		map.put( 2501 , "PresidentOffice@ChinaInterimGov" );
		map.put( 2575 , "garden_team@hotmail" );
		map.put( 2577 , "tuidang@epochtimes" );
		map.put( 2656 , "pyproxy@gmail" );
		map.put( 2665 , "dadaogcd2009@gmail" );
		map.put( 2728 , "falundafa.org" );
		map.put( 2731 , "tuidang@gmail" );
		map.put( 2732 , "tuidangcenter@fastmail" );
		map.put( 2831 , "xianzhang2008xianzhang@inbox" );
		map.put( 3932 , "性爱" );
	}
	
	// 删除某条记录
	public void delete( int key )
	{
		SQLiteDatabase db = getDb( );
		db.execSQL( "DELETE FROM " + TB_NAME + " WHERE " + K_KID + " = " + key );
	}
	
	// 导出所有数据到本地
	public void exportAll( Context context )
	{
		String where = "";
		Cursor cursor = onSelect( selectors , where );
		List< String > list = new ArrayList< String >( );
		List< String > listRank = new ArrayList< String >( );
		// 当数据库数据，则导出，若没有则使用默认map里的数据
		if ( cursor != null && cursor.getCount( ) > 0 )
		{
			try
			{
				cursor.moveToFirst( );
				while ( !( cursor.isAfterLast( ) ) )
				{
					int rank = cursor
						.getInt( cursor.getColumnIndex( KeyWordWorker.K_KEYWORD_LEVEL ) );
					if ( rank != 0 )
					{
						listRank.add(
							cursor.getString( cursor.getColumnIndex( KeyWordWorker.K_KEYWORD ) ) );
					}
					else
					{
						list.add(
							cursor.getString( cursor.getColumnIndex( KeyWordWorker.K_KEYWORD ) ) );
					}
					cursor.moveToNext( );
				}
			}
			catch ( Exception e )
			{
				e.printStackTrace();
			}
			finally
			{
				if ( cursor != null )
				{
					cursor.close( );
				}
			}
		}
//		else
//		{
//			Map< Integer , String > map = new HashMap< Integer , String >( );
//			addKey1( map );
//			for ( Entry< Integer , String > entry : map.entrySet( ) )
//			{
//				list.add( entry.getValue( ) );
//			}
//			map.clear( );
//		}
//
//		Collections.sort( list , comparator );
//		Collections.sort( listRank , comparator );
		KeyWord.getInstance( context ).setMap( list );
		KeyWord.getInstance( context ).setRankMap(listRank);
	}
	
	Comparator< String > comparator = new Comparator< String >( )
	{
		public int compare(String s1 , String s2 )
		{
			return s2.length( ) - s1.length( );
		}
    };
}
