package fr.tmmcl.chatvoiture.ui.fragments

import FriendAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import fr.tmmcl.chatvoiture.R
import fr.tmmcl.chatvoiture.log

class FriendFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_friend_request, container, false)

        // Récupérer le JSON à partir du Bundle
        val friendsJson = arguments?.getString("friendsJson")

        log("fragment")
        log(friendsJson!!)
        // Adapter le RecyclerView avec la liste des demandes d'amis
        val friendRequestRecyclerView = view.findViewById<RecyclerView>(R.id.vertical_recycler_view)
        friendRequestRecyclerView.adapter = FriendAdapter(this.requireContext(), friendsJson)

        return view
    }

}