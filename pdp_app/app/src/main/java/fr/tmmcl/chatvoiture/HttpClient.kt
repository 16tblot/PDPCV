package fr.tmmcl.chatvoiture;

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.Buffer
import java.io.InputStream


class HttpClient
{
    private val JSON: MediaType? = "application/json; charset=utf-8".toMediaTypeOrNull();
    private final val _client = OkHttpClient();

    //form/request-body POST
    //ref: https://square.github.io/okhttp/recipes/#posting-form-parameters-kt-java
    private fun post(body: RequestBody, url: String) : Response? {

        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        var response: Response? = null;

        try
        {
            log(requestBodyToString(request.body!!));//dbg
            response = _client.newCall(request).execute();
        }
        catch (e: Exception) {
            log(e.toString())
        }

        return response;
    }

    //debug:
    private fun requestBodyToString(requestBody: RequestBody): String {
        val buffer = Buffer()
        requestBody.writeTo(buffer)
        return buffer.readUtf8()
    }

    fun signup(username: String, password: String) : Boolean
    {
        val json = Json.encodeToString(API.Credentials(username.trim(), password));
        val body = json.toRequestBody(JSON);

        val response = post(body, API.getUrl(API.requests.signup.str)) ?: return false;

        //à modifier si on doit lire le corps de la reponse :

        if (!response.isSuccessful){
            log("Signup failed.\n$response");
            return false;
        };

        response.close();

        return true;
    }
    fun login(username: String, password: String) : API.LoginResponse? //String? : return string or null
    {
        val json = Json.encodeToString(API.Credentials(username.trim(), password));
        val reqBody = json.toRequestBody(JSON);

        val response = post(reqBody, API.getUrl(API.requests.login.str)) ?: return null;

        if (!response.isSuccessful) return null;

        val respBody = response.body!!.string() //(on peut faire response.body!!.string() qu'une fois par contre apparement)

        //dbg
        log(response);
        log(respBody);
        //

        val respParsed : API.LoginResponse;

        try
        {
            respParsed = Json.decodeFromString<API.LoginResponse>(respBody);
        }
        catch (e: Exception)
        {
            //le parsing peut echouer si l'API est mise à jour et la structure de la reponse differe
            log(e);
            return null;
        }

        //dbg
        log(respParsed.message);
        log(respParsed.token)
        //

        response.close();

        return  respParsed;
    }

    fun updateUser(token: String, immatriculation: String, phone: String, greyCard: ByteArray?) : Boolean
    {
        //(kotlin serialisation ignores optional and null/empty args)
        val json = Json.encodeToString(API.UserDataReq(token, immatriculation, phone, greyCard))

        val reqBody = json.toRequestBody(JSON);
        val response = post(reqBody, API.getUrl(API.requests.update_userdata.str)) ?: return false;

        //dbg
        val respBody = response.body!!.string()
        log(response);
        log(respBody);
        //

        return response.isSuccessful;//only check if response code indicates success
    }

    fun deleteUser(token: String) : Boolean
    {
        val json = Json.encodeToString(API.UserDeleteReq(token));
        val reqBody = json.toRequestBody(JSON);

        val request = Request.Builder()
            .url(API.getUrl(API.requests.delete_account.str))
            .delete(reqBody)//DELETE http request type
            .build()

        var response: Response? = null;

        try
        {
            log(requestBodyToString(request.body!!));//dbg
            response = _client.newCall(request).execute();
        }
        catch (e: Exception) {
            log(e.toString())
        }

        //dbg
        val respBody = response?.body!!.string()
        log(response);
        log(respBody);
        //

        return response.isSuccessful;//only check if response code indicates success
    }

    fun contactUser(sender_token: String, receiver_immatriculation: String) : Boolean
    {
        val json = Json.encodeToString(API.ContactUser(sender_token, receiver_immatriculation))
        val body = json.toRequestBody(JSON)

        val response = post(body, API.getUrl(API.requests.send_connection_request.str)) ?: return false;

        //dbg
        val respBody = response.body!!.string()
        log(response);
        log(respBody);
        //

        if (!response.isSuccessful){
            log("Friend request failed.\n$response");
            return false;
        }
        response.close();
        return true;
    }

