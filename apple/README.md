# 沖縄ビジネスITフェス受付カウンター（Apple）

iPhone（iOS 17以上）とMac（macOS 14以上）で共通のSwiftUI画面を使います。
来場者を1人ずつ加算し、定員50席に対する割合を整数で表示します。
50人では警告せず、51人から割合の横に赤字で「定員オーバー」を表示します。
「リセット」は確認ダイアログなしで0人に戻します。
人数は画面内のメモリだけに保持し、アプリの再起動や新しいウィンドウでは0人から開始します。

## Xcodeで起動

1. `ReceptionCounter.xcodeproj` をXcodeで開きます。
2. iPhoneは `ReceptionCounter-iOS` スキームとiPhone Simulatorを選び、Runします。
3. Macは `ReceptionCounter-macOS` スキームとMy Macを選び、Runします。

実機へのインストール時には、自分のDevelopment Teamを選択してください。
シミュレーターおよびローカルMacでの検証に配布用署名は不要です。

## コマンドライン検証

リポジトリルートで実行します（Xcodeが必要）。

```sh
xcodebuild -project apple/ReceptionCounter.xcodeproj -scheme ReceptionCounter-iOS -sdk iphonesimulator -destination 'generic/platform=iOS Simulator' -derivedDataPath apple/build/ios CODE_SIGNING_ALLOWED=NO build
xcodebuild -project apple/ReceptionCounter.xcodeproj -scheme ReceptionCounter-macOS -destination 'platform=macOS' -derivedDataPath apple/build/macos CODE_SIGNING_ALLOWED=NO test
```

生成済みXcodeプロジェクトを同梱しているため、通常のビルドにXcodeGenは不要です。
ターゲット構成を変更する場合は `project.yml` を編集し、`cd apple && xcodegen generate` で再生成してください。
ビルド結果は `apple/build/`、検証用の動画や画像は `apple/artifacts/` に置き、Gitには追加しません。

## 手動確認

- 起動時に0人・0%。「来場者+1」を5回押して5人・10%。
- 50人・100%では警告なし、51人・102%で赤い「定員オーバー」。
- 「リセット」で0人・0%に戻り、警告が消える。

以上をiPhone SimulatorとmacOSアプリの両方で確認します。
