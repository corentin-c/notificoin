package com.github.corentinc.notificoin.ui.home.searchesRecyclerView

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.github.corentinc.core.search.Search
import com.github.corentinc.notificoin.R
import com.github.corentinc.notificoin.ui.home.HomeFragmentDirections

class SearchViewHolder(
    inflater: LayoutInflater,
    parent: ViewGroup,
    private val searchAdapterListener: SearchAdapterListener
):
    RecyclerView.ViewHolder(
        inflater.inflate(
            R.layout.item_search_recyclerview,
            parent,
            false
        )
    ) {
    private var title: TextView = itemView.findViewById(R.id.searchItemTitle)
    private var button: ImageButton = itemView.findViewById(R.id.searchItemButton)

    fun bind(search: Search) {
        title.text = search.title
        title.setOnClickListener {
            searchAdapterListener.onSearchClicked(search)
        }
        button.setOnClickListener {
            Navigation.findNavController(itemView)
                .navigate(
                    HomeFragmentDirections.homeToEditSearchAction(
                        search.id,
                        search.url,
                        search.title
                    )
                )
        }
    }
}