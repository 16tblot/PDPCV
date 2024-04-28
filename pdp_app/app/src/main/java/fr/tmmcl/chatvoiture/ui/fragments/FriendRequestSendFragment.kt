package fr.tmmcl.chatvoiture.ui.fragments

import FriendRequestSendAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import fr.tmmcl.chatvoiture.R

class FriendRequestSendFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_friend_request_send, container, false)
        val friendRequestSendPending = arguments?.getStringArrayList("friendRequestSendPending") ?: arrayListOf()

        // Récupérer le RecyclerView
        val friendRequestRecyclerView = view.findViewById<RecyclerView>(R.id.vertical_recycler_view)

        // Adapter le RecyclerView avec la liste des demandes d'amis
        friendRequestRecyclerView.adapter = FriendRequestSendAdapter(this.requireContext(), friendRequestSendPending)
        return view
    }
}