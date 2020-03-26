package com.github.lemaki.repository.searchWithAds

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction

@Dao
abstract class SearchWithAdsDao {
    @Transaction
    @Query("SELECT * FROM search")
    abstract fun getAllSearchWithAds(): List<SearchWithAdsEntity>

    @Query("SELECT * FROM search WHERE id = :id ")
    abstract fun getSearchWithAds(id: Int): SearchWithAdsEntity
}