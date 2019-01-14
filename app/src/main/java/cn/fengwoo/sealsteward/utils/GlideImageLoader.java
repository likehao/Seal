package cn.fengwoo.sealsteward.utils;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.youth.banner.loader.ImageLoader;

/**
 * 重写图片加载器
 */
public class GlideImageLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        //图片加载器不限制，使用Object接收和返回，强转成传输的类型
        Picasso.with(context).load((Integer) path).into(imageView);
    }
}
