package fr.tmmcl.chatvoiture;

import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient;
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.Request;
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.Response
import okio.IOException
import java.io.File
class HttpClientOk
{
    private var _client = OkHttpClient();

    //form/request-body POST
    //ref: https://square.github.io/okhttp/recipes/#posting-form-parameters-kt-java
    private suspend fun post(body: RequestBody, url: String) : Response {
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        val response = _client.newCall(request).execute();//-.await();

        println(response.body!!.string())//

        return response;
    }

    suspend fun signup(username: String, password: String) : Boolean {
        val formBody = FormBody.Builder()
            .add(username, Hash256.hashPassword(password))
            .build()

        val response = post(formBody, API.getUrl(API.requests.signup.str))

        //à modifier selon la reponse attendu si on doit lire le corps de la reponse :

        if (!response.isSuccessful)
        {
            println("signup failed $response");
            return false;
        };
        response.body?.close();
        return  true;
    }
    suspend fun login(username: String, password: String) : Boolean {
        val formBody = FormBody.Builder()
            .add(username, Hash256.hashPassword(password))
            .build()

        val response = post(formBody, API.getUrl(API.requests.login.str))

        //à modifier selon la reponse attendu on veut peut etre un token à réutiliser :

        if (!response.isSuccessful)
        {
            println("login failed $response");
            return false;
        };
        return  true;
    }

    // à tester
    //ref: https://square.github.io/okhttp/recipes/#posting-a-multipart-request-kt-java
    suspend fun updateCarId(token: String, imageFile: File) : Boolean
    {
        val imageBody : RequestBody;

        try
        {
            imageBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull());
        }
        catch (e: IOException)
        {
            println("failed to load image $e");
            return false;
        }

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("token", token)
            .addFormDataPart("image","unverified_id.png", imageBody)
            .build()

        val response = post(requestBody, API.getUrl(API.requests.login.str))

        //à modifier selon la reponse attendu ? :

        if (!response.isSuccessful)
        {
            println("upload failed $response");
            return false;
        };
        return  true;
    }
}