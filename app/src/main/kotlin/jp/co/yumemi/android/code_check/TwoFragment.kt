/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import coil.load
import jp.co.yumemi.android.code_check.TopActivity.Companion.lastSearchDate
import jp.co.yumemi.android.code_check.databinding.FragmentTwoBinding

class TwoFragment : Fragment(R.layout.fragment_two) {

    private val args: TwoFragmentArgs by navArgs()
    private var binding: FragmentTwoBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("検索した日時", lastSearchDate.toString())

        binding = FragmentTwoBinding.bind(view)

        val item = args.item

        // bindingがnullだったら処理を終了させる
        if (binding == null){
            return
        }

        binding!!.ownerIconView.load(item.ownerIconUrl)
        binding!!.nameView.text = item.name
        binding!!.languageView.text = item.language
        binding!!.starsView.text = "${item.stargazersCount} stars"
        binding!!.watchersView.text = "${item.watchersCount} watchers"
        binding!!.forksView.text = "${item.forksCount} forks"
        binding!!.openIssuesView.text = "${item.openIssuesCount} open issues"
    }
}
