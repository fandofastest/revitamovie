package com.revita.hdmovies2020.revita_adapter;

import android.content.Context;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.revita.hdmovies2020.R;
import com.revita.hdmovies2020.revita_model.CommonModels;

import java.util.ArrayList;
import java.util.List;

public class EpisodeAdapter extends RecyclerView.Adapter<EpisodeAdapter.OriginalViewHolder> {

    private List<CommonModels> items = new ArrayList<>();
    private Context ctx;
    private boolean isDark;



    public EpisodeAdapter(Context context, List<CommonModels> items, boolean isDark) {
        this.items = items;
        ctx = context;
        this.isDark = isDark;

    }


    @Override
    public EpisodeAdapter.OriginalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        EpisodeAdapter.OriginalViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_episode, parent, false);
        vh = new EpisodeAdapter.OriginalViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(EpisodeAdapter.OriginalViewHolder holder, final int position) {

        CommonModels obj = items.get(position);
        holder.name.setText("Season : "+obj.getTitle());

        if (isDark) {
            holder.name.setBackgroundColor(ctx.getResources().getColor(R.color.overlay_dark_20));
        }
        Log.e("Season Name::",obj.getTitle());


        DirectorApater directorApater=new DirectorApater(ctx,obj.getListEpi(),obj.getTitle(), position);
        Log.e("List", String.valueOf(obj.getTitle()));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(ctx,2);
        gridLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
        holder.recyclerView.setLayoutManager(gridLayoutManager);
        holder.recyclerView.setHasFixedSize(true);
        holder.recyclerView.setAdapter(directorApater);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public RecyclerView recyclerView;


        public OriginalViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.name);
            recyclerView=v.findViewById(R.id.recyclerView);


        }
    }

}