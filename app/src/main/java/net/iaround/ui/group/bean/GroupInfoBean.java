
package net.iaround.ui.group.bean;


import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import net.iaround.R;
import net.iaround.model.im.BaseServerBean;
import net.iaround.ui.datamodel.GroupTopic;
import net.iaround.ui.datamodel.GroupUser;

import java.util.List;


/**
 * @author zhonglong kylin17@foxmail.com
 * @ClassName: GroupInfoBean
 * @Description: 圈子信息的实体类
 * @date 2013-12-10 上午11:29:26
 */
public class GroupInfoBean extends BaseServerBean implements Parcelable {

    /**圈子基本信息**/
    /*** 圈子地址*/
    public String address;
    /*** 圈子名称*/
    public String name;
    /*** 圈子id*/
    public String id;
    /*** 圈子介绍信息*/
    public String content;
    /*** 圈子等级*/
    public int level;
    /*** 圈子图片*/
    public String icon;
    /*** 圈热度标示(0：无标识，1：new ,2:hot)*/
    public int flag;
    /*** 圈子所属分类*/
    public String category;

    /*** 创建者信息*/
    public GroupUser user;

    /**
     * 圈子热度
     */
    public int activesocre;

    /**
     * 圈子范围
     */
    public double rang;

    /**
     * 金币数
     */
    public String gold;

    /**
     * 圈创建时间
     */
    public long datetime;



    /**
     * 圈的分类id
     */
    public int categoryid;

    /**
     * 历史最大范围（km）
     */
    public double oldmaxrange;

    /**
     * 圈子纬度
     */
    public int lat;

    /**
     * 圈子经度
     */
    public int lng;

    /**
     * 圈子图标图片地址
     */
    public String categoryicon;

    /**
     * 圈中心id
     */
    public String landmarkid;

    /**
     * 圈中心名称
     */
    public String landmarkname;

    /**
     * 圈中心纬度
     */
    public int landmarklat;

    /**
     * 圈中心经度
     */
    public int landmarklng;

    /**
     * 圈话题数
     */
    public int topiccount;

    /**
     * 当前用户在该圈中的角色（0创建者；1管理员；2圈成员；3非圈成员,4 粉丝）
     */
    public int grouprole = -1;

    /**
     * 聊天室人数
     */
    public int usercount;

    /**
     * 最新的圈话题信息
     */
    public GroupTopic topic;

    /**
     * 圈消息提醒开关（ 0提示 1不提示）
     */
    public int remindtag;

    /**
     * 1系统图标；0自定义图标
     */
    public int systemiconflag;

    /**
     * 是否有新消息，此状态通过socket下发进行更新
     */
    public boolean isHaveNewMsg = false;

    /**
     * 是否弹出分享弹窗
     */
    public int popup;

    /**
     * 圈状态
     * (1-年费已到期
     * 2-已隐藏，圈子从附近圈子里消失
     * 3-更新资料审核中
     * 4-已隐藏，圈子仅能通过圈号被搜索到)
     */
    public int groupstatus;

    /**
     * 圈子成员上限
     */
    public int maxcount;

    /**
     * 圈子类别( 1-小圈，2-万人圈，3-大圈 )
     */
    public int classify;

    /**
     * 圈子设置
     */
    public GroupRoleBean config;

    /**** 话题数 ****/
    public int topicnum;

    /**** 是否推荐 ***/
    public int recommend;

    /**** 贴吧 ***/
    public GroupPostbar postbar;

    /**
     * 7.2.0技能新增字段
     * 聊吧关注状态，粉丝数，欢迎语，
     */
    //粉丝数
    public long fans_num = 666;//粉丝数量
    //欢迎语
    public String welcome;
    //我在当前聊吧的身份
    public int group_role = 0;//0 吧主 1 管理员 2 成员 3 粉丝 4 游客
    //聊吧所在地
    public String city = "北京";

    public List<GroupSearchUser> group_members;

    /**
     * @param context
     * @return
     * @Title: getGroupDesc
     * @Description: 获取圈介绍显示内容
     */
    public String getGroupDesc(Context context) {
        if (content == null || TextUtils.isEmpty(content)) {
            return context.getString(R.string.not_any_group_desc);
        } else {
            return content;
        }
    }

//    public class GroupMemberBean
//    {
//        public GroupSearchUser user;
////        public String userid;
////        public String nickname;
////        public String icon;
////        public int vip;
////        public int svip;
////        public String notes;
////        public int viplevel;
////        public int age;
////        public String gender;
//    }

    public static final Parcelable.Creator<GroupInfoBean> CREATOR = new Parcelable.Creator<GroupInfoBean>() {

        @Override
        public GroupInfoBean createFromParcel(Parcel source) {
            GroupInfoBean bean = new GroupInfoBean();
            // bean.user =
            // source.readParcelable(GroupUser.class.getClassLoader());
            bean.user = (GroupUser) source.readSerializable();
            bean.id = source.readString();
            bean.name = source.readString();
            bean.icon = source.readString();
            bean.level = source.readInt();
            bean.rang = source.readDouble();
            bean.gold = source.readString();
            bean.datetime = source.readLong();
            bean.flag = source.readInt();
            bean.categoryid = source.readInt();
            bean.category = source.readString();
            bean.address = source.readString();
            bean.oldmaxrange = source.readDouble();
            bean.lat = source.readInt();
            bean.lng = source.readInt();
            bean.content = source.readString();
            bean.categoryicon = source.readString();
            bean.landmarkid = source.readString();
            bean.landmarkname = source.readString();
            bean.landmarklat = source.readInt();
            bean.landmarklng = source.readInt();
            bean.grouprole = source.readInt();
            bean.remindtag = source.readInt();
            bean.systemiconflag = source.readInt();
            bean.classify = source.readInt();
            bean.activesocre = source.readInt();
            return bean;
        }

        @Override
        public GroupInfoBean[] newArray(int size) {
            return new GroupInfoBean[size];
        }

    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // dest.writeParcelable(user, 0);
        dest.writeSerializable(user);
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(icon);
        dest.writeInt(level);
        dest.writeDouble(rang);
        dest.writeString(gold);
        dest.writeLong(datetime);
        dest.writeInt(flag);
        dest.writeInt(categoryid);
        dest.writeString(category);
        dest.writeString(address);
        dest.writeDouble(oldmaxrange);
        dest.writeInt(lat);
        dest.writeInt(lng);
        dest.writeString(content);
        dest.writeString(categoryicon);
        dest.writeString(landmarkid);
        dest.writeString(landmarkname);
        dest.writeInt(landmarklat);
        dest.writeInt(landmarklng);
        dest.writeInt(grouprole);
        dest.writeInt(remindtag);
        dest.writeInt(systemiconflag);
        dest.writeInt(classify);
        dest.writeInt(activesocre);
    }


}
