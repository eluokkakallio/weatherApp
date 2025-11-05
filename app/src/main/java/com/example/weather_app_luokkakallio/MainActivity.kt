package com.example.weather_app_luokkakallio

import android.app.DownloadManager
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weather_app_luokkakallio.ui.theme.Weather_app_LuokkakallioTheme
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.collections.get


//Tampere
const val LONG1 = 23.7871
const val LAT1 = 61.4991
//Ivalo
const val LONG2 = 27.5389
const val LAT2 = 68.6599
//Oulu HUOM MUOKATTU JOENSUU
//const val LONG3 = 25.4682
//const val LAT3 = 65.0124
const val LONG3 = 29.7632
const val LAT3 = 62.6012
//Ähtäri
const val LONG4 = 24.0619
const val LAT4 = 62.554
//Helsinki
const val LONG5 = 24.9354
const val LAT5 = 60.1695

val Context.dataStore by preferencesDataStore(name = "settings")
val DEFAULT_PAGE = stringPreferencesKey("default_page")


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Weather_app_LuokkakallioTheme {
                Navigation()
                }
            }
        }
    }
@Composable
fun Navigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    var storedPage by remember { mutableStateOf("Ladataan...") }


    LaunchedEffect(Unit) {
        readDefaultPage(context).collect { value ->
            storedPage = value
        }
    }

    if (storedPage == "Ladataan...") {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Ladataan asetuksia...")
        }
    } else {
        NavHost(
            navController = navController,
            startDestination = if (storedPage == "not set") "page1" else storedPage
        ) {
            composable("page1") { Page1(navController, storedPage) }
            composable("page2") { Page2(navController, storedPage) }
            composable("page3") { Page3(navController, storedPage) }
            composable("page4") { Page4(navController, storedPage) }
            composable("page5") { Page5(navController, storedPage) }
        }
    }
}



