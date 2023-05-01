package com.example.BeanBotApp


import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BeanBotApp.api.NullOnEmptyConverterFactory
import com.example.BeanBotApp.api.UserApi
import com.example.BeanBotApp.ui.theme.ApiExampleTheme
import com.example.BeanBotApp.ui.theme.Purple700
import com.example.BeanBotApp.ui.theme.WaarschuwingRood
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory


class MainActivitySampleVanGH : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val IP_BB:String? = intent.getStringExtra("IP_BeanBot").toString()



            ApiExampleTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    MainScreen(IP_BB)

                }
            }
        }
    }
}

/*@Preview(showBackground = true)
@Composable
fun DefaultPreview4(){
    val actieveBestelling = remember {
        mutableStateOf(true)
    }
  PendingBestellingScherm(actieveBestelling, )
}*/

@Composable
fun MainScreen(IP : String?) {

   Scaffold(
       topBar = {
           TopAppBar(
               backgroundColor = Purple700,
               title = {
                   Text(
                       text = "Bean Bot Controlepaneel",
                       modifier = Modifier.fillMaxWidth(),
                       textAlign = TextAlign.Center,
                       color = Color.White
                   )
               }
           )
       },
       content = {
          Column(
              modifier = Modifier.fillMaxWidth(),
              verticalArrangement = Arrangement.Center,
              horizontalAlignment = Alignment.CenterHorizontally
          ) {
              val custom_cmd = remember {
                  mutableStateOf(TextFieldValue())
              }

              val beanbotdata = remember {
                  mutableStateOf("")
              }

              var actieveBestelling = remember{ mutableStateOf(false)}




              Spacer(modifier = Modifier.height(15.dp))

              TextField(
                  label = { Text(text = "Custom Commando")},
                  value = custom_cmd.value,
                  onValueChange = { custom_cmd.value = it }
              )

              Spacer(modifier = Modifier.height(15.dp))


              Row() {
                  Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
                      androidx.compose.material3.Button(
                          onClick = {
                              val data = getRequest(

                                  dataState = beanbotdata,
                                  ip_adres = IP
                              )

                              Log.d("Main Activity", beanbotdata.toString())
                          },
                          colors = ButtonDefaults.buttonColors(
                              contentColor = Color.White,
                              containerColor = Purple700
                          )
                      ) {
                          Text(text = "Get Data", color = Color.White)
                      }
                  }

                  Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
                      androidx.compose.material3.Button(
                          onClick = {
                              val data = postRequest(
                                  cmd = custom_cmd.value.text,
                                  dataState = beanbotdata,
                                  ip_adres = IP
                              )

                              Log.d("Main Activity", beanbotdata.toString())
                          },
                          colors = ButtonDefaults.buttonColors(
                              contentColor = Color.White,
                              containerColor = Purple700
                          )
                      ) {
                          Text(text = "Send Data", color = Color.White)
                      }
                  }
              }
                //groetjes
              
              Spacer(modifier = Modifier.height(15.dp))

              Text(text = "Output: ",fontSize = 20.sp)
              Text(text = beanbotdata.value.toString(), fontSize = 20.sp)


              if (actieveBestelling.value){
                  PendingBestellingScherm(actieveBestelling,beanbotdata,IP)
              }else{
                  BestelScherm(actieveBestelling,beanbotdata,IP)
              }

          }
       }
   )
}

fun getRequest(
    dataState: MutableState<String>,
    ip_adres : String?
) {

    val default_ip = "http://192.168.4.1:80"
    //"http://192.168.0.141:3000"
    val ip = if(ip_adres.isNullOrEmpty()) default_ip else "http://"+ip_adres+":8080"

    val retrofit = Retrofit.Builder()
        .baseUrl(ip)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .addConverterFactory(NullOnEmptyConverterFactory())
        .build()

    val api = retrofit.create(UserApi::class.java)

    val call: Call<String?>? = api.getArduinoData()

    call!!.enqueue(object: Callback<String?> {
        override fun onResponse(call: Call<String?>, response: Response<String?>) {
            if(response.isSuccessful) {
                Log.d("Main", "success!" + response.body().toString())
                dataState.value = response.body()!!.toString()

            }
        }

        override fun onFailure(call: Call<String?>, t: Throwable) {
            Log.d("WIFII",call.toString())
            Log.e("Main", "Failed mate " + t.message.toString())
            dataState.value = t.message.toString()
        }
    })
}

fun postRequest(
    cmd: String,
    dataState: MutableState<String>,
    ip_adres : String?
) {
    val default_ip = "http://192.168.4.1:80"
    val ip = if(ip_adres.isNullOrEmpty()) default_ip else "http://"+ip_adres+":8080"

    val retrofit = Retrofit.Builder()
        .baseUrl(ip)
        .addConverterFactory(GsonConverterFactory.create())
        .addConverterFactory(NullOnEmptyConverterFactory())
        .build()

    val api = retrofit.create(UserApi::class.java)

    //val txt = "ditISCOMMANDOOOO"

    val call: Call<String?>? = api.postCommand(cmd)


    call!!.enqueue(object: Callback<String?> {

        override fun onResponse(call: Call<String?>, response: Response<String?>) {

            if(response.isSuccessful) {
                Log.d("Main", "success!" + response.body().toString())
                dataState.value = response.body()!!.toString()
            }
        }

        override fun onFailure(call: Call<String?>, t: Throwable) {
            Log.e("Main", "Failed mate " + t.message.toString())
            dataState.value = t.message.toString()
        }
    })
}

