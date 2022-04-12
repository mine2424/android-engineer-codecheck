/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.*
import jp.co.yumemi.android.code_check.databinding.FragmentOneBinding

// TODO: one twoという意味不明のfragmentを修正する
class OneFragment: Fragment(R.layout.fragment_one){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding= FragmentOneBinding.bind(view)

        val viewModel= OneViewModel(requireContext())

        val layoutManager= LinearLayoutManager(requireContext())

        val dividerItemDecoration=
            DividerItemDecoration(requireContext(), layoutManager.orientation)

        val adapter= CustomAdapter(object : CustomAdapter.OnItemClickListener{
            override fun itemClick(item: RepositoryItem){
                navigateSelectedRepositoryFragment(item)
            }
        })

        // TODO: ネストが深い気がする
        binding.searchInputText
            .setOnEditorActionListener{ editText, action, _ ->
                if (action== EditorInfo.IME_ACTION_SEARCH) {
                    editText.text.toString().let {
                        viewModel.searchResults(it).apply{
                            adapter.submitList(this)
                        }
                    }
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }

        binding.recyclerView.also{
            it.layoutManager= layoutManager
            it.addItemDecoration(dividerItemDecoration)
            it.adapter= adapter
        }
    }

    fun navigateSelectedRepositoryFragment(item: RepositoryItem) {
        // TODO: メソッド名が長すぎないか？
        val action= OneFragmentDirections
            .actionRepositoriesFragmentToRepositoryFragment(item = item)
        findNavController().navigate(action)
    }
}

// TODO: なぜglobalに変数を定義してるのか
val diff_util= object: DiffUtil.ItemCallback<RepositoryItem>(){
    override fun areItemsTheSame(oldItem: RepositoryItem, newItem: RepositoryItem): Boolean {
        return oldItem.name== newItem.name
    }

    override fun areContentsTheSame(oldItem: RepositoryItem, newItem: RepositoryItem): Boolean {
        return oldItem== newItem
    }

}

class CustomAdapter(
    private val itemClickListener: OnItemClickListener,
) : ListAdapter<RepositoryItem, CustomAdapter.ViewHolder>(diff_util){

    class ViewHolder(view: View): RecyclerView.ViewHolder(view)

    interface OnItemClickListener {
    	fun itemClick(item: RepositoryItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    	val view= LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_item, parent, false)
    	return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    	val item = getItem(position)
        (holder.itemView.findViewById<View>(R.id.repositoryNameView) as TextView).text=
            item.name

    	holder.itemView.setOnClickListener{
     		itemClickListener.itemClick(item)
    	}
    }
}
