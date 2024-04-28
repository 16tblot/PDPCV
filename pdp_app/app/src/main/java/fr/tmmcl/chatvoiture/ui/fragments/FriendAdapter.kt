import android.app.Application
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import fr.tmmcl.chatvoiture.AppViewModel
import fr.tmmcl.chatvoiture.R

class FriendAdapter(private val context: Context, private val friendRequestAccept: List<String>) : RecyclerView.Adapter<FriendAdapter.ViewHolder>() {

    private val viewModel: AppViewModel by lazy {
        ViewModelProvider.AndroidViewModelFactory.getInstance(context.applicationContext as Application).create(AppViewModel::class.java)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val immatriculationTextView: TextView = itemView.findViewById(R.id.friend_accept_ima)
        val phoneTextView: TextView = itemView.findViewById(R.id.friend_accept_ima)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.single_friend_accept, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val immatriculation = friendRequestAccept[position]
        holder.immatriculationTextView.text = immatriculation
        holder.phoneTextView.text = immatriculation //TODO : afficher le numero de tel
    }

    override fun getItemCount(): Int {
        return friendRequestAccept.size
    }
}
