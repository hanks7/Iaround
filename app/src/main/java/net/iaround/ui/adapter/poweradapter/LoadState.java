
package net.iaround.ui.adapter.poweradapter;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;

/**
 * Created by Joe on 2016/12/1.
 * Email lovejjfg@gmail.com
 */
@IntDef(flag = true, value = {
        AdapterLoader.STATE_LOADING,
    AdapterLoader.STATE_LASTED,
})
@Target({ PARAMETER, FIELD })
@Retention(RetentionPolicy.SOURCE)
public @interface LoadState {
}
