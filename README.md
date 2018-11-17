# What is TedAdmobDialog?<br/><br/>

## English
**TedAdmobDialog** come from [TedAdHelper](https://github.com/ParkSangGwon/TedAdHelper)<br/>
Until then, we could only do [Mediation](https://www.facebook.com/help/audiencenetwork/1744424245771424?helpref=hc_fnav) through [TedAdHelper](https://github.com/ParkSangGwon/TedAdHelper).<br/>
Becoz Admob did not support Native ad type.<br/>
But nowdays Admob support Native ad type.<br/>

But we still need back press dialog like this.<br/>
![Screenshot](https://github.com/ParkSangGwon/TedAdHelper/blob/master/Screenshot_backpress_en_1.jpeg?raw=true) <br/><br/>

When you use TedAdHelper's back press dialog, you have need request and loading advertise time.<br/>
Because the request is made at the same time the dialog is shown.<br/>

**TedAdmobDialog** support pre advertise request.<br/>
So you can see the dialog and show ads at the same time.

<br/><br/><br/><br/>

## Korean
**TedAdmobDialog**는 [TedAdHelper](https://github.com/ParkSangGwon/TedAdHelper)에서부터 태어난 라이브러리입니다.<br/>
이전까지는 [TedAdHelper](https://github.com/ParkSangGwon/TedAdHelper)를 통해 [미디에이션](https://www.facebook.com/help/audiencenetwork/1744424245771424?helpref=hc_fnav)을 해주었습니다.<br/>
왜냐하면 이전까지 Admob에서는 Native 광고에 대해서는 미디에이션을 지원하지 않았었기 때문입니다.<br/>
하지만 지금은 Admob에서 Native 광고에 대해서도 미디에이션을 지원합니다.<br/><br/>

하지만 뒤로가기를 눌렀을때 팝업형식의 Dialog로 나오게 하는것은 여전히 필요합니다.<br/>
![Screenshot](https://github.com/ParkSangGwon/TedAdHelper/blob/master/Screenshot_backpress_ko_1.jpeg?raw=true)<br/>

TedAdHelper에서는 다이어로그가 보여짐과 동시에 광고를 요청하기때문에 광고가 표시되기까지는 시차가 발생했었습니다.<br/>
**TedAdmobDialog**는 미리 광고를 로딩해두고 Dialog가 실행될때 바로 광고를 보여줄 수 있도록 지원합니다.<br/>




<br/><br/><br/><br/>

## Mediation
![Screenshot](https://enhanceco.files.wordpress.com/2018/01/mediate.png?w=656)<br/>

I recommend you have to use mediation.<br/>
If you use mediation, you can earn more money using highest eCPM network.<br/>
See this document.<br/>
https://developers.google.com/admob/android/mediation<br/>
I use admob, facebook audience network network.<br/>



<br/><br/><br/><br/>

## Setup


### Gradle
[ ![Download](https://api.bintray.com/packages/tkdrnjs0912/maven/tedadmobdialog/images/download.svg) ](https://bintray.com/tkdrnjs0912/maven/tedadmobdialog/_latestVersion)
```javascript
dependencies {
    implementation 'gun0912.ted:tedadmobdialog:x.y.z'
}

```

<br/><br/>




## How to use


### 0. Type
You can use 2 type advertise
- TedAdmobDialog.AdType.NATIVE: [Advanced Native](https://developers.google.com/admob/android/native-advanced)
- TedAdmobDialog.AdType.BANNER: [MEDIUM_RECTANGLE Banner](https://developers.google.com/admob/android/banner)<br/>


### 1. Pre loading
- Make your TedAdmobDialog instance.
- And pre load advertise using `TedAdmobDialog::loadAd()`
```java
 nativeTedAdmobDialog = new TedAdmobDialog.Builder(this, TedAdmobDialog.AdType.NATIVE, AD_TEST_KEY_NATIVE)
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
                 nativeTedAdmobDialog.loadAd();
             }
         })
         .create();

 nativeTedAdmobDialog.loadAd();

```
- When advertise shown, pre load again in `onAdShow()`


### 2. Show TedAdmobDialog
- When you need to show advertise, call `TedAdmobDialog::show()`

```java
indViewById(R.id.btn_native).setOnClickListener(new View.OnClickListener() {
   @Override
   public void onClick(View v) {
       nativeTedAdmobDialog.show();
   }
});
```

## Customize
- `setStartMute()`: If you want muted advertise, call this function in builder.
- `showReviewButton()`: Dialog include `review` button. you can control review button's visibility.


## License 
 ```code
Copyright 2018 Ted Park

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.```

