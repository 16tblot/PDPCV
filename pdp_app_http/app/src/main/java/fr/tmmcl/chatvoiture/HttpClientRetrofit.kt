package fr.tmmcl.chatvoiture

import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import okhttp3.FormBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.converter.scalars.ScalarsConverterFactory

//ref: https://developer.android.com/codelabs/basic-android-kotlin-compose-getting-data-internet

private val client = Retrofit.Builder()
    .baseUrl(API.baseUrl)
    .addConverterFactory(ScalarsConverterFactory.create())
    .build()

interface ChatVoitureApi
{
    @POST("login")
    suspend fun login(@Body body: FormBody): Response<String>

    @POST("signup")
    suspend fun signup(@Body body: FormBody): Response<String>

    //non testé:
    @Multipart
    @PUT("upload")
    suspend fun updateCarId(@Part("image") image: RequestBody, @Part token: String): String
}

object HttpClientRetrofit
{
    val httpService: ChatVoitureApi by lazy {
        client.create(ChatVoitureApi::class.java)
    }

    suspend fun login(username: String, password: String) : Boolean {
        val formBody = FormBody.Builder()
            .add(username, Hash256.hashPassword(password))
            .build()

        //à modifier selon la reponse attendu on veut peut etre un token à réutiliser :

        val response = httpService.login(formBody);

        println(response.body().toString())

        return response.isSuccessful
    }

    suspend fun signup(username: String, password: String) : Boolean
    {
        return login(username, password);
    }
}