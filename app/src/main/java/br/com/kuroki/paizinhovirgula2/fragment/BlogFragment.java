package br.com.kuroki.paizinhovirgula2.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.kuroki.paizinhovirgula2.R;
import br.com.kuroki.paizinhovirgula2.activity.BlogExibirItemActivity;
import br.com.kuroki.paizinhovirgula2.adapter.BlogAdapter;
import br.com.kuroki.paizinhovirgula2.entity.Item;
import br.com.kuroki.paizinhovirgula2.fragment.interfaces.OnItemClickListener;
import br.com.kuroki.paizinhovirgula2.persistence.PaizinhoDataBaseHelper;
import br.com.kuroki.paizinhovirgula2.task.BlogRSSReader;

public class BlogFragment extends Fragment implements OnItemClickListener, SwipeRefreshLayout.OnRefreshListener, BlogAdapter.OnLoadMoreListener {

    private RecyclerView recyclerView;
    private TextView emptyView;
    private BlogAdapter adapter;
    private List<Item> listBlog = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;

    private PaizinhoDataBaseHelper baseHelper;

    private SwipeRefreshLayout swipeRefresh;

    //private BlogRSSReader blogRSSReader;

    public BlogFragment() { }

    @Override
    public void onItemClick(View view, Item item) {
        Intent intent = new Intent(getActivity(), BlogExibirItemActivity.class);
        Log.i("BLOG_FRAGMENT", "ITEM: "+ item);
        intent.putExtra("itemSelecionado", item);
        startActivity(intent);
    }

    private PaizinhoDataBaseHelper getHelper(){
        if (baseHelper == null)
            baseHelper = new PaizinhoDataBaseHelper(getContext());
        return baseHelper;
    }

    private BlogRSSReader getBlogRssReader() {
        /*if (blogRSSReader == null)
            blogRSSReader = new BlogRSSReader(getActivity());
        return blogRSSReader;*/
        return new BlogRSSReader(getActivity());
    }

    @Override
    public void onStart() {
        super.onStart();
        /*if (listBlog == null || listBlog.size() == 0)
            loadData();
        else if (adapter != null) {
            adapter.addAll(listBlog);
            adapter.notifyDataSetChanged();
        }*/
    }

    @Override
    public void onResume() {
        super.onResume();

        if (listBlog == null || listBlog.size() == 0)
            loadData();
        if (adapter != null) {
            adapter.addAll(listBlog);
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_blog, container, false);
        emptyView = (TextView) view.findViewById(R.id.empty_view_blog);
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipe_blog);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycle_blog);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new BlogAdapter(this);
        adapter.setLinearLayoutManager(linearLayoutManager);
        adapter.setRecyclerView(recyclerView);
        adapter.setListener(this);
        recyclerView.setAdapter(adapter);
        swipeRefresh.setOnRefreshListener(this);

        return view;
    }

    @Override
    public void onRefresh() {
        Log.d("BlogFragment","onRefresh");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefresh.setRefreshing(false);
                loadData();
            }
        },1000);
    }

    private void loadData(){
        listBlog.clear();
        try {
            getBlogRssReader().execute("http://paizinhovirgula.com/category/blog/feed");
            listBlog = getHelper().getItemDao().pesquisarNItens(10, 0, true);
            adapter.addAll(listBlog);
            adapter.notifyItemInserted(0);
        } catch (SQLException e) {
            e.printStackTrace();
            listBlog.clear();
            if (emptyView != null)
                emptyView.setText("Desculpe, Site do Paizinho com problemas. Favor, tente mais tarde.");
        }
    }

    @Override
    public void onLoadMore() {
        Log.d("BlogFragment_","onLoadMore");
        adapter.setProgressMore(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("BlogFragment_","run");
                listBlog.clear();
                adapter.setProgressMore(false);
                int start = linearLayoutManager.findLastVisibleItemPosition();
                /*for (int i = start + 1; i <= end; i++) {
                    listBlog.add(new Item("Item " + i));
                }*/
                Log.i("TESTE ", "Position: " + linearLayoutManager.findLastVisibleItemPosition());
                Log.i("TESTE ", "id: " + adapter.getItemIdAtPosition(start-1));
                try {
                    listBlog = getHelper().getItemDao().pesquisarNItens(10, adapter.getItemIdAtPosition(start-1), true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                adapter.addItemMore(listBlog);
                adapter.setMoreLoading(false);
            }
        },2000);
    }
}