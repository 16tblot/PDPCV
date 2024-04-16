package fr.tmmcl.chatvoiture;

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.Buffer
import java.io.File
import java.io.IOException


class HttpClientOk
{
    private val JSON: MediaType? = "application/json; charset=utf-8".toMediaTypeOrNull();

    private var _client = OkHttpClient();


    //form/request-body POST
    //ref: https://square.github.io/okhttp/recipes/#posting-form-parameters-kt-java
    private suspend fun post(body: RequestBody, url: String) : Response {

        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        //dbg
        println(request);

        try
        {
            println(requestBodyToString(request.body!!))
        }
        catch (e: IOException)
        {
            println(e)
        }
        //

        val response = _client.newCall(request).execute();//-.await();

        return response;
    }

    //debug:
    private fun requestBodyToString(requestBody: RequestBody): String {
        val buffer = Buffer()
        requestBody.writeTo(buffer)
        return buffer.readUtf8()
    }

    suspend fun signup(username: String, password: String) : Boolean
    {
        val json = Json.encodeToString(API.Credentials(username.trim(), password));
        val body = json.toRequestBody(JSON);

        val response = post(body, API.getUrl(API.requests.signup.str))

        //à modifier selon la reponse attendu si on doit lire le corps de la reponse :

        if (!response.isSuccessful)
        {
            println("Signup failed.\n$response");
            return false;
        };

        //dbg
        println(response);
        println(response.body!!.string())//(on peut faire response.body!!.string() qu'une fois par contre apparement https://stackoverflow.com/a/58097630)
        //

        response.close();

        return  true;
    }
    suspend fun login(username: String, password: String) : String? //String? : return string or null
    {
        val json = Json.encodeToString(API.Credentials(username.trim(), password));
        val reqBody = json.toRequestBody(JSON);

        val response = post(reqBody, API.getUrl(API.requests.login.str))

        //à modifier selon la reponse attendu on veut peut etre un token à réutiliser :

        if (!response.isSuccessful)
        {
            if(response.code == 401)
            {
                println("Correct login but user not validated.\n$response");
            }
            else println("Login failed.\n$response");

            return null;
        };

        val respBody = response.body!!.string() //(on peut faire response.body!!.string() qu'une fois par contre apparement)

        //dbg
        println(response);
        println(respBody)
        //

        response.close();

        //TODO:  parser le token et le return

        return  respBody;
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