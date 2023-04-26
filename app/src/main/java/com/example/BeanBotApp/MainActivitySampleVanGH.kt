package com.example.BeanBotApp


import android.content.Intent
import android.os.Bundle
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
                    color = MaterialTheme.colors.background
                ) {
                    MainScreen(IP_BB)

                }
            }
        }
    }
}


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



              Spacer(modifier = Modifier.height(15.dp))

              TextField(
                  label = { Text(text = "Custom Commando")},
                  value = custom_cmd.value,
                  onValueChange = { custom_cmd.value = it }
              )

              Spacer(modifier = Modifier.height(15.dp))


              Row() {
                  Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
                      Button(
                          onClick = {
                              val data = sendRequest(
                                  cmd = custom_cmd.value.text,
                                  dataState = beanbotdata,
                                  ip_adres = IP
                              )

                              Log.d("Main Activity", beanbotdata.toString())
                          }
                      ) {
                          Text(text = "Get Data")
                      }
                  }

                  Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
                      Button(
                          onClick = {
                              val data = postRequest(
                                  cmd = custom_cmd.value.text,
                                  dataState = beanbotdata,
                                  ip_adres = IP
                              )

                              Log.d("Main Activity", beanbotdata.toString())
                          }
                      ) {
                          Text(text = "Send Data")
                      }
                  }
              }
                //groetjes
              
              Spacer(modifier = Modifier.height(15.dp))

              Text(text = "Output: ",fontSize = 20.sp)
              Text(text = beanbotdata.value.toString(), fontSize = 20.sp)
              BestelScherm()
          }
       }
   )
}

fun sendRequest(
    cmd: String,
    dataState: MutableState<String>,
    ip_adres : String?
) {

    val default_ip = "http://192.168.4.1:80"
    //"http://192.168.0.141:3000"
    val ip = if(ip_adres.isNullOrEmpty()) default_ip else "http://"+ip_adres+":3000"

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
    val ip = if(ip_adres.isNullOrEmpty()) default_ip else "http://"+ip_adres+":3000"

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
fun BestelScherm() {
    var RodeBonen = "70%"
    var ZwarteBonen = "50%"
    var WitteBonen = "69%"
    var RodeWil by remember { mutableStateOf(0) }
    var ZwarteWil by remember { mutableStateOf(0) }
    var WitteWil by remember { mutableStateOf(0) }
    var sliderPosition by remember { mutableStateOf(150f) }
    var gewichtBonen = sliderPosition.toInt()
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
                    text = "Klik op de gewenste kleur",
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
                    androidx.compose.material3.Text(text = "Kleur",
                        fontSize = 20.sp
                    )
                    androidx.compose.material3.Button(
                        onClick = { RodeWil++ },
                        colors = ButtonDefaults.buttonColors(
                            contentColor = androidx.compose.material3.MaterialTheme.colorScheme.background),
                        modifier = Modifier.width(135.dp)
                    )
                    {
                        androidx.compose.material3.Text(text = "Rode Bonen")
                    }
                    androidx.compose.material3.Button(
                        onClick = { ZwarteWil++ },
                        colors = ButtonDefaults.buttonColors(
                            contentColor = androidx.compose.material3.MaterialTheme.colorScheme.background
                        ),
                        modifier = Modifier.width(135.dp)
                    )
                    {
                        androidx.compose.material3.Text(text = "Zwarte Bonen")
                    }
                    androidx.compose.material3.Button(
                        onClick = { WitteWil++ },
                        colors = ButtonDefaults.buttonColors(
                            contentColor = androidx.compose.material3.MaterialTheme.colorScheme.background
                        ),
                        modifier = Modifier.width(135.dp)
                    )
                    {
                        androidx.compose.material3.Text(text = "Witte Bonen")
                    }
                }

            }
        }
        item { Spacer(modifier = Modifier.height(35.dp)) }
        item {
            Box() {
                androidx.compose.material3.Text(
                    text = "Duid het gewicht aan op de slider",
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
                    .padding(10.dp)
            ) {
                androidx.compose.material3.Slider(
                    modifier = Modifier.semantics { contentDescription = "Localized Description"},
                    value = sliderPosition,
                    onValueChange = { sliderPosition = it },
                    valueRange = 150f..300f,
                    steps = 150
                )
            }

        }
        item { Spacer(modifier = Modifier.height(35.dp)) }
        item {
            androidx.compose.material3.Button(
                onClick = {
                    if ((RodeWil > 0) || (ZwarteWil > 0) || (WitteWil > 0)) {
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    contentColor = androidx.compose.material3.MaterialTheme.colorScheme.background
                )
            )
            {
                androidx.compose.material3.Text(
                    text = "Order",
                    fontSize = 50.sp
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
                    contentColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimary,
                    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier
                    .padding(5.dp)
            ) {
                androidx.compose.material3.Icon(Icons.Filled.ArrowBack, contentDescription = "Go back")


            } }
    }
}