package fr.tmmcl.chatvoiture

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import fr.tmmcl.chatvoiture.databinding.FormBinding
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FormActivity : AppCompatActivity() {

    private lateinit var binding: FormBinding
    private lateinit var coroutineExceptionHandler: CoroutineExceptionHandler;
    private val httpClient = HttpClient();

    /*private var identiteImageUri: Uri? = null

    companion object {
        private const val REQUEST_CODE_PICK_FILE = 1
        private const val REQUEST_CODE_TAKE_PHOTO = 2
        private const val REQUEST_CODE_STORAGE_PERMISSION = 3
    }*/


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.form)

        binding = FormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
            throwable.printStackTrace()
        }

        // Récupérez l'username du user donnée lors de la redirection depuis UnverifiedActivity
        val username = intent.getStringExtra("username")

        // Vérifier et demander l'autorisation de stockage externe si nécessaire
        /*if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_CODE_STORAGE_PERMISSION)
        }

        val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                // Stockez l'URI de l'image sélectionnée dans la variable selectedImageUri
                identiteImageUri = uri
                binding.formButtonDocument.setImageURI(uri) // Afficher l'image sélectionnée dans l'ImageButton
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

        binding.formButtonDocument.setOnClickListener{
            val mimeType = "image/jpeg"
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.SingleMimeType(mimeType)))
        }*/


        binding.formButtonGreyCard.setOnClickListener {

        }

        binding.formButton.setOnClickListener {
            val username = intent.getStringExtra("username").toString()
            val phone = binding.formTextPhone.text.toString()
            val immatriculation = binding.formTextImmatriculation.text.toString()

            val identite = "image"
            val greyCard = "image"
            formhttp(immatriculation, phone, identite, greyCard)
        }

        // Bouton pour prendre une photo
        /*val takePhotoButton: Button = findViewById(R.id.take_photo_button)
        takePhotoButton.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(takePictureIntent, REQUEST_CODE_TAKE_PHOTO)
        }*/
    }


    private fun formhttp(immatriculation:String, phone:String, identite:String, greyCard:String) {
        val ctx = this.baseContext;

        lifecycleScope.launch()
        {
            var success = false;

            //thread io :
            withContext(Dispatchers.IO + coroutineExceptionHandler)
            {
                success = httpClient.updateUserStrings(API.userToken!!, immatriculation, phone)
            }

            //thread main/ui :

            if (success) {
                Toast.makeText(ctx, "User data update successful", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(ctx, "User data update failed", Toast.LENGTH_LONG).show()
            }
        }
    }
}