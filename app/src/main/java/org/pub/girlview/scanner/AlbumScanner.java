package org.pub.girlview.scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.pub.girlview.domain.Item;
import org.pub.girlview.tools.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * ��ǰhref�µ�album(��ͼ���)
 *
 * @author Quanyec
 */
public class AlbumScanner {

    private static Document doc;

    /**
     *
     * @param href  - detail页面中的href
     */
    public AlbumScanner(String href) {
        try {
            doc = Jsoup.connect(href + "album/").get();
            if (doc.text().contains("出错")) {
                doc = Jsoup.connect(href).get();
            };
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ��ȡ���е���ͼ���
     */
    public List<Item> getGirlAlbumItems() {
        List<Item> result = new ArrayList<>();
        Elements eles = doc.selectFirst("#dphoto").select("div.ck-initem");
        for (Element ele : eles) {
            String title = ele.selectFirst("img").attr("alt");
            String src = ele.selectFirst("img").attr("src");
            String href = ele.selectFirst("a").attr("href");
            href = Constants.BASE_URL + href;
            Item item = new Item(title, src, href);
            result.add(item);
        }

        return result;
    }
}
