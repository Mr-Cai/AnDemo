package demo.tencent.ad;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.qq.e.ads.cfg.VideoOption;
import com.qq.e.ads.nativ.MediaView;
import com.qq.e.ads.nativ.NativeADEventListener;
import com.qq.e.ads.nativ.NativeADMediaListener;
import com.qq.e.ads.nativ.NativeADUnifiedListener;
import com.qq.e.ads.nativ.NativeUnifiedAD;
import com.qq.e.ads.nativ.NativeUnifiedADData;
import com.qq.e.ads.nativ.VideoPreloadListener;
import com.qq.e.ads.nativ.widget.NativeAdContainer;
import com.qq.e.comm.constants.AdPatternType;
import com.qq.e.comm.util.AdError;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class NativeADUnifiedSampleActivity extends Activity implements NativeADUnifiedListener {

    private static final int MSG_INIT_AD = 0;
    private static final int MSG_VIDEO_START = 1;
    private static final int AD_COUNT = 1;
    private static final String TAG = NativeADUnifiedSampleActivity.class.getSimpleName();

    private Button mDownloadButton;
    private Button mCTAButton;
    private RelativeLayout mADInfoContainer;
    private NativeUnifiedADData mAdData;
    private H mHandler = new H();
    // 与广告有关的变量，用来显示广告素材的UI
    private NativeUnifiedAD mAdManager;
    private MediaView mMediaView;
    private ImageView mImagePoster;
    private NativeAdContainer mContainer;

    private View mButtonsContainer;
    private Button mPlayButton;
    private Button mPauseButton;
    private Button mStopButton;
    private CheckBox mMuteButton;

    private boolean mPlayMute = true;

    private boolean mPreloadVideo = false;
    private boolean mLoadingAd = false;

    @Nullable
    public static VideoOption getVideoOption(Intent intent) {
        if (intent == null) {
            return null;
        }

        VideoOption videoOption = null;
        boolean noneOption = false;
        if (!noneOption) {
            VideoOption.Builder builder = new VideoOption.Builder();

            builder.setAutoPlayPolicy(VideoOption.AutoPlayPolicy.ALWAYS);
            builder.setAutoPlayMuted(true);
            builder.setDetailPageMuted(false);
            builder.setNeedCoverImage(true);
            builder.setNeedProgressBar(true);
            builder.setEnableDetailPage(true);
            builder.setEnableUserControl(false);

            videoOption = builder.build();
        }
        return videoOption;
    }

    public static int getVideoPlayPolicy(Intent intent, Context context) {
        VideoOption option = getVideoOption(intent);
        if (option == null) {
            return VideoOption.VideoPlayPolicy.AUTO;
        }

        int autoPlayPolicy = option.getAutoPlayPolicy();

        if (autoPlayPolicy == VideoOption.AutoPlayPolicy.ALWAYS) {
            return VideoOption.VideoPlayPolicy.AUTO;
        } else if (autoPlayPolicy == VideoOption.AutoPlayPolicy.WIFI) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifiNetworkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            return wifiNetworkInfo != null && wifiNetworkInfo.isConnected() ? VideoOption.VideoPlayPolicy.AUTO
                    : VideoOption.VideoPlayPolicy.MANUAL;
        } else if (autoPlayPolicy == VideoOption.AutoPlayPolicy.NEVER) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            int hour = calendar.get(Calendar.HOUR_OF_DAY);

            // 简单的播放策略示例，虽然VideoOption设置了从不自动播放，SDK不会主动触发自动播放
            // 但晚上10点以后，开发者自行调用了startVideoPlay进行了视频播放
            // 这在用户看来仍然是"自动播放"的，因为用户没有进行任何操作，视频就开始播放了
            return hour >= 22 ? VideoOption.VideoPlayPolicy.AUTO : VideoOption.VideoPlayPolicy.MANUAL;
        }

        return VideoOption.VideoPlayPolicy.UNKNOWN;
    }

    public static void updateAdAction(Button button, NativeUnifiedADData ad) {
        if (!ad.isAppAd()) {
            button.setText("浏览");
            return;
        }
        switch (ad.getAppStatus()) {
            case 0:
                button.setText("下载");
                break;
            case 1:
                button.setText("启动");
                break;
            case 2:
                button.setText("更新");
                break;
            case 4:
                button.setText(ad.getProgress() + "%");
                break;
            case 8:
                button.setText("安装");
                break;
            case 16:
                button.setText("下载失败，重新下载");
                break;
            default:
                button.setText("浏览");
                break;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_unified_ad_simple);
        initView();

        boolean nonOption = false;
        if (!nonOption) {
            mPlayMute = true;
        }

        mAdManager = new NativeUnifiedAD(this, O.appID, O.nativeID, this);
        mAdManager.setMinVideoDuration(0);
        mAdManager.setMaxVideoDuration(0);
        // 下面设置项为海外流量使用，国内暂不支持
        mAdManager.setVastClassName("com.qq.e.union.demo.adapter.vast.unified.ImaNativeDataAdapter");

        /**
         * 如果广告位支持视频广告，强烈建议在调用loadData请求广告前，调用下面两个方法，有助于提高视频广告的eCPM值 <br/>
         * 如果广告位仅支持图文广告，则无需调用
         */

        /**
         * 设置本次拉取的视频广告，从用户角度看到的视频播放策略<p/>
         *
         * "用户角度"特指用户看到的情况，并非SDK是否自动播放，与自动播放策略AutoPlayPolicy的取值并非一一对应 <br/>
         *
         * 例如开发者设置了VideoOption.AutoPlayPolicy.NEVER，表示从不自动播放 <br/>
         * 但满足某种条件(如晚上10点)时，开发者调用了startVideo播放视频，这在用户看来仍然是自动播放的
         */
        mAdManager.setVideoPlayPolicy(getVideoPlayPolicy(getIntent(), this)); // 本次拉回的视频广告，在用户看来是否为自动播放的

        /**
         * 设置在视频广告播放前，用户看到显示广告容器的渲染者是SDK还是开发者 <p/>
         *
         * 一般来说，用户看到的广告容器都是SDK渲染的，但存在下面这种特殊情况： <br/>
         *
         * 1. 开发者将广告拉回后，未调用bindMediaView，而是用自己的ImageView显示视频的封面图 <br/>
         * 2. 用户点击封面图后，打开一个新的页面，调用bindMediaView，此时才会用到SDK的容器 <br/>
         * 3. 这种情形下，用户先看到的广告容器就是开发者自己渲染的，其值为VideoADContainerRender.DEV
         * 4. 如果觉得抽象，可以参考NativeADUnifiedDevRenderContainerActivity的实现
         */
        mAdManager.setVideoADContainerRender(VideoOption.VideoADContainerRender.SDK); // 视频播放前，用户看到的广告容器是由SDK渲染的
    }

    private void initView() {
        mMediaView = findViewById(R.id.gdt_media_view);
        mImagePoster = findViewById(R.id.img_poster);
        mADInfoContainer = findViewById(R.id.ad_info_container);
        mDownloadButton = findViewById(R.id.btn_download);
        mCTAButton = findViewById(R.id.btn_cta);
        mContainer = findViewById(R.id.native_ad_container);

        mButtonsContainer = findViewById(R.id.video_btns_container);
        mPlayButton = findViewById(R.id.btn_play);
        mPauseButton = findViewById(R.id.btn_pause);
        mStopButton = findViewById(R.id.btn_stop);
        mMuteButton = findViewById(R.id.btn_mute);
    }


    @Override
    public void onADLoaded(List<NativeUnifiedADData> ads) {
        mLoadingAd = false;
        if (ads != null && ads.size() > 0) {
            Message msg = Message.obtain();
            msg.what = MSG_INIT_AD;
            mAdData = ads.get(0);
            msg.obj = mAdData;
            mHandler.sendMessage(msg);
        }
    }

    public void onPreloadVideoClicked(View view) {
        loadAd(true);
    }

    public void onShowAdClicked(View view) {
        loadAd(false);
    }

    private void loadAd(boolean preloadVideo) {
        if (mLoadingAd) {
            return;
        }
        mLoadingAd = true;
        resetAdViews();
        if (mAdData != null) {
            mAdData.destroy();
            mAdData = null;
        }
        mPreloadVideo = preloadVideo;
        if (mAdManager != null) {
            mAdManager.loadData(AD_COUNT);
        }
    }

    private void initAd(final NativeUnifiedADData ad) {
        if (ad.getAdPatternType() == AdPatternType.NATIVE_VIDEO) {
            if (mPreloadVideo) {
                // 如果是视频广告，可以调用preloadVideo预加载视频素材
                Toast.makeText(this, "正在加载视频素材", Toast.LENGTH_SHORT).show();
                ad.preloadVideo(new VideoPreloadListener() {
                    @Override
                    public void onVideoCached() {
                        Log.d(TAG, "onVideoCached");
                        // 视频素材加载完成，此时展示广告不会有进度条。
                        showAd(ad);
                    }

                    @Override
                    public void onVideoCacheFailed(int errorNo, String msg) {
                        Log.d(TAG, "onVideoCacheFailed : " + msg);
                    }
                });
            } else {
                showAd(ad);
            }
        } else {
            showAd(ad);
        }
    }

    private void showAd(final NativeUnifiedADData ad) {
        renderAdUi(ad);

        List<View> clickableViews = new ArrayList<>();
        // 所有广告类型，注册mDownloadButton的点击事件
        clickableViews.add(mDownloadButton);

        ad.bindAdToView(this, mContainer, null, clickableViews);
        if (ad.getAdPatternType() == AdPatternType.NATIVE_VIDEO) {
            // 视频广告，注册mMediaView的点击事件
            mHandler.sendEmptyMessage(MSG_VIDEO_START);

            VideoOption videoOption = getVideoOption(getIntent());
            ad.bindMediaView(mMediaView, videoOption, new NativeADMediaListener() {
                @Override
                public void onVideoInit() {
                    Log.d(TAG, "onVideoInit: ");
                }

                @Override
                public void onVideoLoading() {
                    Log.d(TAG, "onVideoLoading: ");
                }

                @Override
                public void onVideoReady() {
                    Log.d(TAG, "onVideoReady");
                }

                @Override
                public void onVideoLoaded(int videoDuration) {
                    Log.d(TAG, "onVideoLoaded: ");

                }

                @Override
                public void onVideoStart() {
                    Log.d(TAG, "onVideoStart");
                }

                @Override
                public void onVideoPause() {
                    Log.d(TAG, "onVideoPause: ");
                }

                @Override
                public void onVideoResume() {
                    Log.d(TAG, "onVideoResume: ");
                }

                @Override
                public void onVideoCompleted() {
                    Log.d(TAG, "onVideoCompleted: ");
                }

                @Override
                public void onVideoError(AdError error) {
                    Log.d(TAG, "onVideoError: ");
                }

                @Override
                public void onVideoStop() {
                    Log.d(TAG, "onVideoStop");
                }

                @Override
                public void onVideoClicked() {
                    Log.d(TAG, "onVideoClicked");
                }
            });

            mButtonsContainer.setVisibility(View.VISIBLE);

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v == mPlayButton) {
                        ad.startVideo();
                    } else if (v == mPauseButton) {
                        ad.pauseVideo();
                    } else if (v == mStopButton) {
                        ad.stopVideo();
                    }
                }
            };
            mPlayButton.setOnClickListener(listener);
            mPauseButton.setOnClickListener(listener);
            mStopButton.setOnClickListener(listener);

            mMuteButton.setChecked(mPlayMute);
            mMuteButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    ad.setVideoMute(isChecked);
                }
            });
        } else if (ad.getAdPatternType() == AdPatternType.NATIVE_2IMAGE_2TEXT ||
                ad.getAdPatternType() == AdPatternType.NATIVE_1IMAGE_2TEXT) {
            // 双图双文、单图双文：注册mImagePoster的点击事件
            clickableViews.add(mImagePoster);
        } else {
            // 三小图广告：注册native_3img_ad_container的点击事件
            clickableViews.add(findViewById(R.id.native_3img_ad_container));
        }
        ad.setNativeAdEventListener(new NativeADEventListener() {
            @Override
            public void onADExposed() {
                Log.d(TAG, "onADExposed: ");
            }

            @Override
            public void onADClicked() {
                Log.d(TAG, "onADClicked: " + " clickUrl: " + ad.ext.get("clickUrl"));
            }

            @Override
            public void onADError(AdError error) {
                Log.d(TAG, "onADError error code :" + error.getErrorCode()
                        + "  error msg: " + error.getErrorMsg());
            }

            @Override
            public void onADStatusChanged() {
                Log.d(TAG, "onADStatusChanged: ");
                updateAdAction(mDownloadButton, ad);
            }
        });
        updateAdAction(mDownloadButton, ad);
        /**
         * 营销组件
         * 支持项目：智能电话（点击跳转拨号盘），外显表单
         *  bindCTAViews 绑定营销组件监听视图，注意：bindCTAViews的视图不可调用setOnClickListener，否则SDK功能可能受到影响
         *  ad.getCTAText 判断拉取广告是否包含营销组件，如果包含组件，展示组件按钮，否则展示download按钮
         */
        List<View> CTAViews = new ArrayList<>();
        CTAViews.add(mCTAButton);
        ad.bindCTAViews(CTAViews);
        String ctaText = ad.getCTAText(); //获取组件文案
        if (!TextUtils.isEmpty(ctaText)) {
            //如果拉取广告包含CTA组件，则渲染该组件
            //当广告中有营销组件时，隐藏下载按钮，仅为demo示例所用，开发者可自行决定mDownloadButton按钮是否显示
            mCTAButton.setText(ctaText);
            mCTAButton.setVisibility(View.VISIBLE);
            mDownloadButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdData != null) {
            // 必须要在Activity.onResume()时通知到广告数据，以便重置广告恢复状态
            mAdData.resume();
        }
    }

    private void renderAdUi(NativeUnifiedADData ad) {
        int patternType = ad.getAdPatternType();
        TextView titleTxT = findViewById(R.id.text_title);
        TextView descTxT = findViewById(R.id.text_desc);
        if (patternType == AdPatternType.NATIVE_2IMAGE_2TEXT
                || patternType == AdPatternType.NATIVE_VIDEO) {
            mImagePoster.setVisibility(View.VISIBLE);
            ImageView imgLogo = findViewById(R.id.img_logo);
            ImageView imgPoster = findViewById(R.id.img_poster);
            Glide.with(this).load(ad.getIconUrl()).into(imgLogo);
            Glide.with(this).load(ad.getIconUrl()).into(imgPoster);


            titleTxT.setText(ad.getTitle());
            descTxT.setText(ad.getDesc());

        } else if (patternType == AdPatternType.NATIVE_3IMAGE) {

        } else if (patternType == AdPatternType.NATIVE_1IMAGE_2TEXT) {

            titleTxT.setText(ad.getTitle());
            descTxT.setText(ad.getDesc());
        }
    }

    private void resetAdViews() {
        if (mAdData == null) {
            return;
        }
        int patternType = mAdData.getAdPatternType();
        if (patternType == AdPatternType.NATIVE_2IMAGE_2TEXT
                || patternType == AdPatternType.NATIVE_VIDEO) {


        } else if (patternType == AdPatternType.NATIVE_3IMAGE) {


        } else if (patternType == AdPatternType.NATIVE_1IMAGE_2TEXT) {


        }

        mButtonsContainer.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAdData != null) {
            // 必须要在Actiivty.destroy()时通知到广告数据，以便释放内存
            mAdData.destroy();
        }
    }

    @Override
    public void onNoAD(AdError error) {
        Log.d(TAG, "onNoAd error code: " + error.getErrorCode()
                + ", error msg: " + error.getErrorMsg());
        mLoadingAd = false;
    }

    private class H extends Handler {
        public H() {
            super();
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_INIT_AD:
                    NativeUnifiedADData ad = (NativeUnifiedADData) msg.obj;
                    Log.d(TAG, String.format(Locale.getDefault(), "(pic_width,pic_height) = (%d , %d)", ad
                                    .getPictureWidth(),
                            ad.getPictureHeight()));
                    initAd(ad);
                    Log.d(TAG,
                            "eCPM = " + ad.getECPM() + " , eCPMLevel = " + ad.getECPMLevel() + " , " +
                                    "videoDuration = " + ad.getVideoDuration());
                    break;
                case MSG_VIDEO_START:
                    mImagePoster.setVisibility(View.GONE);
                    mMediaView.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }
}