@Composable
fun BestelScherm(actieveBestelling : MutableState<Boolean>, dataState: MutableState<String>, ip_adres : String? ) {
    var RodeBonen = "0%"
    var ZwarteBonen = "0%"
    var WitteBonen = "0%"

    var RodeWil by remember { mutableStateOf(false) }
    var ZwarteWil by remember { mutableStateOf(false) }
    var WitteWil by remember { mutableStateOf(false) }

    val GESELECTEERD_KLEUR = Color.Green

    var gewensteKleur by remember { mutableStateOf("") }

    var sliderPosition by remember { mutableStateOf(150f) }

    var gewichtBonen = sliderPosition.toInt()

    var bestelling_fout by remember { mutableStateOf(false) }
    var server_fout = remember { mutableStateOf(false) }

    var arduino_respons = remember {
        mutableStateOf("")
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(vertical = 8.dp)
    )
    {
        item {
            Box(
                modifier = Modifier
                    .padding(30.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                androidx.compose.material3.Text(
                    text = "Kies de gewenste bonen",
                    style = androidx.compose.material3.MaterialTheme.typography.titleLarge.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Left,
                        lineHeight = 30.sp,

                        )
                )
            }
        }
        item {
            Row() {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    androidx.compose.material3.Text(text = "Stock", fontSize = 20.sp)
                    androidx.compose.material3.Text(
                        text = RodeBonen, fontSize = 35.sp
                    )
                    androidx.compose.material3.Text(
                        text = ZwarteBonen, fontSize = 35.sp
                    )
                    androidx.compose.material3.Text(text = WitteBonen, fontSize = 35.sp)
                }
                Spacer(modifier = Modifier.width(25.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    androidx.compose.material3.Text(text = "Kleur bonen",
                        fontSize = 20.sp
                    )
                    androidx.compose.material3.Button(
                        onClick = {
                            ZwarteWil = false
                            RodeWil = true
                            WitteWil = false },
                        colors = ButtonDefaults.buttonColors(
                            contentColor = if (RodeWil) Color.Black else Color.White,
                            containerColor = if (RodeWil) GESELECTEERD_KLEUR else Purple700
                        ),
                        modifier = Modifier.width(135.dp)
                    )
                    {
                        androidx.compose.material3.Text(text = "Rood")
                    }

                    androidx.compose.material3.Button(
                        onClick = {
                            ZwarteWil = true
                            RodeWil = false
                            WitteWil = false
                                  },
                        colors = ButtonDefaults.buttonColors(
                            contentColor = if (ZwarteWil) Color.Black else Color.White,
                            containerColor = if (ZwarteWil) GESELECTEERD_KLEUR else Purple700
                        ),
                        modifier = Modifier.width(135.dp)
                    )
                    {
                        androidx.compose.material3.Text(text = "Zwart")
                    }

                    androidx.compose.material3.Button(
                        onClick = {
                            ZwarteWil = false
                            RodeWil = false
                            WitteWil = true},
                        colors = ButtonDefaults.buttonColors(
                                contentColor = if (WitteWil) Color.Black else Color.White,
                                containerColor = if (WitteWil) GESELECTEERD_KLEUR else Purple700
                        ),
                        modifier = Modifier.width(135.dp)
                    )
                    {
                        androidx.compose.material3.Text(text = "Wit")
                    }
                }

            }
        }
        item { Spacer(modifier = Modifier.height(35.dp)) }
        item {
            Box() {
                androidx.compose.material3.Text(
                    text = "Hoeveel gram bonen wenst u?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        item {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(1.dp)
            ) {
                androidx.compose.material3.Text(
                    modifier = Modifier.width(50.dp),
                    text = gewichtBonen.toString(),
                    fontSize = 20.sp,
                )
            }
        }
        item {
            Box(
                modifier = Modifier
                    .padding(horizontal = 50.dp)
            ) {
                androidx.compose.material3.Slider(
                    modifier = Modifier.semantics { contentDescription = "Localized Description"},
                    value = sliderPosition,
                    onValueChange = { sliderPosition = it },
                    valueRange = 150f..300f,
                    steps = 150,

                )
            }

        }
        item { Spacer(modifier = Modifier.height(35.dp)) }

        if (bestelling_fout){

            item{
                Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(1.dp)
                ){
                    Text("Kies een bonenkleur.",color = WaarschuwingRood)}
                }
            item { Spacer(modifier = Modifier.height(35.dp)) }
        }

        if (server_fout.value){

            item{
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(1.dp)
                ){
                    val er:String =arduino_respons.value
                    Column{

                        Text("Er ging iets mis met de server",color = WaarschuwingRood)
                        Text("Probeer later opnieuw",color = WaarschuwingRood)
                        Text("Fout: $er",color = WaarschuwingRood)
                    }

                }



            }
            item { Spacer(modifier = Modifier.height(35.dp)) }
        }


        item {
            androidx.compose.material3.Button(
                onClick = {
                    if ( (RodeWil || ZwarteWil || WitteWil) && (gewichtBonen in 150..300)) {

                        bestelling_fout = false
                        if (RodeWil){
                            gewensteKleur = "rood"
                        }else if(ZwarteWil){
                            gewensteKleur = "zwart"
                        }else if(WitteWil){
                            gewensteKleur = "wit"
                        }

                        plaatsBestelling(gewensteKleur,gewichtBonen, ip_adres,arduino_respons,actieveBestelling,server_fout)


                    }else{
                        bestelling_fout = true
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White,
                    containerColor = Purple700
                )
            )
            {
                androidx.compose.material3.Text(
                    text = "Bestel",
                    fontSize = 35.sp
                )
            }
        }
        item { Spacer(modifier = Modifier.height(35.dp)) }


        item { val context = LocalContext.current
            androidx.compose.material3.Button(
                onClick = {
                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
                },
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White,
                    containerColor = Purple700
                ),
                modifier = Modifier
                    .padding(5.dp)
            ) {
                androidx.compose.material3.Icon(Icons.Filled.ArrowBack, contentDescription = "Go back")
            } }
    }
}


