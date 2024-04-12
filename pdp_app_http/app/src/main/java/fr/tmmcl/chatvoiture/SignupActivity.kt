package fr.tmmcl.chatvoiture

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import fr.tmmcl.chatvoiture.databinding.ActivitySignupBinding
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var httpClient: HttpClientOk
    private lateinit var coroutineExceptionHandler: CoroutineExceptionHandler;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseHelper = DatabaseHelper(this)
        httpClient = HttpClientOk()
        coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
            throwable.printStackTrace()
        }

        binding.signupButton.setOnClickListener{
            val signupUsername = binding.signupUsername.text.toString()
            val signupPassword = binding.signupUsername.text.toString()
            signuphttp(signupUsername, signupPassword, HttpImpl.RETROFIT)
        }

        binding.loginRedirect.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun signupdatabase(username: String, password: String){
        val insertedRowId = databaseHelper.insertUser(username, password)
        if( insertedRowId != -1L){
            Toast.makeText(this, "Signup Successful", Toast.LENGTH_LONG).show()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Signup Failed", Toast.LENGTH_LONG).show()
        }
    }

    private enum class HttpImpl
    {
        RETROFIT, OKHTTP
    }
    private fun signuphttp(username: String, password: String, implementation: HttpImpl){
        //var signedUp = false;

        val context = this.baseContext;

        lifecycleScope.launch()
        {
            var signedUp = false;

            withContext(Dispatchers.IO + coroutineExceptionHandler) {
                try
                {
                    if(implementation == HttpImpl.RETROFIT)
                        signedUp = HttpClientRetrofit.login(username, password);
                    if(implementation == HttpImpl.OKHTTP)
                        signedUp = httpClient.signup(username, password);
                }
                catch (e: IOException) {
                    println(e.toString())
                }
                catch (e: HttpException) {
                    println(e.toString())
                }
            }

            if (signedUp) {
                Toast.makeText(context, "Signup Successful", Toast.LENGTH_LONG).show()
                val intent = Intent(context, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(context, "Signup Failed", Toast.LENGTH_LONG).show()
            }
        }
    }
}