package com.ekcapaper.mapgamear.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ekcapaper.mapgamear.R;
import com.ekcapaper.mapgamear.adapter.AdapterGridMusicAlbum;
import com.ekcapaper.mapgamear.data.DataGenerator;
import com.ekcapaper.mapgamear.model.MusicAlbum;
import com.ekcapaper.mapgamear.utils.Tools;
import com.ekcapaper.mapgamear.widget.SpacingItemDecoration;

import java.util.Collections;
import java.util.List;

public class FragmentMusicAlbum extends Fragment {

    public FragmentMusicAlbum() {
    }

    public static FragmentMusicAlbum newInstance() {
        FragmentMusicAlbum fragment = new FragmentMusicAlbum();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_product_grid, container, false);


        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(getActivity(), 4), true));
        recyclerView.setHasFixedSize(true);

        List<MusicAlbum> items = DataGenerator.getMusicAlbum(getActivity());
        Collections.shuffle(items);

        //set data and list adapter
        AdapterGridMusicAlbum mAdapter = new AdapterGridMusicAlbum(getActivity(), items);
        recyclerView.setAdapter(mAdapter);

        // on item list clicked
        mAdapter.setOnItemClickListener(new AdapterGridMusicAlbum.OnItemClickListener() {
            @Override
            public void onItemClick(View view, MusicAlbum obj, int position) {
                Toast.makeText(getActivity(), "Item " + obj.name + " clicked", Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }
}