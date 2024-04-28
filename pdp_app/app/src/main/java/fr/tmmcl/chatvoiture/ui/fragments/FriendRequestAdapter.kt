import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fr.tmmcl.chatvoiture.API
import fr.tmmcl.chatvoiture.R

class FriendRequestAdapter(private val friendRequestPending: List<String>) : RecyclerView.Adapter<FriendRequestAdapter.ViewHolder>() {

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
            //API.userToken!!
            // Action à effectuer lors de l'acceptation de la demande d'ami
        }
        holder.rejectButton.setOnClickListener {
            // Action à effectuer lors du rejet de la demande d'ami
        }
    }

    override fun getItemCount(): Int {
        return friendRequestPending.size
    }
}
