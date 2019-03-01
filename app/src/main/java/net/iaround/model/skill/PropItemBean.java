package net.iaround.model.skill;

/**
 * 作者：zx on 2017/8/18 11:16
 */
public class PropItemBean {
    /**
     * 这参数的命名格式也是醉了
     *
     "PropsShopID": 1,  		//道具商店ID
     "PopsID": 1, 				//道具ID
     "CurrencyType": 2, 		//货币类型 2钻石 1金币 6星星
     "CurrencyNum": 40, 		//货币数量
     "Num": 1, 				    //道具数量
     "Order": 1,  				//排序 倒叙 后端已排好，无需处理。
     "Name": "银星星”, 			//道具名称
     "ICON": “”,				//道具图标
     "SkillUpdateRate": 0, 		//升级增加成功率
     */
    public String PropsShopID;
    public String PopsID;
    public String Name;
    public String ICON;
    public String Num;
    public String SkillUpdateRate;
    public int CurrencyNum;
    public int CurrencyType;
}
