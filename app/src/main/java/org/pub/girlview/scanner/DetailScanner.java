package org.pub.girlview.scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.pub.girlview.domain.Gesture;
import org.pub.girlview.domain.GirlDetail;

import java.io.IOException;
import java.util.ArrayList;

/**
 * ��ǰhref�µ�Ů���������
 *
 * @author Quanyec
 */
public class DetailScanner {

    private static Document doc;

    /**
     * 初始化获取详细信息的href
     * @param href  - detail href
     */
    public DetailScanner(String href) {
        try {
            doc = Jsoup.connect(href).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * ��ȡŮ����Ϣ
     *
     * @return
     */
    public GirlDetail getGirlDetail() {
        Element ele = doc.selectFirst("#dmain");

        String name = ele.selectFirst("h1.ckd-content").text();

        String info = ele.select("p").get(1).text();
        String src = ele.selectFirst("img").attr("src");
        GirlDetail detail = new GirlDetail(name, src, info);
        return detail;
    }


    /**
     * 获取所有评论
     * @return
     */
    public ArrayList<Gesture> getComments() {
        ArrayList<Gesture> result = new ArrayList<>();
        Element ele = doc.selectFirst(".ck-list");
        Elements eles = ele.select("div.ck-title-box");
        for (Element e : eles) {
            String name = "游客";
            String comment = e.select("div.ck-content-wrap").get(1).text();
            String time = e.selectFirst("div[style]").text();
            String area = e.select("div[style]").get(1).text();
            Gesture gesture = new Gesture(name, comment, area, time);
            result.add(gesture);
        }

        return result;
    }

}
