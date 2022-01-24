package com.ekcapaper.racingar.adaptergame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ekcapaper.racingar.R;
import com.ekcapaper.racingar.adapter.AdapterListBasic;
import com.ekcapaper.racingar.model.People;
import com.ekcapaper.racingar.modelgame.item.GameRoomInfo;
import com.ekcapaper.racingar.helper.SwipeItemTouchHelper;
import com.ekcapaper.racingar.utils.Tools;

import java.util.ArrayList;
import java.util.List;

public class AdapterGameRoom extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<GameRoomInfo> items = new ArrayList<>();

    private Context ctx;
    private AdapterGameRoom.OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, GameRoomInfo obj, int position);
    }

    public void setOnItemClickListener(final AdapterGameRoom.OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterGameRoom(Context context, List<GameRoomInfo> items) {
        this.items = items;
        ctx = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView name;
        public View lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.image);
            name = (TextView) v.findViewById(R.id.name);
            lyt_parent = (View) v.findViewById(R.id.lyt_parent);
        }
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof AdapterListBasic.OriginalViewHolder) {
            AdapterGameRoom.OriginalViewHolder view = (AdapterGameRoom.OriginalViewHolder) holder;

            GameRoomInfo p = items.get(holder.getAdapterPosition());
            view.name.setText(p.name);
            Tools.displayImageRound(ctx, view.image, p.image);
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
