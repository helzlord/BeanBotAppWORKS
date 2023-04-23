package com.example.BeanBotApp


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

import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BeanBotApp.api.UserApi
import com.example.BeanBotApp.ui.theme.ApiExampleTheme
import com.example.BeanBotApp.ui.theme.Purple700
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory



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



              Spacer(modifier = Modifier.height(15.dp))

              TextField(
                  label = { Text(text = "User ID")},
                  value = id.value,
                  onValueChange = { id.value = it }
              )

              Spacer(modifier = Modifier.height(15.dp))


              Row() {
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

                  Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
                      Button(
                          onClick = {
                              val data = postRequest(
                                  id = id.value.text,
                                  profileState = profile,
                                  ip_adres = IP
                              )

                              Log.d("Main Activity", profile.toString())
                          }
                      ) {
                          Text(text = "send Data")
                      }
                  }
              }

              
              Spacer(modifier = Modifier.height(15.dp))

              Text(text = "Output: ",fontSize = 20.sp)
              Text(text = profile.component1().toString(), fontSize = 20.sp)
          }
       }
   )
}

fun sendRequest(
    id: String,
    profileState: MutableState<ProfileModel>,
    ip_adres : String?
) {

    val ip = if(ip_adres.isNullOrEmpty()) "http://192.168.0.141:3000" else "http://"+ip_adres+":3000"

    val retrofit = Retrofit.Builder()
        .baseUrl(ip)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api = retrofit.create(UserApi::class.java)

    val call: Call<UserModel?>? = api.getUserById(id)

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

fun postRequest(
    id: String,
    profileState: MutableState<ProfileModel>,
    ip_adres : String?
) {

    val ip = if(ip_adres.isNullOrEmpty()) "http://192.168.0.141:3000" else "http://"+ip_adres+":3000"

    val retrofit = Retrofit.Builder()
        .baseUrl(ip)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api = retrofit.create(UserApi::class.java)

    val profMod = ProfileModel("69","bob","test@test.test")
    val U = UserModel(profMod)
    Log.d("Main","blob:" + U.toString())

    val call: Call<UserModel?>? = api.createUser(U)


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