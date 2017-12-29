package com.olivierpalma.photicker.views;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.olivierpalma.photicker.R;
import com.olivierpalma.photicker.utils.ImageUtil;
import com.olivierpalma.photicker.utils.LongEventeType;
import com.olivierpalma.photicker.utils.PermissionUtil;
import com.olivierpalma.photicker.utils.SocialUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Activity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener, View.OnTouchListener {

    private static final int REQUEST_TAKE_PHOTO = 2;
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

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        List<Integer> mListImages = ImageUtil.getImageList();

        this.mViewHolder.mRelativePhotoContent = (RelativeLayout) this.findViewById(R.id.relative_photo_content_draw);
        final LinearLayout content = (LinearLayout) this.findViewById(R.id.horizontal_content);

        for (int i = 0; i < mListImages.size(); i++) {

            // Obtém elemento de imagem
            ImageView image = new ImageView(this);

            // Substitui a imagem com a imagem sendo iterada
            image.setImageBitmap(ImageUtil.decodeSampledBitmapFromResource(getResources(), mListImages.get(i), 70, 70));
            image.setPadding(20,10,20,10);
            //image.setLayoutParams(new LinearLayout.MarginLayoutParams(150, 150));

            final int position = mListImages.get(i);
            BitmapFactory.Options dimensions = new BitmapFactory.Options();
            dimensions.inJustDecodeBounds = true;

            BitmapFactory.decodeResource(getResources(), mListImages.get(i), dimensions);

            final int width = dimensions.outWidth;
            final int height = dimensions.outHeight;

            image.setOnClickListener(onClickImageOption(this.mViewHolder.mRelativePhotoContent, position, width, height));

            // Adiciona nova imagem
            content.addView(image);
        }


        this.mViewHolder.mLinearSharePanel = (LinearLayout) this.findViewById(R.id.linear_share_panel);
        this.mViewHolder.mLinearControlPanel = (LinearLayout) this.findViewById(R.id.linear_control_panel);

        this.mViewHolder.mButtonTakePhoto = (ImageView) this.findViewById(R.id.image_take_photo);
        this.mViewHolder.mImagePhoto = (ImageView) this.findViewById(R.id.image_photo);

        this.mViewHolder.mButtonZoomIn = (ImageView) this.findViewById(R.id.image_zoom_in);
        this.mViewHolder.mButtonZoomOut = (ImageView) this.findViewById(R.id.image_zoom_out);
        this.mViewHolder.mButtonRotateLeft = (ImageView) this.findViewById(R.id.image_rotate_left);
        this.mViewHolder.mButtonRotateRight = (ImageView) this.findViewById(R.id.image_rotate_right);
        this.mViewHolder.mButtonFinish = (ImageView) this.findViewById(R.id.image_finish);
        this.mViewHolder.mButtonRemove = (ImageView) this.findViewById(R.id.image_remove);

        this.mViewHolder.mButtonInstagram = (ImageView) this.findViewById(R.id.image_instagram);
        this.mViewHolder.mButtonFacebook = (ImageView) this.findViewById(R.id.image_facebook);
        this.mViewHolder.mButtonTwitter = (ImageView) this.findViewById(R.id.image_twitter);
        this.mViewHolder.mButtonWhatsapp = (ImageView) this.findViewById(R.id.image_whatsapp);

        this.setListeners();


    }

    private void setListeners() {
        this.mViewHolder.mButtonZoomIn.setOnClickListener(this);
        this.mViewHolder.mButtonZoomOut.setOnClickListener(this);
        this.mViewHolder.mButtonRotateLeft.setOnClickListener(this);
        this.mViewHolder.mButtonRotateRight.setOnClickListener(this);
        this.mViewHolder.mButtonFinish.setOnClickListener(this);
        this.mViewHolder.mButtonRemove.setOnClickListener(this);
        this.mViewHolder.mButtonTakePhoto.setOnClickListener(this);

        this.mViewHolder.mButtonInstagram.setOnClickListener(this);
        this.mViewHolder.mButtonFacebook.setOnClickListener(this);
        this.mViewHolder.mButtonTwitter.setOnClickListener(this);
        this.mViewHolder.mButtonWhatsapp.setOnClickListener(this);

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
                final ImageView image = new ImageView(Activity.this);
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
            case R.id.image_take_photo:
                if (!PermissionUtil.hasCameraPermission(this)){
                    PermissionUtil.askCameraPermission(this);
                } else {
                    dispatchTakePictureIntent();
                }
                break;
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

            case R.id.image_instagram:
                SocialUtil.ShareImageOnInsta(this, this.mViewHolder.mRelativePhotoContent, view);
                break;
            case R.id.image_facebook:
                SocialUtil.ShareImageOnFacebook(this, this.mViewHolder.mRelativePhotoContent, view);
                break;
            case R.id.image_twitter:
                SocialUtil.ShareImageOnTwitter(this, this.mViewHolder.mRelativePhotoContent, view);
                break;
            case R.id.image_whatsapp:
                SocialUtil.ShareImageOnWhatsapp(this, this.mViewHolder.mRelativePhotoContent, view);
                break;


        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            this.setPhotoAsBackground();
        }
    }

    private void setPhotoAsBackground() {
        int targetW = this.mViewHolder.mImagePhoto.getWidth();
        int targetH = this.mViewHolder.mImagePhoto.getHeight();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(this.mViewHolder.mUriPhotoPath.getPath(), bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(this.mViewHolder.mUriPhotoPath.getPath(), bmOptions);

        Bitmap bitmapRotate = ImageUtil.rotateImageIfRequired(bitmap, this.mViewHolder.mUriPhotoPath);

        this.mViewHolder.mImagePhoto.setImageBitmap(bitmap);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionUtil.CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                new AlertDialog.Builder(this)
                        .setMessage(getString(R.string.without_permission_camera_explanation))
                        .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();
            }
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = ImageUtil.createPhotoFile(this);
                this.mViewHolder.mUriPhotoPath = Uri.fromFile(photoFile);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Não foi possivel iniciar a camera", Toast.LENGTH_SHORT).show();
            }

            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
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

        ImageView mButtonInstagram;
        ImageView mButtonFacebook;
        ImageView mButtonTwitter;
        ImageView mButtonWhatsapp;

        ImageView mButtonZoomIn;
        ImageView mButtonZoomOut;
        ImageView mButtonRotateLeft;
        ImageView mButtonRotateRight;
        ImageView mButtonFinish;
        ImageView mButtonRemove;
        ImageView mButtonTakePhoto;
        ImageView mImagePhoto;

        LinearLayout mLinearSharePanel;
        LinearLayout mLinearControlPanel;
        RelativeLayout mRelativePhotoContent;
        Uri mUriPhotoPath;
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
