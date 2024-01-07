@file:OptIn(DelicateCoroutinesApi::class)

package co.dragva.a500walls

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.lifecycle.Lifecycle.*
import co.dragva.a500walls.ui.theme._500WallsTheme
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

lateinit var prefs: SharedPreferences
lateinit var showSliderDialog: MutableState<Boolean>
lateinit var showRadioDialog: MutableState<Boolean>
lateinit var reDraw: MutableState<Boolean>
lateinit var appFiles: File
lateinit var radioOptionsInt: List<Int>
lateinit var radioOptionsString: List<String>
private lateinit var appContext: Context
lateinit var classContext: Context
lateinit var activity: Activity

lateinit var userCategories: List<ImageCategory>
lateinit var defaultCategories: List<ImageCategory>

lateinit var startForResult: ActivityResultLauncher<Intent>

var minSlider = 8
var maxSlider = 32
var currentValue = 16
var paramName = ""
var sliderLabel = ""
var sliderType = ""

var radioDialogLabel = ""
var radioDialogType = ""

const val CATEGORIES_ACTIVITY = 1

class MainActivity : ComponentActivity() {
    @SuppressLint("ApplySharedPref")
    fun clean() {
        val filesDir = File("${application.filesDir}/images/")
        val images = filesDir.listFiles()

        images?.forEach {
            it.delete()
        }
        filesDir.delete()

        prefs.edit().clear().commit()
        getDatabasePath("500Walls").delete()
    }

    //app-files: /data/user/0/co.dragva.a500walls/files
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = getSharedPreferences("com.dragva.its500walls", MODE_PRIVATE)
        appFiles = File("${application.filesDir}/images/")
        initDB(applicationContext)
        appContext = applicationContext
        classContext = this
        activity = this

        //clean()

        if (!appFiles.exists()) appFiles.mkdir()

        if (prefs.getBoolean("firstRun", true)) {
            prefs.edit().putBoolean("firstRun", false).apply()
            GlobalScope.launch { addDefaultCategories() }
        }

        GlobalScope.launch {
            userCategories = categoryDao.getUserAddedCategories()
            defaultCategories = categoryDao.getDefaultCategories()
        }

        startForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                categorySubline.value = ""
            }

        setContent {
            _500WallsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = Color.White
                ) {
                    Setup()
                    BuildUI(true)
                }
            }
        }
    }
}

@Composable
private fun Setup() {
    reDraw = remember { mutableStateOf(false) }

    if (reDraw.value) {
        reDraw.value = false
        BuildUI(true)
    }
}

