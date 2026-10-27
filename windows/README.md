# 沖縄ビジネスITフェス受付カウンター — Windows

Windows + .NET 10 SDKで実行する、外部パッケージ不要のWPFアプリです。

```powershell
dotnet build windows/OkinawaCounter.csproj -c Release -warnaserror
dotnet run --project windows/OkinawaCounter.csproj -c Release
```

SDKがユーザーフォルダにある場合は、`dotnet` の代わりに `& "$HOME/.dotnet/dotnet.exe"` を使用してください。
生成したexeを直接起動する場合、その環境では `$env:DOTNET_ROOT="$HOME/.dotnet"` を設定してください。

## 検証

```powershell
dotnet run --project windows/tests/CounterState.Tests.csproj -c Release
dotnet format windows/OkinawaCounter.csproj --verify-no-changes
```

実画面で「来場者+1」を5回押すと「5人」「10%」になります。
39人までは緑、40〜50人はオレンジ、51人以上は赤で「定員オーバー」を表示します。
割合は100%を超えて表示し、プログレスバーは満杯で止まります。
「リセット」で0人へ戻ります。再起動時も0人で開始し、人数は保存しません。
数値のアニメーションはWindowsのアニメーション設定に従います。

テストは.NET標準機能のみのコンソール形式で、失敗すると非ゼロ終了します。
