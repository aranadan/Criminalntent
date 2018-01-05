package com.example.andrey.criminalIntent;


import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

public class PictureUtils {
public static Bitmap getScaleBitmap (String path, int destWidth, int destHeight){
    //Чтение размеров изображения на диске
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeFile(path,options);

    float srcWidth = options.outWidth;
    float srcHeight = options.outHeight;

    //Вычисление степени масштабирования
    int inSampleSize = 1;
    if (srcHeight > destHeight || srcWidth > destWidth ){
        float heightScale = srcHeight / destHeight;
        float widthScale = srcWidth / destWidth;
        inSampleSize = Math.round(heightScale > widthScale ? heightScale : widthScale);
    }
    options = new BitmapFactory.Options();
    options.inSampleSize = inSampleSize;

    //Чтение данных и создание отогового изображения
    return BitmapFactory.decodeFile(path,options);
}

public static Bitmap getScaleBitmap(String path, Activity activity){
    Point size = new Point();
    activity.getWindowManager().getDefaultDisplay().getSize(size);

    return getScaleBitmap(path, size.x,size.y);
}
}
