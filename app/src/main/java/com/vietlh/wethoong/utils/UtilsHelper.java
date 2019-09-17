package com.vietlh.wethoong.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.net.Uri;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by vietlh on 2/26/18.
 */

public class UtilsHelper {
    public Bitmap getBitmapFromAssets(Context context, String fileName) {
        /*
            AssetManager
                Provides access to an application's raw asset files.
        */

        /*
            public final AssetManager getAssets ()
                Retrieve underlying AssetManager storage for these resources.
        */
        AssetManager am = context.getAssets();
        InputStream is = null;
        try {
            /*
                public final InputStream open (String fileName)
                    Open an asset using ACCESS_STREAMING mode. This provides access to files that
                    have been bundled with an application as assets -- that is,
                    files placed in to the "assets" directory.

                    Parameters
                        fileName : The name of the asset to open. This name can be hierarchical.
                    Throws
                        IOException
            */
            is = am.open(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*
            BitmapFactory
                Creates Bitmap objects from various sources, including files, streams, and byte-arrays.
        */

        /*
            public static Bitmap decodeStream (InputStream is)
                Decode an input stream into a bitmap. If the input stream is null, or cannot
                be used to decode a bitmap, the function returns null. The stream's
                position will be where ever it was after the encoded data was read.

                Parameters
                    is : The input stream that holds the raw data to be decoded into a bitmap.
                Returns
                    The decoded bitmap, or null if the image data could not be decoded.
        */
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        return bitmap;
    }

    public Drawable getDrawableFromAssets(Context context, String fileName) {
        Drawable drawable = null;
        InputStream inputStream = null;
        try {
            inputStream = context.getAssets().open(fileName);
            drawable = Drawable.createFromStream(inputStream, null);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return drawable;
    }

    //TODO: Handle scaling images if needed (not sure why it works)
    public Bitmap scaleImage(Bitmap image, int targetWidth) {
        int nh = (int) (image.getHeight() * ((float) targetWidth / (float) image.getWidth()));
        Bitmap scaled = Bitmap.createScaledBitmap(image, targetWidth, nh, true);
        return scaled;
    }

    public Bitmap scaleImageByHeight(Bitmap image, int targetHeight) {
        int nw = (int) (image.getWidth() * ((float) targetHeight / (float) image.getHeight()));
        Bitmap scaled = Bitmap.createScaledBitmap(image, nw, targetHeight, true);
        return scaled;
    }

    public int getButtonBackgroundColor(Button button) {
        int buttonColor = 0;

        if (button.getBackground() instanceof ColorDrawable) {
            ColorDrawable cd = (ColorDrawable) button.getBackground();
            buttonColor = cd.getColor();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (button.getBackground() instanceof RippleDrawable) {
                RippleDrawable rippleDrawable = (RippleDrawable) button.getBackground();
                Drawable.ConstantState state = rippleDrawable.getConstantState();
                try {
                    Field colorField = state.getClass().getDeclaredField("mColor");
                    colorField.setAccessible(true);
                    ColorStateList colorStateList = (ColorStateList) colorField.get(state);
                    buttonColor = colorStateList.getDefaultColor();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return buttonColor;
    }

    public int getScreenWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public void hideSection(View view) {
//        ViewGroup.LayoutParams hiddenSection = view.getLayoutParams();
//        hiddenSection.height = 0;
//        view.setLayoutParams(hiddenSection);
        view.setVisibility(View.GONE);
    }

    public void showSection(View view) {
        view.setVisibility(View.VISIBLE);
        ViewGroup.LayoutParams hiddenSection = view.getLayoutParams();
        hiddenSection.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        view.setLayoutParams(hiddenSection);
    }

    public void openUrlInExternalBrowser(Context context, String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://") && !url.startsWith("mailto:") && !url.startsWith("fb://")) {
            url = "http://" + url;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        i.setData(Uri.parse(url));
        context.startActivity(i);
    }

    public void openFacebook(Context context, HashMap<String, HashMap<String, String>> urlSets) {
        int randomNumber = getRandomNumberInRange(0, urlSets.keySet().size() - 1);
        String fbUrl = urlSets.get("set" + randomNumber).get("fbUrl");
        String fbId = urlSets.get("set" + randomNumber).get("fbId");

        try {
            String url = getFacebookPageURL(context, fbUrl, fbId);
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            i.setData(Uri.parse(url));
            context.startActivity(i);
        } catch (Exception ex) {
            openUrlInExternalBrowser(context, fbUrl);
        }
    }

    //method to get the right URL to use in the intent
    private String getFacebookPageURL(Context context, String fbUrl, String fbId) {
        PackageManager packageManager = context.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                return "fb://facewebmodal/f?href=" + fbUrl;
            } else { //older versions of fb app
                return "fb://profile/" + fbId;
            }
        } catch (PackageManager.NameNotFoundException e) {
            return fbUrl; //normal web url
        }
    }

    public String removeLastCharacters(String string, int length) {
        if (string.length() >= length) {
            return string.substring(0, string.length() - length);
        }
        return "";
    }

    public String removeFirstCharacters(String string, int length) {
        if (string.length() >= length) {
            return string.substring(length - 1, string.length() - 1);
        }
        return "";
    }

    public int getRandomNumberInRange(int min, int max) {

        if (min >= max) {
            int temp = min;
            min = max;
            max = temp;
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

    public HashMap<String, HashMap<String, String>> initFBUrlSets(HashMap<String, HashMap<String, String>> sets, int order, String fbUrl, String fbId) {
        HashMap<String, String> urlSet = new HashMap<>();
        urlSet.put("fbUrl", fbUrl);
        urlSet.put("fbId", fbId);
        sets.put("set" + order, urlSet);
        return sets;
    }
}