lateinit var intervalSubline: MutableState<String>
lateinit var minSubline: MutableState<String>
lateinit var maxSubline: MutableState<String>
lateinit var brightnessSubline: MutableState<String>
lateinit var blurSubline: MutableState<String>
lateinit var categorySubline: MutableState<String>
lateinit var ratioSubline: MutableState<String>

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BuildUI(render: Boolean = false) {
    showSliderDialog = remember { mutableStateOf(false) }
    if (showSliderDialog.value) SliderDialog()

    showRadioDialog = remember { mutableStateOf(false) }
    if (showRadioDialog.value) RadioDialog()

    intervalSubline = remember { mutableStateOf("interval subline") }
    minSubline = remember { mutableStateOf("min subline") }
    maxSubline = remember { mutableStateOf("max subline") }
    brightnessSubline = remember { mutableStateOf("brightness subline") }
    blurSubline = remember { mutableStateOf("blur subline") }
    categorySubline = remember { mutableStateOf("category subline") }
    ratioSubline = remember { mutableStateOf("ratio subline") }

    Scaffold(containerColor = Color.White, topBar = {
        TopAppBar(colors = topAppBarColors(
            containerColor = Color(0xff0b2b09), titleContentColor = Color.White
        ), title = {
            Text("500 Walls")
        })
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
        ) {
            if (render) {
                val numCategories = prefs.getInt("categoriesSelected", 1)
                val updateInt = prefs.getInt("updateInt", 30)
                val portraitRatio = prefs.getInt("portraitRatio", 75)

                intervalSubline.value =
                    if (updateInt < 60) if (updateInt == 1) "$updateInt minute" else "$updateInt minutes"
                    else if (updateInt == 60) "${updateInt / 60} hour" else "${updateInt / 60} hours"
                minSubline.value = prefs.getInt("minImages", 8).toString() + " images"
                maxSubline.value = prefs.getInt("maxImages", 36).toString() + " images"
                brightnessSubline.value = prefs.getInt("brightness", 75).toString() + "%"
                blurSubline.value = prefs.getInt("blur", 0).toString() + "%"
                categorySubline.value =
                    numCategories.toString() + " " + (if (numCategories == 1) "category" else "categories") + " selected"
                ratioSubline.value = "$portraitRatio% Portrait, ${100 - portraitRatio}% Landscape"
            }

            Row {
                TitleBuilder(mainText = "Image Settings")
            }
            Row {
                BoxBuilder(mainText = "Brightness",
                    subline = brightnessSubline.value,
                    clickFunction = {
                        setBrightness()
                        showSliderDialog.value = true
                    })
            }
            Row {
                BoxBuilder(mainText = "Blur", subline = blurSubline.value, clickFunction = {
                    setBlur()
                    showSliderDialog.value = true
                })
            }
            Row {
                BoxBuilder(
                    mainText = "Categories",
                    subline = categorySubline.value,
                    clickFunction = {
                        val intent = Intent(classContext, Categories::class.java)
                        intent.putExtra("ACTIVITY", CATEGORIES_ACTIVITY)
                        startForResult.launch(intent)
                    })
            }
            Row {
                BoxBuilder(
                    mainText = "Background Configurator",
                    subline = "Click this to configure the background + home screen",
                    clickFunction = { setBackground() },
                    false
                )
            }

            Row {
                TitleBuilder(mainText = "App Settings")
            }
            Row {
                BoxBuilder(
                    mainText = "Update Interval",
                    subline = intervalSubline.value,
                    clickFunction = {
                        updateIntervalClick()
                        showRadioDialog.value = true
                    })
            }
            Row {
                BoxBuilder(mainText = "Minimum number of images per category",
                    subline = minSubline.value,
                    clickFunction = {
                        minImagesClick()
                        showSliderDialog.value = true
                    })
            }
            Row {
                BoxBuilder(mainText = "Max number of images per category",
                    subline = maxSubline.value,
                    clickFunction = {
                        maxImagesClick()
                        showSliderDialog.value = true
                    })
            }
            Row {
                BoxBuilder(mainText = "Portrait:Landscape ratio",
                    subline = ratioSubline.value,
                    clickFunction = {
                        setRatio()
                        showSliderDialog.value = true
                    })
            }
        }
    }
}

fun minImagesClick() {
    minSlider = 1
    maxSlider = 24
    currentValue = prefs.getInt("minImages", 8)
    paramName = "minImages"
    sliderLabel = "Minimum number of images to keep"
    sliderType = "images"
}

fun maxImagesClick() {
    minSlider = 24
    maxSlider = 72
    currentValue = prefs.getInt("maxImages", 36)
    paramName = "maxImages"
    sliderLabel = "Max number of images to keep"
    sliderType = "images"
}

fun updateIntervalClick() {
    radioOptionsInt = listOf(1, 2, 5, 10, 15, 30, 60, 120, 180, 360, 720, 1440)
    paramName = "updateInt"
    radioDialogLabel = "Update Interval"
    radioDialogType = "time"
}

fun setBackground() {
    Log.d("background", "background set clicked")
}

fun setBrightness() {
    minSlider = 0
    maxSlider = 100
    currentValue = prefs.getInt("brightness", 75)
    paramName = "brightness"
    sliderLabel = "Brightness %"
    sliderType = "percentage"
}

fun setBlur() {
    minSlider = 0
    maxSlider = 100
    currentValue = prefs.getInt("blur", 0)
    paramName = "blur"
    sliderLabel = "Blur %"
    sliderType = "percentage"
}

fun setRatio() {
    minSlider = 0
    maxSlider = 100
    currentValue = prefs.getInt("portraitRatio", 75)
    paramName = "portraitRatio"
    sliderLabel = "Portrait:Landscape Ratio"
    sliderType = "ratio"
}

@Preview(showBackground = true)
@Composable
fun WallsPreview() {
    _500WallsTheme {
        //BuildUI()
        //RadioDialog()
    }
}