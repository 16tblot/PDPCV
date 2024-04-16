package fr.tmmcl.chatvoiture

import kotlinx.serialization.Serializable

object API
{
    public val baseUrl = "https://thomas-blot.emi.u-bordeaux.fr/flask/"
    enum class requests(val str: String) {
        login("login"),
        signup("register"),
        set_car_id("set_car_id"),
        contact_car("contact_car")
    }

    fun getUrl(request: String) : String
    {
        return "$baseUrl$request";
    }

    @Serializable
    data class Credentials(val username: String, val password: String)

    public var userToken: String? = null;
}