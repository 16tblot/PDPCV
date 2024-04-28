import android.app.Application
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import fr.tmmcl.chatvoiture.API
import fr.tmmcl.chatvoiture.AppViewModel
import fr.tmmcl.chatvoiture.R
import fr.tmmcl.chatvoiture.log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FriendRequestAdapter(private val context: Context, private val friendRequestPending: List<String>) : RecyclerView.Adapter<FriendRequestAdapter.ViewHolder>() {

    private val viewModel: AppViewModel by lazy {
        ViewModelProvider.AndroidViewModelFactory.getInstance(context.applicationContext as Application).create(AppViewModel::class.java)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val immatriculationTextView: TextView = itemView.findViewById(R.id.friend_request_ima)
        val acceptButton: Button = itemView.findViewById(R.id.yes_button)
        val rejectButton: Button = itemView.findViewById(R.id.nop_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.single_friend_request, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val immatriculation = friendRequestPending[position]
        holder.immatriculationTextView.text = immatriculation
        holder.acceptButton.setOnClickListener {
            // Action à effectuer lors de l'acceptation de la demande d'ami
            viewModel.viewModelScope.launch {
                var success = false
                // Utilisation de withContext pour exécuter la requête HTTP de manière asynchrone sur le thread IO
                withContext(Dispatchers.IO) {
                    success = viewModel.httpClient.acceptFriendRequest(API.userToken!!, immatriculation)
                }
                log(success)
                // Affichage de l'état de la requête dans une AlertDialog
                val alertDialogBuilder = AlertDialog.Builder(context)
                alertDialogBuilder
                    .setMessage(if (success) "Demande d'ami acceptée !" else "Something wrong happened")
                    .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                    .create()
                    .show()
            }
        }
        holder.rejectButton.setOnClickListener {
            // Action à effectuer lors du rejet de la demande d'ami
            viewModel.viewModelScope.launch {
                var success = false
                // Utilisation de withContext pour exécuter la requête HTTP de manière asynchrone sur le thread IO
                withContext(Dispatchers.IO) {
                    success = viewModel.httpClient.rejectFriendRequest(API.userToken!!, immatriculation)
                }
                // Affichage de l'état de la requête dans une AlertDialog
                val alertDialogBuilder = AlertDialog.Builder(context)
                alertDialogBuilder
                    .setMessage(if (success) "Demande d'ami refusée !" else "Something wrong happened")
                    .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                    .create()
                    .show()
            }
        }
    }

    override fun getItemCount(): Int {
        return friendRequestPending.size
    }
}
