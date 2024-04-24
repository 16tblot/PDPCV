package fr.tmmcl.chatvoiture.ui.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import fr.tmmcl.chatvoiture.API
import fr.tmmcl.chatvoiture.AppViewModel
import fr.tmmcl.chatvoiture.databinding.FragmentFormBinding
import fr.tmmcl.chatvoiture.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Form : Fragment() {
    private lateinit var binding: FragmentFormBinding
    private val viewModel: AppViewModel by viewModels()

    companion object {
        private const val REQUEST_CODE_PICK_FILE = 1
        private const val REQUEST_CODE_TAKE_PHOTO = 2
        private const val REQUEST_CODE_STORAGE_PERMISSION = 3
    }

    private var identiteImageUri: Uri? = null
    private var greyCardImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        super.onCreate(savedInstanceState);

        binding = FragmentFormBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        val pickMedia = createImagePicker();
        val openDocumentId = createFilePicker()
        { uri ->
            if (uri != null) {
                // Stockez l'URI de l'image sélectionnée dans la variable selectedImageUri
                identiteImageUri = uri
                binding.formButtonDocument.setImageURI(uri)
            }
        };

        val openDocumentCar = createFilePicker()
        { uri ->
            if (uri != null) {
                // Stockez l'URI de l'image sélectionnée dans la variable selectedImageUri
                greyCardImageUri = uri
                binding.formButtonGreyCard.setImageURI(uri)
            }
        };

        binding.formButtonDocument.setOnClickListener{

            //https://developer.android.com/training/data-storage/shared/documents-files
            //"Because the user is involved in selecting the files or directories that your app can access, this mechanism doesn't require any system permissions"
            //if(!checkAskPermission()) return@setOnClickListener

            //https://developer.android.com/training/data-storage/shared/photopicker#persist-media-file-access pas forcement besoin de permission non plus
            //pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.SingleMimeType("image/*")))
            openDocumentId.launch(arrayOf("image/*", "application/pdf"))
        }


        binding.formButtonGreyCard.setOnClickListener {
            openDocumentCar.launch(arrayOf("image/*", "application/pdf"))
        }

        binding.formButton.setOnClickListener {
            val phone = binding.formTextPhone.text.toString()
            val immatriculation = binding.formTextImmatriculation.text.toString()

            formhttp(immatriculation, phone, identiteImageUri, greyCardImageUri)
        }

        // Bouton pour prendre une photo
        /*val takePhotoButton: Button = findViewById(R.id.take_photo_button)
        takePhotoButton.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(takePictureIntent, REQUEST_CODE_TAKE_PHOTO)
        }*/
    }


    private fun createImagePicker(): ActivityResultLauncher<PickVisualMediaRequest>
    {
        return registerForActivityResult(ActivityResultContracts.PickVisualMedia())
        {
                uri ->

            if (uri != null)
            {
                // Stockez l'URI de l'image sélectionnée dans la variable selectedImageUri
                identiteImageUri = uri
                binding.formButtonDocument.setImageURI(uri) // Afficher l'image sélectionnée dans l'ImageButton
            }
            else log("PhotoPicker", "No media selected")
        }
    }

    private fun createFilePicker(lamdba: (Uri) -> Any): ActivityResultLauncher<Array<String>>
    {
        return registerForActivityResult(ActivityResultContracts.OpenDocument())
        {
                uri->
            lamdba.invoke(uri!!);
        }
    }

    //https://stackoverflow.com/a/73630987
    //https://developer.android.com/training/data-storage/shared/documents-files
    //"Because the user is involved in selecting the files or directories that your app can access, this mechanism doesn't require any system permissions"
    /*
    private fun isPermissionGranted() : Boolean
    {
        return (ContextCompat.checkSelfPermission(this.baseContext, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }
    private fun checkAskPermission() : Boolean
    {
        var granted = isPermissionGranted();

        if(!granted)
        {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_CODE_STORAGE_PERMISSION)

            granted = isPermissionGranted();
        }

        return granted;
    }*/


    private fun formhttp(immatriculation:String, phone:String, identite:Uri?, greyCard:Uri?) {
        val ctx = this.context;

        viewModel.viewModelScope.launch()
        {
            var success = false;

            //thread io :
            withContext(Dispatchers.IO)
            {
                success = viewModel.httpClient.updateUserStrings(API.userToken!!, immatriculation, phone);

                //test future 'blob':
                /*
                val doc = DocumentFile.fromSingleUri(ctx, identite!!);
                val inputStream = contentResolver.openInputStream(identite!!);
                httpClient.uploadFile(API.userToken!!, doc?.name!!, inputStream, API.requests.send_car_id)
                */
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