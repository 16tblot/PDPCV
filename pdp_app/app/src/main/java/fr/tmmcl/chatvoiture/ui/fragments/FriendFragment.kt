package fr.tmmcl.chatvoiture.ui.fragments

import FriendAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import fr.tmmcl.chatvoiture.R

class FriendFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_friend_accept, container, false)
        val allFriendRequestAccept = arguments?.getStringArrayList("allFriendRequestAccept") ?: arrayListOf()

        // Récupérer le RecyclerView
        val friendAcceptRecyclerView = view.findViewById<RecyclerView>(R.id.vertical_recycler_view)

        // Adapter le RecyclerView avec la liste des demandes d'amis
        friendAcceptRecyclerView.adapter = FriendAdapter(this.requireContext(), allFriendRequestAccept)
        return view
    }
}