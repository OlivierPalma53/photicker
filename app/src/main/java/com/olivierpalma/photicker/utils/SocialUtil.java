package com.olivierpalma.photicker.utils;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.olivierpalma.photicker.R;
import com.olivierpalma.photicker.views.Activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by olivierpalma on 29/12/2017.
 */

public class SocialUtil {

    private static final String HASHTAG = "#photickerapp";

    public static void ShareImageOnInsta(Activity activity, RelativeLayout mRelativePhotoContent, View view) {
        PackageManager pkManager = activity.getPackageManager();
        try {
            // Da erro caso não encontre o aplicativo
            pkManager.getPackageInfo("com.instagram.android", 0);

            try {
                Bitmap image = ImageUtil.drawBitmap(mRelativePhotoContent);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temp_file.jpg");
                try {
                    f.createNewFile();
                    FileOutputStream fo = new FileOutputStream(f);
                    fo.write(bytes.toByteArray());
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/temp_file.jpg"));
                    sendIntent.setType("image/*");
                    sendIntent.setPackage("com.instagram.android");
                    view.getContext().startActivity(Intent.createChooser(sendIntent, activity.getString(R.string.share_image)));
                } catch (IOException e) {
                }

            } catch (Exception e) {
            }

        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(activity, R.string.instagram_not_installed, Toast.LENGTH_SHORT).show();
        }
    }

    public static void ShareImageOnFacebook(Activity activity, RelativeLayout mRelativePhotoContent, View view) {
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(ImageUtil.drawBitmap(mRelativePhotoContent))
                .build();

        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();

        new ShareDialog(activity).show(content);
    }

    public static void ShareImageOnTwitter(Activity activity, RelativeLayout mRelativePhotoContent, View view) {
        PackageManager pkManager = activity.getPackageManager();
        try {
            // Da erro caso não encontre o aplicativo
            pkManager.getPackageInfo("com.twitter.android", 0);

            try {
                Intent tweetIntent = new Intent(Intent.ACTION_SEND);

                Bitmap image = ImageUtil.drawBitmap(mRelativePhotoContent);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temp_file.jpg");

                f.createNewFile();
                FileOutputStream fo = new FileOutputStream(f);
                fo.write(bytes.toByteArray());

                tweetIntent.putExtra(Intent.EXTRA_TEXT, HASHTAG);
                tweetIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/temp_file.jpg"));
                tweetIntent.setType("image/jpeg");
                PackageManager pm = activity.getPackageManager();
                List<ResolveInfo> lract = pm.queryIntentActivities(tweetIntent, PackageManager.MATCH_DEFAULT_ONLY);
                boolean resolved = false;
                for (ResolveInfo ri : lract) {
                    if (ri.activityInfo.name.contains("twitter")) {
                        tweetIntent.setClassName(ri.activityInfo.packageName,
                                ri.activityInfo.name);
                        resolved = true;
                        break;
                    }
                }

                view.getContext().startActivity(resolved ? tweetIntent : Intent.createChooser(tweetIntent, "Choose one"));
            } catch (final ActivityNotFoundException e) {
                Toast.makeText(activity, R.string.twitter_not_installed, Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(activity, R.string.twitter_not_installed, Toast.LENGTH_SHORT).show();
        }
    }

    public static void ShareImageOnWhatsapp(android.app.Activity activity, RelativeLayout mRelativePhotoContent, View view) {
        PackageManager packageManager = activity.getPackageManager();

        try {
            packageManager.getPackageInfo("com.whatsapp", 0 );

            String fileName = "temp_file"+ System.currentTimeMillis() + ".jpg";

            try {
                mRelativePhotoContent.setDrawingCacheEnabled(true);
                mRelativePhotoContent.buildDrawingCache(true);

                File imageFile = new File(Environment.getExternalStorageDirectory(), fileName);
                FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
                mRelativePhotoContent.getDrawingCache(true).compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                fileOutputStream.close();

                mRelativePhotoContent.setDrawingCacheEnabled(false);
                mRelativePhotoContent.destroyDrawingCache();

                try {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, HASHTAG);
                    sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/" + fileName));

                    sendIntent.setType("image/jpg");
                    sendIntent.setPackage("com.whatsapp");

                    view.getContext().startActivity(Intent.createChooser(sendIntent, activity.getString(R.string.share_image)));

                } catch (Exception e){
                    Toast.makeText(activity, R.string.unexpected_error, Toast.LENGTH_SHORT).show();
                }

            } catch (FileNotFoundException e) {
                Toast.makeText(activity, R.string.unexpected_error, Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(activity, R.string.unexpected_error, Toast.LENGTH_SHORT).show();
            }
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(activity, R.string.whatsapp_not_installed, Toast.LENGTH_SHORT).show();
        }
    }
}
