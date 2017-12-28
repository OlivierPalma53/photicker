package com.olivierpalma.photicker.views;

import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.olivierpalma.photicker.R;
import com.olivierpalma.photicker.utils.ImageUtil;
import com.olivierpalma.photicker.utils.LongEventeType;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener, View.OnTouchListener {

    private ViewHolder mViewHolder = new ViewHolder();
    private ImageView mImageSelected;
    private boolean mAutoIncrement;
    private LongEventeType mLongEventType;
    private Handler mRepeatUpdateHandle = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        List<Integer> mListImages = ImageUtil.getImageList();

        this.mViewHolder.mRelativePhotoContent = (RelativeLayout) this.findViewById(R.id.relative_photo_content_draw);
        final LinearLayout content = (LinearLayout) this.findViewById(R.id.horizontal_content);

        for (Integer imageId : mListImages) {
            ImageView image = new ImageView(this);
            image.setImageBitmap(ImageUtil.decodeSampledBitmapFromResource(getResources(), imageId, 70, 70));
            image.setPadding(20, 10, 20, 10);

            BitmapFactory.Options dimensions = new BitmapFactory.Options();
            dimensions.inJustDecodeBounds = true;

            BitmapFactory.decodeResource(getResources(), imageId, dimensions);

            final int width = dimensions.outWidth;
            final int height = dimensions.outHeight;

            image.setOnClickListener(onClickImageOption(this.mViewHolder.mRelativePhotoContent, imageId, width, height));

            content.addView(image);
        }

        this.mViewHolder.mLinearSharePanel = (LinearLayout) this.findViewById(R.id.linear_share_panel);
        this.mViewHolder.mLinearControlPanel = (LinearLayout) this.findViewById(R.id.linear_control_panel);

        this.mViewHolder.mButtonZoomIn = (ImageView) this.findViewById(R.id.image_zoom_in);
        this.mViewHolder.mButtonZoomOut = (ImageView) this.findViewById(R.id.image_zoom_out);
        this.mViewHolder.mButtonRotateLeft = (ImageView) this.findViewById(R.id.image_rotate_left);
        this.mViewHolder.mButtonRotateRight = (ImageView) this.findViewById(R.id.image_rotate_right);
        this.mViewHolder.mButtonFinish = (ImageView) this.findViewById(R.id.image_finish);
        this.mViewHolder.mButtonRemove = (ImageView) this.findViewById(R.id.image_remove);

        this.setListeners();


    }

    private void setListeners() {
        this.mViewHolder.mButtonZoomIn.setOnClickListener(this);
        this.mViewHolder.mButtonZoomOut.setOnClickListener(this);
        this.mViewHolder.mButtonRotateLeft.setOnClickListener(this);
        this.mViewHolder.mButtonRotateRight.setOnClickListener(this);
        this.mViewHolder.mButtonFinish.setOnClickListener(this);
        this.mViewHolder.mButtonRemove.setOnClickListener(this);

        this.mViewHolder.mButtonZoomIn.setOnLongClickListener(this);
        this.mViewHolder.mButtonZoomOut.setOnLongClickListener(this);
        this.mViewHolder.mButtonRotateLeft.setOnLongClickListener(this);
        this.mViewHolder.mButtonRotateRight.setOnLongClickListener(this);

        this.mViewHolder.mButtonZoomIn.setOnTouchListener(this);
        this.mViewHolder.mButtonZoomOut.setOnTouchListener(this);
        this.mViewHolder.mButtonRotateLeft.setOnTouchListener(this);
        this.mViewHolder.mButtonRotateRight.setOnTouchListener(this);
    }

    private View.OnClickListener onClickImageOption(final RelativeLayout relativeLayout, final Integer imageId, int width, int height) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ImageView image = new ImageView(MainActivity.this);
                image.setImageResource(imageId);
                relativeLayout.addView(image);

                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) image.getLayoutParams();
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);

                mImageSelected = image;
                
                toogleControlPanel(true);

                image.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {

                        float x, y;

                        switch (motionEvent.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                mImageSelected = image;
                                toogleControlPanel(true);
                                break;
                            case MotionEvent.ACTION_MOVE:
                                int coords[] = {0,0};
                                relativeLayout.getLocationOnScreen(coords);
                                x = (motionEvent.getRawX() - (image.getWidth() /2));
                                y = motionEvent.getRawY() - ((coords[1] + 100) + (image.getHeight()/2));

                                image.setX(x);
                                image.setY(y);
                                break;
                            case MotionEvent.ACTION_UP:
                                break;
                        }


                        return true;
                    }
                });
                
            }
        };
    }

    private void toogleControlPanel(boolean showControlPanel) {
        if (showControlPanel) {
            this.mViewHolder.mLinearControlPanel.setVisibility(View.VISIBLE);
            this.mViewHolder.mLinearSharePanel.setVisibility(View.GONE);
        } else {
            this.mViewHolder.mLinearControlPanel.setVisibility(View.GONE);
            this.mViewHolder.mLinearSharePanel.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.image_zoom_in:
                ImageUtil.handleZoomIn(this.mImageSelected);
                break;
            case R.id.image_zoom_out:
                ImageUtil.handleZoomOut(this.mImageSelected);
                break;
            case R.id.image_rotate_left:
                ImageUtil.handleRotateLeft(this.mImageSelected);
                break;
            case R.id.image_rotate_right:
                ImageUtil.handleRotateRight(this.mImageSelected);
                break;
            case R.id.image_finish:
                this.toogleControlPanel(false);
                break;
            case R.id.image_remove:
                this.mViewHolder.mRelativePhotoContent.removeView(this.mImageSelected);
                this.toogleControlPanel(false);
                break;

        }

    }

    @Override
    public boolean onLongClick(View view) {
        if(view.getId() == R.id.image_zoom_in) this.mLongEventType = LongEventeType.ZoomIn;
        if(view.getId() == R.id.image_zoom_out) this.mLongEventType = LongEventeType.ZoomOut;
        if(view.getId() == R.id.image_rotate_left) this.mLongEventType = LongEventeType.RotateLeft;
        if(view.getId() == R.id.image_rotate_right) this.mLongEventType = LongEventeType.RotateRight;
        mAutoIncrement = true;
        new RtpUpdater().run();
        return false;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int id = view.getId();

        if (id == R.id.image_zoom_in || id == R.id.image_zoom_out || id == R.id.image_rotate_right || id == R.id.image_rotate_left) {

            if (motionEvent.getAction() == MotionEvent.ACTION_UP && mAutoIncrement) {
                mAutoIncrement = false;
                this.mLongEventType = null;
            }

        }
        return false;
    }

    private static class ViewHolder {

        ImageView mButtonZoomIn;
        ImageView mButtonZoomOut;
        ImageView mButtonRotateLeft;
        ImageView mButtonRotateRight;
        ImageView mButtonFinish;
        ImageView mButtonRemove;

        LinearLayout mLinearSharePanel;
        LinearLayout mLinearControlPanel;
        RelativeLayout mRelativePhotoContent;
    }

    private class RtpUpdater implements Runnable {
        @Override
        public void run() {
            if (mAutoIncrement) {
                mRepeatUpdateHandle.postDelayed(new RtpUpdater(), 50);
            }

            if (mLongEventType != null) {
                switch (mLongEventType) {
                    case ZoomIn:
                        ImageUtil.handleZoomIn(mImageSelected);
                        break;
                    case ZoomOut:
                        ImageUtil.handleZoomOut(mImageSelected);
                        break;
                    case RotateLeft:
                        ImageUtil.handleRotateLeft(mImageSelected);
                        break;
                    case RotateRight:
                        ImageUtil.handleRotateRight(mImageSelected);
                        break;
                }
            }
        }
    }
}
