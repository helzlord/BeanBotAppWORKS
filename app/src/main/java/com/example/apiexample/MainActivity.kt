package com.example.apiexample

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.apiexample.api.UserApi
import com.example.apiexample.ui.theme.ApiExampleTheme
import com.example.apiexample.ui.theme.Purple700
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import androidx.compose.material.Text

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ApiExampleTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainScreen()
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
fun MainScreen() {
    var RodeBonen = "70%"
    var ZwarteBonen = "50%"
    var WitteBonen = "69%"
    var RodeWil = 0
    var ZwarteWil = 0
    var WitteWil = 0
    var sliderPosition by remember { mutableStateOf(150f) }
    var gewichtBonen = sliderPosition.toInt()
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
                    mutableStateOf(
                        ProfileModel(
                            age = "",
                            name = "",
                            email = ""
                        )
                    )
                }

                Text(
                    text = "API Sample",
                    style = TextStyle(
                        fontSize = 40.sp,
                        fontFamily = FontFamily.Cursive
                    )
                )

                Spacer(modifier = Modifier.height(15.dp))

                TextField(
                    label = { Text(text = "User ID") },
                    value = id.value,
                    onValueChange = { id.value = it }
                )

                Spacer(modifier = Modifier.height(15.dp))

                Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
                    Button(
                        onClick = {
                            val data = sendRequest(
                                id = id.value.text,
                                profileState = profile
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
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    )
    {
        item{Text(text = RodeBonen, fontSize = 35.sp)}
        item {
            Button(
                onClick = { RodeWil++ }
            )
            {
                Text(text = "Rode Bonen")
            }
        }
        item {
            Row() {
                Text(text = ZwarteBonen, fontSize = 35.sp)
                Spacer(modifier = Modifier.width(30.dp))
                Button(
                    onClick = { ZwarteWil++ }
                )
                {
                    Text(text = "Zwarte Bonen")
                }
            }
        }
        item {
            Box() {
                Row() {
                    Text(text = WitteBonen, fontSize = 35.sp)
                    Spacer(modifier = Modifier.width(30.dp))
                    Button(
                        onClick = { WitteWil++ },
                    )
                    {
                        Text(text = "Witte Bonen")
                    }
                }
            }
        }
        item {
            Box() {
                Text(
                    text = "Duid het gewenste gewicht aan op de slider",
                    fontSize = 18.sp
                )
            }
        }
        item {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(1.dp)
            ) {
                Text(
                    modifier = Modifier.width(50.dp),
                    text = gewichtBonen.toString(),
                    fontSize = 20.sp
                )
            }
        }
        item {
            Slider(
                modifier = Modifier.semantics { contentDescription = "Localized Description" },
                value = sliderPosition,
                onValueChange = { sliderPosition = it },
                valueRange = 150f..300f,
                steps = 150
            )
        }
        item {
            Button(
                onClick = {
                    if ((RodeWil > 0) || (ZwarteWil > 0) || (WitteWil > 0)) {
                    }
                }
            )
            {
                Text(
                    text = "Order",
                    fontSize = 50.sp
                )
            }
        }
    }
}

fun sendRequest(
    id: String,
    profileState: MutableState<ProfileModel>
) {
    val retrofit = Retrofit.Builder()
        .baseUrl("http://192.168.0.109:3000")
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