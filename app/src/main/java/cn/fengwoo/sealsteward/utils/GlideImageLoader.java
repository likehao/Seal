package cn.fengwoo.sealsteward.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;
import com.youth.banner.loader.ImageLoader;

/**
 * 重写图片加载器
 */
public class GlideImageLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        if(path.getClass().equals(String.class)){
            Picasso.with(context).load(path.toString()).into(imageView);
        }
        else{
            Picasso.with(context).load((int)path).into(imageView);
        }

    }
}
