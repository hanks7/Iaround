package net.iaround.model.skill;

import net.iaround.model.entity.BaseEntity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by sjq on 2017/8/16.
 */

public class RankSkillParentEntity extends BaseEntity implements Serializable{
    private static final long serialVersionUID = 7635127122654526974L;

    public List<InteractiveBean> getInteractiveBeanList() {
        return interactiveBeanList;
    }

    public void setInteractiveBeanList(List<InteractiveBean> interactiveBeanList) {
        this.interactiveBeanList = interactiveBeanList;
    }

    public List<GrowingBean> getGrowingBeanList() {
        return growingBeanList;
    }

    public void setGrowingBeanList(List<GrowingBean> growingBeanList) {
        this.growingBeanList = growingBeanList;
    }

    private List<InteractiveBean> interactiveBeanList;//互动榜
    public static class InteractiveBean {
        private String icon;//技能头像
        private String name;//技能名字

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getIconList() {
            return iconList;
        }

        public void setIconList(List<String> iconList) {
            this.iconList = iconList;
        }

        private List<String > iconList;//前三名的头像URL

    }
    private List<GrowingBean> growingBeanList;//成长榜
    public static class GrowingBean {
        private String icon;//技能头像

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getIconList() {
            return iconList;
        }

        public void setIconList(List<String> iconList) {
            this.iconList = iconList;
        }

        private String name;//技能名字
        private List<String > iconList;//前三名的头像URL

    }
}
