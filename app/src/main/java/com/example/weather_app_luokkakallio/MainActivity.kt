package com.example.weather_app_luokkakallio

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weather_app_luokkakallio.ui.theme.Weather_app_LuokkakallioTheme
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.coroutines.resume


//Tampere
const val TOWN1 = "Tampere"
const val LONG1 = 23.7871
const val LAT1 = 61.4991
//Ivalo
const val TOWN2 = "Ivalo"
const val LONG2 = 27.5389
const val LAT2 = 68.6599

const val TOWN3 = "Joensuu"
const val LONG3 = 29.7632
const val LAT3 = 62.6012

const val TOWN4 = "Ähtäri"
const val LONG4 = 24.0619
const val LAT4 = 62.554

const val TOWN5 = "Helsinki"
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
            composable("page1") { Page(navController, storedPage, 1,  LAT1, LONG1, "Tampere") }
            composable("page2") { Page(navController, storedPage, 2, LAT2, LONG2, "Ivalo") }
            composable("page3") { Page(navController, storedPage, 3,LAT3, LONG3, "Joensuu") }
            composable("page4") { Page(navController, storedPage,  4,LAT4, LONG4, "Ähtäri") }
            composable("page5") { Page(navController, storedPage, 5, LAT5, LONG5, "helsinki") }
        }
    }
}



@Composable
fun Page(navController: NavHostController, storedPage: String?, id: Int, lat: Double, long: Double, town: String) {
    var temperature by remember { mutableStateOf<Double?>(null) }
    var rain by remember { mutableStateOf<Double?>(null) }
    var city by remember { mutableStateOf(town) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    //Säätietojen haku
    LaunchedEffect(lat, long) {
        try {
            val (temp, r) = fetchWeather(lat, long)
            temperature = temp
            rain = r
        } catch (e: Exception) {
            Log.e("WEATHER_ERROR", "Sään haku epäonnistui: ${e.message}")

        }
    }

    Column(
        modifier = Modifier
            .padding(all=20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        var newCity by remember { mutableStateOf("") }
        var newLat by remember { mutableStateOf("") }
        var newLong by remember { mutableStateOf("") }

        //Yläosa koostuu säätiedoista ja oletussivunappulasta
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 70.dp, bottom = 30.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,

            ){
            //Säätietojen näyttö
            Text(city, fontSize = 40.sp)
            if (temperature != null && rain != null) {
                Text("$temperature °C", modifier = Modifier.padding(top = 30.dp, bottom = 10.dp), fontSize = 70.sp)
                Text("Sademäärä $rain mm", fontSize = 20.sp)
            } else {
                Text("Ladataan...", modifier = Modifier.padding(top = 30.dp, bottom = 10.dp), fontSize = 50.sp)
            }

            //Oletussivun asetus
            Button(onClick = {
                scope.launch {
                    storeDefaultPage(context, "page$id")
                }
            })
            { Text(text = "Aseta oletuskaupungiksi") }
        }

        ButtonRow(navController = navController)
        var errorText by remember { mutableStateOf("") }

        //Uuden kaupungin tietojen haku ja sivun päivitys
        SearchCard(
            onSearch = { searchCity, searchLat, searchLong ->
                // Tämä koodi suoritetaan, kun SearchCard ilmoittaa onnistuneesta hausta.
                errorText = ""
                scope.launch {
                    try {
                        val (temp, r) = fetchWeather(searchLat, searchLong)
                        temperature = temp
                        rain = r
                        city = searchCity
                    } catch (e: Exception) {
                        errorText = "Säätietojen haku epäonnistui. Tarkista koordinaatit."
                        Log.e("FETCH_ERROR", "Haku epäonnistui", e)
                    }
                }
            },
            onSearchError = { errorMessage ->
                errorText = errorMessage
            }
        )
        Text(
            text = errorText,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
fun SearchCard(
    onSearch: (city: String, lat: Double, long: Double) -> Unit,
    onSearchError: (errorMessage: String) -> Unit
) {
    var newCity by remember { mutableStateOf("") }
    var newLat by remember { mutableStateOf("") }
    var newLong by remember { mutableStateOf("") }

    Card(modifier = Modifier.padding(top = 70.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text("Hae kaupunkia \uD83D\uDD0E", modifier = Modifier.padding(bottom = 8.dp, start = 8.dp))
            TextField(
                value = newCity,
                onValueChange = { newCity = it },
                label = { Text(text = "Syötä kaupungin nimi") }
            )
            TextField(
                value = newLat,
                onValueChange = { newLat = it },
                label = { Text(text = "Syötä latitude") }
            )
            TextField(
                value = newLong,
                onValueChange = { newLong = it },
                label = { Text(text = "Syötä longitude") }
            )

            Button(
                modifier = Modifier.padding(top = 8.dp),
                onClick = {
                    val newLatDouble = newLat.toDoubleOrNull()
                    val newLongDouble = newLong.toDoubleOrNull()

                    // Tarkistetaana että tarvittavat syötteet
                    if (newCity.isNotEmpty() && newLatDouble != null && newLongDouble != null) {
                        onSearch(newCity, newLatDouble, newLongDouble)

                        newCity = ""
                        newLat = ""
                        newLong = ""
                    } else {
                        onSearchError("Tarkista syötteet. Kaupungin nimi ei voi olla tyhjä, ja koordinaattien on oltava numeroita.")
                    }
                })
            {
                Text(text = "Hae")
            }
        }
    }
}

@Composable
fun ButtonRow(navController: NavHostController) {
    Column(modifier = Modifier
        .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Row {
            Button(modifier = Modifier .padding(all=5.dp),
                onClick = { navController.navigate("page1") }) { Text(text = "Tampere") }
            Button(modifier = Modifier .padding(all=5.dp),
                onClick = { navController.navigate("page2") }) { Text(text = "Ivalo") }
            Button(modifier = Modifier .padding(all=5.dp),
                onClick = { navController.navigate("page3") }) { Text(text = "Joensuu") }
        }
        Row {
            Button(modifier = Modifier .padding(all=5.dp),
                onClick = { navController.navigate("page4") }) { Text(text = "Ähtäri") }
            Button(modifier = Modifier .padding(all=5.dp),
                onClick = { navController.navigate("page5") }) { Text(text = "Helsinki") }
        }
    }
}


suspend fun fetchWeather(latitude: Double, longitude: Double): Pair<Double, Double> = withContext(Dispatchers.IO) {
    suspendCancellableCoroutine { continuation ->
        val url = "https://api.open-meteo.com/v1/forecast?latitude=$latitude&longitude=$longitude&hourly=temperature_2m,rain&current=temperature_2m,rain"
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()
        val call = client.newCall(request)

        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                if (continuation.isActive) continuation.resumeWithException(e)
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                val body = response.body?.string()
                if (body != null && response.isSuccessful) {
                    try {
                        val json = JSONObject(body)
                        val current = json.getJSONObject("current")
                        val temperature = current.getDouble("temperature_2m")
                        val rain = current.getDouble("rain")
                        continuation.resume(Pair(temperature, rain))
                    } catch (e: Exception) {
                        continuation.resumeWithException(e)
                    }
                } else {
                    continuation.resumeWithException(IOException("Virheellinen vastaus palvelimelta"))
                }
            }
        })
        continuation.invokeOnCancellation {
            call.cancel()
        }
    }
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