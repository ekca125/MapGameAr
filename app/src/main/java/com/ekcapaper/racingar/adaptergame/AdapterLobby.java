package com.ekcapaper.racingar.adaptergame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ekcapaper.racingar.R;
import com.ekcapaper.racingar.modelgame.item.GameLobbyRoomItem;
import com.ekcapaper.racingar.utils.Tools;

import java.util.ArrayList;
import java.util.List;

public class AdapterLobby extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<GameLobbyRoomItem> items = new ArrayList<>();

    private Context ctx;
    private AdapterLobby.OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, GameLobbyRoomItem obj, int position);
    }

    public void setOnItemClickListener(final AdapterLobby.OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterLobby(Context context, List<GameLobbyRoomItem> items) {
        this.items = items;
        ctx = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView group_id;
        public TextView match_id;
        public TextView name;
        public View lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            lyt_parent = (View) v.findViewById(R.id.lyt_parent);
            group_id = (TextView) v.findViewById(R.id.group_id);
            match_id = (TextView) v.findViewById(R.id.match_id);
            name = (TextView) v.findViewById(R.id.name);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game_room_lobby_info, parent, false);
        vh = new AdapterLobby.OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof AdapterLobby.OriginalViewHolder) {
            AdapterLobby.OriginalViewHolder view = (AdapterLobby.OriginalViewHolder) holder;

            GameLobbyRoomItem gri = items.get(holder.getAdapterPosition());
            view.name.setText(items.get(holder.getAdapterPosition()).name);
            view.group_id.setText(items.get(holder.getAdapterPosition()).groupId);
            view.match_id.setText(items.get(holder.getAdapterPosition()).matchId);
            view.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, items.get(holder.getAdapterPosition()), holder.getAdapterPosition());
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}
