package com.github.corentinc.core.home

import com.github.corentinc.core.repository.SharedPreferencesRepository
import com.github.corentinc.core.repository.search.SearchRepository
import com.github.corentinc.core.repository.searchWithAds.SearchAdsRepository
import com.github.corentinc.core.search.Search
import com.github.corentinc.core.ui.home.HomePresenter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeInteractor(
    val homePresenter: HomePresenter,
    private val searchRepository: SearchRepository,
    private val searchAdsRepository: SearchAdsRepository,
    private val sharedPreferencesRepository: SharedPreferencesRepository,
    private val searches: Map<String, String> = mapOf(
        "https://www.leboncoin.fr/recherche/?category=2&locations=Nantes&regdate=2010-max" to "Voiture",
        "https://www.leboncoin.fr/recherche/?text=jeu%20switch&locations=Nantes__47.23898554566441_-1.5262136157260586_10000" to "Jeu Switch"
    )
) {

    fun onStart(
        isBatteryWhiteListAlreadyGranted: Boolean,
        wasBatteryWhiteListDialogAlreadyShown: Boolean
    ) {
        if (sharedPreferencesRepository.shouldShowBatteryWhiteListDialog && !isBatteryWhiteListAlreadyGranted && !wasBatteryWhiteListDialogAlreadyShown
        ) {
            homePresenter.presentBatteryWhitelistPermissionAlertDialog()
        }
        CoroutineScope(Dispatchers.IO).launch {
            var searchWithAds = searchAdsRepository.getAllSortedSearchAdsPosition()
            if (searchWithAds.isEmpty()) {
                searches.forEach {
                    searchRepository.addSearch(Search(title = it.value, url = it.key))
                }
                searchWithAds = searchAdsRepository.getAllSortedSearchAdsPosition()
            }
            withContext(Dispatchers.Main) {
                homePresenter.presentSearches(searchWithAds.map { it.search }.toMutableList())
            }
        }
    }

    fun onBatteryWhiteListAlertDialogNeutralButtonPressed() {
        sharedPreferencesRepository.shouldShowBatteryWhiteListDialog = false
    }

    fun onCreateAdButtonPressed() {
        CoroutineScope(Dispatchers.IO).launch {
            val url = searches.toList()[0].first
            val title = searches.toList()[0].second
            val id = searchRepository.addSearch(Search(url = url, title = title))
            withContext(Dispatchers.Main) {
                homePresenter.presentEditSearchScreen(id.toInt(), url, title)
            }
        }
    }

    fun onSearchDeleted(deletedSearch: Search) {
        CoroutineScope(Dispatchers.IO).launch {
            searchAdsRepository.delete(deletedSearch)
        }
    }
}