# 沖縄ビジネスITフェス受付カウンター — Apple

外部依存のない SwiftUI アプリ。共通の画面・状態を macOS と iPhone で使用します。

## 起動

- Xcode 15 以降で `OkinawaCounter.xcodeproj` を開き、`OkinawaCounter` スキームを選択。
- 実行先を My Mac または iPhone シミュレーターにして Run。
- 対象: macOS 14 以降 / iOS 17 以降。実機に入れる場合は開発チームの署名設定が必要です。

リポジトリのルートから署名なしでビルドする場合:

```sh
xcodebuild -project apple/OkinawaCounter.xcodeproj -scheme OkinawaCounter -configuration Debug -destination 'platform=macOS' -derivedDataPath apple/.build/mac CODE_SIGNING_ALLOWED=NO build
xcodebuild -project apple/OkinawaCounter.xcodeproj -scheme OkinawaCounter -configuration Debug -sdk iphonesimulator -destination 'generic/platform=iOS Simulator' -derivedDataPath apple/.build/ios CODE_SIGNING_ALLOWED=NO build
```

## 状態のテスト

```sh
mkdir -p apple/.build
xcrun swiftc apple/OkinawaCounter/CounterState.swift apple/Tests/CounterStateTests.swift -o apple/.build/counter-tests
apple/.build/counter-tests
```

0・5・39・40・50・51人、割合、色の状態、バーの上限、リセットを検証します。
各プラットフォームの画面では0人から「来場者+1」を5回押して「5人」「10%」を確認します。

## 仕様上の判断

- 人数はウィンドウごとの一時的な状態です。アプリを再起動すると0人に戻り、端末間同期・永続化はしません。
- 40〜50人はオレンジ、51人以上は赤で「定員オーバー」を割合の横に表示します。
- 割合は100%を超えて表示し、プログレスバーのみ100%で止めます。
- 0人からリセット可能。確認ダイアログは設けず、直ちに0人に戻します。
- 数字とバーは短いアニメーションで変化します。「視差効果を減らす」設定ではアニメーションしません。
- 小さい画面や大きい文字では縦スクロールでき、白いカードはダークモードでも維持します。
- 画像・フォント・ライブラリは追加せず、システムフォントと標準 SF Symbols のみ使用します。
