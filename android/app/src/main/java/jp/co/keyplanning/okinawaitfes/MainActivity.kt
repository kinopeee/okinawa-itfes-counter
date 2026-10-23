package jp.co.keyplanning.okinawaitfes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider

private val Ocean = Color(0xFF006D77)
private val Ink = Color(0xFF17394D)
private val Muted = Color(0xFF536B78)
private val Green = Color(0xFF167346)
private val Orange = Color(0xFFB75C00)
private val Red = Color(0xFFB3261E)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val model = ViewModelProvider(this)[CounterViewModel::class.java]
        setContent {
            MaterialTheme(colorScheme = lightColorScheme(primary = Ocean, onPrimary = Color.White,
                surface = Color.White, onSurface = Ink, error = Red)) {
                CounterApp(model)
            }
        }
    }
}

@Composable
private fun CounterApp(model: CounterViewModel) {
    BackHandler(enabled = model.screen != Screen.COUNTER) { model.navigate(Screen.COUNTER) }
    Box(
        Modifier.fillMaxSize().background(Brush.verticalGradient(
            listOf(Color(0xFF073E68), Color(0xFF087C91), Color(0xFF26BAA3)),
        )).windowInsetsPadding(WindowInsets.safeDrawing),
        contentAlignment = Alignment.Center,
    ) {
        key(model.screen) {
            Column(
                Modifier.widthIn(max = 580.dp).fillMaxWidth().verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("OKINAWA  /  BUSINESS IT FES", color = Color(0xFFB7EEE6),
                        fontSize = 11.sp, letterSpacing = 2.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text("沖縄ビジネスITフェス", color = Color.White, fontSize = 20.sp,
                        fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    Text("受付カウンター", color = Color.White, fontSize = 16.sp)
                }
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(28.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        if (model.screen != Screen.COUNTER) {
                            TextButton(onClick = { model.navigate(Screen.COUNTER) }, enabled = !model.saving) {
                                Text("カウンターに戻る")
                            }
                        }
                        if (model.loading) Notice("回答データを読み込み中…")
                        if (model.loadError) {
                            Notice("回答データを読み込めません。破損・未対応の形式、または読み込みエラーです。読み直せるまで保存できません。既存データは変更していません。", error = true)
                            OutlinedButton(onClick = model::reload, enabled = !model.loading) { Text("読み直す") }
                        }
                        when (model.screen) {
                            Screen.COUNTER -> Counter(model)
                            Screen.SURVEY -> Survey(model)
                            Screen.RESULTS -> Results(model)
                        }
                    }
                }
                Text("沖縄の出会いを、ひとつずつ。", color = Color.White, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun ColumnScope.Counter(model: CounterViewModel) {
    val color by animateColorAsState(when (capacity(model.count)) {
        Capacity.NORMAL -> Green
        Capacity.WARNING -> Orange
        Capacity.OVER -> Red
    }, label = "capacity")
    val progress by animateFloatAsState((model.count / 50f).coerceAtMost(1f), label = "progress")
    Text("現在の来場者数", color = Muted, modifier = Modifier.align(Alignment.CenterHorizontally))
    AnimatedContent(targetState = model.count, label = "visitors",
        modifier = Modifier.align(Alignment.CenterHorizontally)) { count ->
        Text("${count}人", fontSize = if (count < 10000) 64.sp else 36.sp,
            fontWeight = FontWeight.ExtraBold, color = Ink)
    }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically) {
        Text("${percentage(model.count)}%", color = color, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        if (model.count > 50) Text("  定員オーバー", color = Red, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
    LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth().height(10.dp),
        color = color, trackColor = Color(0xFFE8F0F1))
    Text("定員50席", color = Muted, fontSize = 13.sp,
        modifier = Modifier.align(Alignment.CenterHorizontally))
    Button(onClick = model::increment, modifier = Modifier.fillMaxWidth().heightIn(min = 60.dp),
        shape = RoundedCornerShape(16.dp)) { Text("来場者+1", fontSize = 20.sp, fontWeight = FontWeight.Bold) }
    OutlinedButton(onClick = model::reset, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
        shape = RoundedCornerShape(16.dp)) { Text("リセット") }
    HorizontalDivider(color = Color(0xFFE6ECEF))
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        TextButton(onClick = { model.navigate(Screen.SURVEY) }) { Text("アンケート") }
        TextButton(onClick = { model.navigate(Screen.RESULTS) }) { Text("集計") }
    }
    Text("リセットは来場者数だけを0に戻します。", fontSize = 12.sp, color = Muted)
}

@Composable
private fun Survey(model: CounterViewModel) {
    Heading("来場者アンケート")
    Text("3問すべて必須・各1つ選択\nお名前や連絡先は収集しません。", fontSize = 14.sp, color = Muted)
    if (model.saved) Notice("回答を保存しました")
    Question("01  満足度", Questions.satisfaction, model.satisfaction, !model.saving) { model.select(0, it) }
    Question("02  来場のきっかけ", Questions.discovery, model.discovery, !model.saving) { model.select(1, it) }
    Question("03  年代", Questions.age, model.age, !model.saving) { model.select(2, it) }
    if (model.saveError) Notice("保存できませんでした。入力と既存データは保持しています。「回答を保存」で再試行してください。", error = true)
    if (model.saving) Notice("保存中です。しばらくお待ちください。")
    else if (!model.canSave) Text("全3問を選択し、回答データを読み込むと保存できます。", fontSize = 13.sp, color = Muted)
    Button(onClick = model::save, enabled = model.canSave,
        modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp), shape = RoundedCornerShape(16.dp)) {
        Text(if (model.saving) "保存中…" else "回答を保存", fontSize = 18.sp)
    }
    if (model.saved) Notice("回答を保存しました")
    TextButton(onClick = { model.navigate(Screen.COUNTER) }, enabled = !model.saving,
        modifier = Modifier.fillMaxWidth()) { Text("カウンターに戻る") }
    Text("回答はこの端末内だけに保存されます。", fontSize = 12.sp, color = Muted)
}

@Composable
private fun Question(title: String, choices: List<Choice>, selected: String?, enabled: Boolean, select: (String) -> Unit) {
    Column(Modifier.selectableGroup(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(title, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp).semantics { heading() })
        choices.forEach { choice ->
            val checked = selected == choice.value
            Row(Modifier.fillMaxWidth().background(if (checked) Color(0xFFE0F3EF) else Color(0xFFF5F8FA),
                RoundedCornerShape(12.dp)).selectable(selected = checked, enabled = enabled, role = Role.RadioButton,
                onClick = { select(choice.value) }).heightIn(min = 48.dp).padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = checked, onClick = null, enabled = enabled)
                Text(choice.label, modifier = Modifier.padding(start = 12.dp), fontSize = 15.sp)
            }
        }
    }
}

@Composable
private fun Results(model: CounterViewModel) {
    Heading("アンケート集計")
    if (model.loading || model.loadError) {
        Text("回答データを読み込むと集計を表示します。", color = Muted)
        return
    }
    Text("総回答数 ${model.responses.size}件", fontSize = 28.sp, fontWeight = FontWeight.Bold)
    if (model.responses.isEmpty()) Notice("回答はまだありません")
    Summary("満足度", Questions.satisfaction, counts(model.responses, Questions.satisfaction) { it.satisfaction.toString() })
    Summary("来場のきっかけ", Questions.discovery, counts(model.responses, Questions.discovery) { it.discovery })
    Summary("年代", Questions.age, counts(model.responses, Questions.age) { it.ageGroup })
    TextButton(onClick = { model.navigate(Screen.COUNTER) }, modifier = Modifier.fillMaxWidth()) { Text("カウンターに戻る") }
}

@Composable
private fun Summary(title: String, choices: List<Choice>, totals: List<Int>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        HorizontalDivider(color = Color(0xFFE6ECEF))
        Text(title, fontWeight = FontWeight.Bold, modifier = Modifier.semantics { heading() })
        choices.forEachIndexed { index, choice ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(choice.label, modifier = Modifier.weight(1f), fontSize = 14.sp)
                Text("${totals[index]}件", fontWeight = FontWeight.Bold, color = Ocean)
            }
        }
    }
}

@Composable
private fun Heading(text: String) {
    Text(text, fontSize = 23.sp, fontWeight = FontWeight.Bold, modifier = Modifier.semantics { heading() })
}

@Composable
private fun Notice(text: String, error: Boolean = false) {
    Text(text, color = if (error) Red else Ocean, fontSize = 14.sp,
        modifier = Modifier.fillMaxWidth().background(if (error) Color(0xFFFCEAE8) else Color(0xFFE0F3EF),
            RoundedCornerShape(12.dp)).padding(12.dp).semantics { liveRegion = LiveRegionMode.Polite })
}
