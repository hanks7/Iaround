
package net.iaround.ui.activity;


import android.app.Activity;
import android.content.Context;

import net.iaround.tools.CommonFunction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;


public class CloseAllActivity {
    public static long pauseSystemTime;

    private LinkedList<Activity> mActs;
    private static CloseAllActivity instance = null;
    public static Context appContext = null;

    private CloseAllActivity() {
        mActs = new LinkedList<Activity>();
    }

    public static void setPauseSystemTimeTime()
    {
        pauseSystemTime = System.currentTimeMillis();
    }

    public static long getPauseTime()
    {
        return pauseSystemTime;
    }

    public synchronized static CloseAllActivity getInstance() {
        if (instance == null) {
            instance = new CloseAllActivity();
        }
        return instance;
    }

    public void addActivity(Activity act) {
        synchronized (CloseAllActivity.this) {
            mActs.addFirst(act);
        }
    }

    public void removeActivity(Activity act) {
        synchronized (CloseAllActivity.this) {
            if (mActs != null && mActs.indexOf(act) >= 0) {
                mActs.remove(act);
            }
        }
    }

    /**
     * 当打开activity使用标志:Intent.FLAG_ACTIVITY_REORDER_TO_FRONT,
     * 需要把CloseActivity对应的activity移动到顶端
     */
    public void reorderActivityToFront(Activity act) {
        synchronized (CloseAllActivity.this) {
            if (mActs != null && mActs.indexOf(act) > 0) {
                mActs.remove(act);
                mActs.addFirst(act);
            }
        }
    }

    public Activity getTopActivity() {
        synchronized (CloseAllActivity.this) {
            return (mActs == null || mActs.size() <= 0) ? null : mActs.get(0);
        }
    }

    public Activity getSecondActivity() {
        synchronized (CloseAllActivity.this) {
            return (mActs == null || mActs.size() <= 1) ? null : mActs.get(1);
        }
    }

    public void close() {
        synchronized (CloseAllActivity.this) {
            Activity act;
            while (mActs.size() != 0) {
                act = mActs.poll();
                act.finish();
            }
        }
    }

    /**
     * 关闭其他activity，唯独排除activityClass指定的activity
     *
     * @param activityClass
     */
    public void closeExcept(Class<?> activityClass) {
        synchronized (CloseAllActivity.this) {
            Activity act;
            Iterator<Activity> activityIterator = mActs.iterator();
            while (activityIterator.hasNext()) {
                act = activityIterator.next();
                if (!act.getClass().getName().equals(activityClass.getName())) {
                    act.finish();
                    activityIterator.remove();
                }
            }
        }
    }

    public void whatActivityInProgram() {
        synchronized (CloseAllActivity.this) {
            Activity act;
            Iterator<Activity> activityIterator = mActs.iterator();
            CommonFunction.log("CloseAllActivity", "===============begin=================");
            while (activityIterator.hasNext()) {
                act = activityIterator.next();
                CommonFunction.log("CloseAllActivity", "close():" + act.getClass().getName());
            }
            CommonFunction.log("CloseAllActivity", "=================end===============");
        }
    }

    public boolean isActivityExist(Class<?> activityClass) {
        synchronized (CloseAllActivity.this) {
            Activity act;
            Iterator<Activity> activityIterator = mActs.iterator();
            while (activityIterator.hasNext()) {
                act = activityIterator.next();
                if (act.getClass().getName().equals(activityClass.getName())) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * 关闭activityClass指定的activity
     *
     * @param activityClass
     */
    public void closeTarget(Class<?> activityClass) {
        synchronized (CloseAllActivity.this) {
            Activity act;
            Iterator<Activity> activityIterator = mActs.iterator();
            while (activityIterator.hasNext()) {
                act = activityIterator.next();
                if (act.getClass().getName().equals(activityClass.getName())) {
                    act.finish();
                    activityIterator.remove();
                }
            }
        }
    }


    /**
     * @Title: backToMainActivity
     * @Description: 回到首页侧栏承载的界面
     */
    public void backToMainActivity() {
        synchronized (this) {
            CommonFunction.log("CloseAllActivity", "===============backToMainActivity=================");
            while (mActs.size() > 0) {
                Activity activity = mActs.getFirst();
                if (activity instanceof MainFragmentActivity) {
                    break;
                } else {
                    activity.finish();
                    mActs.remove(activity);
                }
            }
        }
    }

    public int getSize() {
        return mActs.size();
    }

    public ArrayList<Activity> getTargetActivity(Class<?> activityClass) {
        ArrayList<Activity> activities = new ArrayList<Activity>();
        synchronized (CloseAllActivity.this) {
            Activity act;
            int size = mActs.size();
            for (int i = 0; i < size; i++) {
                act = mActs.get(i);
                if (act.getClass().getName().equals(activityClass.getName())) {
                    activities.add(act);
                }
            }
        }

        return activities;
    }
    public Activity getTargetActivityOne(Class<?> activityClass) {
        ArrayList<Activity> activities = new ArrayList<Activity>();
        synchronized (CloseAllActivity.this) {
            Activity act;
            int size = mActs.size();
            if (size > 0){
                for (int i = 0; i < size; i++) {
                    act = mActs.get(i);
                    if (act.getClass().getName().equals(activityClass.getName())) {
                        return act;
                    }
                }
            }else{
                return null;
            }

        }
        if(activities.size()==0) {
            return null;
        }
        return activities.get(0);
    }
}
