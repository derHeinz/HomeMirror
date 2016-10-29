package com.morristaedt.mirror.modules;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

import com.morristaedt.mirror.rssreader.AndroidRSSFeedParser;
import com.morristaedt.mirror.rssreader.Feed;
import com.morristaedt.mirror.rssreader.FeedMessage;
import com.morristaedt.mirror.utils.URIUtil;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Heinz on 25.10.2016.
 */

public class NASAIotdModule {

    public interface NASAIotdListener {
        void onNASAIotdToday(Bitmap picture);
    }

    /**
     * Fetch the the latest xkcd comic, but only show it if its new today
     *
     * @param listener
     */
    public static void getNASAImageOfTheDay(final NASAIotdModule.NASAIotdListener listener) {
        new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... params) {
                String url = "http://www.nasa.gov/rss/dyn/lg_image_of_the_day.rss";
                InputStream in = null;
                try {
                    AndroidRSSFeedParser parser = new AndroidRSSFeedParser();
                    Feed feed = parser.load(url);

                    // get first (a.k.a. latest) message
                    FeedMessage msg = feed.getMessages().get(0);
                    String headline = msg.getTitle();
                    //Uri uri = Uri.parse(msg.getEnclosureUrl());

                    // first decode with inJustDecodeBounds to check dimensions
                    in = URIUtil.load(msg.getEnclosureUrl());
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeStream(in, null, options);
                    in.close();

                    // Calculate inSampleSize
                    options.inSampleSize = calculateInSampleSize(options, 200, 200);

                    // Decode bitmap with inSampleSize set
                    options.inJustDecodeBounds = false;
                    in = URIUtil.load(msg.getEnclosureUrl());
                    Bitmap img = BitmapFactory.decodeStream(in, null, options);

                    return img;
                } catch (Exception e) {
                    // not great to catch general exceptions, but this lib is being sketchy
                    Log.e("NewsModule", "Error parsing RSS");
                    return null;
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            // TODO make real error handling.
                            e.printStackTrace();
                        }
                    }
                }
            }

            protected int calculateInSampleSize(
                    BitmapFactory.Options options, int reqWidth, int reqHeight) {
                // Raw height and width of image
                final int height = options.outHeight;
                final int width = options.outWidth;
                int inSampleSize = 1;

                if (height > reqHeight || width > reqWidth) {

                    final int halfHeight = height / 2;
                    final int halfWidth = width / 2;

                    // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                    // height and width larger than the requested height and width.
                    while ((halfHeight / inSampleSize) >= reqHeight
                            && (halfWidth / inSampleSize) >= reqWidth) {
                        inSampleSize *= 2;
                    }
                }

                return inSampleSize;
            }

            @Override
            protected void onPostExecute(@Nullable Bitmap img) {
                listener.onNASAIotdToday(img);
            }
        }.execute();

    }

}
