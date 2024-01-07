package co.dragva.a500walls

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Switch
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
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
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import co.dragva.a500walls.ui.theme._500WallsTheme
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

var natureCategories = arrayListOf(
    "/r/EarthPorn",
    "/r/BotanicalPorn",
    "/r/WaterPorn",
    "/r/SeaPorn",
    "/r/SkyPorn",
    "/r/FirePorn",
    "/r/DesertPorn",
    "/r/WinterPorn",
    "/r/AutumnPorn",
    "/r/WeatherPorn",
    "/r/GeologyPorn",
    "/r/SpacePorn",
    "/r/BeachPorn",
    "/r/MushroomPorn",
    "/r/SpringPorn",
    "/r/SummerPorn",
    "/r/LavaPorn",
    "/r/LakePorn"
)
var syntheticCategories = arrayListOf(
    "/r/CityPorn",
    "/r/VillagePorn",
    "/r/RuralPorn",
    "/r/ArchitecturePorn",
    "/r/HousePorn",
    "/r/CabinPorn",
    "/r/ChurchPorn",
    "/r/AbandonedPorn",
    "/r/CemeteryPorn",
    "/r/InfrastructurePorn",
    "/r/MachinePorn",
    "/r/CarPorn",
    "/r/F1Porn",
    "/r/MotorcyclePorn",
    "/r/MilitaryPorn",
    "/r/GunPorn",
    "/r/KnifePorn",
    "/r/BoatPorn",
    "/r/RidesPorn",
    "/r/DestructionPorn",
    "/r/ThingsCutInHalfPorn",
    "/r/StarshipPorn",
    "/r/ToolPorn",
    "/r/TechnologyPorn",
    "/r/BridgePorn",
    "/r/PolicePorn",
    "/r/SteamPorn",
    "/r/RetailPorn",
    "/r/SpaceFlightPorn",
    "/r/roadporn",
    "/r/drydockporn"
)
var organicCategories = arrayListOf(
    "/r/AnimalPorn", "/r/HumanPorn", "/r/EarthlingPorn",
    "/r/AdrenalinePorn", "/r/ClimbingPorn", "/r/SportsPorn", "/r/AgriculturePorn", "/r/TeaPorn",
    "/r/BonsaiPorn", "/r/FoodPorn", "/r/CulinaryPorn", "/r/DessertPorn"
)
var aestheticCategories = arrayListOf(
    "/r/DesignPorn", "/r/RoomPorn", "/r/AlbumArtPorn",
    "/r/MetalPorn", "/r/MoviePosterPorn", "/r/TelevisionPosterPorn", "/r/ComicBookPorn",
    "/r/StreetArtPorn", "/r/AdPorn", "/r/ArtPorn", "/r/FractalPorn", "/r/InstrumentPorn",
    "/r/ExposurePorn", "/r/MacroPorn", "/r/MicroPorn", "/r/GeekPorn", "/r/MTGPorn", "/r/GamerPorn",
    "/r/PowerWashingPorn", "/r/AerialPorn", "/r/OrganizationPorn", "/r/FashionPorn", "/r/AVPorn",
    "/r/ApocalypsePorn", "/r/InfraredPorn", "/r/ViewPorn", "/r/HellscapePorn", "/r/sculptureporn"
)
var scholasticCategories = arrayListOf(
    "/r/HistoryPorn", "/r/UniformPorn", "/r/BookPorn",
    "/r/NewsPorn", "/r/QuotesPorn", "/r/FuturePorn", "/r/FossilPorn", "/r/MegalithPorn",
    "/r/ArtefactPorn"
)

@Entity
data class ImageCategory(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "category") var category: String,
    @ColumnInfo(name = "last_post_id") var lastPostId: String,
    @ColumnInfo(name = "enabled") var enabled: Boolean,
    @ColumnInfo(name = "userAdded") var userAdded: Boolean = false
)

@Dao
interface ImageCategoryDao {
    @Query("SELECT * FROM imagecategory")
    fun getAll(): List<ImageCategory>

    @Insert
    fun insert(categories: ImageCategory): Long

    @Delete
    fun delete(imageCategory: ImageCategory)

    @Query("UPDATE imagecategory set enabled = :enabled WHERE id = :id")
    fun setEnabled(enabled: Boolean, id: Int)

    @Query("SELECT COUNT(*) FROM imagecategory where enabled = 1")
    fun enabledCount(): Int

    @Query("SELECT MAX(id) FROM imagecategory")
    fun getLastID(): Int

    @Query("SELECT * FROM imagecategory where userAdded = 1")
    fun getUserAddedCategories(): List<ImageCategory>

    @Query("SELECT * from imagecategory where userAdded = 0")
    fun getDefaultCategories(): List<ImageCategory>
}

