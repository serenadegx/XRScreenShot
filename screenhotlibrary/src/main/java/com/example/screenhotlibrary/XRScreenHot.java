package com.example.screenhotlibrary;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;

public class XRScreenHot {
    private static XRScreenHot mInstance;
    private static final String[] KEYWORDS = {
            "screenshot", "screen_shot", "screen-shot", "screen shot",
            "screencapture", "screen_capture", "screen-capture", "screen capture",
            "screencap", "screen_cap", "screen-cap", "screen cap"
    };

    /**
     * 读取媒体数据库时需要读取的列(截图路径，截图时间戳)
     */
    private static final String[] MEDIA_PROJECTIONS = {
            MediaStore.Images.ImageColumns.DATA,
            MediaStore.Images.ImageColumns.DATE_TAKEN,
    };

    private Context mContext;
    private MediaContentObserver mInternalObserver;
    private MediaContentObserver mExternalObserver;
    private Handler mHandler;
    private Handler handler;


    private XRScreenHot() {

    }

    private XRScreenHot(Context context) {
        this.mContext = context;
        handler = new Handler();
    }

    public static XRScreenHot with(Context context) {
        if (mInstance == null) {
            mInstance = new XRScreenHot(context);
        }
        return mInstance;
    }

    public void start(ScreenHotListener listener) {
        HandlerThread mHandlerThread = new HandlerThread("Screenshot_Observer");
        mHandlerThread.start();

        mHandler = new Handler(mHandlerThread.getLooper());
        mInternalObserver = new MediaContentObserver(MediaStore.Images.Media.INTERNAL_CONTENT_URI, mHandler, listener);
        mExternalObserver = new MediaContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, mHandler, listener);

        mContext.getContentResolver().registerContentObserver(MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                false, mInternalObserver);
        mContext.getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                false, mExternalObserver);
    }

    public void recycle() {
        if (mInternalObserver != null && mExternalObserver != null) {
            mContext.getContentResolver().unregisterContentObserver(mInternalObserver);
            mContext.getContentResolver().unregisterContentObserver(mExternalObserver);
        }
    }

    /**
     * 媒体内容观察者(观察媒体数据库的改变)
     */
    private class MediaContentObserver extends ContentObserver {

        private Uri mContentUri;
        private ScreenHotListener mListener;

        public MediaContentObserver(Uri contentUri, Handler handler, ScreenHotListener listener) {
            super(handler);
            mContentUri = contentUri;
            mListener = listener;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            handleMediaContentChange(mContentUri);
        }

        private void handleMediaContentChange(Uri contentUri) {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Cursor cursor = mContext.getContentResolver().query(contentUri, MEDIA_PROJECTIONS, null, null,
                        MediaStore.Images.ImageColumns.DATE_ADDED + " desc limit 1");
                if (cursor == null) {
                    return;
                }
                if (!cursor.moveToFirst()) {
                    return;
                }
                final String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA));
                final long dateTaken = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN));
                if (checkScreenHot(path)) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mListener.onScreenHotSuccess(path, dateTaken);
                        }

                    });
                }
            }

        }

        private boolean checkScreenHot(String path) {
            for (String keyWord :
                    KEYWORDS) {
                if (path.toLowerCase().contains(keyWord)) {
                    return true;
                }
            }
            return false;
        }
    }
}
