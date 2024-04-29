import android.app.Application
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import fr.tmmcl.chatvoiture.API
import fr.tmmcl.chatvoiture.AppViewModel
import fr.tmmcl.chatvoiture.R
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class FriendAdapter(private val context: Context, private val friendsJson: String?) : RecyclerView.Adapter<FriendAdapter.ViewHolder>() {

    private val friends: List<API.FriendInfo> = Json.decodeFromString<List<API.FriendInfo>>(friendsJson ?: "[]")

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val immatriculationTextView: TextView = itemView.findViewById(R.id.friend_accept_ima)
        val phoneTextView: TextView = itemView.findViewById(R.id.friend_accept_phone)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.single_friend_accept, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val friend = friends[position]
        holder.immatriculationTextView.text = friend.immatriculation
        holder.phoneTextView.text = friend.phone ?: "N/A" // Affiche "N/A" si le téléphone est null
    }

    override fun getItemCount(): Int {
        return friends.size
    }
}
