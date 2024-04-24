package fr.tmmcl.chatvoiture.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import fr.tmmcl.chatvoiture.AppViewModel
import fr.tmmcl.chatvoiture.R
import fr.tmmcl.chatvoiture.databinding.FragmentSignupBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Signup : Fragment() {

    private lateinit var binding: FragmentSignupBinding
    private val viewModel: AppViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        super.onCreate(savedInstanceState);

        binding = FragmentSignupBinding.inflate(inflater, container, false);
        binding.signupButton.setOnClickListener{
            val signupUsername = binding.signupUsername.text.toString()
            val signupPassword = binding.signupPassword.text.toString()
            signuphttp(signupUsername, signupPassword)
        }

        binding.loginRedirect.setOnClickListener {
            findNavController().navigate(R.id.action_SignupFragment_to_LoginFragment);
        }
        // Inflate the layout for this fragment
        return binding.root;
    }

    private fun signuphttp(username: String, password: String){
        val context = this.context;

        viewModel.viewModelScope.launch()
        {
            var signedUp = false;

            withContext(Dispatchers.IO)
            {
                signedUp = viewModel.httpClient.signup(username, password);
            }

            if (signedUp) {
                Toast.makeText(context, "Signup Successful", Toast.LENGTH_LONG).show()
                findNavController().navigate(R.id.action_SignupFragment_to_LoginFragment);
            } else {
                Toast.makeText(context, "Signup Failed", Toast.LENGTH_LONG).show()
            }
        }
    }
}