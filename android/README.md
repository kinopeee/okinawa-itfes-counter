# Android 受付カウンター

Kotlin / Jetpack Compose / Android SDK のみを使う、通信しない受付カウンターと来場者アンケートです。
共通仕様は仕様コミット `40b410d` の README を参照しています。

## ビルド・検証

- JDK 17、Android SDK Platform 35 / Build Tools 35.0.0、Android SDK Platform Tools
- Gradle Wrapper を同梱。`ANDROID_HOME` を設定するか、未追跡の `local.properties` で SDK を指定してください。
- Android 8.0 (API 26) 以上。エミュレーターは API 35 を推奨します。
- Linux では `./scripts/setup-linux.sh` で JDK 17 と Android SDK の必要なパッケージを準備できます。

```sh
cd android
./gradlew :app:testDebugUnitTest :app:assembleDebug :app:lintDebug
# エミュレーターを起動した状態で、実際のAndroid JSON/ファイルAPIを検証
./gradlew :app:connectedDebugAndroidTest
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n jp.co.keyplanning.okinawaitfes/.MainActivity
```

### ビルドツールの依存固定

AGP 8.9.2 が生成する `_internal-unified-test-platform-*` のホスト・テスト用設定だけに、既知の問題を修正した Guava 33.7.2、Protobuf 3.25.5、Netty 4.1.138、Kotlin stdlib 2.1.20 を適用しています。アプリの実行時依存や AndroidX Test APK の依存は変更しません。

## 設計上の既定値

- カウンターは画面移動・画面回転では保持し、アプリのプロセス終了後は0に戻します。回答だけを永続化します。
- 満席の50人はオレンジ、51人から赤。プログレスバーは100%で止め、数字の割合は100%を超えて表示します。
- 保存中はフォーム・戻る操作を止め、連打を拒否します。成功時のみフォームを初期化し、集計を更新します。
- `filesDir/survey-responses.json` をUTF-8で保存します。一時ファイルを同じディレクトリに書き、`fsync` 後に `Os.rename` で原子的に置換します。置換失敗はエラーとして伝えます。
- 読み込みは厳密です。必須フィールド・型・値・UUID・UTC時刻・重複ID/キー・未知のフィールド・余分なJSONを検証し、不正なデータを空扱いにしません。保存前にもファイルを再検証します。
- 読み込みエラーは「読み直す」、保存エラーは入力を保持して「回答を保存」で再試行できます。修復・削除・編集・同期機能はありません。
- Androidのバックアップを無効にし、インターネット権限を要求しません。外部UI・グラフ・保存ライブラリや画像・フォントは使用しません。JUnit / AndroidX Test はテスト専用です。

## 実UI確認手順

1. 新規インストールまたはアプリデータ消去後、集計の初回メッセージと全選択肢0件を確認。
2. 来場者+1を5回押して5人・10%。39/40/50/51人で色と警告を確認。
3. アンケート未選択では保存不可。途中入力後に戻り、再度開くと未選択。
4. 異なる回答を2件保存し、成功表示・フォーム初期化・集計増分と全選択肢0件を確認。
5. カウンター値が変わらないこと、リセットしても回答が残ることを確認。
6. アプリを強制終了・再起動し、回答2件と内訳の保持を確認。
7. 小画面でも縦スクロールですべての選択肢・保存・戻るに到達できることを確認。

自動テストは実UI確認の代わりにはしません。録画・スクリーンショットはPRを参照してください。
