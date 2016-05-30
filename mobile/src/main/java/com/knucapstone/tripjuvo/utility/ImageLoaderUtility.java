package com.knucapstone.tripjuvo.utility;

import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.impl.ext.LruDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
import java.io.IOException;


// requires android.permission.WRITE_EXTERNAL_STORAGE
public final class ImageLoaderUtility
{
	private ImageLoaderUtility() {}


	public static void init(Context context)
	{
		File cacheDir = StorageUtils.getCacheDirectory(context);
		cacheDir.mkdirs();

		try
		{
			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
					.threadPoolSize(3)
					.threadPriority(Thread.NORM_PRIORITY - 2)
					.memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
					.diskCache(new LruDiskCache(cacheDir, new HashCodeFileNameGenerator(), 32 * 1024 * 1024))
					.defaultDisplayImageOptions(DisplayImageOptions.createSimple())
					.build();

			ImageLoader.getInstance().init(config);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