@Database(entities = [ImageCategory::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun ImageCategoryDao(): ImageCategoryDao
}

lateinit var db: RoomDatabase
lateinit var mainDb: RoomDatabase
lateinit var categoryDao: ImageCategoryDao
lateinit var mainCategoryDao: ImageCategoryDao
fun initDB(appContext: Context) {
    val dbInit = Room.databaseBuilder(
        appContext,
        AppDatabase::class.java, "500Walls"
    ).build()

    val mainDbInit = Room.databaseBuilder(
        appContext,
        AppDatabase::class.java, "500Walls"
    ).allowMainThreadQueries().build()

    db = dbInit
    mainDb = mainDbInit

    categoryDao = dbInit.ImageCategoryDao()
    mainCategoryDao = mainDbInit.ImageCategoryDao()
}

fun addDefaultCategories() {
    var imageCat: ImageCategory

    natureCategories.forEach {
        imageCat = ImageCategory(category = it, lastPostId = "", enabled = false)
        if (it == "/r/EarthPorn") {
            imageCat.enabled = true
        }
        categoryDao.insert(imageCat)
    }

    syntheticCategories.forEach {
        categoryDao.insert(ImageCategory(category = it, lastPostId = "", enabled = false))
    }
    organicCategories.forEach {
        categoryDao.insert(ImageCategory(category = it, lastPostId = "", enabled = false))
    }
    aestheticCategories.forEach {
        categoryDao.insert(ImageCategory(category = it, lastPostId = "", enabled = false))
    }
    scholasticCategories.forEach {
        categoryDao.insert(ImageCategory(category = it, lastPostId = "", enabled = false))
    }
}

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

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun SwitchBuilder(item: ImageCategory) {
    return Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp)
    ) {
        var isChecked by remember { mutableStateOf(item.enabled) }
        Text(text = item.category, modifier = Modifier
            .clickable { isChecked = !isChecked }
            .align(Alignment.CenterStart))
        Switch(checked = isChecked, modifier = Modifier.align(Alignment.CenterEnd),
            onCheckedChange = {
                isChecked = it
                item.enabled = it
                GlobalScope.launch { categoryDao.setEnabled(it, item.id) }
            })
    }
}

@Composable
fun BoxBuilder(
    mainText: String,
    subline: String,
    clickFunction: () -> (Unit),
    drawDivider: Boolean = true
) {
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
        if (drawDivider)
            Divider(modifier = Modifier.padding(top = 55.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("ApplySharedPref", "UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RadioDialog() {
    Dialog(
        onDismissRequest = { showRadioDialog.value = false }) {
        Card(modifier = Modifier.padding(16.dp)) {
            Column() {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.CenterStart)
                        .padding(start = 16.dp)
                        .height(50.dp), verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(radioDialogLabel, fontSize = 20.sp)
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f, false)
                ) {
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .wrapContentHeight()
                    ) {
                        if (radioDialogType == "time") {
                            val position = radioOptionsInt.indexOf(prefs.getInt(paramName, 30))
                            var selectedOption by remember { mutableIntStateOf(radioOptionsInt[position]) }
                            radioOptionsInt.forEach {
                                val timeString =
                                    if (it < 60) if (it == 1) "$it minute" else "$it minutes"
                                    else if (it == 60) "${it / 60} hour" else "${it / 60} hours"
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(selected = (it == selectedOption),
                                        onClick = {
                                            selectedOption = it
                                            prefs.edit().putInt(paramName, selectedOption).commit()
                                            showRadioDialog.value = false
                                        })
                                    Box(modifier = Modifier.fillMaxWidth()) {
                                        Text(text = timeString,
                                            modifier = Modifier.clickable {
                                                selectedOption = it
                                                prefs.edit().putInt(paramName, selectedOption)
                                                    .commit()
                                                showRadioDialog.value = false
                                            })
                                    }
                                }
                            }
                        }
                    }
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .wrapContentSize(Alignment.CenterStart)
                        .padding(start = 16.dp)
                        .height(50.dp), verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.End
                    ) {
                        TextButton(
                            onClick = { showRadioDialog.value = false }) {
                            Text(text = "Cancel")
                        }
                    }
                }
            }
        }
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
                when (sliderType) {
                    "images" -> sliderLabel = "${sliderPosition.toInt()} images"
                    "percentage" -> sliderLabel = "${sliderPosition.toInt()}%"
                    "ratio" -> sliderLabel =
                        "${sliderPosition.toInt()}% Portrait, ${100 - sliderPosition.toInt()}% Landscape"
                }

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
                    modifier = Modifier.padding(start=16.dp, end=16.dp)
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

@Preview(showBackground = true)
@Composable
fun UtilsPreview() {
    _500WallsTheme {
        //BuildUI()
        RadioDialog()
    }
}