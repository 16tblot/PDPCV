package fr.tmmcl.chatvoiture

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import fr.tmmcl.chatvoiture.databinding.ActivitySignupBinding
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SignupActivity : AppCompatActivity() {


    private lateinit var binding: ActivitySignupBinding
    private val httpClient = HttpClient();
    private lateinit var coroutineExceptionHandler: CoroutineExceptionHandler;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //databaseHelper = DatabaseHelper(this)
        coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
            throwable.printStackTrace()
        }

        binding.signupButton.setOnClickListener{
            val signupUsername = binding.signupUsername.text.toString()
            val signupPassword = binding.signupPassword.text.toString()//Hash256.hashPassword(binding.signupPassword.text.toString())
            //signupdatabase(signupUsername, signupPassword)
            signuphttp(signupUsername, signupPassword)
        }

        binding.loginRedirect.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun signuphttp(username: String, password: String){
        val context = this.baseContext;

        lifecycleScope.launch()
        {
            var signedUp = false;

            withContext(Dispatchers.IO + coroutineExceptionHandler)
            {
                signedUp = httpClient.signup(username, password);
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