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
import java.io.IOException

class SignupActivity : AppCompatActivity() {


    private lateinit var binding: ActivitySignupBinding
    //private val client = OkHttpClient()
    //private val url = "https://chatvoiture.com/"
    private val httpClient = HttpClientOk();
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
            val signupPassword = Hash256.hashPassword(binding.signupPassword.text.toString())
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

            withContext(Dispatchers.IO + coroutineExceptionHandler) {
                try
                {
                    signedUp = httpClient.signup(username, password);
                }
                catch (e: IOException) {
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
    //    private fun signupdatabase(username: String, password: String){
//        val requestBody = FormBody.Builder()
//            .add("username", username)
//            .add("password", password)
//            .build()
//
//        val request = Request.Builder().url(url).post(requestBody).build()
//        client.newCall(request).execute().use { response ->
//            if (!response.isSuccessful) throw IOException("Unexpected code $response")
//
//            val responseBody = response.body?.string()
//            if (responseBody.isNullOrEmpty()){
//                Toast.makeText(this, "Signup Successful", Toast.LENGTH_LONG).show()
//                val intent = Intent(this, LoginActivity::class.java)
//                startActivity(intent)
//                finish()
//            } else {
//                Toast.makeText(this, "Signup Failed", Toast.LENGTH_LONG).show()
//            }
//        }
//    }

    /*
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
          val signupPassword = Hash256.hashPassword(binding.signupUsername.text.toString())
          signupdatabase(signupUsername, signupPassword)
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
  }*/
}