package net.iaround.ui.dynamic.bean;

import net.iaround.model.im.BaseServerBean;

import java.io.Serializable;

/**
 * Created by admin on 2017/4/21.
 */

public class DynamicItemBeanParent extends BaseServerBean implements Serializable {
    private static final long serialVersionUID = -6457778577098838550L;
    private DynamicItemBean dynamic;

    public DynamicItemBean getDynamic() {
        return dynamic;
    }

    public void setDynamic(DynamicItemBean dynamic) {
        this.dynamic = dynamic;
    }
}
