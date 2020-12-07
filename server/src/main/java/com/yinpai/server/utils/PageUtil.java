package com.yinpai.server.utils;

import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class PageUtil {

    /**
     * @author weilai
     * @param total
     * @param page
     * @param size
     * @return
     */
    public static String pageHtml(Long total, Integer page, Integer size) {

        HttpServletRequest request = ProjectUtil.getRequest();
        String url = request.getQueryString();
        Map<String, String> map = ProjectUtil.getUrlParams(url);
        map.remove("page");
        String url_args = ProjectUtil.getUrlParamsByMap(map);
        if (!StringUtils.isEmpty(url_args)) {
            url_args = "&".concat(url_args);
        }
        String str = "";
        Integer last_page = (int) Math.ceil( (double)total / size);
        if (last_page > 1) {
            Integer window = 6;
            str += "<ul class=\"pagination\">";
            str += buildPageHtml("prev", 0, 0, page, url_args);
            if (last_page < (window + 6)) {
                str += buildPageHtml("loop", 1, last_page, page, url_args);
            } else if (page <= window) {
                str += buildPageHtml("loop", 1, window + 3, page, url_args);
                str += buildPageHtml("behind", 0, last_page, page, url_args);
            } else if (page > (last_page - window)) {
                str += buildPageHtml("front", 0, 0, page, url_args);
                str += buildPageHtml("loop", last_page - window - 2, last_page + 1, page, url_args);
            } else {
                str += buildPageHtml("front", 0, 0, page, url_args);
                str += buildPageHtml("loop", page - window / 2, page + window / 2, page, url_args);
                str += buildPageHtml("behind", 0, last_page, page, url_args);
            }
            str += buildPageHtml("next", 0, last_page, page, url_args);
            str += "</ul>";
        }
        return str;
    }

    /**
     * @author
     * @param type
     * @param start
     * @param end
     * @param current
     * @param url_args
     * @return
     */
    private static String buildPageHtml(String type, Integer start, Integer end, Integer current, String url_args) {
        String str = "";
        switch (type) {
            case "prev":
                if (current == 1) {
                    str += "<li class=\"disabled\"><span>&laquo;</span></li>";
                } else {
                    str += "<li><a href=\"?page=" + (current - 1) + url_args + "\">&laquo;</a></li>";
                }
                break;
            case "front":
                str += "<li><a href=\"?page=1" + url_args + "\">1</a></li><li><a href=\"?page=2" + url_args +"\">2</a></li><li class=\"disabled\"><span>...</span>";
                break;
            case "loop":
                for (int i = start; i <= end; i++) {
                    if (i == current) {
                        str += "<li class=\"active\"><span>" + i + "</span></li>";
                    } else {
                        str += "<li><a href=\"?page=" + i + url_args + "\">" + i + "</a></li>";
                    }
                }
                break;
            case "behind":
                str += "<li class=\"disabled\"><span>...</span><li><a href=\"?page=" + (end - 1) + url_args + "\">" + (end - 1) + "</a></li><li><a href=\"?page=" + end + url_args + "\">" + end + "</a></li>";
                break;
            case "next":
                if (current == end) {
                    str += "<li class=\"disabled\"><span>&raquo;</span></li>";
                } else {
                    str += "<li><a href=\"?page=" + (current + 1) + url_args + "\">&raquo;</a></li>";
                }
                break;
            default:
                break;
        }
        return str;
    }
}
