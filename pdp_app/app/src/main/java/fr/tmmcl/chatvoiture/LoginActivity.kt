package fr.tmmcl.chatvoiture

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import fr.tmmcl.chatvoiture.databinding.ActivityLoginBinding
import fr.tmmcl.chatvoiture.databinding.ActivitySignupBinding
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var httpClient: HttpClientOk
    private lateinit var coroutineExceptionHandler: CoroutineExceptionHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseHelper = DatabaseHelper(this)
        httpClient = HttpClientOk()
        coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
            throwable.printStackTrace()
        }

        binding.loginButton.setOnClickListener{
            val loginUsername = binding.loginUsername.text.toString()
            val loginPassword = binding.loginUsername.text.toString()
            //logindatabase(loginUsername, loginPassword)
            loginhttp(loginUsername, loginPassword, HttpImpl.OKHTTP)
        }

        binding.signupRedirect.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun logindatabase(username: String, password: String){
        val userExists = databaseHelper.readUser(username, password)
        if(userExists){
            Toast.makeText(this, "Login Successful", Toast.LENGTH_LONG).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show()
        }
    }

    private enum class HttpImpl
    {
        RETROFIT, OKHTTP
    }

    private fun loginhttp(username: String, password: String, implementation: HttpImpl)
    {
        val ctx = this.baseContext;

        lifecycleScope.launch()
        {
            var logedIn = false;

            //thread io :

            withContext(Dispatchers.IO + coroutineExceptionHandler)
            {
                try
                {
                    if(implementation == HttpImpl.RETROFIT)
                        logedIn = HttpClientRetrofit.login(username, password);
                    if(implementation == HttpImpl.OKHTTP)
                        logedIn = httpClient.login(username, password);
                }
                catch (e: IOException) {
                    println(e.toString())
                }
                catch (e: HttpException) {
                    println(e.toString())
                }
            }

            //thread main/ui :

            if (logedIn) {
                Toast.makeText(ctx, "Login Successful", Toast.LENGTH_LONG).show()
                val intent = Intent(ctx, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(ctx, "Login Failed", Toast.LENGTH_LONG).show()
            }
        }
    }
}