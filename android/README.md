# 沖縄ビジネスITフェス受付カウンター（Android）

Kotlin / Jetpack Compose と Android / AndroidX の標準コンポーネントのみを使う受付アプリ。
外部の画像・フォント・サードパーティーUIライブラリは使用しません。

## ビルド

- JDK 17、Android SDK Platform 35、Build Tools 35.0.0
- Gradle 8.11.1（同梱の Gradle Wrapper を使用）
- Android 8.0（API 26）以上の端末またはエミュレーター
- `ANDROID_HOME` を SDK のディレクトリに設定するか、`local.properties` に `sdk.dir` を設定

このフォルダで実行します。

```sh
./gradlew :app:assembleDebug :app:lintDebug :app:assembleDebugAndroidTest
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n jp.co.keyplanning.itfescounter/.MainActivity
# 起動済みのエミュレーターでUI回帰テスト
./gradlew :app:connectedDebugAndroidTest
```

## 動作

- 「来場者+1」を5回押すと「5人」「10%」。リセットで「0人」「0%」。
- 40人からオレンジ、51人から赤に変わり「定員オーバー」を割合の横に表示。
- 割合は100%を超えて表示し、プログレスバーは100%で止まります。
- 数字・進捗・色の変化に標準Composeアニメーションを使用。
- 画面回転・システムによるActivity再生成には人数を保持します。明示終了後の永続保存はしません。
- 小さい画面・横画面はスクロール可能です。フォントは端末の標準フォントです。

自動UIテストは5回追加、リセット、Activity再生成、40/50/51人の割合・警告を確認します。
実際のエミュレーター操作の録画・スクリーンショットはPRに掲載します。
