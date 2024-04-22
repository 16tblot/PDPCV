package fr.tmmcl.chatvoiture

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import fr.tmmcl.chatvoiture.databinding.ActivityLoginBinding
import fr.tmmcl.chatvoiture.databinding.UnverifiedUserBinding
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class UnverifiedUserActivity : AppCompatActivity() {

    private lateinit var binding: UnverifiedUserBinding
    private var username = "lefavre"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = UnverifiedUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //databaseHelper = DatabaseHelper(this)

        binding.unverifiedButtonReceiving.setOnClickListener {
            val intent = Intent(this, FormActivity::class.java)
            intent.putExtra("username", username)
            startActivity(intent)
            finish()
        }

        binding.unverifiedSendImmatriculation.setOnClickListener {
            // Code d'une pop-up
            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setMessage("Vérifie d'abord ton identité avant d'utiliser notre application !")

            alertDialogBuilder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss() // fermeture pop-up
            })

            // Créez et affichez la pop-up
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }
    }
}