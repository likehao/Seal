package cn.fengwoo.sealsteward.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class PermissionUtils {

    private PermissionUtils() {

    }

    //需要申请的权限
    private static String[] permissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    //权限检查
    public static String[] checkPermissions(Context context) {
        List<String> data = new ArrayList<>(); //存储未申请的权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                int checkSelfPermission = ContextCompat.checkSelfPermission(context, permission);
                if (checkSelfPermission == PackageManager.PERMISSION_DENIED) ;  //权限被拒绝
                data.add(permission);
            }
        }
        return data.toArray(new String[data.size()]);
    }

}
