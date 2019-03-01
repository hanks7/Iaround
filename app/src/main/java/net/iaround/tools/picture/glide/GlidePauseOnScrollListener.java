package net.iaround.tools.picture.glide;

import com.bumptech.glide.Glide;

import net.iaround.BaseApplication;

import cn.finalteam.galleryfinal.PauseOnScrollListener;

/**
 * Desction:
 * Author:pengjianbo
 * Date:2016/1/9 0009 18:18
 */
public class GlidePauseOnScrollListener extends PauseOnScrollListener {

    public GlidePauseOnScrollListener(boolean pauseOnScroll, boolean pauseOnFling) {
        super(pauseOnScroll, pauseOnFling);
    }

    @Override
    public void resume() {
        if(null!=BaseApplication.appContext) {
            Glide.with(BaseApplication.appContext).resumeRequests();
        }
    }

    @Override
    public void pause() {
        if(null!=BaseApplication.appContext) {
            Glide.with(BaseApplication.appContext).pauseRequests();
        }
    }
}
