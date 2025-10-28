package com.example.weather_app_luokkakallio

import android.app.DownloadManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    NavHost(
        navController = navController,
        startDestination = "default"
    ){
        composable("default") {
            DefaultPage(navController)
        }

        composable("page2") {
            Page2(navController)
        }
    }
}


@Composable
fun DefaultPage(navController: NavHostController){
    val weatherInfo = Apicall(61.4991, 23.7871)

    Column(modifier = Modifier
    .fillMaxSize()
    .padding(all=16.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center)
{
    Text("Default page")
    Text(weatherInfo)

    Button(onClick = {navController.navigate("page2")})
    {
        Text(text="Helsinki")
    }

}
}


@Composable
fun Page2(navController: NavHostController){
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(all=16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center)
    {
        Text(
            text="Helsinki"
        )
        Button(onClick = {navController.navigate("default")})
        {
            Text(text="Default")
        }

    }
}

@Composable
fun Apicall(latitude: Double, longitude: Double): String {
    var text by remember { mutableStateOf("Loading...") }

    val url = "https://api.open-meteo.com/v1/forecast?latitude=$latitude&longitude=$longitude&hourly=temperature_2m,rain&current=temperature_2m,rain"
    val client = OkHttpClient()
    val request = Request.Builder().url(url).build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            Log.e("HTTP", "Verkkovirhe: ${e.message}")
            Handler(Looper.getMainLooper()).post {
                text = "Verkkovirhe: ${e.message}"
            }
        }

        override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
            val body = response.body?.string()
            if (body != null) {
                val json = JSONObject(body)
                val current = json.getJSONObject("current")
                val temperature = current.getDouble("temperature_2m")
                val rain = current.getDouble("rain")

                Handler(Looper.getMainLooper()).post {
                    text = "Lämpötila nyt: $temperature °C Vesimäärä: $rain mm"
                }
            }
        }
    })
    return text
}