fun plaatsBestelling(
    gewensteKleur: Any,
    gewichtBonen: Int,
    ip_adres: String?,
    arduino_response: MutableState<String>,
    actieveBestelling: MutableState<Boolean>,
    server_fout: MutableState<Boolean>
) {

    Log.d("debuggin","$gewensteKleur en $gewichtBonen")


    postRequest("$gewensteKleur+$gewichtBonen", arduino_response,ip_adres)

    Handler(Looper.getMainLooper()).postDelayed({
        //Do something after 100ms
        getRequest(arduino_response, ip_adres)
    }, 100)


    Handler(Looper.getMainLooper()).postDelayed({
        //Do something after 100ms
        if (arduino_response.value == "ok"){
            Log.d("helo","went well + $arduino_response")
            actieveBestelling.value = true
            server_fout.value = false
        }else{
            Log.d("helo","went bad + $arduino_response")
            server_fout.value = true
        }
    }, 400)


}

@Composable
fun PendingBestellingScherm(actieveBestelling: MutableState<Boolean>,dataState: MutableState<String> ,ip_adres : String? ){
    Box(
        modifier = Modifier.padding(1.dp),
        contentAlignment = Alignment.Center
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center

        ) {
            val arduino_response = remember {
                mutableStateOf("")
            }
            var fout by remember {
                mutableStateOf(false)
            }

            Spacer(modifier = Modifier.height(15.dp))
            Text("Even geduld, uw bestelling wordt verwerkt...")

            Spacer(modifier = Modifier.height(35.dp))
            if (fout){


                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(2.dp)
                ){
                    Spacer(modifier = Modifier.height(15.dp))
                    Text("Geen OK respons van server gekregen",color = WaarschuwingRood)}

                 Spacer(modifier = Modifier.height(35.dp))
            }

            Row{
                androidx.compose.material3.Button(
                    onClick = {
                        postRequest("stop", dataState,ip_adres)

                        Handler(Looper.getMainLooper()).postDelayed({
                            //Do something after 100ms
                            getRequest(arduino_response, ip_adres)
                        }, 100)

                        Handler(Looper.getMainLooper()).postDelayed({
                            //Do something after 100ms
                            if ( arduino_response.value == "ok"){
                                actieveBestelling.value = false
                            }else{
                                fout = true
                            }
                        }, 400)

                    },
                    colors = ButtonDefaults.buttonColors(

                        containerColor = WaarschuwingRood
                    )
                ){
                    Text("Annuleer bestelling", color = Color.Black)
                }
                Spacer(modifier = Modifier.width (20.dp))

                androidx.compose.material3.Button(
                    onClick = {
                        postRequest("comfirmed", dataState,ip_adres)

                        Handler(Looper.getMainLooper()).postDelayed({
                            //Do something after 100ms
                            getRequest(arduino_response, ip_adres)
                        }, 100) // arduino tijd geven om in te gaan op post

                        Handler(Looper.getMainLooper()).postDelayed({
                            //Do something after 100ms
                            if ( arduino_response.value == "ok"){
                                actieveBestelling.value = false
                            }else{
                                fout = true
                            }
                        }, 400) // tijd geven om response te krijgen
                    },
                    colors = ButtonDefaults.buttonColors(

                        containerColor = Purple700
                    )
                ){
                    Text("Bevestig bestelling", color = Color.White)
                }
            }
        }
    }
}