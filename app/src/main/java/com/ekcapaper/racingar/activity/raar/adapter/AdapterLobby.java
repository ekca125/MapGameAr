package com.ekcapaper.racingar.activity.raar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ekcapaper.racingar.R;
import com.ekcapaper.racingar.adapter.AdapterListBasic;
import com.ekcapaper.racingar.model.People;
import com.ekcapaper.racingar.utils.Tools;

import java.util.ArrayList;
import java.util.List;

public class AdapterLobby extends AdapterListBasic{

    public AdapterLobby(Context context, List<People> items) {
        super(context, items);
    }
}
