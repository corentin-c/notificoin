package com.github.corentinc.notificoin.ui.home

import android.content.Context
import android.os.Bundle
import android.os.PowerManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.corentinc.core.home.HomeInteractor
import com.github.corentinc.core.search.Search
import com.github.corentinc.notificoin.R
import com.github.corentinc.notificoin.ui.home.searchesRecyclerView.SearchAdapter
import com.github.corentinc.notificoin.ui.home.searchesRecyclerView.SearchAdapterListener
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment(
    val homeInteractor: HomeInteractor,
    private val adapter: SearchAdapter
): Fragment(), HomeDisplay {
    private val homeViewModel: HomeViewModel by viewModel()
    private lateinit var onDestinationChangedListener: NavController.OnDestinationChangedListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    init {
        (homeInteractor.homePresenter as HomePresenterImpl).homeDisplay = this
    }

    override fun onStart() {
        homeFragmentSearchesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
        }
        homeFragmentCreateAdButton.setOnClickListener {
            homeInteractor.onCreateAdButtonPressed()
        }
        val context = requireContext()
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        bindViewModel()
        homeInteractor.onStart(
            powerManager != null && powerManager.isIgnoringBatteryOptimizations(context.packageName),
            homeViewModel.shouldShowBatteryWhiteListAlertDialog.value == false
        )
        addOnDestinationChangedListener()
        super.onStart()
    }

    private fun addOnDestinationChangedListener() {
        onDestinationChangedListener =
            NavController.OnDestinationChangedListener { _, destination, _ ->
                if (destination.label != resources.getString(R.string.titleHome)) {
                    beforeFragmentPause()
                }
            }
        findNavController().addOnDestinationChangedListener(onDestinationChangedListener)
    }

    override fun onPause() {
        beforeFragmentPause()
        findNavController().removeOnDestinationChangedListener(onDestinationChangedListener)
        super.onPause()
    }

    private fun beforeFragmentPause() {
        if (adapter.isSearchAdsPositionListInitialized()) {
            homeInteractor.beforeFragmentPause(adapter.searchAdsPositionList)
        }
    }

    override fun onDestroyView() {
        adapter.searchAdsPositionList = mutableListOf()
        super.onDestroyView()
    }

    override fun displayEditAdScreen(id: Int, url: String, title: String) {
        findNavController()
            .navigate(
                HomeFragmentDirections.editSearchAction(
                    id,
                    url,
                    title
                )
            )
    }

    override fun displayAdListScreen(searchId: Int) {
        findNavController()
            .navigate(
                HomeFragmentDirections.goToAdListAction(
                    searchId
                )
            )
    }

    override fun displayUndoDeleteSearch(search: Search) {
        Snackbar.make(
            homeFragmentSnackBar,
            getString(R.string.homeFragmentSearchDeleted),
            Snackbar.LENGTH_LONG
        )
            .setAction(getString(R.string.undo)) {
                adapter.searchAdsPositionList.add(search)
                adapter.notifyItemInserted(adapter.searchAdsPositionList.size)
                homeInteractor.onRestoreSearch(search)
            }.setActionTextColor(resources.getColor(R.color.primaryColor, requireContext().theme))
            .show()
    }

    override fun displayBatteryWarningFragment() {
        homeViewModel.shouldShowBatteryWhiteListAlertDialog.value = false
        findNavController().navigate(R.id.action_navigation_home_to_navigation_battery_warning)
    }

    private fun bindViewModel() {
        homeViewModel.searchAdsPositionList.observe(
            this.viewLifecycleOwner,
            Observer {
                if (!adapter.isSearchAdsPositionListInitialized() || (adapter.isSearchAdsPositionListInitialized() && adapter.searchAdsPositionList != it)) {
                    createRecyclerView(it)
                }
            }
        )
    }

    private fun createRecyclerView(searchList: MutableList<Search>) {
        adapter.searchAdsPositionList = searchList
        adapter.searchAdapterListener = object: SearchAdapterListener {
            override fun onSearchDeleted(search: Search) {
                homeInteractor.onSearchDeleted(search)
            }

            override fun onSearchClicked(search: Search) {
                homeInteractor.onSearchClicked(search)
            }
        }
        homeFragmentSearchesRecyclerView.adapter = adapter
        adapter.touchHelper.attachToRecyclerView(homeFragmentSearchesRecyclerView)
    }
}