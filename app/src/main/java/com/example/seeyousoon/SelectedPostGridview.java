package com.example.seeyousoon;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

public class SelectedPostGridview extends AppCompatActivity {

    ImageView postImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_post_gridview);

        postImage = findViewById(R.id.postImage);
        Intent intent = getIntent();
        postImage.setImageResource(intent.getIntExtra("image",0));
    }
}
