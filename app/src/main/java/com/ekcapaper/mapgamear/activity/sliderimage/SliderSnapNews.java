package com.ekcapaper.mapgamear.activity.sliderimage;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import com.ekcapaper.mapgamear.R;
import com.ekcapaper.mapgamear.adapter.AdapterSnapGeneric;
import com.ekcapaper.mapgamear.data.DataGenerator;
import com.ekcapaper.mapgamear.helper.StartSnapHelper;
import com.ekcapaper.mapgamear.model.Image;
import com.ekcapaper.mapgamear.utils.Tools;

import java.util.List;

public class SliderSnapNews extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider_snap_news);
        Tools.setSystemBarColor(this);
        initComponent();
    }

    private void initComponent() {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // generate data
        List<Image> items = DataGenerator.getImageDate(this).subList(0, 8);

        recyclerView.setAdapter(new AdapterSnapGeneric(this, items, R.layout.item_snap_news));
        recyclerView.setOnFlingListener(null);
        new StartSnapHelper().attachToRecyclerView(recyclerView);

        (findViewById(R.id.bt_menu)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
