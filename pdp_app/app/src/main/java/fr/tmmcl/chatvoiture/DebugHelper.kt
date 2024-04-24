package fr.tmmcl.chatvoiture

import android.util.Log

inline fun log(tag: String?, message: Any)
{
    if(BuildConfig.DEBUG)
    {
        Log.d(tag, message.toString())
    }
}

inline fun <reified T> T.log(message: Any)
{
    return log(T::class.simpleName, message.toString())
}