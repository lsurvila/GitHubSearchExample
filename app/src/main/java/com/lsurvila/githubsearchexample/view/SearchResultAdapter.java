package com.lsurvila.githubsearchexample.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.lsurvila.githubsearchexample.R;
import com.lsurvila.githubsearchexample.model.GitHubRepo;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {

    private List<GitHubRepo> gitHubRepoList = new ArrayList<>();
    private AdapterView.OnItemClickListener listener;

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_result, parent, false), listener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        GitHubRepo gitHubRepo = getItem(position);
        holder.searchItemName.setText(gitHubRepo.getRepositoryName());
        holder.searchItemFavorite.setImageResource(gitHubRepo.isFavorite() ? R.drawable.ic_favorite_black_24px : R.drawable.ic_favorite_border_black_24px);
    }

    @Override
    public int getItemCount() {
        return gitHubRepoList.size();
    }

    public void setItems(List<GitHubRepo> gitHubRepos) {
        gitHubRepoList = gitHubRepos;
        notifyDataSetChanged();
    }

    public void appendItems(List<GitHubRepo> gitHubRepos) {
        int currentSize = gitHubRepos.size();
        gitHubRepoList.addAll(gitHubRepos);
        notifyItemRangeChanged(currentSize, gitHubRepoList.size());
    }

    public GitHubRepo getItem(final int position) {
        return gitHubRepoList.get(position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.search_item_name)
        TextView searchItemName;

        @Bind(R.id.search_item_favorite)
        ImageView searchItemFavorite;

        private final AdapterView.OnItemClickListener listener;

        ViewHolder(View view, AdapterView.OnItemClickListener listener) {
            super(view);
            ButterKnife.bind(this, view);
            this.listener = listener;
        }

        @OnClick(R.id.search_item_favorite)
        void onClick() {
            if (listener != null) {
                listener.onItemClick(null, searchItemFavorite, getLayoutPosition(), getLayoutPosition());
            }
        }

    }




}
