package com.github.corentinc.repository.webpage

import org.jsoup.nodes.Document

class WebPageRepository(private val webPageDataSource: WebPageDataSource) {
    fun getWebPage(url: String): Document {
        return webPageDataSource.getWebPage(url)
    }
}