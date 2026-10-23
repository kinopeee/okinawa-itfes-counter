# 沖縄ビジネスITフェス受付カウンター (Windows)

Windows 10/11 または Windows Server 2022 と .NET 10 SDK が必要です。
WPF と .NET の標準機能のみを使用し、外部ライブラリ・画像・フォントは含みません。

```powershell
dotnet build windows/OkinawaCounter.csproj -c Release
dotnet run --project windows/OkinawaCounter.csproj -c Release
dotnet run --project windows/tests/CounterState.Tests.csproj -c Release
dotnet format windows/OkinawaCounter.csproj --verify-no-changes
```

- 0〜39人は緑、40〜50人はオレンジ、51人以上は赤。
- 50人を超えても人数と割合は増加し、バーのみ100%で止まります。
- 人数は起動中だけ保持し、再起動時は0人です。リセットは確認ダイアログなしで即時反映します。
- Windowsのアニメーション設定が有効な場合、数字変更時に短い拡大アニメーションを表示します。
- Tabでボタン間を移動し、SpaceまたはEnterでも操作できます。

`tests` は追加パッケージ不要のコンソール型回帰テストです。失敗時は非ゼロで終了します。
