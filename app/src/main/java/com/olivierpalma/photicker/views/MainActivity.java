package com.olivierpalma.photicker.views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.olivierpalma.photicker.R;
import com.olivierpalma.photicker.utils.ImageUtil;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        List<Integer> mListImages = ImageUtil.getImageList();

        final LinearLayout content = (LinearLayout) this.findViewById(R.id.horizontal_content);

        for (Integer imageId : mListImages) {
            ImageView image = new ImageView(this);
            image.setImageBitmap(ImageUtil.decodeSampledBitmapFromResource(getResources(), imageId, 70, 70));
            image.setPadding(20, 10, 20, 10);
            content.addView(image);
        }

    }
}
