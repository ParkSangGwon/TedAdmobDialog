package gun0912.ted.tedadmobdialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.IntDef;
import androidx.appcompat.app.AlertDialog;

import com.google.ads.mediation.facebook.FacebookAdapter;
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
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.ads.formats.NativeContentAdView;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;


public class TedAdmobDialog extends AlertDialog {
    private static final String TAG = "ted";
    private NativeAppInstallAdView nativeAppInstallAdView;
    private NativeContentAdView nativeContentAdView;
    private ProgressBar progressView;
    private LinearLayout bannerContainer;
    private Builder builder;
    private NativeAppInstallAd nativeAppInstallAd;
    private NativeContentAd nativeContentAd;

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
                nativeAppInstallAdView.setVisibility(View.GONE);
                nativeContentAdView.setVisibility(View.GONE);
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
        nativeAppInstallAdView = findViewById(R.id.nativeAppInstallAdView);
        nativeContentAdView = findViewById(R.id.nativeContentAdView);
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

        if (nativeAppInstallAd != null) {
            bindNativeView(nativeAppInstallAd, nativeAppInstallAdView);
            nativeAppInstallAd = null;
        }else if(nativeContentAd!=null){
            bindNativeView(nativeContentAd, nativeContentAdView);
            nativeContentAd = null;
        } else{
            loadNative(false);
        }

    }


    public void loadNative() {
        loadNative(true);
    }

    private void loadNative(final boolean preLoad) {
        Log.d(TAG, "loadNative()");
        AdLoader.Builder adLoaderBuilder = new AdLoader.Builder(getContext(), builder.unitId);

        adLoaderBuilder
                .forAppInstallAd(new NativeAppInstallAd.OnAppInstallAdLoadedListener() {
                    @Override
                    public void onAppInstallAdLoaded(NativeAppInstallAd temp) {
                        nativeAppInstallAd = temp;
                        if (!preLoad) {
                            showNative();
                        }
                    }
                })
                .forContentAd(new NativeContentAd.OnContentAdLoadedListener() {
                    @Override
                    public void onContentAdLoaded(NativeContentAd temp) {
                        nativeContentAd = temp;
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

        AdRequest adRequest = new AdRequest.Builder()
                //.addNetworkExtrasBundle(FacebookAdapter.class, extras)
                .build();

        adLoader.loadAd(adRequest);
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

    private void bindNativeView(NativeContentAd nativeAd, NativeContentAdView adView) {
        Log.d(TAG, "bindNativeView() NativeContentAd");
        VideoController vc = nativeAd.getVideoController();

        ImageView ivImage = adView.findViewById(R.id.iv_image);
        MediaView mediaView = adView.findViewById(R.id.mediaview);

        ivImage.setVisibility(View.GONE);
        mediaView.setVisibility(View.GONE);
        List<NativeAd.Image> images = nativeAd.getImages();
        if (vc.hasVideoContent()) {
            adView.setMediaView(mediaView);
            mediaView.setVisibility(View.VISIBLE);

        } else if (images != null && !images.isEmpty()) {
            adView.setImageView(ivImage);
            ivImage.setVisibility(View.VISIBLE);
            ivImage.setImageDrawable(images.get(0).getDrawable());
        }

        adView.setHeadlineView(adView.findViewById(R.id.tv_name));
        adView.setBodyView(adView.findViewById(R.id.tv_body));
        adView.setCallToActionView(adView.findViewById(R.id.tv_call_to_action));
        adView.setLogoView(adView.findViewById(R.id.iv_logo));
        adView.setAdvertiserView(adView.findViewById(R.id.tv_etc));

        // Some assets are guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        ((TextView) adView.getCallToActionView()).setText(nativeAd.getCallToAction());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getLogo() == null) {
            adView.getLogoView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getLogoView()).setImageDrawable(nativeAd.getLogo().getDrawable());
            adView.getLogoView().setVisibility(View.VISIBLE);
        }


        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        adView.setNativeAd(nativeAd);
        nativeAppInstallAdView.setVisibility(View.VISIBLE);
        nativeContentAdView.setVisibility(View.VISIBLE);
        progressView.setVisibility(View.GONE);

        if (builder.onBackPressListener != null) {
            builder.onBackPressListener.onAdShow();
        }
    }

    private void bindNativeView(NativeAppInstallAd nativeAd, NativeAppInstallAdView adView) {
        Log.d(TAG, "bindNativeView() NativeAppInstallAd");
        VideoController vc = nativeAd.getVideoController();

        ImageView ivImage = adView.findViewById(R.id.iv_image);
        MediaView mediaView = adView.findViewById(R.id.mediaview);

        ivImage.setVisibility(View.GONE);
        mediaView.setVisibility(View.GONE);
        List<NativeAd.Image> images = nativeAd.getImages();
        if (vc.hasVideoContent()) {
            adView.setMediaView(mediaView);
            mediaView.setVisibility(View.VISIBLE);

        } else if (images != null && !images.isEmpty()) {
            adView.setImageView(ivImage);
            ivImage.setVisibility(View.VISIBLE);
            ivImage.setImageDrawable(images.get(0).getDrawable());
        }

        adView.setHeadlineView(adView.findViewById(R.id.tv_name));
        adView.setBodyView(adView.findViewById(R.id.tv_body));
        adView.setCallToActionView(adView.findViewById(R.id.tv_call_to_action));
        adView.setIconView(adView.findViewById(R.id.iv_logo));
        adView.setStoreView(adView.findViewById(R.id.tv_etc));

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


        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
            adView.getStoreView().setVisibility(View.VISIBLE);
        }

        adView.setNativeAd(nativeAd);
        nativeAppInstallAdView.setVisibility(View.VISIBLE);
        nativeContentAdView.setVisibility(View.VISIBLE);
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

        ivImage.setVisibility(View.GONE);
        mediaView.setVisibility(View.GONE);
        List<NativeAd.Image> images = nativeAd.getImages();
        if (vc.hasVideoContent()) {
            adView.setMediaView(mediaView);
            mediaView.setVisibility(View.VISIBLE);

        } else if (images != null && !images.isEmpty()) {
            adView.setImageView(ivImage);
            ivImage.setVisibility(View.VISIBLE);
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


    @Retention(RetentionPolicy.SOURCE)
    @IntDef({AdType.NATIVE, AdType.BANNER})
    public @interface AdType {
        int NATIVE = 1;
        int BANNER = 2;
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
