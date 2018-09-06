package org.pub.girlview.scanner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.pub.girlview.domain.GirlDetail;

import java.io.IOException;

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

}
