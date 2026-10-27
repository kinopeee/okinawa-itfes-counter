package jp.co.keyplanning.okinawacounter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Ocean = Color(0xFF073965)
private val Emerald = Color(0xFF12B99A)
private val Ink = Color(0xFF123650)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme(colorScheme = lightColorScheme(primary = Color(0xFF007E92))) {
                CounterScreen()
            }
        }
    }
}

@Composable
private fun CounterScreen() {
    var visitors by rememberSaveable { mutableIntStateOf(0) }
    val attendance = Attendance(visitors)
    val capacityColor = when (attendance.level) {
        CapacityLevel.NORMAL -> Color(0xFF198754)
        CapacityLevel.WARNING -> Color(0xFFB65C00)
        CapacityLevel.OVER -> Color(0xFFC62828)
    }
    val progress by animateFloatAsState(attendance.progress, tween(250), label = "capacity")

    Box(
        Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Ocean, Emerald)))
            .safeDrawingPadding(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("WELCOME TO OKINAWA", color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp, letterSpacing = 2.sp)
            Spacer(Modifier.height(12.dp))
            Text(stringResource(R.string.app_name), color = Color.White, fontSize = 24.sp,
                fontWeight = FontWeight.Bold, textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(max = 440.dp))
            Spacer(Modifier.height(28.dp))
            Card(
                modifier = Modifier.widthIn(max = 440.dp).fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 14.dp),
            ) {
                Column(
                    Modifier.padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("現在の来場者数", color = Ink.copy(alpha = 0.7f), fontSize = 16.sp)
                    Spacer(Modifier.height(8.dp))
                    AnimatedContent(
                        targetState = visitors,
                        transitionSpec = {
                            (fadeIn(tween(180)) + slideInVertically { it / 8 }) togetherWith fadeOut(tween(120))
                        },
                        label = "visitors",
                        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                    ) { count ->
                        Text("${count}人", fontSize = 68.sp, fontWeight = FontWeight.ExtraBold,
                            color = Ink, textAlign = TextAlign.Center)
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("${attendance.percent}%", color = capacityColor, fontSize = 28.sp,
                            fontWeight = FontWeight.Bold)
                        if (attendance.level == CapacityLevel.OVER) {
                            Spacer(Modifier.width(12.dp))
                            Text("定員オーバー", color = Color(0xFFC62828), fontSize = 16.sp,
                                fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(12.dp),
                        color = capacityColor,
                        trackColor = Color(0xFFE8F2EF),
                    )
                    Spacer(Modifier.height(12.dp))
                    Text("定員50席", color = Ink.copy(alpha = 0.65f), fontSize = 14.sp)
                    Spacer(Modifier.height(28.dp))
                    Button(
                        onClick = { visitors = attendance.increment().visitors },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 60.dp),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Text("来場者+1", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = { visitors = attendance.reset().visitors },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Text("リセット", fontSize = 16.sp)
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
            Text("沖縄の出会いを、ひとつずつ。", color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp)
        }
    }
}
