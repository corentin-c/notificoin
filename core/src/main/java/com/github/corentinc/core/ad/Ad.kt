package com.github.corentinc.core.ad

import org.joda.time.DateTime

data class Ad(val id: Int, val title: String, val publicationDate: DateTime, val url: String)