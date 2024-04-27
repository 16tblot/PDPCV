package fr.tmmcl.chatvoiture.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import fr.tmmcl.chatvoiture.API
import fr.tmmcl.chatvoiture.AppViewModel
import fr.tmmcl.chatvoiture.R
import fr.tmmcl.chatvoiture.databinding.FragmentLoginBinding
import fr.tmmcl.chatvoiture.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Login : Fragment() {

    private lateinit var binding: FragmentLoginBinding;
    private val viewModel: AppViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        super.onCreate(savedInstanceState)
        binding = FragmentLoginBinding.inflate(inflater, container, false);

        binding.loginButton.setOnClickListener{
            val loginUsername = binding.loginUsername.text.toString()
            val loginPassword = binding.loginPassword.text.toString()
            binding.loginPassword.setText("");
            loginhttp(loginUsername, loginPassword);
        }

        binding.signupRedirect.setOnClickListener {
            findNavController().navigate(R.id.action_LoginFragment_to_SignupFragment)
        }

        return binding.root
    }

    private fun loginhttp(username: String, password: String)
    {
        val ctx = this.context;

        //coroutine
        viewModel.viewModelScope.launch()
        {
            var response : API.LoginResponse? = null;

            //thread io:
            withContext(Dispatchers.IO){
                response = viewModel.httpClient.login(username, password);
            }

            //thread main/ui :
            if (response == null){
                Toast.makeText(ctx, "Login Failed", Toast.LENGTH_LONG).show();
                return@launch;//return (depuis coroutine).
            }

            //TODO: enregistrer le token mais pas en clair
            API.userToken = response!!.token;
            Toast.makeText(ctx, "Login Successful", Toast.LENGTH_LONG).show()

            val certified = API.isUserCertified(response!!);
            viewModel.setUserCertified(certified);
            log("userIsCertified=$certified");

            val bundle = bundleOf("certified" to certified);
            findNavController().navigate(R.id.action_LoginFragment_to_MainFragment, bundle)
        }
    }
}