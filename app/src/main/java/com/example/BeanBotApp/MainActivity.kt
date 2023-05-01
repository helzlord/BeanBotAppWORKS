package com.example.BeanBotApp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.Orientation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Absolute.Center
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.TextField
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.*
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint

import androidx.compose.ui.graphics.Color

import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType

import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import androidx.compose.ui.unit.sp
import com.example.BeanBotApp.ui.theme.ApiExampleTheme
import com.example.BeanBotApp.ui.theme.Grey
import com.example.apiexample.R


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ApiExampleTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Grey
                ) {
                    BgImage()
                    Box() {
                        Column(){
                            WelkomTekst()
                            InputField()
                        }

                    }
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun DefaultPreview2() {
    ApiExampleTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Grey
        ) {
            BgImage()
            Box() {
                Column {
                    WelkomTekst()
                    InputField()
                }
                
            }
        }

    }
}

@Composable
fun WelkomTekst(){
    Box(
        modifier = Modifier

            .padding(30.dp),
        contentAlignment = Alignment.TopCenter

    ){


        Text(text = "Vul het IP-adres van de Bean Bot in om te verbinden",
            // kleur hardcoded zodat altijd opvalt op de achtergrond
            style = MaterialTheme.typography.titleLarge.copy(

                color = Color.White,//MaterialTheme.colorScheme.onPrimary,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Left,
                lineHeight = 30.sp,
                
            )
        )
    }

}

@Composable
fun BgImage(){
    Box(
        modifier = with (Modifier){
            fillMaxSize()
                .paint(
                    // Replace with your image id
                    painterResource(id = R.drawable.renderfoto),
                    contentScale = ContentScale.FillHeight)

        })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputField(){

    var defaultText by remember { mutableStateOf("") }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.padding(10.dp)
    ){
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){

            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                shape = RoundedCornerShape(22.dp),

                //Anders raar lijntje onder search bar
                colors = TextFieldDefaults.textFieldColors(unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    textColor = MaterialTheme.colorScheme.primary,
                    containerColor = MaterialTheme.colorScheme.surface),


                leadingIcon = {Icon(painter = painterResource(id = R.drawable.baseline_speaker_phone_24), contentDescription = "searchIcon")},

                value = defaultText,
                onValueChange = {
                    defaultText = it
                },
                label = { Text("IP Bean Bot:", color = MaterialTheme.colorScheme.primary) },
                placeholder = {Text("xxx.xxx.xxx",color =MaterialTheme.colorScheme.primary)},

                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            val context = LocalContext.current
            Button(
                onClick = {  val intent = Intent(context, MainActivitySampleVanGH::class.java)
                    intent.putExtra("IP_BeanBot",defaultText)
                    context.startActivity(intent)
                          },
                colors = ButtonDefaults.buttonColors(
                    contentColor = MaterialTheme.colorScheme.background,
                ),
                modifier = Modifier
                    .padding(5.dp)
            ) {
                Icon( Icons.Filled.ArrowForward, contentDescription = "Submit")
            }

        }
    }
}