    fun viewFriendRequestReceive(token: String) : Array<API.FriendRequest>? {
        val json = Json.encodeToString(API.SendUser(token))
        val body = json.toRequestBody(JSON)

        val response = post(body, API.getUrl(API.requests.get_all_connections.str)) ?: return null

        if (!response.isSuccessful){
            log("Pas de demande d'amis en attente, va prendre la route!\n$response")
            return null
        }

        val responseBody = response.body?.string()
        response.close()

        log(responseBody!!);

        val friendRequests = Json.decodeFromString<API.FriendRequests>(responseBody)
        log(friendRequests.receive)
        return friendRequests.receive
    }


    fun acceptFriendRequest(token: String, target_immatriculation: String) : Boolean {
        val json = Json.encodeToString(API.AnswerConnection(token, target_immatriculation))
        val body = json.toRequestBody(JSON)

        val response = post(body, API.getUrl(API.requests.accept_connection.str)) ?: return false;

        //dbg
        val respBody = response.body!!.string()
        log(response);
        log(respBody);
        //

        if (!response.isSuccessful){
            log("Failed to accept friend request.\n$response");
            return false;
        }
        response.close();
        return true;
    }

    fun rejectFriendRequest(token: String, target_immatriculation: String) : Boolean {
        val json = Json.encodeToString(API.AnswerConnection(token, target_immatriculation))
        val body = json.toRequestBody(JSON)

        val response = post(body, API.getUrl(API.requests.reject_connection.str)) ?: return false;

        //dbg
        val respBody = response.body!!.string()
        log(response);
        log(respBody);
        //

        if (!response.isSuccessful){
            log("Failed to reject friend request.\n$response");
            return false;
        }
        response.close();
        return true;
    }

    fun viewFriendRequestSend(token: String) : Array<API.FriendRequest>? {
        val json = Json.encodeToString(API.SendUser(token))
        val body = json.toRequestBody(JSON)

        val response = post(body, API.getUrl(API.requests.get_all_connections.str)) ?: return null

        if (!response.isSuccessful){
            log("Pas de demande d'amis en attente, va prendre la route!\n$response")
            return null
        }

        val responseBody = response.body?.string()
        response.close()

        log(responseBody!!);

        val friendRequests = Json.decodeFromString<API.FriendRequests>(responseBody)
        log(friendRequests.send)
        return friendRequests.send
    }

    fun getFriendList(token: String) : List<API.FriendInfo>? {
        val json = Json.encodeToString(API.SendUser(token))
        val body = json.toRequestBody(JSON)

        val response = post(body, API.getUrl(API.requests.get_friendlist.str)) ?: return null

        if (!response.isSuccessful){
            log("Pas encore d'amis, va prendre la route !\n$response")
            return null
        }

        val responseBody = response.body?.string()
        response.close()

        log(responseBody!!)

        val friendResponse = Json.decodeFromString<API.Friend>(responseBody)
        log("return httpclient")
        return friendResponse.friend
    }




    // à retester avec la prochaine maj de l'api
    //ref: https://square.github.io/okhttp/recipes/#posting-a-multipart-request-kt-java
    suspend fun uploadFile(token: String, fileName: String, inputStream: InputStream?, requestName: API.requests) : Boolean
    {
        val fileBody : RequestBody;

        try
        {
            val byteArray = inputStream!!.readBytes();
            inputStream.close();

            fileBody = byteArray.toRequestBody("*/*".toMediaTypeOrNull(), 0, byteArray.size);
        }
        catch (e: Exception)
        {
            log("failed to read file or parse as RequestBody $e");
            return false;
        }

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("token", token)
            .addFormDataPart("image", fileName, fileBody)
            .build()

        val response = post(requestBody, API.getUrl(requestName.str)) ?: return false;

        if (!response.isSuccessful)
        {
            log("upload failed $response");
            return false;
        };
        return  true;
    }
}