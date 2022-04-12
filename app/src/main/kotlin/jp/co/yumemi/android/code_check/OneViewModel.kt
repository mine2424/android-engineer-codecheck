/*
 * Copyright © 2021 YUMEMI Inc. All rights reserved.
 */
package jp.co.yumemi.android.code_check

import android.content.Context
import android.os.Parcelable
import androidx.lifecycle.ViewModel
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.android.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import jp.co.yumemi.android.code_check.TopActivity.Companion.lastSearchDate
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.parcelize.Parcelize
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

/**
 * TwoFragment で使う
 */
class OneViewModel(
    private val context: Context
) : ViewModel() {

    // 検索結果
    fun searchResults(inputText: String): List<item> = runBlocking {
        val client = HttpClient(Android)

        return@runBlocking GlobalScope.async {
            val response: HttpResponse = client.get("https://api.github.com/search/repositories") {
                header("Accept", "application/vnd.github.v3+json")
                parameter("q", inputText)
            }

            val items = mutableListOf<item>()
            val jsonBody = JSONObject(response.body<String>())
            val jsonItems = jsonBody.optJSONArray("items")!!

            // 検索結果の該当個数分itemsに追加される
            generateItemsFromJSON(jsonItems)

            lastSearchDate = Date()

            return@async items.toList()
        }.await()
    }

    // json型からitemクラスに変換する
    private fun generateItemsFromJSON(jsonItems: JSONArray): List<item> {
        val items = mutableListOf<item>()

        for (i in 0 until jsonItems.length()) {
            val jsonItem = jsonItems.optJSONObject(i)!!
            items.add(item.fromJSON(jsonItem))
        }

        return items
    }
}

// HACK: クラス名を変更するとunresolved reference "item"となってしまうので一旦この命名のままにする
@Parcelize
data class item(
    val name: String,
    val ownerIconUrl: String,
    val language: String,
    val stargazersCount: Long,
    val watchersCount: Long,
    val forksCount: Long,
    val openIssuesCount: Long,
) : Parcelable {
    companion object {
        fun fromJSON(json: JSONObject): item {
            return item(
                name = json.optString("full_name"),
                ownerIconUrl = json.optJSONObject("owner")!!.optString("avatar_url"),
                language = json.optString("language"),
                stargazersCount = json.optLong("stargazers_count"),
                watchersCount = json.optLong("watchers_count"),
                forksCount = json.optLong("forks_conut"),
                openIssuesCount = json.optLong("open_issues_count"),
            )
        }
    }

}