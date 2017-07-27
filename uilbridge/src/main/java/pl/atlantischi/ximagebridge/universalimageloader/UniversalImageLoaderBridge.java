package pl.atlantischi.ximagebridge.universalimageloader;

import java.io.File;

import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.display.CircleBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.StorageUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import pl.atlantischi.ximagebridge.interfaces.IUniversalImageLoaderBridge;
import pl.atlantischi.ximagebridge.options.BridgeOptions;
import pl.atlantischi.ximagebridge.universalimageloader.processor.BlurProcessor;

import static pl.atlantischi.ximagebridge.util.Preconditions.*;

/**
 * Created by admin on 2017/7/7.
 */

public class UniversalImageLoaderBridge implements IUniversalImageLoaderBridge {

    @Override
    public void initialize(Context context) {
        ImageLoader.getInstance().init(buildImageConfig(context));
    }

    private ImageLoaderConfiguration buildImageConfig(Context context) {
        return ImageLoaderConfiguration.createDefault(context);
        //        File cacheDir = StorageUtils.getCacheDirectory(context);  //缓存文件夹路径
        //        return new ImageLoaderConfiguration.Builder(context)
        //                    .memoryCacheExtraOptions(480, 800) // default = device screen dimensions 内存缓存文件的最大长宽
        //                    .diskCacheExtraOptions(480, 800, null)  // 本地缓存的详细信息(缓存的最大长宽)，最好不要设置这个
        //                    .taskExecutor(...)
        //                    .taskExecutorForCachedImages(...)
        //                    .threadPoolSize(3) // default  线程池内加载的数量
        //                    .threadPriority(Thread.NORM_PRIORITY - 2) // default 设置当前线程的优先级
        //                    .tasksProcessingOrder(QueueProcessingType.FIFO) // default
        //                    .denyCacheImageMultipleSizesInMemory()
        //                    .memoryCache(new LruMemoryCache(2 * 1024 * 1024)) //可以通过自己的内存缓存实现
        //                    .memoryCacheSize(2 * 1024 * 1024)  // 内存缓存的最大值
        //                    .memoryCacheSizePercentage(13) // default
        //                    .diskCache(new UnlimitedDiscCache(cacheDir)) // default 可以自定义缓存路径
        //                    .diskCacheSize(50 * 1024 * 1024) // 50 Mb sd卡(本地)缓存的最大值
        //                    .diskCacheFileCount(100)  // 可以缓存的文件数量
        //                     //default为使用HASHCODE对UIL进行加密命名， 还可以用MD5(new Md5FileNameGenerator())加密
        //                    .diskCacheFileNameGenerator(new HashCodeFileNameGenerator())
        //                    .imageDownloader(new BaseImageDownloader(context)) // default
        //                    .imageDecoder(new BaseImageDecoder()) // default
        //                    .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
        //                    .discCache(new LimitedAgeDiscCache(cacheDir, 7 * 24 * 60 * 60))// 自定义缓存路径,7天后自动清除缓存
        //                    .writeDebugLogs() // 打印debug log
        //                    .build();
    }

    @Override
    public void display(Uri uri, ImageView imageView) {
    }

    @Override
    public void display(Uri uri, ImageView imageView, BridgeOptions bridgeOptions) {
        checkNotNull(uri);
        checkNotNull(imageView);
        final Context context = imageView.getContext();
        DisplayImageOptions options = null;
        ImageSize imageSize = null;
        if (bridgeOptions != null) {
            DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
            //                .showStubImage(R.drawable.ic_stub)//缓冲过程中图片
            //                .showImageForEmptyUri(R.mipmap.ic_launcher)// 设置图片Uri为空或是错误的时候显示的图片
            //                .showImageOnFail(R.drawable.ic_error)// 设置图片加载或解码过程中发生错误显示的图片
            //                .cacheInMemory(true)//缓存道内存
            //                .cacheOnDisk(true)//缓存到硬盘
            //                .bitmapConfig(Bitmap.Config.ARGB_8888)   //设置图片的解码类型
            if (bridgeOptions.isCircle) {
                builder.displayer(new CircleBitmapDisplayer());
            } else if (bridgeOptions.roundCorner > 0) {
                builder.displayer(new RoundedBitmapDisplayer(bridgeOptions.roundCorner));
            }
            if (bridgeOptions.blurRadius > 0) {
                builder.postProcessor(new BlurProcessor(context, bridgeOptions.blurRadius));
            }
            options = builder.build();
            if (bridgeOptions.size != null && bridgeOptions.size.isValid()) {
                int imageWidth = bridgeOptions.size.width;
                int imageHeight = bridgeOptions.size.height;
                imageSize = new ImageSize(imageWidth, imageHeight);
            }
            if (bridgeOptions.placeHolderDrawable != null) {
                builder.showImageOnLoading(bridgeOptions.placeHolderDrawable);
            } else if (bridgeOptions.placeHolderResId > 0) {
                builder.showImageOnLoading(bridgeOptions.placeHolderResId);
            }
        }

        ImageLoader.getInstance().displayImage(uri.toString(), new ImageViewAware(imageView), options, imageSize,
                new ImageLoadingListenerImpl(), new ImageLoadingProgressListenerImpl());
    }

    private static class ImageLoadingListenerImpl extends SimpleImageLoadingListener {

    }

    private static class ImageLoadingProgressListenerImpl implements ImageLoadingProgressListener {

        @Override
        public void onProgressUpdate(String imageUri, View view, int current, int total) {

        }
    }

    @Override
    public void getBitmapFromUri(Uri uri, final BitmapLoader bitmapLoader) {
        checkNotNull(uri);
        ImageLoader.getInstance().loadImage(uri.toString(), new ImageLoadingListenerImpl(){
            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (bitmapLoader == null) {
                    return;
                }
                bitmapLoader.onBitmapLoaded(loadedImage);
            }
        });
    }

}
