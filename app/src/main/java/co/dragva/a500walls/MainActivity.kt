package co.dragva.a500walls

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import co.dragva.a500walls.ui.theme._500WallsTheme

lateinit var prefs: SharedPreferences
lateinit var showSliderDialog: MutableState<Boolean>
lateinit var reDraw: MutableState<Boolean>

var minSlider = 8
var maxSlider = 32
var currentValue = 16
var paramName = ""
var sliderLabel = ""
var sliderType = ""

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = getSharedPreferences("com.dragva.its500walls", MODE_PRIVATE)


        if (prefs.getBoolean("firstRun", true)) {
            val prefsEditor = prefs.edit()
            prefsEditor.putInt("minImages", 8)
            prefsEditor.putInt("maxImages", 128)
            prefsEditor.putInt("updateInt", 30)
            prefsEditor.putBoolean("firstRun", false)
            prefsEditor.apply()
        }

        setContent {
            _500WallsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Setup()
                    BuildUI(true)
                }
            }
        }
    }
}

@Composable
fun Setup() {
    reDraw = remember { mutableStateOf(false) }

    if (reDraw.value) {
        reDraw.value = false
        BuildUI(true)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuildUI(render: Boolean = false) {
    showSliderDialog = remember { mutableStateOf(false) }
    if (showSliderDialog.value)
        SliderDialog()

    Scaffold(
        topBar = {
            TopAppBar(
                colors = topAppBarColors(
                    containerColor = Color(0xff0b2b09),
                    titleContentColor = Color.White
                ),
                title = {
                    Text("500 Walls")
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
        ) {
            var intervalSubline = "interval subline"
            var minSubline = "min subline"
            var maxSubline = "max subline"
            var brightnessSubline = "brightness subline"
            var blurSubline = "blur subline"

            if (render) {
                Log.d("UI Draw", "Getting Prefs")
                intervalSubline = prefs.getInt("updateInt", 30).toString() + " minutes"
                minSubline = prefs.getInt("minImages", 8).toString() + " images"
                maxSubline = prefs.getInt("maxImages", 128).toString() + " images"
                brightnessSubline = prefs.getInt("brightness", 75).toString() + "%"
                blurSubline = prefs.getInt("blur", 0).toString() + "%"
            }

            Row {
                TitleBuilder(mainText = "App Settings")
            }
            Row {
                BoxBuilder(mainText = "Update Interval",
                    subline = intervalSubline,
                    clickFunction = { updateIntervalClick() })
            }
            Row {
                BoxBuilder(mainText = "Min Number of Images",
                    subline = minSubline,
                    clickFunction = {
                        minImagesClick()
                        showSliderDialog.value = true
                    })
            }
            Row {
                BoxBuilder(mainText = "Max Number of Images",
                    subline = maxSubline,
                    clickFunction = {
                        maxImagesClick()
                        showSliderDialog.value = true
                    })
            }
            Row {
                BoxBuilder(mainText = "Categories", subline = "subline", ::categoriesClick)
            }
            Row {
                BoxBuilder(mainText = "Background Configurator",
                    subline = "Click this to configure the background + home screen",
                    clickFunction = { setBackground() })
            }

            Row {
                TitleBuilder(mainText = "Image Settings")
            }
            Row {
                BoxBuilder(
                    mainText = "Brightness",
                    subline = brightnessSubline,
                    clickFunction = { setBrightness() })
            }
            Row {
                BoxBuilder(mainText = "Blur",
                    subline = blurSubline,
                    clickFunction = { setBlur() })
            }

        }
    }
}

fun minImagesClick() {
    minSlider = 1
    maxSlider - 32
    currentValue = prefs.getInt("minImages", 8)
    paramName = "minImages"
    sliderLabel = "Minimum number of images to keep"
    sliderType = "images"
}

fun maxImagesClick() {
    minSlider = 32
    maxSlider = 512
    currentValue = prefs.getInt("maxImages", 128)
    paramName = "maxImages"
    sliderLabel = "Max number of images to keep"
    sliderType = "images"
}

fun updateIntervalClick() {
    Log.d("interval", "Interval clicked")
}

fun categoriesClick() {
    Log.d("categories", "Categories clicked")
}

fun setBackground() {
    Log.d("background", "background set clicked")
}

fun setBrightness() {

}

fun setBlur() {

}

@Preview(showBackground = true)
@Composable
fun WallsPreview() {
    _500WallsTheme {
        //BuildUI()
        BuildUI()
    }
}