@Composable
fun Page1(navController: NavHostController, storedPage: String?){
    val (temperature, rain) = apicall(LAT1, LONG1)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(modifier= Modifier
        .fillMaxSize()
        .padding(top=60.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top) {

        Button(onClick = {
            scope.launch {
                storeDefaultPage(context, "page1")

            }
        })
        {
            Text(text = "Aseta oletukseksi, oletus on $storedPage")
        }

    }


    Column(modifier = Modifier
    .fillMaxSize()
    .padding(all=16.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center)
{
    Text("Tampere")
    Text("Lämpötila $temperature")
    Text("Sademäärä $rain")

    Row(){
        Button(onClick = {navController.navigate("page1")})
        {
            Text(text="Tampere")
        }
        Button(onClick = {navController.navigate("page2")})
        {
            Text(text="Ivalo")
        }
        Button(onClick = {navController.navigate("page3")})
        {
            Text(text="Oulu")
        }
        Button(onClick = {navController.navigate("page4")})
        {
            Text(text="Ähtäri")
        }
        Button(onClick = {navController.navigate("page5")})
        {
            Text(text="Helsinki")
        }
    }


}
}


@Composable
fun Page2(navController: NavHostController, storedPage: String?){
    val (temperature, rain) = apicall(LAT2, LONG2)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column(modifier= Modifier
        .fillMaxSize()
        .padding(top=60.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top) {

        Button(onClick = {
            scope.launch {
                storeDefaultPage(context, "page2")

            }
        })
        {
            Text(text = "Aseta oletukseksi, oletus on $storedPage")
        }

    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(all=16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center)
    {
        Text("Ivalo")
        Text("Lämpötila $temperature")
        Text("Sademäärä $rain")

        Row(){
            Button(onClick = {navController.navigate("page1")})
            {
                Text(text="Tampere")
            }
            Button(onClick = {navController.navigate("page2")})
            {
                Text(text="Ivalo")
            }
            Button(onClick = {navController.navigate("page3")})
            {
                Text(text="Oulu")
            }
            Button(onClick = {navController.navigate("page4")})
            {
                Text(text="Ähtäri")
            }
            Button(onClick = {navController.navigate("page5")})
            {
                Text(text="Helsinki")
            }
        }


    }
}

@Composable
fun Page3(navController: NavHostController, storedPage: String?){
    val (temperature, rain) = apicall(LAT3, LONG3)

    Column(modifier= Modifier
        .fillMaxSize()
        .padding(top=60.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top) {
        Text("Click default")
    }


    Column(modifier = Modifier
        .fillMaxSize()
        .padding(all=16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center)
    {
        Text("Oulu")
        Text("Lämpötila $temperature")
        Text("Sademäärä $rain")

        Row(){
            Button(onClick = {navController.navigate("page1")})
            {
                Text(text="Tampere")
            }
            Button(onClick = {navController.navigate("page2")})
            {
                Text(text="Ivalo")
            }
            Button(onClick = {navController.navigate("page3")})
            {
                Text(text="Oulu")
            }
            Button(onClick = {navController.navigate("page4")})
            {
                Text(text="Ähtäri")
            }
            Button(onClick = {navController.navigate("page5")})
            {
                Text(text="Helsinki")
            }
        }


    }
}
@Composable
fun Page4(navController: NavHostController, storedPage: String?){
    val (temperature, rain) = apicall(LAT4, LONG4)

    Column(modifier= Modifier
        .fillMaxSize()
        .padding(top=60.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top) {
        Text("Click default")
    }


    Column(modifier = Modifier
        .fillMaxSize()
        .padding(all=16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center)
    {
        Text("Ähtäri")
        Text("Lämpötila $temperature")
        Text("Sademäärä $rain")

        Row(){
            Button(onClick = {navController.navigate("page1")})
            {
                Text(text="Tampere")
            }
            Button(onClick = {navController.navigate("page2")})
            {
                Text(text="Ivalo")
            }
            Button(onClick = {navController.navigate("page3")})
            {
                Text(text="Oulu")
            }
            Button(onClick = {navController.navigate("page4")})
            {
                Text(text="Ähtäri")
            }
            Button(onClick = {navController.navigate("page5")})
            {
                Text(text="Helsinki")
            }
        }


    }
}
@Composable
fun Page5(navController: NavHostController, storedPage: String?){
    val (temperature, rain) = apicall(LAT5, LONG5)

    Column(modifier= Modifier
        .fillMaxSize()
        .padding(top=60.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top) {
        Text("Click default")
    }


    Column(modifier = Modifier
        .fillMaxSize()
        .padding(all=16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center)
    {
        Text("Helsinki")
        Text("Lämpötila $temperature")
        Text("Sademäärä $rain")

        Row(){
            Button(onClick = {navController.navigate("page1")})
            {
                Text(text="Tampere")
            }
            Button(onClick = {navController.navigate("page2")})
            {
                Text(text="Ivalo")
            }
            Button(onClick = {navController.navigate("page3")})
            {
                Text(text="Oulu")
            }
            Button(onClick = {navController.navigate("page4")})
            {
                Text(text="Ähtäri")
            }
            Button(onClick = {navController.navigate("page5")})
            {
                Text(text="Helsinki")
            }
        }


    }
}

@Composable
fun apicall(latitude: Double, longitude: Double): Pair<Double?, Double?> {
    var result by remember { mutableStateOf<Pair<Double?, Double?>>(Pair(null, null)) }

    //LAunchedEffect estää jatkuvan apinrasittamisen --> hakee apista vain kun muutos
    LaunchedEffect(latitude, longitude) {
        val url =
            "https://api.open-meteo.com/v1/forecast?latitude=$latitude&longitude=$longitude&hourly=temperature_2m,rain&current=temperature_2m,rain"
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("HTTP", "Verkkovirhe: ${e.message}")
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                val body = response.body?.string()
                if (body != null) {
                    val json = JSONObject(body)
                    val current = json.getJSONObject("current")
                    val temperature = current.getDouble("temperature_2m")
                    val rain = current.getDouble("rain")

                    Handler(Looper.getMainLooper()).post {
                        result = Pair(temperature, rain)
                    }
                }
            }

        })
    }
        return result
    }

suspend fun storeDefaultPage(context: Context, name: String) {
    context.dataStore.edit { settings ->
        settings[DEFAULT_PAGE ] = name
    }
}

fun readDefaultPage(context: Context): Flow<String>  {
    return context.dataStore.data
        .map{settings->
            settings[DEFAULT_PAGE]?:"not set"
        }
}






