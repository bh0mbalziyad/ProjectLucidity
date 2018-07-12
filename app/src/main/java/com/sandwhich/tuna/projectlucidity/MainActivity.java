package com.sandwhich.tuna.projectlucidity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import com.sandwhich.tuna.projectlucidity.adapters.RecyclerAdapter;
import com.sandwhich.tuna.projectlucidity.models.ItemModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    RecyclerView rv1;
    List<ItemModel> itemModels;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
    }

    private void initUI() {
        rv1=findViewById(R.id.rv1);
        RecyclerAdapter myAdapter = new RecyclerAdapter(this);
        itemModels = new ArrayList<>();
        int i =5;
        do{
            itemModels.add(new ItemModel("Prod1",4000.0,R.drawable.ic_launcher_background));
            i--;
        }
        while (i>=0);
        myAdapter.addItems(itemModels);
        rv1.setAdapter(myAdapter);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv1.setLayoutManager(mLinearLayoutManager);
        //todo continue testing with recycler view
    }
}
