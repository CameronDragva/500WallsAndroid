package co.dragva.a500walls

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.dragva.a500walls.ui.theme._500WallsTheme

class Categories : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            _500WallsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    val activity: Categories = this
                    //Setup(activity)
                    BuildUI(activity = activity)
                }
            }
        }
    }

    override fun onStop() {
        closeAction()
        super.onStop()
    }
}

@SuppressLint("ApplySharedPref")
fun closeAction() {
    var catCount = 0
    for (cat in defaultCategories) {
        if(cat.enabled) catCount++
    }
    for (cat in userCategories) {
        if (cat.enabled) catCount++
    }

    prefs.edit().putInt("categoriesSelected", catCount).commit()
}

//@Composable
//private fun Setup(activity: Activity) {
//    redrawCategory = remember { mutableStateOf(false) }
//
//    if (redrawCategory.value) {
//        redrawCategory.value = false
//        //BuildUI(true, activity)
//    }
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BuildUI(render: Boolean = false, activity: Activity = Activity()) {
    Scaffold(containerColor = Color.White,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Gray,
                ),
                title = {
                    Text("Categories")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        closeAction()
                        activity.finish()
                    }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Add Category"
                        )
                    }
                },
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
        ) {
            var drawn = 0
            item {
                TitleBuilder(mainText = "User Categories")
                if (userCategories.isNotEmpty()) {

                } else {
                    Text(text = "No User Categories", modifier = Modifier.padding(start = 16.dp))
                }
            }
            item {
                TitleBuilder(mainText = "Default Categories")
                if (defaultCategories.isNotEmpty()) {
                    for (it in defaultCategories) {
                        SwitchBuilder(
                            item = it
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    _500WallsTheme {
        BuildUI()
    }
}