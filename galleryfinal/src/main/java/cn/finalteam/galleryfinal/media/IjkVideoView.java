///*
// * Copyright (C) 2015 Bilibili
// * Copyright (C) 2015 Zhang Rui <bbcallen@gmail.com>
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package cn.finalteam.galleryfinal.media;
//
//import android.annotation.TargetApi;
//import android.content.Context;
//import android.media.AudioManager;
//import android.media.MediaPlayer;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.net.Uri;
//import android.os.Build;
//import android.support.annotation.NonNull;
//import android.util.AttributeSet;
//import android.util.Log;
//import android.view.Gravity;
//import android.view.MotionEvent;
//import android.view.View;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//import android.widget.MediaController;
//import android.widget.ProgressBar;
//import android.widget.Toast;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Locale;
//import java.util.Map;
//import java.util.Timer;
//import java.util.TimerTask;
//
//import cn.finalteam.galleryfinal.R;
//import tv.danmaku.ijk.media.player.IMediaPlayer;
//import tv.danmaku.ijk.media.player.IjkMediaPlayer;
//import tv.danmaku.ijk.media.player.TextureMediaPlayer;
//import tv.danmaku.ijk.media.player.misc.ITrackInfo;
//
//public class IjkVideoView extends FrameLayout implements MediaController.MediaPlayerControl {
//
//
//    private String TAG = "IjkVideoView";
//
//    public static final int EOF = 1;
//    public static final int END = 2;
//    public static final int QUIT = 3;
//    public static final int ERROR = 4;
//    public static final int NO_NET = 5;
//    public static final int NO_INFO = 6;
//    public static final int CDN_CHANGE = 7;
//
//    // private static final boolean USE_MEDIACODEC_AUTO_ROTATE = true;
//    // private static final boolean USE_OPENSLES = true;
//    private static final boolean USE_MEDIACODEC_AUTO_ROTATE = true;
//    private static final boolean USE_OPENSLES = true;
//    private static final boolean DETACHED_SURFACE_TEXTURE_VIEW = false;
//    private static final boolean ENABLE_SURFACE_VIEW = false;
//    private static final boolean ENABLE_TEXTURE_VIEW = false;
//    private static final boolean ENABLE_NO_VIEW = false;
//
//    // settable by the client
//    private Uri mUri;
//    private Map<String, String> mHeaders;
//
//    // all possible internal states
//    private static final int STATE_ERROR = -1;
//    private static final int STATE_IDLE = 0;
//    private static final int STATE_PREPARING = 1;
//    private static final int STATE_PREPARED = 2;
//    private static final int STATE_PLAYING = 3;
//    private static final int STATE_PAUSED = 4;
//    private static final int STATE_PLAYBACK_COMPLETED = 5;
//
//    // mCurrentState is a VideoView object's current state.
//    // mTargetState is the state that a method caller intends to reach.
//    // For instance, regardless the VideoView object's current state,
//    // calling pause() intends to bring the object to a target state
//    // of STATE_PAUSED.
//    private int mCurrentState = STATE_IDLE;
//    private int mTargetState = STATE_IDLE;
//
//    // All the stuff we need for playing and showing a video
//    private IRenderView.ISurfaceHolder mSurfaceHolder = null;
//    private IMediaPlayer mMediaPlayer = null;
//    // private int mAudioSession;
//    private int mVideoWidth;
//    private int mVideoHeight;
//    private int mSurfaceWidth;
//    private int mSurfaceHeight;
//    private int mVideoRotationDegree;
//    private IMediaController mMediaController;
//    private IMediaPlayer.OnCompletionListener mOnCompletionListener;
//    private IMediaPlayer.OnPreparedListener mOnPreparedListener;
//    private int mCurrentBufferPercentage;
//    private IMediaPlayer.OnErrorListener mOnErrorListener;
//    private IMediaPlayer.OnInfoListener mOnInfoListener;
//    private IMediaPlayer.OnTimedTextListener mOnTimdTextListener;
//    private IMediaPlayer.OnSeekCompleteListener mOnSeekCompleteListener;
//    private int mSeekWhenPrepared; // recording the seek position while
//    // preparing
//    private boolean mCanPause = true;
//    private boolean mCanSeekBack = true;
//    private boolean mCanSeekForward = true;
//
//    /** Subtitle rendering widget overlaid on top of the video. */
//    // private RenderingWidget mSubtitleWidget;
//
//    /**
//     * Listener for changes to subtitle data, used to redraw when needed.
//     */
//    // private RenderingWidget.OnChangedListener mSubtitlesChangedListener;
//
//    private Context mAppContext;
//    private IRenderView mRenderView;
//
//    private ImageView clickIv;
//    private ProgressBar bottomProgressBar;
//
//    private int mVideoSarNum;
//    private int mVideoSarDen;
//
//    protected static Timer UPDATE_PROGRESS_TIMER;
//    protected ProgressTimerTask mProgressTimerTask;
//
//    private IFlyMediaCallback flyMediaCallback = null;
//    public IMediaPlayer getMediaPlayer(){
//        return mMediaPlayer;
//    }
//    public void setCallback(IFlyMediaCallback cb) {this.flyMediaCallback = cb;}
//    private GPLogEventListenr listenr;
//    public void setGPLogListener(GPLogEventListenr l) { this.listenr = l; }
//
//    public IjkVideoView(Context context) {
//        super(context);
//        initVideoView(context);
//    }
//
//    private static IjkVideoView instance = null;
//
//    public static IjkVideoView getInstance() {
//        if (instance == null) {
//            instance = new IjkVideoView(null);
//        } else {
//            instance.mCurrentState = STATE_IDLE;
//            instance.mTargetState = STATE_IDLE;
//        }
//        return instance;
//    }
//
//    public IjkVideoView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        initVideoView(context);
//    }
//
//    public IjkVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        initVideoView(context);
//    }
//
//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    public IjkVideoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//        initVideoView(context);
//    }
//
//    // REMOVED: onMeasure
//    // REMOVED: onInitializeAccessibilityEvent
//    // REMOVED: onInitializeAccessibilityNodeInfo
//    // REMOVED: resolveAdjustedSize
//    private void initVideoView(Context context) {
//        mAppContext = context.getApplicationContext();
//
//        initRenders();
//
//        mVideoWidth = 0;
//        mVideoHeight = 0;
//        // REMOVED: getHolder().addCallback(mSHCallback);
//        // REMOVED:
//        // getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//        setFocusable(true);
//        setFocusableInTouchMode(true);
//        requestFocus();
//        // REMOVED: mPendingSubtitleTracks = new Vector<Pair<InputStream,
//        // MediaFormat>>();
//        mCurrentState = STATE_IDLE;
//        mTargetState = STATE_IDLE;
//    }
//
//    public void setRenderView(IRenderView renderView) {
//        if (mRenderView != null) {
//            if (mMediaPlayer != null)
//                mMediaPlayer.setDisplay(null);
//
//            View renderUIView = mRenderView.getView();
//            mRenderView.removeRenderCallback(mSHCallback);
//            mRenderView = null;
//            removeView(renderUIView);
//        }
//
//        if (renderView == null)
//            return;
//
//        mRenderView = renderView;
//
//        renderView.setAspectRatio(mCurrentAspectRatio);
//        if (mVideoWidth > 0 && mVideoHeight > 0)
//            renderView.setVideoSize(mVideoWidth, mVideoHeight);
//        if (mVideoSarNum > 0 && mVideoSarDen > 0)
//            renderView.setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen);
//
//        View renderUIView = mRenderView.getView();
//        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER);
//        renderUIView.setLayoutParams(lp);
//        addView(renderUIView);
//
//        mRenderView.addRenderCallback(mSHCallback);
//        mRenderView.setVideoRotation(mVideoRotationDegree);
//    }
//
//    public void setAspectRatio(int aspectRatio){
//        mRenderView.setAspectRatio(aspectRatio);
//    }
//
//    public void setRender(int render) {
//        switch (render) {
//            case RENDER_NONE:
//                setRenderView(null);
//                break;
//            case RENDER_TEXTURE_VIEW: {
//                TextureRenderView renderView = new TextureRenderView(getContext());
//                if (mMediaPlayer != null) {
//                    renderView.getSurfaceHolder().bindToMediaPlayer(mMediaPlayer);
//                    renderView.setVideoSize(mMediaPlayer.getVideoWidth(), mMediaPlayer.getVideoHeight());
//                    renderView.setVideoSampleAspectRatio(mMediaPlayer.getVideoSarNum(), mMediaPlayer.getVideoSarDen());
//                    renderView.setAspectRatio(mCurrentAspectRatio);
//                }
//                setRenderView(renderView);
//                break;
//            }
//            case RENDER_SURFACE_VIEW: {
//                SurfaceRenderView renderView = new SurfaceRenderView(getContext());
//                setRenderView(renderView);
//                break;
//            }
//            default:
//                Log.e(TAG, String.format(Locale.getDefault(), "invalid render %d\n", render));
//                break;
//        }
//    }
//
//    /**
//     * Sets video path.
//     *
//     * @param path
//     *            the path of the video.
//     */
//    public void setVideoPath(String path) {
//        setVideoURI(Uri.parse(path));
//
//    }
//
//
//    public boolean needLoad() {
//        return mMediaPlayer == null;
//    }
//
//    /**
//     * Sets video URI.
//     *
//     * @param uri
//     *            the URI of the video.
//     */
//    public void setVideoURI(Uri uri) {
//        setVideoURI(uri, null);
//    }
//
//    /**
//     * Sets video URI using specific headers.
//     *
//     * @param uri
//     *            the URI of the video.
//     * @param headers
//     *            the headers for the URI request. Note that the cross domain
//     *            redirection is allowed by default, but that can be changed
//     *            with key/value pairs through the headers parameter with
//     *            "android-allow-cross-domain-redirect" as the key and "0" or
//     *            "1" as the value to disallow or allow cross domain
//     *            redirection.
//     */
//    private void setVideoURI(Uri uri, Map<String, String> headers) {
//        mUri = uri;
//        mHeaders = headers;
//        mSeekWhenPrepared = 0;
////        openVideo(1);
//        requestLayout();
//        invalidate();
//    }
//
//    public void stopWithoutRelease() {
//        if (mMediaPlayer != null) {
//            mMediaPlayer.stop();
//            mCurrentState = STATE_IDLE;
//            mTargetState = STATE_IDLE;
//            AudioManager am = (AudioManager) mAppContext.getSystemService(Context.AUDIO_SERVICE);
//            am.abandonAudioFocus(null);
//            if (flyMediaCallback != null)
//                flyMediaCallback.engineStop(this);
//        }
//    }
//
//    public void stopPlayback() {
//        if (mMediaPlayer != null) {
//            mMediaPlayer.stop();
//            mMediaPlayer.release();
//            mMediaPlayer = null;
//            mCurrentState = STATE_IDLE;
//            mTargetState = STATE_IDLE;
//            AudioManager am = (AudioManager) mAppContext.getSystemService(Context.AUDIO_SERVICE);
//            am.abandonAudioFocus(null);
//            if (flyMediaCallback != null)
//                flyMediaCallback.engineStop(this);
//        }
//    }
//
//    private void openVideo(int from) {
//
//        Log.d(TAG,"openVideo");
//        if (mUri == null || mSurfaceHolder == null) {
//            return;
//        }
//
//
//        release(false);
//
//        AudioManager am = (AudioManager) mAppContext.getSystemService(Context.AUDIO_SERVICE);
//        am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
//
//        try {
//            if (mMediaPlayer == null) {
//                mMediaPlayer = createPlayer();
//            } else {
//                mMediaPlayer.reset();
//            }
//
//            mMediaPlayer.setOnPreparedListener(mPreparedListener);
//            mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
//            mMediaPlayer.setOnCompletionListener(mCompletionListener);
//            mMediaPlayer.setOnErrorListener(mErrorListener);
//            mMediaPlayer.setOnInfoListener(mInfoListener);
//            mMediaPlayer.setOnTimedTextListener(mOnTimdTextListener);
////            mMediaPlayer.setOnGPLogListener(mGPLogListener);
//            mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
//            mMediaPlayer.setOnSeekCompleteListener(mOnSeekCompleteListener);
//
//            mCurrentBufferPercentage = 0;
//            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
//                mMediaPlayer.setDataSource(mAppContext, mUri, mHeaders);
//            } else {
//                mMediaPlayer.setDataSource(mUri.toString());
//            }
//
//            bindSurfaceHolder(mMediaPlayer, mSurfaceHolder);
//            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//            mMediaPlayer.setScreenOnWhilePlaying(true);
//            mMediaPlayer.prepareAsync();
//
//            mCurrentState = STATE_PREPARING;
//            attachMediaController();
//
////			long initEndTime = WKTVConfigManager.getInstance().getConfig().getNowTimeMilliseconds();
////			RoomController.stupidLog("Java init player cost time = " + (initEndTime - bean1.t1) + "ms. From = " + from);
//        } catch (IOException ex) {
//            Log.w(TAG, "Unable to open content: " + mUri, ex);
//            mCurrentState = STATE_ERROR;
//            mTargetState = STATE_ERROR;
//            mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
//            return;
//        } catch (IllegalArgumentException ex) {
//            Log.w(TAG, "Unable to open content: " + mUri, ex);
//            mCurrentState = STATE_ERROR;
//            mTargetState = STATE_ERROR;
//            mErrorListener.onError(mMediaPlayer, MediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
//            return;
//        } finally {
//
//        }
//    }
//
//    public void setMediaController(IMediaController controller) {
//        if (mMediaController != null) {
//            mMediaController.hide();
//        }
//        mMediaController = controller;
//        attachMediaController();
//    }
//
//    private void attachMediaController() {
//        if (mMediaPlayer != null && mMediaController != null) {
//            mMediaController.setMediaPlayer(this);
//            View anchorView = this.getParent() instanceof View ? (View) this.getParent() : this;
//            mMediaController.setAnchorView(anchorView);
//            mMediaController.setEnabled(isInPlaybackState());
//        }
//    }
//
//    IMediaPlayer.OnVideoSizeChangedListener mSizeChangedListener = new IMediaPlayer.OnVideoSizeChangedListener() {
//        public void onVideoSizeChanged(IMediaPlayer mp, int width, int height, int sarNum, int sarDen) {
//            mVideoWidth = mp.getVideoWidth();
//            mVideoHeight = mp.getVideoHeight();
//            mVideoSarNum = mp.getVideoSarNum();
//            mVideoSarDen = mp.getVideoSarDen();
//
////			if (mVideoWidth > mVideoHeight) {
////				BasicConfig.getInstance().setLandscapeVideo(true);
////			} else {
////				BasicConfig.getInstance().setLandscapeVideo(false);
////			}
////
////			BasicConfig.getInstance().setVideoRealWidth(mVideoWidth);
////			BasicConfig.getInstance().setVideoRealHeight(mVideoHeight);
//
//            if (mVideoWidth != 0 && mVideoHeight != 0) {
//                if (mRenderView != null) {
//                    mRenderView.setVideoSize(mVideoWidth, mVideoHeight);
//                    mRenderView.setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen);
//                }
//                // REMOVED: getHolder().setFixedSize(mVideoWidth, mVideoHeight);
//                requestLayout();
//            }
//        }
//    };
//
//    IMediaPlayer.OnPreparedListener mPreparedListener = new IMediaPlayer.OnPreparedListener() {
//        public void onPrepared(IMediaPlayer mp) {
//            mCurrentState = STATE_PREPARED;
//
//            // Get the capabilities of the player for this stream
//            // REMOVED: Metadata
//
//            if (mOnPreparedListener != null) {
//                mOnPreparedListener.onPrepared(mMediaPlayer);
//            }
//            if (mMediaController != null) {
//                mMediaController.setEnabled(true);
//            }
//            mVideoWidth = mp.getVideoWidth();
//            mVideoHeight = mp.getVideoHeight();
//
//            int seekToPosition = mSeekWhenPrepared; // mSeekWhenPrepared may be
//            // changed after seekTo()
//            // call
//            if (seekToPosition != 0) {
//                seekTo(seekToPosition);
//            }
//            if (mVideoWidth != 0 && mVideoHeight != 0) {
//                // Log.i("@@@@", "video size: " + mVideoWidth +"/"+
//                // mVideoHeight);
//                // REMOVED: getHolder().setFixedSize(mVideoWidth, mVideoHeight);
//                if (mRenderView != null) {
//                    mRenderView.setVideoSize(mVideoWidth, mVideoHeight);
//                    mRenderView.setVideoSampleAspectRatio(mVideoSarNum, mVideoSarDen);
//                    if (!mRenderView.shouldWaitForResize() || mSurfaceWidth == mVideoWidth && mSurfaceHeight == mVideoHeight) {
//                        // We didn't actually change the size (it was already at
//                        // the size
//                        // we need), so we won't get a "surface changed"
//                        // callback, so
//                        // start the video here instead of in the callback.
//                        if (mTargetState == STATE_PLAYING) {
//                            start();
//                            if (mMediaController != null) {
//                                mMediaController.show();
//                            }
//                        } else if (!isPlaying() && (seekToPosition != 0 || getCurrentPosition() > 0)) {
//                            if (mMediaController != null) {
//                                // Show the media controls when we're paused
//                                // into a video and make 'em stick.
//                                mMediaController.show(0);
//                            }
//                        }
//                    }
//                }
//
//            } else {
//                // We don't know the video size yet, but should start anyway.
//                // The video size might be reported to us later.
//                if (mTargetState == STATE_PLAYING) {
//                    start();
//                }
//            }
//
//
//        }
//    };
//
//    private IMediaPlayer.OnCompletionListener mCompletionListener = new IMediaPlayer.OnCompletionListener() {
//        public void onCompletion(IMediaPlayer mp) {
//            mCurrentState = STATE_PLAYBACK_COMPLETED;
//            mTargetState = STATE_PLAYBACK_COMPLETED;
//            if (mMediaController != null) {
//                mMediaController.hide();
//            }
//            if (mOnCompletionListener != null) {
//                mOnCompletionListener.onCompletion(mMediaPlayer);
//            }
//        }
//    };
//
//    private long startBufferTime;
//
//    private IMediaPlayer.OnInfoListener mInfoListener = new IMediaPlayer.OnInfoListener() {
//        public boolean onInfo(IMediaPlayer mp, int arg1, int arg2) {
//            if (mOnInfoListener != null) {
//                mOnInfoListener.onInfo(mp, arg1, arg2);
//            }
//            switch (arg1) {
//                case IMediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
//                    Log.d(TAG, "MEDIA_INFO_VIDEO_TRACK_LAGGING:");
//                    break;
//                case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
//                    Log.d(TAG, "MEDIA_INFO_VIDEO_RENDERING_START:");
//                    if(flyMediaCallback!=null)
//                        flyMediaCallback.engineStart(this);
//                    break;
////			case IMediaPlayer.MEDIA_INFO_INIT_PLAYER_OVER:
////				break;
////			case IMediaPlayer.MEDIA_INFO_START_OPEN_STREAM:
////				break;
////			case IMediaPlayer.MEDIA_INFO_URL_BEFORE_302:
////				break;
////			case IMediaPlayer.MEDIA_INFO_URL_AFTER_302:
////				break;
//                case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
//                    Log.d(TAG, "MEDIA_INFO_BUFFERING_START:");
//
////				if (null != bean1) {
////					bean1.buffertimes++;
////				}
////				if (null != bean2) {
////					bean2.buffertimes++;
////				}
////
////				startBufferTime = WKTVConfigManager.getInstance().getConfig().getNowTimeMilliseconds();
////
////				RoomController.stupidLog("Start buffer! Total buffer times = " + (null != bean1 ? bean1.buffertimes : 0));
//
//                    break;
//                case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
//                    Log.d(TAG, "MEDIA_INFO_BUFFERING_END:");
////				long ct = WKTVConfigManager.getInstance().getConfig().getNowTimeMilliseconds() - startBufferTime;
////				RoomController.stupidLog("End buffer! Cost time = " + ct + "ms.");
////
////				if (null != bean1) {
////					bean1.bufferlength += ct;
////				}
////				if (null != bean2) {
////					bean2.bufferlength += ct;
////				}
//
//                    break;
//                case IMediaPlayer.MEDIA_INFO_NETWORK_BANDWIDTH:
//                    Log.d(TAG, "MEDIA_INFO_NETWORK_BANDWIDTH: " + arg2);
//                    break;
//                case IMediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
//                    Log.d(TAG, "MEDIA_INFO_BAD_INTERLEAVING:");
//                    break;
//                case IMediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
//                    Log.d(TAG, "MEDIA_INFO_NOT_SEEKABLE:");
//                    break;
//                case IMediaPlayer.MEDIA_INFO_METADATA_UPDATE:
//                    Log.d(TAG, "MEDIA_INFO_METADATA_UPDATE:");
//                    break;
//                case IMediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE:
//                    Log.d(TAG, "MEDIA_INFO_UNSUPPORTED_SUBTITLE:");
//                    break;
//                case IMediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT:
//                    Log.d(TAG, "MEDIA_INFO_SUBTITLE_TIMED_OUT:");
//                    break;
//                case IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED:
//                    mVideoRotationDegree = arg2;
//                    Log.d(TAG, "MEDIA_INFO_VIDEO_ROTATION_CHANGED: " + arg2);
//                    if (mRenderView != null)
//                        mRenderView.setVideoRotation(arg2);
//                    break;
//                case IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:
//                    Log.d(TAG, "MEDIA_INFO_AUDIO_RENDERING_START:");
//                    break;
//            }
//            return true;
//        }
//    };
//
//    private long t0, t1, t2;
////    private IMediaPlayer.OnGPLogListener mGPLogListener = new IMediaPlayer.OnGPLogListener() {
////        @Override
////        public void onGPLog(int type) {
////            switch (type) {
////                case IMediaPlayer.GPLOG_TYPE_INIT_PLAYER_OVER:
////                    t0 = LiveSessionBean.getNowTimeMilliseconds();
////                    listenr.stupidLog("Init player cost time in c++ = " + (t0 - bean1.t0) + "ms.");
////                    break;
////                case IMediaPlayer.GPLOG_TYPE_START_OPEN_STREAM:
////                    t1 = LiveSessionBean.getNowTimeMilliseconds();
////                    listenr.stupidLog("Start open Stream in c++ = " + (t1 - t0) + "ms.");
////                    break;
////                case IMediaPlayer.GPLOG_TYPE_URL_BEFORE_302:
////                    if (null != bean1) {
////                        bean1.t1 = LiveSessionBean.getNowTimeMilliseconds();
////                        listenr.stupidLog("Start connect to server in c++ = " + (bean1.t1 - t1) + "ms.");
////                        listenr.stupidLog("Start connect to server cross java = " + (bean1.t1 - bean1.t0) + "ms.");
////                    }
////                    break;
//////			case IMediaPlayer.GPLOG_TYPE_URL_AFTER_302:
//////				if (null != bean1 && bean1.t3 == -1) {
//////					bean1.t4 = LiveSessionBean.getNowTimeMilliseconds();
//////					listenr.stupidLog("Connect to server over in c++ = " + (bean1.t4 - bean1.t1) + "ms.");
//////				}
//////				break;
////                case IMediaPlayer.GPLOG_TYPE_DECODE_FIRST_FRAME:
////                    t2 = LiveSessionBean.getNowTimeMilliseconds();
////                    if (null != bean1) {
////                        listenr.stupidLog("Decode first frame in c++ = " + (t2 - bean1.t1) + "ms.");
////                    }
////                    break;
////                case IMediaPlayer.GPLOG_TYPE_DRAW_FIRST_FRAME:
////                    if (null != bean1 && bean1.t3 == -1) {
////                        bean1.t3 = LiveSessionBean.getNowTimeMilliseconds();
//////					bean1.state = 1;
////                        Log.i("stupidlog", "Draw at" + bean1.t3);
////                        listenr.stupidLog("Draw first frame in c++ = " + (bean1.t3 - bean1.t1) + "ms. From click cost time "+(bean1.t3-LiveSessionBean.showTime)+"ms.");
////                        listenr.stupidLog("------------------------Playing------------------------");
////                    }
////                    break;
////                case IMediaPlayer.GPLOG_TYPE_START_QUEUE_FIRST_PIC:
////                    tt0 = LiveSessionBean.getNowTimeMilliseconds();
////                    listenr.stupidLog("Start queue first pic.");
////                    break;
////                case IMediaPlayer.GPLOG_TYPE_QUEUE_FIRST_PIC_OVER:
////                    listenr.stupidLog("Queue first pic over cost time = " + (LiveSessionBean.getNowTimeMilliseconds() - tt0) + "ms.");
////                    break;
//////			case IMediaPlayer.GPLOG_TYPE_WE_WAIT_DATA:
//////				RoomController.stupidLog("We wait data.........cost 10ms.");
//////				break;
//////			case IMediaPlayer.GPLOG_TYPE_START_PREPARE_PLAYER:
//////				tt1 = WKTVConfigManager.getInstance().getConfig().getNowTimeMilliseconds();
//////				RoomController.stupidLog("Start prepare player.");
//////				break;
//////			case IMediaPlayer.GPLOG_TYPE_PREPARE_PLAYER_OVER:
//////				RoomController.stupidLog("Prepare player over cost time = " + (WKTVConfigManager.getInstance().getConfig().getNowTimeMilliseconds() - tt1) + "ms.");
//////				break;
//////			case IMediaPlayer.GPLOG_TYPE_PREPARE_PLAYER_STEP_1:
//////				a0 = WKTVConfigManager.getInstance().getConfig().getNowTimeMilliseconds();
//////				RoomController.stupidLog("Prepare player step 1 cost time = " + (a0 - tt1) + "ms.");
//////				break;
//////			case IMediaPlayer.GPLOG_TYPE_PREPARE_PLAYER_STEP_2:
//////				a1 = WKTVConfigManager.getInstance().getConfig().getNowTimeMilliseconds();
//////				RoomController.stupidLog("Prepare player step 2 cost time = " + (a1 - a0) + "ms.");
//////				break;
//////			case IMediaPlayer.GPLOG_TYPE_PREPARE_PLAYER_STEP_3:
//////				a2 = WKTVConfigManager.getInstance().getConfig().getNowTimeMilliseconds();
//////				RoomController.stupidLog("Prepare player step 3 cost time = " + (a2 - a1) + "ms.");
//////				break;
//////			case IMediaPlayer.GPLOG_TYPE_PREPARE_PLAYER_STEP_4:
//////				a3 = WKTVConfigManager.getInstance().getConfig().getNowTimeMilliseconds();
//////				RoomController.stupidLog("Prepare player step 4 cost time = " + (a3 - a2) + "ms.");
//////				break;
//////			case IMediaPlayer.GPLOG_TYPE_PREPARE_PLAYER_STEP_5:
//////				a4 = WKTVConfigManager.getInstance().getConfig().getNowTimeMilliseconds();
//////				RoomController.stupidLog("Prepare player step 5 cost time = " + (a4 - a3) + "ms.");
//////				break;
//////			case IMediaPlayer.GPLOG_TYPE_PREPARE_PLAYER_STEP_6:
//////				a5 = WKTVConfigManager.getInstance().getConfig().getNowTimeMilliseconds();
//////				RoomController.stupidLog("Prepare player step 6 cost time = " + (a5 - a4) + "ms.");
//////				break;
//////			case IMediaPlayer.GPLOG_TYPE_PREPARE_PLAYER_STEP_7:
//////				RoomController.stupidLog("Prepare player step 7 cost time = " + (WKTVConfigManager.getInstance().getConfig().getNowTimeMilliseconds() - a5) + "ms.");
//////				break;
////                default:
////                    break;
////            }
////        }
////    };
//
//    private long a0, a1, a2, a3, a4, a5;
//
//    private long tt0, tt1;
//
//    private IMediaPlayer.OnErrorListener mErrorListener = new IMediaPlayer.OnErrorListener() {
//        public boolean onError(IMediaPlayer mp, int framework_err, int impl_err) {
////			RoomController.stupidLog("mediaPlayer error framework_err="+framework_err+" impl_err="+impl_err );
//            Log.d(TAG, "Error: " + framework_err + "," + impl_err);
//            mCurrentState = STATE_ERROR;
//            mTargetState = STATE_ERROR;
//            if (mMediaController != null) {
//                mMediaController.hide();
//            }
//
//			/* If an error handler has been supplied, use it and finish. */
//            if (mOnErrorListener != null) {
//                if (mOnErrorListener.onError(mMediaPlayer, framework_err, impl_err)) {
//                    if (flyMediaCallback != null)
//                        flyMediaCallback.engineError(this, framework_err, impl_err + "");
//                    return true;
//                }
//            }
//            cancelProgressTimer();
//            return true;
//        }
//    };
//
//    private IMediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener = new IMediaPlayer.OnBufferingUpdateListener() {
//        public void onBufferingUpdate(IMediaPlayer mp, int percent) {
//            mCurrentBufferPercentage = percent;
//        }
//    };
//
//    /**
//     * Register a callback to be invoked when the media file is loaded and ready
//     * to go.
//     *
//     * @param l
//     *            The callback that will be run
//     */
//    public void setOnPreparedListener(IMediaPlayer.OnPreparedListener l) {
//        mOnPreparedListener = l;
//    }
//
//    /**
//     * Register a callback to be invoked when the end of a media file has been
//     * reached during playback.
//     *
//     * @param l
//     *            The callback that will be run
//     */
//    public void setOnCompletionListener(IMediaPlayer.OnCompletionListener l) {
//        mOnCompletionListener = l;
//    }
//
//    /**
//     * Register a callback to be invoked when an error occurs during playback or
//     * setup. If no listener is specified, or if the listener returned false,
//     * VideoView will inform the user of any errors.
//     *
//     * @param l
//     *            The callback that will be run
//     */
//    public void setOnErrorListener(IMediaPlayer.OnErrorListener l) {
//        mOnErrorListener = l;
//    }
//
//    /**
//     * Register a callback to be invoked when an informational event occurs
//     * during playback or setup.
//     *
//     * @param l
//     *            The callback that will be run
//     */
//    public void setOnInfoListener(IMediaPlayer.OnInfoListener l) {
//        mOnInfoListener = l;
//    }
//
//    public void setOnTimedTextListener(IMediaPlayer.OnTimedTextListener l) {
//        mOnTimdTextListener = l;
//    }
//
//    public void setOnSeekCompleteListener(IMediaPlayer.OnSeekCompleteListener l) {
//        mOnSeekCompleteListener = l;
//    }
//
//    // REMOVED: mSHCallback
//    private void bindSurfaceHolder(IMediaPlayer mp, IRenderView.ISurfaceHolder holder) {
//        if (mp == null)
//            return;
//
//        if (holder == null) {
//            mp.setDisplay(null);
//            return;
//        }
//
//        holder.bindToMediaPlayer(mp);
//    }
//
//    IRenderView.IRenderCallback mSHCallback = new IRenderView.IRenderCallback() {
//        @Override
//        public void onSurfaceChanged(@NonNull IRenderView.ISurfaceHolder holder, int format, int w, int h) {
//            if (holder.getRenderView() != mRenderView) {
//                Log.e(TAG, "onSurfaceChanged: unmatched render callback\n");
//                return;
//            }
//
//            mSurfaceWidth = w;
//            mSurfaceHeight = h;
//            boolean isValidState = (mTargetState == STATE_PLAYING);
//            boolean hasValidSize = !mRenderView.shouldWaitForResize() || (mVideoWidth == w && mVideoHeight == h);
//            if (mMediaPlayer != null && isValidState && hasValidSize) {
//                if (mSeekWhenPrepared != 0) {
//                    seekTo(mSeekWhenPrepared);
//                }
//                start();
//            }
//        }
//
//        @Override
//        public void onSurfaceCreated(@NonNull IRenderView.ISurfaceHolder holder, int width, int height) {
////			XLog.debug("Keven","=====currentTime onSurfaceCreated start "+System.currentTimeMillis());
//            if (holder.getRenderView() != mRenderView) {
//                Log.e(TAG, "onSurfaceCreated: unmatched render callback\n");
//                return;
//            }
////			RoomController.stupidLog("onSurfaceCreated ");
//
//            mSurfaceHolder = holder;
//            if (mMediaPlayer != null) {
//                bindSurfaceHolder(mMediaPlayer, holder);//必须重新绑定，提前绑定无效
//            } else {
//                if (getVisibility() == View.VISIBLE) {
//                    Log.d(TAG,"onSurfaceCreated");
////                    openVideo(2);
//                }
//            }
//            if(mSurfaceCallback!=null&&!surfaceHasCreated){
//                mSurfaceCallback.surfaceCreated();
//            }
//        }
//
//        @Override
//        public void onSurfaceDestroyed(@NonNull IRenderView.ISurfaceHolder holder) {
//            if (holder.getRenderView() != mRenderView) {
//                Log.e(TAG, "onSurfaceDestroyed: unmatched render callback\n");
//                return;
//            }
////			if (FunctionConfig.USE_VIDEOVIEW_SINGLETON) {
////				surfaceHasCreated=false;
////				mSurfaceHolder.getSurfaceHolder().getSurface().release();
////				mUri = null;
////			} else {
////				mSurfaceHolder = null;
////			}
//
//            mSurfaceHolder = null;
//            // after we return from this we can't use the surface any more
//            // REMOVED: if (mMediaController != null) mMediaController.hide();
//            // REMOVED: release(true);
//            releaseWithoutStop();
//        }
//    };
//
//    private boolean surfaceHasCreated;
//
//    public boolean isSurfaceCreated(){
//        return surfaceHasCreated;
//    }
//
//    public void releaseWithoutStop() {
//        if (mMediaPlayer != null)
//            mMediaPlayer.setDisplay(null);
//    }
//
//    /*
//     * release the media player in any state
//     */
//    public void release(boolean cleartargetstate) {
//        if (mMediaPlayer != null) {
//            mMediaPlayer.reset();
//            mMediaPlayer.release();
//            mMediaPlayer = null;
//            // REMOVED: mPendingSubtitleTracks.clear();
//            mCurrentState = STATE_IDLE;
//            if (cleartargetstate) {
//                mTargetState = STATE_IDLE;
//            }
//            AudioManager am = (AudioManager) mAppContext.getSystemService(Context.AUDIO_SERVICE);
//            am.abandonAudioFocus(null);
//        }
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
//
//        if (isInPlaybackState() && mMediaController != null) {
//            toggleMediaControlsVisiblity();
//        }
//
//        if (isPlaying()) {
//            pause();
//            clickIv.setVisibility(View.VISIBLE);
//        }else{
//            clickIv.setVisibility(View.GONE);
//        }
//        return false;
//    }
//
//    public void setClickIv(ImageView clickIv) {
//        this.clickIv = clickIv;
//    }
//
//    @Override
//    public boolean onTrackballEvent(MotionEvent ev) {
//        if (isInPlaybackState() && mMediaController != null) {
//            toggleMediaControlsVisiblity();
//        }
//        return false;
//    }
//
//    private void toggleMediaControlsVisiblity() {
//        if (mMediaController.isShowing()) {
//            mMediaController.hide();
//        } else {
//            mMediaController.show();
//        }
//    }
//
//    @Override
//    public void start() {
//        if (isInPlaybackState()) {
//            if (getContext() != null && !isWifiConnected(getContext())) {
//                Toast.makeText(getContext(), getResources().getString(R.string.start_play_hint), Toast.LENGTH_SHORT);
//            }
//            mMediaPlayer.start();
//            mCurrentState = STATE_PLAYING;
//            if (UPDATE_PROGRESS_TIMER == null)
//                UPDATE_PROGRESS_TIMER = new Timer();
//            if (mProgressTimerTask == null)
//                mProgressTimerTask = new ProgressTimerTask();
//            UPDATE_PROGRESS_TIMER.schedule(mProgressTimerTask, 0, 300);
//        }
//
//
//        mTargetState = STATE_PLAYING;
//    }
//
//    @Override
//    public void pause() {
//        if (flyMediaCallback != null)
//            flyMediaCallback.enginePause(this);
//
//        if (isInPlaybackState()) {
//            if (mMediaPlayer.isPlaying()) {
//                mMediaPlayer.pause();
//                mCurrentState = STATE_PAUSED;
//            }
//        }
//        mTargetState = STATE_PAUSED;
//    }
//
//    public void suspend() {
//        release(false);
//    }
//
//    public void resume() {
//        Log.d(TAG,"resume");
//        openVideo(3);
//    }
//
//    @Override
//    public int getDuration() {
//        if (isInPlaybackState()) {
//            return (int) mMediaPlayer.getDuration();
//        }
//
//        return -1;
//    }
//
//    @Override
//    public int getCurrentPosition() {
//        if (isInPlaybackState()) {
//            return (int) mMediaPlayer.getCurrentPosition();
//        }
//        return 0;
//    }
//
//    @Override
//    public void seekTo(int msec) {
//        if (isInPlaybackState()) {
//            mMediaPlayer.seekTo(msec);
//            mSeekWhenPrepared = 0;
//        } else {
//            mSeekWhenPrepared = msec;
//        }
//    }
//
//    public void setBottomProgressBar(ProgressBar bottomProgressBar) {
//        this.bottomProgressBar = bottomProgressBar;
//    }
//
//    @Override
//    public boolean isPlaying() {
//        return isInPlaybackState() && mMediaPlayer.isPlaying();
//    }
//
//    @Override
//    public int getBufferPercentage() {
//        if (mMediaPlayer != null) {
//            return mCurrentBufferPercentage;
//        }
//        return 0;
//    }
//
//    public boolean isInPlaybackState() {
//        return (mMediaPlayer != null && mCurrentState != STATE_ERROR && mCurrentState != STATE_IDLE && mCurrentState != STATE_PREPARING);
//    }
//
//    @Override
//    public boolean canPause() {
//        return mCanPause;
//    }
//
//    @Override
//    public boolean canSeekBackward() {
//        return mCanSeekBack;
//    }
//
//    @Override
//    public boolean canSeekForward() {
//        return mCanSeekForward;
//    }
//
//    @Override
//    public int getAudioSessionId() {
//        return 0;
//    }
//
//    // REMOVED: getAudioSessionId();
//    // REMOVED: onAttachedToWindow();
//    // REMOVED: onDetachedFromWindow();
//    // REMOVED: onLayout();
//    // REMOVED: draw();
//    // REMOVED: measureAndLayoutSubtitleWidget();
//    // REMOVED: setSubtitleWidget();
//    // REMOVED: getSubtitleLooper();
//
//    // -------------------------
//    // Extend: Aspect Ratio
//    // -------------------------
//
//    private static final int[] s_allAspectRatio = { IRenderView.AR_ASPECT_FIT_PARENT, IRenderView.AR_ASPECT_FILL_PARENT, IRenderView.AR_ASPECT_WRAP_CONTENT,
//            // IRenderView.AR_MATCH_PARENT,
//            IRenderView.AR_16_9_FIT_PARENT, IRenderView.AR_4_3_FIT_PARENT };
//    private int mCurrentAspectRatioIndex = 0;
//    private int mCurrentAspectRatio = s_allAspectRatio[0];
//
//    public int toggleAspectRatio() {
//        mCurrentAspectRatioIndex++;
//        mCurrentAspectRatioIndex %= s_allAspectRatio.length;
//
//        mCurrentAspectRatio = s_allAspectRatio[mCurrentAspectRatioIndex];
//        if (mRenderView != null)
//            mRenderView.setAspectRatio(mCurrentAspectRatio);
//        return mCurrentAspectRatio;
//    }
//
//    // -------------------------
//    // Extend: Render
//    // -------------------------
//    public static final int RENDER_NONE = 0;
//    public static final int RENDER_SURFACE_VIEW = 1;
//    public static final int RENDER_TEXTURE_VIEW = 2;
//
//    private List<Integer> mAllRenders = new ArrayList<Integer>();
//    private int mCurrentRenderIndex = 0;
//    private int mCurrentRender = RENDER_NONE;
//
//    private void initRenders() {
//        mAllRenders.clear();
//
//        if (ENABLE_SURFACE_VIEW)
//            mAllRenders.add(RENDER_SURFACE_VIEW);
//        if (ENABLE_TEXTURE_VIEW && Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//            mAllRenders.add(RENDER_TEXTURE_VIEW);
//        if (ENABLE_NO_VIEW)
//            mAllRenders.add(RENDER_NONE);
//
//        if (mAllRenders.isEmpty())
//            mAllRenders.add(RENDER_SURFACE_VIEW);
//        mCurrentRender = mAllRenders.get(mCurrentRenderIndex);
//        setRender(mCurrentRender);
//    }
//
//    // -------------------------
//    // Extend: Player
//    // -------------------------
//    public IMediaPlayer createPlayer() {
//        IMediaPlayer mediaPlayer = null;
//
//        IjkMediaPlayer ijkMediaPlayer = null;
//        if (mUri != null) {
//            ijkMediaPlayer = new IjkMediaPlayer();
//            IjkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_ERROR);
////			PrefUtil pf = new PrefUtil(mAppContext, Constant.APP_PREF);
////			if (pf.getBoolean(Constant.IS_HARD_DECODE, Snippet.isSupportMediaCodecHardDecoder())) {
////            if (Snippet.isSupportMediaCodecHardDecoder()) {
//                System.out.println("ijk mediacodec 1");
//                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
//                if (USE_MEDIACODEC_AUTO_ROTATE) {
//                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1);
//                } else {
//                    ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 0);
//                }
////				QUtils.onEvent("video_decode_type", "hardware");
////            } else {
////                System.out.println("ijk mediacodec 0");
////                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 0);
//////				QUtils.onEvent("video_decode_type", "software");
////            }
//
//            if (USE_OPENSLES) {
//                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 1);
//            } else {
//                ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 0);
//            }
//
//            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32);
//
//            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 12);
//
//            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 1);
//
//            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);
//
//            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);
//
//            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max_cached_duration", 0);
//            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "infbuf", 0);
//            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "packet-buffering", 1);
//        }
//        mediaPlayer = ijkMediaPlayer;
//
//        if (DETACHED_SURFACE_TEXTURE_VIEW) {
//            mediaPlayer = new TextureMediaPlayer(mediaPlayer);
//        }
//
//        return mediaPlayer;
//    }
//
//    // -------------------------
//    // Extend: Background
//    // -------------------------
//    public ITrackInfo[] getTrackInfo() {
//        if (mMediaPlayer == null)
//            return null;
//
//        return mMediaPlayer.getTrackInfo();
//    }
//
//    public void selectTrack(int stream) {
//        MediaPlayerCompat.selectTrack(mMediaPlayer, stream);
//    }
//
//    public void deselectTrack(int stream) {
//        MediaPlayerCompat.deselectTrack(mMediaPlayer, stream);
//    }
//
//    public int getSelectedTrack(int trackType) {
//        return MediaPlayerCompat.getSelectedTrack(mMediaPlayer, trackType);
//    }
//
//
//    private OnSurfaceCallback mSurfaceCallback;
//    public void setOnSurfaceCallback(OnSurfaceCallback callback){
//        mSurfaceCallback=callback;
//    }
//
//
//
//    public interface OnSurfaceCallback{
//        void surfaceCreated();
//    }
//
//    public class ProgressTimerTask extends TimerTask {
//        @Override
//        public void run() {
//            if (mCurrentState == STATE_PLAYING || mCurrentState == STATE_PAUSED) {
////                Log.v(TAG, "onProgressUpdate " + "[" + this.hashCode() + "] ");
//                getHandler().post(new Runnable() {
//                    @Override
//                    public void run() {
//                        int index = getCurrentPosition() > 0 ? 800 : 0;
//                        int position = getCurrentPosition() + index;
//                        int duration = getDuration();
//
//                        int progress = position * 100 / (duration == 0 ? 1 : duration);
//
//                        Log.d("gaohang","progress = "+progress +"    position="+position+"    duration"+ duration);
//                        bottomProgressBar.setProgress(progress);
//                    }
//                });
//            }
//        }
//    }
//
//    public void cancelProgressTimer() {
//        if (UPDATE_PROGRESS_TIMER != null) {
//            UPDATE_PROGRESS_TIMER.cancel();
//        }
//        if (mProgressTimerTask != null) {
//            mProgressTimerTask.cancel();
//        }
//    }
//
//    /**
//     * This method requires the caller to hold the permission ACCESS_NETWORK_STATE.
//     *
//     * @param context context
//     * @return if wifi is connected,return true
//     */
//    public static boolean isWifiConnected(Context context) {
//        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
//        return networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
//    }
//
//
//}
