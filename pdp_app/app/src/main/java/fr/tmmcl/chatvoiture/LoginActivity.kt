package fr.tmmcl.chatvoiture

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import fr.tmmcl.chatvoiture.databinding.ActivityLoginBinding
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var coroutineExceptionHandler: CoroutineExceptionHandler;
    private val httpClient = HttpClient();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //databaseHelper = DatabaseHelper(this)
        coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
            throwable.printStackTrace()
        }

        binding.loginButton.setOnClickListener{
            val loginUsername = binding.loginUsername.text.toString()
            val loginPassword = binding.loginPassword.text.toString()
            loginhttp(loginUsername, loginPassword);
        }

        binding.signupRedirect.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun loginhttp(username: String, password: String)
    {
        val ctx = this.baseContext;

        //coroutine
        lifecycleScope.launch()
        {
            var response : API.LoginResponse? = null;

            //thread io:
            withContext(Dispatchers.IO + coroutineExceptionHandler)
            {
                //on utilise le thread 'io' car on ne veut pas bloquer le thread de l'ui
                response = httpClient.login(username, password);
            }

            //thread main/ui :

            if (response == null)
            {
                Toast.makeText(ctx, "Login Failed", Toast.LENGTH_LONG).show();
                return@launch;//return (depuis coroutine).
            }

            //TODO: enregistrer le token mais pas en clair
            API.userToken = response!!.token;

            Toast.makeText(ctx, "Login Successful", Toast.LENGTH_LONG).show()

            val nextActivity: Intent;

            if(API.isUserCertified(response!!)) nextActivity = Intent(ctx, FormActivity::class.java)
            else nextActivity = Intent(ctx, UnverifiedUserActivity::class.java)

            startActivity(nextActivity)
            finish()
        }
    }
}