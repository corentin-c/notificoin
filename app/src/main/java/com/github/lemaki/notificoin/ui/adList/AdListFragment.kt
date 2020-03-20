package com.github.lemaki.notificoin.ui.adList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.github.lemaki.core.adList.AdListErrorType.*
import com.github.lemaki.core.adList.AdListInteractor
import com.github.lemaki.notificoin.R
import kotlinx.android.synthetic.main.fragment_ad_list.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class AdListFragment: Fragment() {

    private val adListViewModel: AdListViewModel by sharedViewModel()
    private val adListInteractor: AdListInteractor by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_ad_list, container, false)
        adListInteractor.onStart()
        bindViewModel()
        return root
    }

    private fun bindViewModel() {
        adListViewModel.adListViewModel.observe(this.viewLifecycleOwner, Observer {
            textAdsFragment?.text = it.text
            hideProgressBar()
        })
        adListViewModel.errorType.observe(this.viewLifecycleOwner, Observer { adListErrorType ->
            adListErrorType?.let {
                val text = when (it) {
                    CONNECTION -> getString(R.string.adListConnectionErrorMessage)
                    PARSING -> getString(R.string.adListParsingErrorMessage)
                    UNKNOWN -> getString(R.string.adListUnknownErrorMessage)
                }
                textAdsFragment?.text = text
                hideProgressBar()
            }
        })
    }

    private fun hideProgressBar() {
        progressBarAdsFragment?.isVisible = false
    }
}