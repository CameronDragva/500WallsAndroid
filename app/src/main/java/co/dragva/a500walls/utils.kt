package co.dragva.a500walls

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun TitleBuilder(mainText: String) {
    return Box(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = mainText,
            color = Color(0xff0b2b09),
            modifier = Modifier.padding(start = 15.dp, bottom = 10.dp, top = 15.dp),
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun BoxBuilder(mainText: String, subline: String, clickFunction: () -> (Unit), drawDivider: Boolean = true) {
    return Box(modifier = Modifier
        .clickable { clickFunction() }
        .fillMaxWidth()) {
        Text(text = mainText, modifier = Modifier.padding(top = 5.dp, bottom = 5.dp, start = 15.dp))
        Text(
            text = subline,
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 10.dp, top = 30.dp, start = 15.dp),
            color = Color.Gray
        )
        if(drawDivider)
            Divider(modifier = Modifier.padding(top = 55.dp))
    }
}

@SuppressLint("ApplySharedPref")
@Composable
fun SliderDialog() {
    var sliderPosition by remember { mutableFloatStateOf(currentValue.toFloat()) }
    Dialog(onDismissRequest = {
        showSliderDialog.value = false
    }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
            ) {
                Text(
                    text = sliderLabel,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.Center),
                    textAlign = TextAlign.Center,
                )
                var sliderLabel = ""
                if (sliderType == "images")
                    sliderLabel = "${sliderPosition.toInt()} images"

                Text(
                    text = sliderLabel,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.Center),
                    textAlign = TextAlign.Center,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Slider(
                    value = sliderPosition,
                    onValueChange = { sliderPosition = it },
                    valueRange = minSlider.toFloat()..maxSlider.toFloat(),
                )
                Row {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TextButton(onClick = {
                            showSliderDialog.value = false
                            Log.d("Dialog", "Cancel Clicked")
                        }) {
                            Text(text = "Cancel")
                        }
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TextButton(onClick = {
                            prefs.edit().putInt(paramName, sliderPosition.toInt()).commit()
                            Log.d("Dialog", "Save Clicked")
                            Log.d("Dialog", prefs.getInt(paramName, -1).toString())
                            showSliderDialog.value = false
                            reDraw.value = true
                        }) {
                            Text(text = "Save")
                        }
                    }

                }
            }
        }
    }
}