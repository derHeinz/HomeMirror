package com.morristaedt.mirror.rssreader;

import android.util.Xml;

import com.morristaedt.mirror.utils.URIUtil;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Heinz on 28.10.2016.
 * With thanks to http://www.vogella.com/tutorials/RSSFeed/article.html.
 */

public class AndroidRSSFeedParser {

    private static final String ns = null;

    public Feed load(String uri) throws XmlPullParserException, IOException {
        return parse(URIUtil.load(uri));
    }

    public Feed parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private Feed readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<FeedMessage> items = new ArrayList();

        // create Feed
        Feed result = new Feed(null, null, null, null, null, null);
        // TODO read stuff from the feed itself

        parser.require(XmlPullParser.START_TAG, ns, "rss");

        // don't know why but 2 times next is a good idea here:
        parser.next();
        parser.next();

        parser.require(XmlPullParser.START_TAG, ns, "channel");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("item")) {
                items.add(readItem(parser));
            } else {
                skip(parser);
            }
        }
        result.getMessages().addAll(items);
        return result;
    }

    private FeedMessage readItem(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "item");

        FeedMessage msg = new FeedMessage();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("title")) {
                msg.setTitle(readTextElement(parser, "title"));
            } else if (name.equals("description")) {
                msg.setDescription(readTextElement(parser, "description"));
            } else if (name.equals("enclosure")) {
                String[] readEnclosureUrls = readTagAttributeValuesAsText(parser, "enclosure", new String[]{"url"});
                msg.setEnclosureUrl(readEnclosureUrls[0]);
            } else {
                skip(parser);
            }
        }
        return msg;
    }

    // Processes link tags in the feed.
    private String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
        String link = "";
        parser.require(XmlPullParser.START_TAG, ns, "link");
        String tag = parser.getName();
        String relType = parser.getAttributeValue(null, "rel");
        if (tag.equals("link")) {
            if (relType.equals("alternate")){
                link = parser.getAttributeValue(null, "href");
                parser.nextTag();
            }
        }
        parser.require(XmlPullParser.END_TAG, ns, "link");
        return link;
    }

    /**
     * Reads given attributes from the tag given in as String.
     * @param parser
     * @param tagName
     * @param attributes
     * @return
     * @throws IOException
     * @throws XmlPullParserException
     */
    private String[] readTagAttributeValuesAsText(XmlPullParser parser, String tagName, String[] attributes) throws IOException, XmlPullParserException {
        String[] result = new String[attributes.length];

        parser.require(XmlPullParser.START_TAG, ns, tagName);
        for (int i = 0; i<attributes.length; i++) {
            result[i] = parser.getAttributeValue(null, attributes[i]);
        }

        //parser.require(XmlPullParser.END_TAG, ns, tagName);
        return result;
    }

    /**
     * Reads text element given by the tagName.
     * @param parser
     * @param tagName
     * @return
     * @throws IOException
     * @throws XmlPullParserException
     */
    private String readTextElement(XmlPullParser parser, String tagName) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, tagName);
        String result = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, tagName);
        return result;
    }

    // For the tags title and summary, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    /**
     * Skip elements.
     * @param parser
     * @throws XmlPullParserException
     * @throws IOException
     */
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

}
