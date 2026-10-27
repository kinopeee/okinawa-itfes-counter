# 沖縄ビジネスITフェス受付カウンター（Android）

Kotlin / Jetpack Compose / Material 3 のAndroidアプリです。Android公式ライブラリのみを実行時に使用し、画像や追加フォントは使用しません。

## ビルドと起動

JDK 17、Android SDK Platform 35、Android SDK Build Tools 35.0.0 を用意し、`ANDROID_HOME` を設定するか、このフォルダの `local.properties` にSDKの場所を指定します。

```sh
cd android
./gradlew :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n jp.co.keyplanning.okinawacounter/.MainActivity
```

Android Studioでこのフォルダを開くこともできます。Android 8.0（API 26）以上に対応します。

## 動作

- 「来場者+1」を5回押すと「5人」「10%」。リセットで0人に戻ります。
- 0〜39人は緑、40〜50人はオレンジ、51人以上は赤と「定員オーバー」。割合は100%を超えて表示し、バーは満杯で止めます。
- 数字には短いフェード／スライド、バーには短い伸縮アニメーションを付けています。
- 画面回転やAndroidの状態復元には対応します。明示的な終了後も保存し続ける永続ストレージは使用しません。
- 小さい画面や横画面はスクロールできます。配色はイベント受付用の固定ライトテーマです。

## 手動確認

起動直後の0人から「来場者+1」を5回タップし、5人・10%になることを確認します。40人・80%でオレンジ、50人・100%では警告なし、51人・102%で赤い警告表示、リセット後0人・0%も確認します。
