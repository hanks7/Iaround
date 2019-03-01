package net.iaround.model.entity;

/**
 * @author：liush on 2016/12/9 17:56
 */
public class UserEntity extends BaseEntity {

    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data{

        private String nickname;
        private String headPic;
        private String bindphone;
        private int vip;
        private int cursee;
        private int curlinkadd;//新添加联系人
        private int curdynamic;//好友动态

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getHeadPic() {
            return headPic;
        }

        public void setHeadPic(String headPic) {
            this.headPic = headPic;
        }

        public String getBindphone() {
            return bindphone;
        }

        public void setBindphone(String bindphone) {
            this.bindphone = bindphone;
        }

        public int getVip() {
            return vip;
        }

        public void setVip(int vip) {
            this.vip = vip;
        }

        public int getCursee() {
            return cursee;
        }

        public void setCursee(int cursee) {
            this.cursee = cursee;
        }

        public int getCurlinkadd() {
            return curlinkadd;
        }

        public void setCurlinkadd(int curlinkadd) {
            this.curlinkadd = curlinkadd;
        }

        public int getCurdynamic() {
            return curdynamic;
        }

        public void setCurdynamic(int curdynamic) {
            this.curdynamic = curdynamic;
        }
    }
}
