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
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class FriendAdapter(private val context: Context, private val friendsJson: String?) : RecyclerView.Adapter<FriendAdapter.ViewHolder>() {

    private val viewModel: AppViewModel by lazy {
        ViewModelProvider.AndroidViewModelFactory.getInstance(context.applicationContext as Application).create(AppViewModel::class.java)
    }
    private val friends: List<API.FriendInfo> = Json.decodeFromString<List<API.FriendInfo>>(friendsJson ?: "[]")

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val immatriculationTextView: TextView = itemView.findViewById(R.id.friend_accept_ima)
        val phoneTextView: TextView = itemView.findViewById(R.id.friend_accept_phone)
        val deleteButton: Button = itemView.findViewById(R.id.friend_accept_delete_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.single_friend_accept, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val friend = friends[position]
        holder.immatriculationTextView.text = friend.immatriculation
        holder.phoneTextView.text = friend.phone

        holder.deleteButton.setOnClickListener {
            // Action à effectuer lors de l'acceptation de la demande d'ami
            viewModel.viewModelScope.launch {
                var success = false
                // Utilisation de withContext pour exécuter la requête HTTP de manière asynchrone sur le thread IO
                withContext(Dispatchers.IO) {
                    success = viewModel.httpClient.deleteFriend(API.userToken!!, friend.immatriculation)
                }
                log(success)
                // Affichage de l'état de la requête dans une AlertDialog
                val alertDialogBuilder = AlertDialog.Builder(context)
                alertDialogBuilder
                    .setMessage(if (success) "Ami supprimé !" else "Something wrong happened")
                    .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                    .create()
                    .show()
            }
        }
    }

    override fun getItemCount(): Int {
        return friends.size
    }
}
