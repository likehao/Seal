package cn.fengwoo.sealsteward.engine;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.maning.imagebrowserlibrary.ImageEngine;

import cn.fengwoo.sealsteward.R;

/**
 * 预览图片所需的图片引擎  （MNImageBrowser）
 */
public class GlideImageEngine implements ImageEngine {

    @Override
    public void loadImage(Context context, String url, ImageView imageView, final View progressView) {
        Glide.with(context)
                .asBitmap()
                .load(url)
                .apply(new RequestOptions().fitCenter().error(R.mipmap.ic_launcher).placeholder(R.drawable.default_placeholder))
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        progressView.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        progressView.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(imageView);
    }
}
