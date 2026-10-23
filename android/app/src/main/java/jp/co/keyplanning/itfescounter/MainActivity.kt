package jp.co.keyplanning.itfescounter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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

private val Ocean = Color(0xFF073B68)
private val Emerald = Color(0xFF008C80)
private val Green = Color(0xFF16824B)
private val Orange = Color(0xFFB85A00)
private val Red = Color(0xFFBD2635)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )
        setContent {
            MaterialTheme(colorScheme = lightColorScheme(primary = Emerald)) {
                ReceptionCounter()
            }
        }
    }
}

@Composable
fun ReceptionCounter() {
    var visitors by rememberSaveable { mutableIntStateOf(0) }
    val percentage = visitors.toLong() * 100 / 50
    val progress by animateFloatAsState(
        targetValue = (visitors / 50f).coerceIn(0f, 1f),
        animationSpec = tween(250),
        label = "capacity progress",
    )
    val statusColor by animateColorAsState(
        targetValue = when {
            visitors > 50 -> Red
            visitors >= 40 -> Orange
            else -> Green
        },
        label = "capacity color",
    )

    Box(
        modifier = Modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Ocean, Emerald)),
        ).safeDrawingPadding(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.widthIn(max = 480.dp).fillMaxWidth()
                .verticalScroll(rememberScrollState()).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                stringResource(R.string.event_name), color = Color.White,
                fontSize = 20.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center,
            )
            Text(
                stringResource(R.string.counter_title), color = Color.White,
                fontSize = 16.sp, modifier = Modifier.padding(top = 6.dp, bottom = 28.dp),
            )
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(stringResource(R.string.visitors_label), color = Ocean, fontSize = 15.sp)
                    AnimatedContent(
                        targetState = visitors,
                        transitionSpec = {
                            (fadeIn(tween(180)) + scaleIn(tween(180), initialScale = 0.94f))
                                .togetherWith(fadeOut(tween(100)))
                        },
                        modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                        label = "visitor count",
                    ) { count ->
                        Text(
                            stringResource(R.string.visitors, count), color = Ocean,
                            fontSize = 64.sp, fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center, modifier = Modifier.padding(vertical = 16.dp),
                        )
                    }
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            stringResource(R.string.percentage, percentage), color = statusColor,
                            fontSize = 22.sp, fontWeight = FontWeight.Bold,
                        )
                        if (visitors > 50) {
                            Text(
                                stringResource(R.string.over_capacity), color = Red,
                                fontSize = 18.sp, fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                    LinearProgressIndicator(
                        progress = { progress }, color = statusColor,
                        trackColor = Color(0xFFE5EFED),
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp).height(10.dp),
                    )
                    Text(
                        stringResource(R.string.capacity), color = Color(0xFF526777),
                        fontSize = 14.sp, modifier = Modifier.padding(top = 10.dp),
                    )
                    Spacer(Modifier.height(28.dp))
                    Button(
                        onClick = { if (visitors < Int.MAX_VALUE) visitors++ },
                        modifier = Modifier.fillMaxWidth().height(60.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Emerald),
                    ) {
                        Text(stringResource(R.string.add_visitor), fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = { visitors = 0 },
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp).height(50.dp),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Text(stringResource(R.string.reset), color = Ocean, fontSize = 16.sp)
                    }
                }
            }
            Text(
                stringResource(R.string.welcome), color = Color.White,
                fontSize = 13.sp, modifier = Modifier.padding(top = 28.dp),
            )
        }
    }
}
