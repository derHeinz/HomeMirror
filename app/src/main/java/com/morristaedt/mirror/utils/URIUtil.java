package com.morristaedt.mirror.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Heinz on 28.10.2016.
 */

public class URIUtil {

    /**
     * Get content of URL/URI as input stream.
     *
     * @param uri
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    public static InputStream load(String uri) throws IOException {
        HttpGet httpget = new HttpGet(uri);
        InputStream result = null;
        DefaultHttpClient client = new DefaultHttpClient();
        HttpResponse e = client.execute(httpget);
        StatusLine status = e.getStatusLine();
        if (status.getStatusCode() != 200) {
            throw new IOException("Error reported");
        }

        HttpEntity entity = e.getEntity();
        result = entity.getContent();
        return result;
    }
}
