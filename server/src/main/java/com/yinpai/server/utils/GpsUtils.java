package com.yinpai.server.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.geo.Point;
import org.springframework.util.StringUtils;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/21 4:27 下午
 */
@Slf4j
public class GpsUtils {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GpsPoint {
        /**
         * 经度
         */
        private Double longitude;

        /**
         * 纬度
         */
        private Double latitude;
    }

    public static GpsPoint gpsStringToGpsPoint(String gps) {
        if (!StringUtils.isEmpty(gps)) {
            try {
                String[] gpsArr = gps.split(",");
                return GpsPoint.builder().latitude(Double.parseDouble(gpsArr[0])).longitude(Double.parseDouble(gpsArr[1])).build();
            } catch (Exception e) {
                log.error("GPS坐标转换失败:" + gps);
            }
        }
        return null;
    }

    public static Point gpsStringToPoint(String gps) {
        if (!StringUtils.isEmpty(gps)) {
            try {
                String[] gpsArr = gps.split(",");
                return new Point(Double.parseDouble(gpsArr[1]), Double.parseDouble(gpsArr[0]));
            } catch (Exception e) {
                log.error("GPS坐标转换失败:" + gps);
            }
        }
        return null;
    }

    public static Point geoPointToPoint(GeoPoint geoPoint) {
        if (geoPoint != null) {
            return new Point(geoPoint.getLon(), geoPoint.getLat());
        }
        return null;
    }

    public static GeoPoint pointToGeoPoint(Point point) {
        if (point != null) {
            return new GeoPoint(point.getX(), point.getY());
        }
        return null;
    }

    public static GeoPoint gpsStringToGeoPoint(String gps) {
        GpsPoint gpsPoint = gpsStringToGpsPoint(gps);
        if (gpsPoint != null) {
            return new GeoPoint(gpsPoint.getLatitude(), gpsPoint.getLongitude());
        }
        return null;
    }
}
