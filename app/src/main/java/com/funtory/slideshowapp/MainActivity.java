package com.funtory.slideshowapp;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.funtory.slideshowimageview.SlideshowImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.iv_expand)
    SlideshowImageView slideshowImageView;

    @BindView(R.id.rv_list)
    RecyclerView rvList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        initViews();

    }

    private void initViews(){
        setSupportActionBar(toolbar);

        rvList.setItemAnimator(new DefaultItemAnimator());
        rvList.setLayoutManager(new LinearLayoutManager(this));

        rvList.setAdapter(new DummyAdapter());

        slideshowImageView.setImages(R.drawable.test1);

        //비동기로 추가될 경우 모사
        slideshowImageView.postDelayed(new Runnable() {
            @Override
            public void run() {
                slideshowImageView.addImages(R.drawable.test3, R.drawable.test4, R.drawable.test5);
            }
        }, 2000);

        //비동기로 추가될 경우 모사
        slideshowImageView.postDelayed(new Runnable() {
            @Override
            public void run() {
                slideshowImageView.addImages(R.drawable.test2, R.drawable.test6, R.drawable.test7,  R.drawable.test8,  R.drawable.test9);
            }
        }, 2000);
    }

    class DummyAdapter extends RecyclerView.Adapter{
        List<String> dummy = new ArrayList<>();

        public DummyAdapter(){
            for(int i = 0 ; i < 100 ; i++){
                dummy.add("Item"+i);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new RecyclerView.ViewHolder(LayoutInflater.from(MainActivity.this).inflate(android.R.layout.simple_list_item_1, null)){};
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((TextView)holder.itemView).setText(dummy.get(position));
        }

        @Override
        public int getItemCount() {
            return dummy.size();
        }
    }

}
