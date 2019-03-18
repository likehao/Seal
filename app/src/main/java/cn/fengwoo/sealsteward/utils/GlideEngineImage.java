package cn.fengwoo.sealsteward.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.zhihu.matisse.engine.ImageEngine;

import cn.fengwoo.sealsteward.R;

/**
 * Glide加载图片类,重写此类防止Glide4.0之后api调用方式更改会导致出错
 */
public class GlideEngineImage implements ImageEngine{
    /**
     * 加载缩列图
     */
    @Override
    public void loadThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView, Uri uri) {

        Glide.with(context)
                .load(uri)
              //  .asBitmap()  // some .jpeg files are actually gif
                .placeholder(placeholder)   //这里可自己添加占位图
                .override(resize, resize)
                .centerCrop()
            //    .error(R.drawable.photo_03)
                .into(imageView);
    }

    @Override
    public void loadGifThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView,
                                 Uri uri) {
        Glide.with(context)
                .load(uri)
           //     .asBitmap()
                .placeholder(placeholder)
                .override(resize, resize)
                .centerCrop()
           //     .error(R.drawable.photo_03)//这里可自己添加出错图
                .into(imageView);
    }

    @Override
    public void loadImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri) {
        Glide.with(context)
                .load(uri)
                .override(resizeX, resizeY)
                .priority(Priority.HIGH)
                .fitCenter()
                .into(imageView);
    }

    @Override
    public void loadGifImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri) {
        Glide.with(context)
                .load(uri)
           //     .asGif()
                .override(resizeX, resizeY)
                .priority(Priority.HIGH)
                .into(imageView);
    }

    @Override
    public boolean supportAnimatedGif() {
        return true;
    }

}
