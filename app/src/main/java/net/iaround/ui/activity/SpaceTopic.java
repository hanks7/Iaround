
package net.iaround.ui.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import net.iaround.ui.comon.SuperActivity;
import net.iaround.ui.view.SpaceTopicView;


/**
 * 用户图片列表，实际操作都转给了{@link SpaceTopicView} 处理
 *
 * @author 余勋杰
 */
public class SpaceTopic extends SuperActivity {
    private SpaceTopicView view;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long uid = getIntent().getExtras().getLong("uid");
        int type = getIntent().getExtras().getInt("type");
        view = new SpaceTopicView(this, uid, type);
        setContentView(view);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (view != null) {
            view.onResume();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (view != null) {
            view.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (view != null) {
            return view.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }
}
