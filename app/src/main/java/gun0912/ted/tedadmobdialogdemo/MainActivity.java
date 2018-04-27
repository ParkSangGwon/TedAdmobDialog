package gun0912.ted.tedadmobdialogdemo;

import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.ads.AdListener;

import gun0912.ted.tedadmobdialog.OnBackPressListener;
import gun0912.ted.tedadmobdialog.TedAdmobDialog;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "ted";

    public static final String AD_TEST_KEY_BANNER = "ca-app-pub-3940256099942544/6300978111";
    public static final String AD_TEST_KEY_NATIVE = "ca-app-pub-3940256099942544/2247696110";

    TedAdmobDialog nativeTedAdmobDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setNativeBtn();
        setBannerBtn();

    }

    private void setBannerBtn() {

        findViewById(R.id.btn_banner).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new TedAdmobDialog.Builder(MainActivity.this, TedAdmobDialog.AdType.BANNER, AD_TEST_KEY_BANNER)
                        .setAdListener(new AdListener() {

                        })
                        .showReviewButton(false)
                        .create()
                        .show();

            }
        });

    }

    private void setNativeBtn() {
        nativeTedAdmobDialog = new TedAdmobDialog.Builder(MainActivity.this, TedAdmobDialog.AdType.NATIVE, AD_TEST_KEY_NATIVE)
                .setOnBackPressListener(new OnBackPressListener() {
                    @Override
                    public void onReviewClick() {

                    }

                    @Override
                    public void onFinish() {
                        finish();
                    }

                    @Override
                    public void onAdShow() {
                        log("onAdShow");
                        nativeTedAdmobDialog.loadNative();
                    }
                })
                .create();

        findViewById(R.id.btn_native).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nativeTedAdmobDialog.show();
            }
        });

        nativeTedAdmobDialog.loadNative();

    }

    private void log(String text) {
        Log.d(TAG, text);
    }
}
