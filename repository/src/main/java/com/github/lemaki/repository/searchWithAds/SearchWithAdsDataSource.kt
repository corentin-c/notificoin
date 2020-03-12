package com.github.lemaki.repository.searchWithAds

class SearchWithAdsDataSource(
    private val searchWithAdsDao: SearchWithAdsDao
) {
    fun getAllSearchWithAds() = searchWithAdsDao.getAllSearchWithAds().map { it.toSearchWithAds() }
}