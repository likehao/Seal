package cn.fengwoo.sealsteward.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Point;

import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.filter.Filter;
import com.zhihu.matisse.internal.entity.IncapableCause;
import com.zhihu.matisse.internal.entity.Item;
import com.zhihu.matisse.internal.utils.PhotoMetadataUtils;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import cn.fengwoo.sealsteward.R;

/**
 * matisse图片选择的过滤器
 */
public class GifSizeFilter extends Filter {
    private int mMinWidth;
    private int mMinHeight;
    private int mMaxSize;

    public GifSizeFilter(int minWidth, int minHeight, int maxSizeInBytes) {
        mMinWidth = minWidth;
        mMinHeight = minHeight;
        mMaxSize = maxSizeInBytes;
    }

    /**
     * 返回需要过滤的数据类型
     * @return
     */
    @Override
    public Set<MimeType> constraintTypes() {
        return new HashSet<MimeType>() {{
            add(MimeType.GIF);
        }};
    }

    /**
     * 决定是否过滤，过滤的话就return new IncapableCause(“......”); 填入过滤的原因即可
     * @param context
     * @param item
     * @return
     */
    @SuppressLint("StringFormatInvalid")
    @Override
    public IncapableCause filter(Context context, Item item) {
        if (!needFiltering(context, item))
            return null;

        Point size = PhotoMetadataUtils.getBitmapBound(context.getContentResolver(), item.getContentUri());
        if (size.x < mMinWidth || size.y < mMinHeight || item.size > mMaxSize) {
            return new IncapableCause(IncapableCause.DIALOG, context.getString(R.string.error_gif, mMinWidth,
                    String.valueOf(PhotoMetadataUtils.getSizeInMB(mMaxSize))));
        }

   /*     try {
            InputStream inputStream = getContentResolver().openInputStream(item.getContentUri());
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, options);
            int width = options.outWidth;
            int height = options.outHeight;
            if (width >= 500)
                return new IncapableCause("宽度超过500px");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/
        return null;
    }
}
