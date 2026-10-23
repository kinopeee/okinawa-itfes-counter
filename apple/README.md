# 沖縄ビジネスITフェス受付カウンター — Apple

外部ライブラリ・画像・フォントを使用しない、iPhone / macOS 共通の SwiftUI アプリです。

## Xcode で起動

1. `ReceptionCounter.xcodeproj` を Xcode で開きます。
2. Scheme を `ReceptionCounter`、実行先を iPhone シミュレーターまたは `My Mac` にします。
3. Run（⌘R）で起動します。実機 iPhone 向けの署名は Signing & Capabilities で自分の Team を指定してください。

最低対応バージョンは iOS 17 / macOS 14。数値の SwiftUI `numericText` アニメーションを利用しています。

## コマンドライン検証

このフォルダで実行します。追加パッケージは不要です。

```sh
xcodebuild -project ReceptionCounter.xcodeproj -scheme ReceptionCounter -destination 'platform=macOS' -derivedDataPath DerivedData/mac test CODE_SIGNING_ALLOWED=NO
xcodebuild -project ReceptionCounter.xcodeproj -scheme ReceptionCounter -destination 'generic/platform=iOS Simulator' -derivedDataPath DerivedData/ios build CODE_SIGNING_ALLOWED=NO
xcodebuild -project ReceptionCounter.xcodeproj -scheme ReceptionCounter -destination 'platform=macOS' -derivedDataPath DerivedData/mac analyze CODE_SIGNING_ALLOWED=NO
```

単体テストは 0→5人/10%、リセット、および39/40/50/51人の色区分・割合・バーの上限を確認します。GUI確認では各アプリを0人から開始し、「来場者+1」を5回押して「5人」「10%」を確認します。

## 仕様上の判断

- 人数は起動中のウィンドウ単位で保持し、再起動で0に戻ります。永続化・端末間同期は行いません。
- 40〜50人はオレンジ、51人から赤と「定員オーバー」。50人では警告しません。
- 50人を超えても人数と割合は増加します。バーは100%で止め、割合は102%など実数を表示します。
- リセットは確認ダイアログなしで0人に戻します。
- macOS はネイティブ SwiftUI アプリ（Mac Catalyst ではありません）。
- 白いカードと文字のコントラストを維持するためライト表示を固定。スクロール可能なレイアウトと「視差効果を減らす」に対応します。
- 配布用アイコン・App Store 署名設定は対象外です。
