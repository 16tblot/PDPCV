package fr.tmmcl.chatvoiture

object API
{
    public val baseUrl = "http://192.168.28.174:8000/app/"//http://192.168.1.145:8000/app/"https://thomas-blot.emi.u-bordeaux.fr"
    enum class requests(val str: String) {
        login("login"),
        signup("signup"),
        set_car_id("set_car_id"),
        contact_car("contact_car")
    }

    fun getUrl(request: String) : String
    {
        return "$baseUrl$request";
    }
}