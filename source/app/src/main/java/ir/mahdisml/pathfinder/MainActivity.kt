package ir.mahdisml.pathfinder

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.*


class MainActivity : ComponentActivity() {

    lateinit var ctx: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ctx  = this
        setContent {
            Scaffold {
                Column {
                    SplashScreen()
                }
            }

        }
    }

    //@Preview(heightDp = 800 , widthDp = 480)
    //@Preview(heightDp = 1280 , widthDp = 720)
    //@Preview(device = Devices.AUTOMOTIVE_1024p)
    //@Preview(device = Devices.PIXEL_4_XL)
    //@Preview(device = Devices.NEXUS_7)
    //@Preview(heightDp = 1080 , widthDp = 2400)//s21
    //@Preview(heightDp = 1440 , widthDp = 3200)//s21u
    //@Preview(heightDp = 1228 , widthDp = 2700)//p50p
    @Preview(device = Devices.NEXUS_5)
    @SuppressLint("CoroutineCreationDuringComposition")
    @Composable
    fun SplashScreen() {
        val job1 = rememberCoroutineScope()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        listOf(Colors.MainLighter,Colors.Main,Colors.Main,
                            Colors.Main,Colors.Main,Colors.MainLighter)
                    )
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column (
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Image(
                        modifier = Modifier
                            .padding(top = 30.dp)
                            .fillMaxWidth(0.512f * 0.7f)
                            .fillMaxHeight(0.512f * 0.7f)
                        ,
                        painter = painterResource(id = R.drawable.finish),
                        contentDescription = "title",
                        colorFilter = ColorFilter.tint(Colors.White)


                    )
                }
                Row(
                    modifier = Modifier.padding(top = 40.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        text = Strings.appName,
                        color = Colors.White,
                        fontSize = 40.sp
                    )
                }
            }
            Column (
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Image(
                        painter = painterResource(id = R.drawable.mahdismllogo),
                        contentDescription = "mahdisml",
                        modifier = Modifier
                            .fillMaxWidth(0.807f * 0.8f)
                            .fillMaxHeight(0.288f * 0.8f)
                    )
                }
                Row(
                    modifier = Modifier.padding(top = 40.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    CircularProgressIndicator(
                        color=Colors.White,
                        modifier = Modifier.size(27.dp),
                        strokeWidth = 2.dp
                    )
                }
            }

        }
        job1.launch{
            delay(7000)
            ctx.startActivity(Intent(ctx, HomeActivity::class.java))
            (ctx as Activity).finish()
        }

    }
}

