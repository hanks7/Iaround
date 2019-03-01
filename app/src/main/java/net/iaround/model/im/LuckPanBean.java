package net.iaround.model.im;

import java.util.List;

/**
 * 抽奖接口返回的对象
     {
         "propId": 3,
         "propType": 1,    ／／ 中奖类型 1 是免费，2 是钻石  1|2
         "count": {
             "free": 9,
             "diamond": 10
         },
         "msg": "ok",
         "status": 200
     }
 */
public class LuckPanBean extends BaseServerBean {
    private int propId;
    private int propType;
    private String msg;
    private CountBean count;

    public int getPropId() {
        return propId;
    }

    public void setPropId(int propId) {
        this.propId = propId;
    }

    public int getPropType() {
        return propType;
    }

    public void setPropType(int propType) {
        this.propType = propType;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


    public CountBean getCount() {
        return count;
    }

    public void setCount(CountBean count) {
        this.count = count;
    }

    public LuckPanBean(int free, int diamond){
        CountBean bean = new CountBean();
        bean.free = free;
        bean.diamond = diamond;
        setCount(bean);
    }

    public class CountBean
    {
        public int getFree() {
            return free;
        }

        public void setFree(int free) {
            this.free = free;
        }

        public int getDiamond() {
            return diamond;
        }

        public void setDiamond(int diamond) {
            this.diamond = diamond;
        }

        private int free;
        private int diamond;
    }
}
