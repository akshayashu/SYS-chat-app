package com.example.seeyousoon.extraActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.example.seeyousoon.R;

public class PostsGridView extends AppCompatActivity {

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts_grid_view);

        imageView = findViewById(R.id.image);

        Intent intent = getIntent();
        imageView.setImageResource(intent.getIntExtra("image",0));
    }
}
