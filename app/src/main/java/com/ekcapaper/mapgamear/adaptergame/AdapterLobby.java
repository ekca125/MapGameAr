package com.ekcapaper.mapgamear.adaptergame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ekcapaper.mapgamear.R;
import com.ekcapaper.mapgamear.modelgame.item.GameLobbyRoomItem;

import java.util.ArrayList;
import java.util.List;

public class AdapterLobby extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<GameLobbyRoomItem> items = new ArrayList<>();

    private final Context ctx;
    private AdapterLobby.OnItemClickListener mOnItemClickListener;

    public AdapterLobby(Context context, List<GameLobbyRoomItem> items) {
        this.items = items;
        ctx = context;
    }

    public void setOnItemClickListener(final AdapterLobby.OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
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
            view.room_name.setText(items.get(holder.getAdapterPosition()).roomName);
            view.room_desc.setText(items.get(holder.getAdapterPosition()).roomDesc);
            view.distance_center.setText(items.get(holder.getAdapterPosition()).distanceCenter);
            view.match_id.setText(items.get(holder.getAdapterPosition()).matchId);
            view.game_type_desc.setText(items.get(holder.getAdapterPosition()).gameTypeDesc);
            view.time_limit_desc.setText(items.get(holder.getAdapterPosition()).timeLimitDesc);
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

    public interface OnItemClickListener {
        void onItemClick(View view, GameLobbyRoomItem obj, int position);
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView room_name;
        public TextView room_desc;
        public TextView distance_center;
        public TextView match_id;
        public TextView game_type_desc;
        public TextView time_limit_desc;
        public View lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            lyt_parent = (View) v.findViewById(R.id.lyt_parent);
            room_name = (TextView) v.findViewById(R.id.room_name);
            room_desc = (TextView) v.findViewById(R.id.room_desc);
            distance_center = (TextView) v.findViewById(R.id.distance_center);
            match_id = (TextView) v.findViewById(R.id.match_id);
            game_type_desc = (TextView) v.findViewById(R.id.game_type_desc);
            time_limit_desc = v.findViewById(R.id.time_limit_desc);
        }
    }

}
