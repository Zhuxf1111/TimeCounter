/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androiddevchallenge.ui.theme.MyTheme
import java.text.DecimalFormat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyTheme {
                MyApp()
            }
        }
    }
}

private const val TOTAL_TIME = 5 * 60 * 1000L
// Start building your app here!
@Composable
fun MyApp() {
    val totalTime: Long by rememberSaveable { mutableStateOf(TOTAL_TIME) }
    var remainTime: Long by rememberSaveable { mutableStateOf(TOTAL_TIME) }
    var countDownTimer: CountDownTimer? by rememberSaveable { mutableStateOf(null) }
    Surface {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(start = 18.dp, end = 18.dp, top = 52.dp, bottom = 52.dp)
        ) {
            Counter(
                modifier = Modifier
                    .shadow(5.dp, shape = RoundedCornerShape(corner = CornerSize(22.dp)), true)
                    .background(color = Color.White)
                    .fillMaxWidth()
                    .aspectRatio(ratio = 1f),
                total = totalTime, remain = remainTime
            )
            Row(
                modifier = Modifier
                    .padding(top = 79.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ActionButton(
                    painterId = R.drawable.icon_reset,
                    onClick = {
                        if (countDownTimer != null) {
                            countDownTimer?.cancel()
                        }
                        countDownTimer = null
                        remainTime = TOTAL_TIME
                    }
                )
                ActionButton(
                    painterId = if (countDownTimer == null) { R.drawable.icon_play } else { R.drawable.icon_pause },
                    onClick = {
                        if (countDownTimer == null) {
                            countDownTimer = object : CountDownTimer(remainTime, 100) {
                                override fun onTick(millisUntilFinished: Long) {
                                    remainTime = millisUntilFinished
                                }

                                override fun onFinish() {
                                    remainTime = 0
                                    countDownTimer = null
                                }
                            }
                            countDownTimer?.start()
                        } else {
                            countDownTimer?.cancel()
                            countDownTimer = null
                        }
                    }
                )
            }
        }
    }
}

private val greyTextStyle: SpanStyle = SpanStyle(fontWeight = FontWeight.Normal, fontSize = 40.sp, color = Color.Gray)

private val df = DecimalFormat("00")

@Composable
fun Counter(modifier: Modifier, total: Long, remain: Long) {
    var brush: Brush? by remember { mutableStateOf(null) }
    var path: Path by remember { mutableStateOf(Path()) }
    val mins = remain / 60000
    val second = remain % 60000 / 1000
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(
            modifier = Modifier
                .padding(horizontal = 28.dp)
                .onSizeChanged {
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFFB0FF00), Color(0xFFFF7E00)),
                        start = Offset(Float.POSITIVE_INFINITY, 0f),
                        end = Offset(0f, Float.POSITIVE_INFINITY)
                    )
                }
                .fillMaxWidth()
                .aspectRatio(ratio = 1f),
            onDraw = {
//                drawCircle(brush = brush!!, style = Stroke(width = 8.dp.value))
                val startDegree = -90f + 360f * (total - remain) / total
                path.reset()
                path.addArc(
                    oval = Rect(
                        offset = Offset.Zero,
                        size = Size(width = size.minDimension, height = size.minDimension)
                    ),
                    startAngleDegrees = startDegree,
                    sweepAngleDegrees = 360f - startDegree - 90f
                )
                drawPath(path = path, brush = brush!!, style = Stroke(width = 8.dp.value))
            }
        )
        Text(
            buildAnnotatedString {
                append("$mins")
                withStyle(style = greyTextStyle) {
                    append("m")
                }
                append(":${df.format(second)}")
                withStyle(style = greyTextStyle) {
                    append("s")
                }
            },
            fontFamily = FontFamily.Default, fontSize = 60.sp, color = Color.Black, fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun ActionButton(modifier: Modifier = Modifier, painterId: Int, onClick: () -> Unit) {
    Button(
        colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
        contentPadding = PaddingValues(0.dp),
        modifier = modifier
            .wrapContentWidth()
            .wrapContentHeight()
            .shadow(5.dp, shape = RoundedCornerShape(15.dp), true)
            .padding(0.dp),
        onClick = onClick
    ) {
        Image(
            modifier = Modifier.size(65.dp, 65.dp),
            painter = painterResource(id = painterId),
            contentDescription = null
        )
    }
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp()
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    MyTheme(darkTheme = true) {
        MyApp()
    }
}
