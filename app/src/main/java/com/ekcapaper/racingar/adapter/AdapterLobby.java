package com.ekcapaper.racingar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ekcapaper.racingar.R;
import com.ekcapaper.racingar.modelgame.item.GameLobbyRoomInfo;
import com.ekcapaper.racingar.utils.Tools;

import java.util.ArrayList;
import java.util.List;

public class AdapterLobby extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<GameLobbyRoomInfo> items = new ArrayList<>();

    private Context ctx;
    private AdapterLobby.OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, GameLobbyRoomInfo obj, int position);
    }

    public void setOnItemClickListener(final AdapterLobby.OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterLobby(Context context, List<GameLobbyRoomInfo> items) {
        this.items = items;
        ctx = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView name;
        public TextView distance_center;
        public TextView description;
        public View lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.image);
            name = (TextView) v.findViewById(R.id.name);
            distance_center = (TextView) v.findViewById(R.id.distance_center);
            description = (TextView) v.findViewById(R.id.description);
            lyt_parent = (View) v.findViewById(R.id.lyt_parent);
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

            GameLobbyRoomInfo gri = items.get(holder.getAdapterPosition());
            view.name.setText(gri.name);
            view.description.setText(gri.getGameTypeString());
            view.distance_center.setText(gri.distanceCenter);
            Tools.displayImageRound(ctx, view.image, gri.getImage());
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
