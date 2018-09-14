package org.pub.girlview.scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.pub.girlview.domain.Girl;
import org.pub.girlview.tools.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * ��ǰͼ���scanner
 *
 * @author Quanyec
 */
public class ShowScanner {

    private static Document doc;

    private String href;

    public ShowScanner(String href, Integer index) throws IOException {
        this.href = href;
        Document doc = Jsoup.connect(href).get();
        Element ele = doc.selectFirst("#pagediv").selectFirst("span.page");
        String text = ele.text();
        int page = Integer.parseInt(text.substring(text.indexOf('/') + 1));
        if (index > 0 && index <= page) {
            ShowScanner.doc = Jsoup.connect(href + index + ".html").get();
        } else {
            throw new RuntimeException("page have been out of the total page-index!");
        }
    }

    public ShowScanner(String href) {
        this.href = href;
    }

    public List<Document> getAllDoc(String href) {
        List<Document> result = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(href).get();
            Element ele = doc.selectFirst("#pagediv").selectFirst("span.page");
            String text = ele.text();
            int page = Integer.parseInt(text.substring(text.indexOf('/') + 1));
            for (int i = 1; i <= page; ++i) {
                Document d = Jsoup.connect(href + i + ".html").get();
                result.add(d);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * ��ʾ��ҳ������ͼƬ
     *
     * @return
     */
    public ArrayList<String> getPageImages() {
        ArrayList<String> result = new ArrayList<>();
        Elements eles = doc.selectFirst("#idiv").select("div.ck-parent-div");
        for (Element ele : eles) {
            String src = ele.selectFirst("img").attr("src");
            result.add(src);
        }

        return result;
    }


    /**
     * Get all images
     *
     * @return
     */
    public ArrayList<String> getAllImages() {
        ArrayList<String> result = new ArrayList<>();
        for (Document doc : getAllDoc(href)) {
            Elements eles = doc.selectFirst("#idiv").select("div.ck-parent-div");
            for (Element ele : eles) {
                String src = ele.selectFirst("img").attr("src");
                result.add(src);
            }
        }

        return result;
    }


    /**
     * Get CurrentAlbum Girl
     *
     * @return
     */
    public Girl getCurrentGirl() {
        Element ele = doc.selectFirst("#dgirl").selectFirst("div.ck-initem");
        String name = ele.selectFirst("span.ck-title").text();
        String src = ele.selectFirst("img").attr("src");
        String href = ele.select("a").attr("href");
        href = Constants.BASE_URL + href;
        String desc = "";

        return new Girl(name, src, href, desc);
    }
}
