package com.yinpai.server.utils;

import com.yinpai.server.service.AdminService;
import com.yinpai.server.thread.threadlocal.LoginAdminThreadLocal;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ProjectUtil {

    public static boolean isAjax() {
        HttpServletRequest request = ProjectUtil.getRequest();
        String xrw = request.getHeader("x-requested-with");
        return !StringUtils.isEmpty(xrw) && "XMLHttpRequest".equals(xrw);
    }

    /**
     * 获取Specification
     * @author weilai
     * @param map
     * @return Specification
     */
    public static Specification getSpecification(Map<String, String> map) {
        return (Specification) (root, query, criteriaBuilder) -> {
            HttpServletRequest request = ProjectUtil.getRequest();
            List<Predicate> predicates = new ArrayList<>();
            for (String key : map.keySet()) {
                String param = request.getParameter(key);
                switch (map.get(key)) {
                    case "like":
                        if (!StringUtils.isEmpty(param)) {
                            predicates.add(criteriaBuilder.like(root.get(key), "%" + param + "%"));
                        }
                        break;
                    case "!=":
                        if (!StringUtils.isEmpty(param)) {
                            predicates.add(criteriaBuilder.notEqual(root.get(key), param));
                        }
                        break;
                    case "=":
                        if (!StringUtils.isEmpty(param)) {
                            predicates.add(criteriaBuilder.equal(root.get(key), param));
                        }
                        break;
                    default:
                        predicates.add(criteriaBuilder.equal(root.get(key), map.get(key)));
                        break;
                }
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }

    /**
     * 获取HttpServletRequest对象
     * @author weilai
     * @return HttpServletRequest
     */
    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    /**
     * 获取HttpServletResponse对象
     * @author weiai
     * @return HttpServletResponse
     */
    public static HttpServletResponse getResponse(){
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
    }

    /**
     * md5加密
     * @author weilai
     * @param plainText
     * @return
     */
    public static String md5(String plainText) {
        //定义一个字节数组
        byte[] secretBytes = null;
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            //对字符串进行加密
            md.update(plainText.getBytes());
            //获得加密后的数据
            secretBytes = md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("没有md5这个算法！");
        }
        //将加密后的数据转换为16进制数字
        String md5code = new BigInteger(1, secretBytes).toString(16);// 16进制数字
        // 如果生成数字未满32位，需要前面补0
        int length = md5code.length();
        for (int i = 0; i < 32 - length; i++) {
            md5code = "0" + md5code;
        }
        return md5code;
    }

    /**
     * 获取ip
     * @author weilai
     * @return
     */
    public static String getIpAddr() {
        HttpServletRequest request = ProjectUtil.getRequest();
        String ipAddress = null;
        try {
            ipAddress = request.getHeader("x-forwarded-for");
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
                if (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
                    // 根据网卡取本机配置的IP
                    InetAddress inet = null;
                    try {
                        inet = InetAddress.getLocalHost();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    ipAddress = inet.getHostAddress();
                }
            }
            // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            if (ipAddress != null && ipAddress.length() > 15) { // "***.***.***.***".length()
                // = 15
                if (ipAddress.indexOf(",") > 0) {
                    ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
                }
            }
        } catch (Exception e) {
            ipAddress="";
        }
        return ipAddress;
    }

    /**
     * 生成密码
     * @author weilai
     * @param password
     * @return
     */
    public static String password(String password) {
        return md5(password.concat("weilaiaifengyan"));
    }

    /**
     * @author weilai
     * @param param
     * @return
     */
    public static Map<String, String> getUrlParams(String param) {
        Map<String, String> map = new HashMap<>();
        if (StringUtils.isEmpty(param)) {
            return map;
        }
        String[] params = param.split("&");
        for (int i = 0; i < params.length; i++) {
            String[] p = params[i].split("=");
            if (p.length == 2) {
                map.put(p[0], p[1]);
            }
        }
        return map;
    }

    /**
     * @author weilai
     * @param map
     * @return
     */
    public static String getUrlParamsByMap(Map<String, String> map) {
        if (map == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        if (map.size() > 0) {
            for (String key : map.keySet()) {
                sb.append(key + "=");
                if (StringUtils.isEmpty(map.get(key))) {
                    sb.append("&");
                } else {
                    String value = map.get(key);
                    try {
                        value = URLEncoder.encode(value, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    sb.append(value + "&");
                }
            }
            sb = sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }
}
