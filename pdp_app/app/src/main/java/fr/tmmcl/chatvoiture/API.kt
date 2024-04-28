package fr.tmmcl.chatvoiture

import kotlinx.serialization.Serializable

object API
{
    public var userToken: String? = null;
    public val baseUrl = "https://thomas-blot.emi.u-bordeaux.fr/flask/"
    enum class requests(val str: String) {
        login("login"),
        signup("register"),
        update_userdata("update"),
        send_car_id("send_car_id"),
        contact_car("contact_car"),
        delete_account("delete"),
        send_connection_request("send_connection_request"),
        get_all_connections("get_all_connections"),
        accept_connection("accept_connection"),
        reject_connection("reject_connection")
    }

    fun getUrl(request: String) : String
    {
        return "$baseUrl$request";
    }

    //login/signup:
    @Serializable
    data class Credentials(val username: String, val password: String)
    @Serializable
    data class LoginResponse(val message: String, val token: String)
    //(signup not parsed, success=HTTP_OK)

    //if user logs in, server responds "Login successful" or "You are not certified"
    fun isUserCertified(response: LoginResponse): Boolean
    {
        return !response.message.contains("not certified");
    }

    //update/delete:
    @Serializable
    data class UserDataReq(val token: String, val immatriculation: String, val phone: String)

    @Serializable
    data class UserDataReqIma(val token: String, val immatriculation: String)

    @Serializable
    data class UserDataReqPhone(val token: String, val phone: String)
    @Serializable
    data class UserDeleteReq(val token: String)

    @Serializable
    data class ContactUser(val sender_token: String, val receiver_immatriculation: String)

    @Serializable
    data class ViewFriendRequest(val token: String)

    //get_all_connections
    @Serializable
    data class FriendRequest(val immatriculation: String, val status: String, val username: String)
    @Serializable
    data class FriendRequests(val receive: Array<FriendRequest>, val send : Array<FriendRequest>)
    //

    @Serializable
    data class AnswerConnection(val token: String, val target_immatriculation: String)

    @Serializable
    data class CommonResponse(val message: String)
}