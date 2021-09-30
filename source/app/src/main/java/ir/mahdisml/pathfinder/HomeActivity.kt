package ir.mahdisml.pathfinder

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.model.*
import com.google.maps.android.ktx.awaitMap
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import kotlinx.coroutines.launch
import io.ktor.client.request.*
import io.ktor.client.statement.*

import kotlinx.serialization.json.*

class HomeActivity : ComponentActivity() {

    private lateinit var ctx: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ctx  = this
        setContent {
            Scaffold {
                Column {
                    Home()
                }
            }
        }
    }


    //@Preview (widthDp = 1080 , heightDp = 720)
    //@Preview(device = Devices.PIXEL_4_XL)
    @Preview(device = Devices.NEXUS_5)
    @SuppressLint("CoroutineCreationDuringComposition")
    @Composable
    fun Home() {

        var state by remember { mutableStateOf(0)}
        var mapWeightState by remember { mutableStateOf(0.9f)}
        var buttonTextState by remember { mutableStateOf(Strings.button0)}
        var title1ColorState by remember { mutableStateOf(Colors.Gray)}
        var title2ColorState by remember { mutableStateOf(Colors.Gray)}
        val mapView = rememberMapViewWithLifecycle()
        val job1 = rememberCoroutineScope()
        val job2 = rememberCoroutineScope()
        var map by remember { mutableStateOf<GoogleMap?>(null)}
        var pointA by remember { mutableStateOf<Marker?>(null)}
        var pointB by remember { mutableStateOf<Marker?>(null)}
        var pointALatlng by remember { mutableStateOf<LatLng?>(null)}
        var pointBLatlng by remember { mutableStateOf<LatLng?>(null)}
        var polyLine by remember { mutableStateOf<Polyline?>(null)}

        fun setState (nextState:Int){
            when (nextState){
                0 -> { // start
                    state = 0
                    mapWeightState = 0.9f
                    buttonTextState = Strings.button0
                }
                1 -> { // a
                    state = 1
                    mapWeightState = 0.8f
                    buttonTextState = Strings.button1
                    title1ColorState = Colors.Main
                    title2ColorState = Colors.Gray
                }
                2 -> { // b
                    state = 2
                    mapWeightState = 0.8f
                    buttonTextState = Strings.button2
                    title1ColorState = Colors.Gray
                    title2ColorState = Colors.Main
                }
                3 -> { // loading
                    state = 3
                    mapWeightState = 0.9f
                }
                4 -> { // failed
                    state = 4
                    mapWeightState = 0.9f
                    buttonTextState = Strings.button4
                }
                5 -> { // success
                    state = 5
                    mapWeightState = 0.9f
                    buttonTextState = Strings.button5
                }
            }
        }

        fun calculate (){
            job2.launch {
                val client = HttpClient(Android)
                val server = "https://api.openrouteservice.org/v2/directions/driving-car"
                val origin = "${pointALatlng!!.longitude},${pointALatlng!!.latitude}"
                val destination = "${pointBLatlng!!.longitude},${pointBLatlng!!.latitude}"
                val key = "XXX"
                try {
                    val response: HttpResponse = client.get("$server?api_key=$key&start=$origin&end=$destination")
                    val stringBody: String = response.receive()
                    Log.i("SMLSML",stringBody)

                    val road = mutableListOf(pointALatlng!!)

                    val json = Json {
                        isLenient = true
                        prettyPrint = true
                        ignoreUnknownKeys = true
                    }


                    val element = json.parseToJsonElement(stringBody)

                    element
                        .jsonObject["features"]!!
                        .jsonArray[0]
                        .jsonObject["geometry"]!!
                        .jsonObject["coordinates"]!!
                        .jsonArray.forEach {
                            road.add(LatLng(it.jsonArray[1].jsonPrimitive.double,
                                it.jsonArray[0].jsonPrimitive.double
                            ))
                        }

                    road.add(pointBLatlng!!)

                    val polylineOptions = PolylineOptions()
                    road.forEach {
                        polylineOptions.add(it)
                    }
                    polyLine = map?.addPolyline(polylineOptions)

                    setState(5)
                } catch (e: Exception) {
                    setState(4)
                }
            }
        }

        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(
            true
        ) {
            override fun handleOnBackPressed() {
                when (state){
                    0->{
                        (ctx as Activity).finish()
                    }
                    1->{
                        setState(0)
                    }
                    2->{
                        setState(1)
                    }
                    3->{
                        setState(2)
                    }
                    4->{
                        setState(2)
                    }
                    5->{
                        polyLine?.remove()
                        setState(2)
                    }

                }
            }
        })

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        listOf(
                            Colors.Main, Colors.Main,
                            Colors.Main, Colors.Main, Colors.MainLighter
                        )
                    )
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            if (state == 1 || state == 2) {
                Row(
                    modifier = Modifier.weight(0.1f),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(all = 7.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .background(color = Colors.White),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = Strings.title1,
                            color = title1ColorState,
                            modifier = Modifier.clickable(onClick = {
                                if (state == 2) {
                                    setState(1)
                                }
                            })
                        )
                        Text(
                            text = Strings.title2,
                            color = title2ColorState,
                            modifier = Modifier.clickable(onClick = {
                                
                            })
                        )
                    }
                }
            }

            Row (
                modifier = Modifier.weight(mapWeightState),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(all = 7.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .background(color = Colors.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth()
                    ){
                        AndroidView({ mapView}) { mapView ->
                            if (map == null) {
                                job1.launch {

                                    map = mapView.awaitMap()
                                    map!!.uiSettings.isZoomControlsEnabled = true

                                    map!!.animateCamera(
                                        CameraUpdateFactory.newCameraPosition(
                                            CameraPosition.fromLatLngZoom(LatLng(36.3412743,59.5293824),15.0f)
                                        )
                                    )

                                    map!!.setOnMapClickListener {
                                        when (state) {
                                            1 -> {
                                                Log.i("SMLSML", it.toString())
                                                pointALatlng = it
                                                val markerOptions = MarkerOptions()
                                                    .title("Point A")
                                                    .position(it)
                                                pointA?.remove()
                                                pointA = map!!.addMarker(markerOptions)

                                            }
                                            2 -> {
                                                Log.i("SMLSML", it.toString())
                                                pointBLatlng = it
                                                val markerOptions = MarkerOptions()
                                                    .title("Point B")
                                                    .position(it)
                                                pointB?.remove()
                                                pointB = map!!.addMarker(markerOptions)
                                            }
                                            else -> {

                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }
            Row (
                modifier = Modifier.weight(0.1f),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        when (state){
                            0 -> {setState(1)}
                            1 -> { pointALatlng?.let{ setState(2) }}
                            2 -> {
                                pointBLatlng?.let{
                                    setState(3)
                                    calculate()
                                }

                            }
                            3 -> {}
                            4 -> {
                                pointBLatlng?.let{
                                    setState(3)
                                    calculate()
                                }

                            }
                            5 -> {
                                polyLine?.remove()
                                setState(1)
                            }
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(
                        backgroundColor = Colors.White,
                        contentColor = Colors.Main
                    ),
                    modifier = Modifier
                        .fillMaxSize(0.7f)

                ) {
                    if (state == 3){
                        CircularProgressIndicator(
                            color=Colors.Main,
                            modifier = Modifier.size(27.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(buttonTextState)
                    }
                }

            }

        }
    }

}