package com.ekcapaper.mapgamear.adaptergame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ekcapaper.mapgamear.R;
import com.ekcapaper.mapgamear.modelgame.item.GameRoomInfo;

import java.util.ArrayList;
import java.util.List;

public class AdapterGameRoom extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<GameRoomInfo> items = new ArrayList<>();

    private final Context ctx;
    private AdapterGameRoom.OnItemClickListener mOnItemClickListener;

    public AdapterGameRoom(Context context, List<GameRoomInfo> items) {
        this.items = items;
        ctx = context;
    }

    public void setOnItemClickListener(final AdapterGameRoom.OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game_room, parent, false);
        vh = new AdapterGameRoom.OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof AdapterGameRoom.OriginalViewHolder) {
            AdapterGameRoom.OriginalViewHolder view = (AdapterGameRoom.OriginalViewHolder) holder;

            GameRoomInfo p = items.get(holder.getAdapterPosition());
            view.user_id.setText(p.userId);
            view.user_name.setText(p.userName);
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
        void onItemClick(View view, GameRoomInfo obj, int position);
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public TextView user_name;
        public TextView user_id;
        public View lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            user_name = (TextView) v.findViewById(R.id.user_name);
            user_id = (TextView) v.findViewById(R.id.user_id);
            lyt_parent = (View) v.findViewById(R.id.lyt_parent);
        }
    }
}
