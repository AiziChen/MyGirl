package org.pub.girlview.scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.pub.girlview.domain.Girl;
import org.pub.girlview.domain.Item;
import org.pub.girlview.tools.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 主页解析
 *
 * @author Quanyec
 */
public class IndexScanner {

    private static Document doc;
    private static Document galleryDoc;
    private static Document hotestDoc;
    private static Document updateDoc;

    /**
     * Document singleton instance
     *
     * @return
     */
    public static Document getDoc() {
        if (doc == null) {
            try {
                IndexScanner.doc = Jsoup.connect(Constants.BASE_URL).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return doc;
    }

    public static Document getGalleryDoc() {
        if (galleryDoc == null) {
            try {
                IndexScanner.galleryDoc = Jsoup.connect(Constants.BASE_URL + "/gallery/").get();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return galleryDoc;
    }

    public static Document getHotestDoc() {
        if (hotestDoc == null) {
            try {
                IndexScanner.hotestDoc = Jsoup.connect(Constants.BASE_URL + "/rank/").get();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return hotestDoc;
    }

    public static Document getUpdateDoc() {
        if (updateDoc == null) {
            try {
                IndexScanner.updateDoc = Jsoup.connect(Constants.BASE_URL + "/tag/new/").get();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return updateDoc;
    }

    /**
     * 获取最新的女神套图
     *
     * @return
     */
    public static List<Item> getGalleryItems() {
        List<Item> result = new ArrayList<>();
        Elements newList = getGalleryDoc().selectFirst("#gallerydiv").select("div.ck-initem");
        for (Element ele : newList) {
            String src = ele.selectFirst("mip-img").attr("src");
            String href = ele.selectFirst("a").attr("href");
            String title = ele.selectFirst("mip-img").attr("alt");
            Item item = new Item(title, src, href);
            result.add(item);
        }
        return result;
    }

    /**
     * 获取最新的女神列表
     *
     * @return
     */
    public static List<Girl> getUpdateGirls() {
        List<Girl> result = new ArrayList<>();
        Elements updateList = getUpdateDoc().selectFirst("#dlist").select("div.ck-initem");
        for (Element ele : updateList) {
            String name = ele.selectFirst("span.ck-title").text();
            String src = ele.selectFirst("img").attr("src");
            String href = ele.selectFirst("a").attr("href");
            href = Constants.BASE_URL + href;
            String desc = ele.selectFirst("li.ticket_btn").selectFirst("a").text();
            Girl girl = new Girl(name, src, href, desc);
            result.add(girl);
        }
        return result;
    }

    public static List<Girl> getHotestGirls() {
        List<Girl> result = new ArrayList<>();
        Elements updateList = getHotestDoc().selectFirst("#dlist").select("div.ck-initem");
        for (Element ele : updateList) {
            String name = ele.selectFirst("span.ck-title").text();
            String src = ele.selectFirst("img").attr("src");
            String href = ele.selectFirst("a").attr("href");
            href = Constants.BASE_URL + href;
            String desc = ele.select("div.ck-content-wrap").get(1).text();
            Girl girl = new Girl(name, src, href, desc);
            result.add(girl);
        }
        return result;
    }
}
