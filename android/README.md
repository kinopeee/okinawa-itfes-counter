# 沖縄ビジネスITフェス受付カウンター (Android)

Kotlin / Jetpack Compose のシングル画面アプリ。Android 8.0 (API 26) 以降に対応します。

## ビルド

JDK 17、Android SDK Platform 35、Build Tools 35.0.0 が必要です。
`ANDROID_HOME` を設定するか、このフォルダの `local.properties` に `sdk.dir` を設定してください。

```sh
cd android
./gradlew :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n jp.co.keyplanning.itfescounter/.MainActivity
```

Android Studio では、この `android/` フォルダをプロジェクトとして開いて実行できます。

## 動作

- 「来場者+1」で1人加算。「リセット」で即座に0人に戻ります。
- 定員50席を基準に割合を整数表示します（1人につき2%）。50人は100%、51人から赤字の「定員オーバー」を割合の横に表示します。
- `rememberSaveable` で画面回転・システムによるActivity復元時に人数を維持します。アプリを終了して新しく開始した場合の永続保存は行いません。
- ネットワーク通信・権限・外部サービスは不要です。
- ローカルのビルド成果物、録画、SDKなどの補助ファイルはGit管理外に置きます。

## 手動確認

1. 起動時の0人・0%を確認する。
2. 「来場者+1」を5回タップし、5人・10%を確認する。
3. 50人・100%では警告なし、51人・102%で赤い警告が割合の横に出ることを確認する。
4. 「リセット」で0人・0%に戻り、警告が消えることを確認する。
