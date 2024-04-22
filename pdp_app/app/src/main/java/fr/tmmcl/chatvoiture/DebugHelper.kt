package fr.tmmcl.chatvoiture

import android.util.Log

inline fun <reified T> T.log(message: Any)
{
    if(BuildConfig.DEBUG)
    {
        Log.d(T::class.simpleName, message.toString())
    }
}