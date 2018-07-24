package com.sandwhich.tuna.projectlucidity.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.sandwhich.tuna.projectlucidity.R;
import com.sandwhich.tuna.projectlucidity.adapters.RecyclerAdapter;
import com.sandwhich.tuna.projectlucidity.models.ItemModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    RecyclerView rv1;
    List<ItemModel> itemModels;

    @Override
    public void onBackPressed() {
        Toast.makeText(MainActivity.this,"BackPressed",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
    }

    private void initUI() {
        rv1=findViewById(R.id.main_recycler);
        RecyclerAdapter myAdapter = new RecyclerAdapter(this);
        itemModels = new ArrayList<>();
        int i =5;
        do{
            itemModels.add(new ItemModel("www.huffpost.com","8h","National Emergency! \nNation in danger"));
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
