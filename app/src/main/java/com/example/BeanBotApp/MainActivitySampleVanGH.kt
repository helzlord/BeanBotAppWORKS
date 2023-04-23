package com.example.BeanBotApp

import android.content.Intent.getIntent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BeanBotApp.api.UserApi
import com.example.BeanBotApp.ui.theme.ApiExampleTheme
import com.example.BeanBotApp.ui.theme.Purple700
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import android.content.Intent
import androidx.compose.ui.platform.LocalContext



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

data class ProfileModel(
    var age: String,
    var name: String,
    var email: String,
)

data class UserModel(
    var profile: ProfileModel
)

@Composable
fun MainScreen(IP : String?) {

   Scaffold(
       topBar = {
           TopAppBar(
               backgroundColor = Purple700,
               title = {
                   Text(
                       text = "Simple API Request",
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
              val id = remember {
                  mutableStateOf(TextFieldValue())
              }

              val profile = remember {
                  mutableStateOf(ProfileModel(
                      age = "",
                      name = "",
                      email = ""
                  ))
              }

              Text(
                  text="API Sample",
                  style= TextStyle(
                      fontSize = 40.sp,
                      fontFamily = FontFamily.Cursive
                  )
              )

              Spacer(modifier = Modifier.height(15.dp))

              TextField(
                  label = { Text(text = "User ID")},
                  value = id.value,
                  onValueChange = { id.value = it }
              )

              Spacer(modifier = Modifier.height(15.dp))

              Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
                  Button(
                      onClick = {
                          val data = sendRequest(
                              id = id.value.text,
                              profileState = profile,
                              ip_adres = IP
                          )

                          Log.d("Main Activity", profile.toString())
                      }
                  ) {
                      Text(text = "Get Data")
                  }
              }
              
              Spacer(modifier = Modifier.height(15.dp))
              
              Text(text = profile.component1().toString(), fontSize = 40.sp)
          }
       }
   )
}

fun sendRequest(
    id: String,
    profileState: MutableState<ProfileModel>,
    ip_adres : String?
) {

    val ip = if(ip_adres.isNullOrEmpty()) "http://192.168.0.141:3000" else ip_adres
    val retrofit = Retrofit.Builder()
        .baseUrl(ip)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api = retrofit.create(UserApi::class.java)

    val call: Call<UserModel?>? = api.getUserById(id);

    call!!.enqueue(object: Callback<UserModel?> {
        override fun onResponse(call: Call<UserModel?>, response: Response<UserModel?>) {
            if(response.isSuccessful) {
                Log.d("Main", "success!" + response.body().toString())
                profileState.value = response.body()!!.profile
            }
        }

        override fun onFailure(call: Call<UserModel?>, t: Throwable) {
            Log.e("Main", "Failed mate " + t.message.toString())
        }
    })
}