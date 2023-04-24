package com.example.BeanBotApp.api


import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path


public interface UserApi {
    /*@Headers(
        "Accept: application/json, text/html"
    )*/
    /*
    @GET("users/{id}")
    abstract fun getUserById(@Path("id") id: String): Call<UserModel?>?
    //@POST("users/{id}")

    //abstract fun createUser(@Path("id") id: String,@Body user: UserModel?): Call<UserModel?>?

    @POST("users")

    abstract fun createUser(@Body user: UserModel?): Call<UserModel?>?
    */
    @GET("dummy")
    abstract fun getArduinoData(): Call<String?>?

    //@POST("users/{id}")

    //abstract fun createUser(@Path("id") id: String,@Body user: UserModel?): Call<UserModel?>?

    @POST("CMDS/{commando}/CMDEND")

    abstract fun postCommand(@Path("commando") commando: String): Call<String?>?
    // Errors hier boeien niet echt want arduino leest cmds

}