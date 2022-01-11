package com.ekcapaper.racingar.activity.raar.adapter;

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
import com.ekcapaper.racingar.activity.raar.model.GameRoomInfo;
import com.ekcapaper.racingar.adapter.AdapterListSwipe;
import com.ekcapaper.racingar.helper.SwipeItemTouchHelper;
import com.ekcapaper.racingar.model.Social;
import com.ekcapaper.racingar.utils.Tools;

import java.util.ArrayList;
import java.util.List;

public class AdapterGameRoom extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements SwipeItemTouchHelper.SwipeHelperAdapter{

    private List<GameRoomInfo> items = new ArrayList<>();
    private List<GameRoomInfo> items_swiped = new ArrayList<>();

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

    public class OriginalViewHolder extends RecyclerView.ViewHolder implements SwipeItemTouchHelper.TouchViewHolder {
        public ImageView image;
        public TextView name;
        public ImageButton bt_move;
        public Button bt_undo;
        public View lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.image);
            name = (TextView) v.findViewById(R.id.name);
            bt_move = (ImageButton) v.findViewById(R.id.bt_move);
            bt_undo = (Button) v.findViewById(R.id.bt_undo);
            lyt_parent = (View) v.findViewById(R.id.lyt_parent);
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(ctx.getResources().getColor(R.color.grey_5));
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_swipe, parent, false);
        vh = new AdapterGameRoom.OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof AdapterGameRoom.OriginalViewHolder) {
            final AdapterGameRoom.OriginalViewHolder view = (AdapterGameRoom.OriginalViewHolder) holder;

            final GameRoomInfo p = items.get(position);
            view.name.setText(p.name);
            Tools.displayImageOriginal(ctx, view.image, p.image);
            view.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, items.get(position), position);
                    }
                }
            });

            view.bt_undo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    items.get(position).swiped = false;
                    items_swiped.remove(items.get(position));
                    notifyItemChanged(position);
                }
            });

            if (p.swiped) {
                view.lyt_parent.setVisibility(View.GONE);
            } else {
                view.lyt_parent.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                for (GameRoomInfo s : items_swiped) {
                    int index_removed = items.indexOf(s);
                    if (index_removed != -1) {
                        items.remove(index_removed);
                        notifyItemRemoved(index_removed);
                    }
                }
                items_swiped.clear();
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onItemDismiss(int position) {

        // handle when double swipe
        if (items.get(position).swiped) {
            items_swiped.remove(items.get(position));
            items.remove(position);
            notifyItemRemoved(position);
            return;
        }

        items.get(position).swiped = true;
        items_swiped.add(items.get(position));
        notifyItemChanged(position);
    }
}
