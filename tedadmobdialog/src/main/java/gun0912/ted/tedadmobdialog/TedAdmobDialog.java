package gun0912.ted.tedadmobdialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.v7.app.AlertDialog;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;


public class TedAdmobDialog extends AlertDialog {
    private static final String TAG = "ted";

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({AdType.NATIVE, AdType.BANNER})
    public @interface AdType {
        int NATIVE = 1;
        int BANNER = 2;
    }

    private UnifiedNativeAdView unifiedNativeAdView;
    private ProgressBar progressView;
    private LinearLayout bannerContainer;

    private UnifiedNativeAd nativeAd;
    private Builder builder;

    public TedAdmobDialog(Builder builder, int theme) {
        super(builder.context, theme);
        this.builder = builder;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("ted", "onCreate()");
        setContentView(R.layout.dialog_tedadmob);
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        Window window = getWindow();
        layoutParams.copyFrom(window.getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(layoutParams);
        setCanceledOnTouchOutside(false);
        initView();

        setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                progressView.setVisibility(View.VISIBLE);
                bannerContainer.setVisibility(View.GONE);
                bannerContainer.removeAllViews();
                unifiedNativeAdView.setVisibility(View.GONE);
                switch (builder.adType) {
                    case AdType.BANNER:
                        showBanner(bannerContainer);
                        break;
                    case AdType.NATIVE:
                        showNative();
                        break;
                }
            }
        });
    }

    private void initView() {
        unifiedNativeAdView = findViewById(R.id.unifiedNativeAdView);
        progressView = findViewById(R.id.progressView);
        bannerContainer = findViewById(R.id.view_banner_container);
        findViewById(R.id.tv_finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFinishClick();
            }
        });


        TextView tvReview = findViewById(R.id.tv_review);
        View viewBtnDivider = findViewById(R.id.view_btn_divider);

        if (builder.showReviewButton) {
            tvReview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onReviewClick();
                }
            });
        } else {
            tvReview.setVisibility(View.GONE);
            viewBtnDivider.setVisibility(View.GONE);
        }
    }

    private void onFinishClick() {
        if (builder.onBackPressListener != null) {
            builder.onBackPressListener.onFinish();
        }
        dismiss();
    }

    private void onReviewClick() {
        openPlayStore();
        if (builder.onBackPressListener != null) {
            builder.onBackPressListener.onReviewClick();
        }
    }

    private void openPlayStore() {
        String packageName = getContext().getPackageName();
        try {

            getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + packageName)));
        }


    }

    private void showNative() {

        if (nativeAd != null) {
            bindNativeView(nativeAd, unifiedNativeAdView);
            nativeAd = null;
        } else {
            loadNative(false);
        }

    }

    public void loadNative() {
        loadNative(true);
    }

    private void loadNative(final boolean preLoad) {
        Log.d(TAG, "loadNative()");
        AdLoader.Builder adLoaderBuilder = new AdLoader.Builder(getContext(), builder.unitId);

        adLoaderBuilder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {

            @Override
            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                Log.d(TAG, "onUnifiedNativeAdLoaded()");
                nativeAd = unifiedNativeAd;
                if (!preLoad) {
                    showNative();
                }
            }

        });

        VideoOptions videoOptions = new VideoOptions.Builder()
                .setStartMuted(this.builder.startMute)
                .build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();

        adLoaderBuilder.withNativeAdOptions(adOptions);

        if (builder.adListener != null) {
            adLoaderBuilder.withAdListener(builder.adListener);
        }

        AdLoader adLoader = adLoaderBuilder.build();
        adLoader.loadAd(new AdRequest.Builder().build());
    }

    private void showBanner(LinearLayout bannerContainer) {
        AdView admobBannerView = new AdView(getContext());
        AdRequest adRequest = new AdRequest.Builder().build();

        admobBannerView.setAdSize(AdSize.MEDIUM_RECTANGLE);
        admobBannerView.setAdUnitId(builder.unitId);
        admobBannerView.setAdListener(builder.adListener);

        admobBannerView.loadAd(adRequest);
        bannerContainer.addView(admobBannerView);
        bannerContainer.setVisibility(View.VISIBLE);
        progressView.setVisibility(View.GONE);
        if (builder.onBackPressListener != null) {
            builder.onBackPressListener.onAdShow();
        }
    }


    private void bindNativeView(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {
        Log.d(TAG, "bindNativeView()");
        VideoController vc = nativeAd.getVideoController();

        ImageView ivImage = adView.findViewById(R.id.iv_image);
        MediaView mediaView = adView.findViewById(R.id.mediaview);

        if (vc.hasVideoContent()) {
            adView.setMediaView(mediaView);
            ivImage.setVisibility(View.GONE);
        } else {
            adView.setImageView(ivImage);
            mediaView.setVisibility(View.GONE);
            List<NativeAd.Image> images = nativeAd.getImages();
            ivImage.setImageDrawable(images.get(0).getDrawable());

        }

        adView.setHeadlineView(adView.findViewById(R.id.tv_name));
        adView.setBodyView(adView.findViewById(R.id.tv_body));
        adView.setCallToActionView(adView.findViewById(R.id.tv_call_to_action));
        adView.setIconView(adView.findViewById(R.id.iv_logo));
        adView.setAdvertiserView(adView.findViewById(R.id.tv_etc));

        // Some assets are guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        ((TextView) adView.getCallToActionView()).setText(nativeAd.getCallToAction());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }


        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        adView.setNativeAd(nativeAd);
        adView.setVisibility(View.VISIBLE);
        progressView.setVisibility(View.GONE);

        if (builder.onBackPressListener != null) {
            builder.onBackPressListener.onAdShow();
        }

    }

    public static class Builder {
        private Context context;
        @AdType
        private int adType;
        private String unitId;
        private boolean startMute = true;
        private AdListener adListener;
        private OnBackPressListener onBackPressListener;
        private boolean showReviewButton = true;

        public Builder(Context context, @AdType int adType, String unitId) {
            this.context = context;
            this.adType = adType;
            this.unitId = unitId;
        }

        public Builder setStartMute(boolean startMute) {
            this.startMute = startMute;
            return this;
        }

        public Builder setAdListener(AdListener adListener) {
            this.adListener = adListener;
            return this;
        }

        public Builder setOnBackPressListener(OnBackPressListener onBackPressListener) {
            this.onBackPressListener = onBackPressListener;
            return this;
        }

        public Builder showReviewButton(boolean showReviewButton) {
            this.showReviewButton = showReviewButton;
            return this;
        }

        public TedAdmobDialog create() {
            return new TedAdmobDialog(this, R.style.TedAdmobDialog);
        }

    }
}
