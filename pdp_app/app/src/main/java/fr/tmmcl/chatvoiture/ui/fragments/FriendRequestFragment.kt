package fr.tmmcl.chatvoiture.ui.fragments

import FriendRequestAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import fr.tmmcl.chatvoiture.R

class FriendRequestFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_friend_request, container, false)
        val friendRequestPending = arguments?.getStringArrayList("friendRequestPending") ?: arrayListOf()

        //recuperer le recyclerview
        val friendRequestRecyclerView = view?.findViewById<RecyclerView>(R.id.friend_request_recyclerview)
        friendRequestRecyclerView?.adapter = FriendRequestAdapter(this.requireContext(), friendRequestPending)
        return view
    }

}