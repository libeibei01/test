package com.example.myapplication

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class ListActivity : AppCompatActivity() {
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ListAdapter
    private var page = 1
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        recyclerView = findViewById(R.id.recyclerView)

        // 设置下拉刷新
        swipeRefreshLayout.setOnRefreshListener {
            refreshData()
        }

        // 设置RecyclerView
        adapter = ListAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // 设置上拉加载
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                if (lastVisibleItem >= totalItemCount - 3 && !isLoading) {
                    loadMoreData()
                }
            }
        })

        // 初始加载数据
        loadData()
    }

    private fun refreshData() {
        page = 1
        loadData()
    }

    private fun loadMoreData() {
        if (!isLoading) {
            isLoading = true
            page++
            loadData()
        }
    }

    private fun loadData() {
        // 模拟网络请求
        Handler(Looper.getMainLooper()).postDelayed({
            val newItems = generateItems(page)
            if (page == 1) {
                adapter.updateData(newItems)
                swipeRefreshLayout.isRefreshing = false
            } else {
                adapter.addData(newItems)
            }
            isLoading = false
        }, 1000)
    }

    private fun generateItems(page: Int): List<String> {
        val items = mutableListOf<String>()
        val start = (page - 1) * 20
        for (i in 1..20) {
            items.add("Item ${start + i}")
        }
        return items
    }
}

class ListAdapter : RecyclerView.Adapter<ListAdapter.ViewHolder>() {
    private val items = mutableListOf<String>()

    fun updateData(newItems: List<String>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun addData(newItems: List<String>) {
        val startPosition = items.size
        items.addAll(newItems)
        notifyItemRangeInserted(startPosition, newItems.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.textView)

        fun bind(item: String) {
            textView.text = item
        }
    }
} 