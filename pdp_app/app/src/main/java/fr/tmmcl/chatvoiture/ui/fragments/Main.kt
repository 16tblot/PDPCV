package fr.tmmcl.chatvoiture.ui.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import fr.tmmcl.chatvoiture.API
import fr.tmmcl.chatvoiture.AppViewModel
import fr.tmmcl.chatvoiture.R
import fr.tmmcl.chatvoiture.databinding.FragmentMainBinding
import fr.tmmcl.chatvoiture.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Main : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private val viewModel: AppViewModel by viewModels()
    private var certifiedArg: Boolean = false;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.isUserCertified().observe(this)
        {
            isCertified ->
                if(isCertified) switchToVerifiedComponents()
        }
        certifiedArg = arguments?.getBoolean("certified")!!;
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        // Inflate the layout for this fragment
        binding = FragmentMainBinding.inflate(inflater, container, false);
        return binding.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(!certifiedArg)
        {
            initUnverifiedComponents()
        }
        else
        {
            switchToVerifiedComponents();
            initVerifiedComponents();
        }

        //commun
        binding.deleteAccountBtn.setOnClickListener(){
            // Code d'une pop-up
            val alertDialogBuilder = AlertDialog.Builder(this.requireContext())
            alertDialogBuilder.setMessage("Voulez vous vraiment supprimer votre compte?")
                .setCancelable(false)
                .setPositiveButton("Oui")
                {
                    dialog, id ->

                    val ctx = this.context;
                    var success : Boolean = false;

                    viewModel.viewModelScope.launch()
                    {
                        withContext(Dispatchers.IO)
                        {
                            // supprimer le compte
                            success = viewModel.httpClient.deleteUser(API.userToken!!);
                        }
                        if(success) //plus d'utilisateur: retourner au login
                        {
                            Toast.makeText(ctx, "Deletion successful", Toast.LENGTH_LONG).show()
                            findNavController().navigate(R.id.action_MainFragment_to_LoginFragment);
                        }
                        else Toast.makeText(ctx, "Deletion failed", Toast.LENGTH_LONG).show()
                    }

                }
                .setNegativeButton("Non") { dialog, id -> dialog.dismiss() }

            alertDialogBuilder.create().show()
        }
    }

    private fun initUnverifiedComponents()
    {
        log("init unverified components");
        binding.unverified.unverifiedButtonReceiving.setOnClickListener {
            val bundle = Bundle().apply {
                putBoolean("certifiedArg", certifiedArg)
            }
            findNavController().navigate(R.id.action_MainFragment_to_FormFragment, bundle)
        }

        binding.sendImmatriculation.setOnClickListener {
            val alertDialogBuilder = AlertDialog.Builder(this.requireContext())
            alertDialogBuilder.setMessage("Vérifie d'abord ton identité avant d'utiliser notre application !")

            alertDialogBuilder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss() // fermeture pop-up
            })

            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }

    }

    private fun switchToVerifiedComponents()
    {
        log("switch to verified components");
        binding.verified.root.isEnabled = true;
        binding.verified.root.visibility = View.VISIBLE;

        binding.unverified.root.isEnabled = false;
        binding.unverified.root.visibility = View.GONE;
    }

    private fun initVerifiedComponents()
    {
        binding.sendImmatriculation.setOnClickListener {
            val immatriculation = binding.immatriculationTxt.text.toString()

            val ctx = requireContext()

            // Appeler la fonction contactUser dans une coroutine
            viewModel.viewModelScope.launch {
                var success = false

                // Utilisation de withContext pour exécuter la requête HTTP de manière asynchrone sur le thread IO
                withContext(Dispatchers.IO) {
                    success = viewModel.httpClient.contactUser(API.userToken!!, immatriculation)
                }

                // Affichage de l'état de la requête dans une AlertDialog
                val alertDialogBuilder = AlertDialog.Builder(ctx)
                alertDialogBuilder
                    .setMessage(if (success) "La demande a été envoyée avec succès." else "Échec de l'envoi de la demande pour la plaque d'immatriculation $immatriculation.")
                    .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                    .create()
                    .show()
            }
        }

        binding.verified.verifyUpdateData.setOnClickListener(){
            val bundle = Bundle().apply {
                putBoolean("certifiedArg", certifiedArg)
            }
            findNavController().navigate(R.id.action_MainFragment_to_FormFragment, bundle)
        }

        binding.verified.verifyButtonFriend.setOnClickListener(){

        }

        binding.verified.verifyButtonReceiving.setOnClickListener {
            val ctx = requireContext()
            viewModel.viewModelScope.launch {
                var friends: Array<API.FriendRequest>? = null

                // Utilisation de withContext pour exécuter la requête HTTP de manière asynchrone sur le thread IO
                withContext(Dispatchers.IO) {
                    friends = viewModel.httpClient.viewFriendRequest(API.userToken!!)
                }

                if (friends != null) {
                    val friendRequestPending = mutableListOf<String>()
                    for (request in friends!!)
                        if(request.status.equals("pending"))
                            friendRequestPending.add(request.immatriculation)

                    val bundle = Bundle().apply {
                        putStringArrayList("friendRequestPending", ArrayList(friendRequestPending))
                    }
                    findNavController().navigate(R.id.action_MainFragment_to_FriendRequestFragment, bundle)

                }
                else {
                    val alertDialogBuilder = AlertDialog.Builder(ctx)
                    alertDialogBuilder
                        .setMessage("Échec : pas de demande d'amis en attente.")
                        .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                        .create()
                        .show()
                }
            }
        }

        binding.verified.verifyButtonSending.setOnClickListener(){

        }

    }
}