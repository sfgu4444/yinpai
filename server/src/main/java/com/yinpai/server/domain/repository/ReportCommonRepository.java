package com.yinpai.server.domain.repository;

import com.yinpai.server.domain.entity.OriginalApply;
import com.yinpai.server.vo.admin.ReportVo;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;

public interface ReportCommonRepository extends JpaRepository<OriginalApply, Integer>, JpaSpecificationExecutor<OriginalApply> {



     //作品点击量  %Y-
    @Query(nativeQuery = true, value = "Select count(1) total From yp_works_look_log " +
            "Where DATE_FORMAT(create_time,'%Y-%m-%d %H:%i:%S') >= (:startTime) " +
            "and DATE_FORMAT(create_time,'%Y-%m-%d %H:%i:%S' ) <= (:endTime) " +
            "and type != (:type)")
    Long getYpWorksLookLogCount(@Param("startTime") String startTime, @Param("endTime") String endTime, @Param("type") Integer type);
    @Query(nativeQuery = true, value ="SELECT DISTINCT(DATE_FORMAT(create_time,'%m-%d')) as time ,count(1)  as total " +
            "from yp_works_look_log " +
            "WHERE DATE_FORMAT(create_time,'%Y-%m-%d %H:%i:%S') >= (:startTime)  " +
            "and DATE_FORMAT(create_time,'%Y-%m-%d %H:%i:%S' ) <= (:endTime)  " +
            "and type != (:type) GROUP BY time")
    List<Map<String,Object>> getYpWorksLookLogByDay(@Param("startTime") String startTime, @Param("endTime") String endTime,@Param("type") Integer type);

    // 发布量
    @Query(nativeQuery = true, value = "Select count(1) total From yp_works " +
            "Where DATE_FORMAT(create_time,'%Y-%m-%d %H:%i:%S') >= (:startTime) " +
            "and DATE_FORMAT(create_time,'%Y-%m-%d %H:%i:%S' ) <= (:endTime) " +
            "and type != (:type)")
    Long getYpWorksCount(@Param("startTime") String startTime, @Param("endTime") String endTime, @Param("type") Integer type);
    @Query(nativeQuery = true, value ="SELECT DISTINCT(DATE_FORMAT(create_time,'%m-%d')) as time ,count(1)  as total " +
            "from yp_works " +
            "WHERE DATE_FORMAT(create_time,'%Y-%m-%d %H:%i:%S') >= (:startTime)  " +
            "and DATE_FORMAT(create_time,'%Y-%m-%d %H:%i:%S' ) <= (:endTime)  " +
            "and type != (:type) GROUP BY time")
    List<Map<String,Object>> getYpWorksByDay(@Param("startTime") String startTime, @Param("endTime") String endTime,@Param("type") Integer type);

    //登陆统计
    @Query(nativeQuery = true, value = "Select COUNT(1) total From yp_login_log " +
            "Where DATE_FORMAT(create_time,'%Y-%m-%d %H:%i:%S') >= (:startTime) " +
            "and DATE_FORMAT(create_time,'%Y-%m-%d %H:%i:%S' ) <= (:endTime)")
    Long getYpLoginLogCount(@Param("startTime") String startTime, @Param("endTime") String endTime);
    @Query(nativeQuery = true, value = "SELECT DISTINCT(DATE_FORMAT(create_time,'%m-%d')) as time ,count(1)  as total " +
            "from yp_login_log " +
            "WHERE DATE_FORMAT(create_time,'%Y-%m-%d %H:%i:%S') >= (:startTime) " +
            "and DATE_FORMAT(create_time,'%Y-%m-%d %H:%i:%S' ) <= (:endTime)  GROUP BY time")
    List<Map<String,Object>> getYpLoginLogByDay(@Param("startTime") String startTime, @Param("endTime") String endTime);


    //交易次数
    @Query(nativeQuery = true, value = "Select COUNT(1) total From yp_user_order " +
            "Where DATE_FORMAT(time_start,'%Y-%m-%d %H:%i:%S') >= (:startTime) " +
            "and DATE_FORMAT(time_start,'%Y-%m-%d %H:%i:%S' ) <= (:endTime)")
    Long getYpUserOrderCount(@Param("startTime") String startTime, @Param("endTime") String endTime);
    @Query(nativeQuery = true, value = "SELECT DISTINCT(DATE_FORMAT(time_start,'%m-%d')) as time ,count(1)  as total " +
            "from yp_user_order " +
            "WHERE DATE_FORMAT(time_start,'%Y-%m-%d %H:%i:%S') >= (:startTime) " +
            "and DATE_FORMAT(time_start,'%Y-%m-%d %H:%i:%S' ) <= (:endTime)  GROUP BY time")
    List<Map<String,Object>> getYpUserOrderByDay( @Param("startTime") String startTime, @Param("endTime") String endTime);

    //注册量
    @Query(nativeQuery = true, value = "Select COUNT(1) total From yp_user " +
            "Where DATE_FORMAT(create_time,'%Y-%m-%d %H:%i:%S') >= (:startTime) " +
            "and DATE_FORMAT(create_time,'%Y-%m-%d %H:%i:%S' ) <= (:endTime)")
    Long getYpUserCount(@Param("startTime") String startTime, @Param("endTime") String endTime);
    @Query(nativeQuery = true, value = "SELECT DISTINCT(DATE_FORMAT(create_time,'%m-%d')) as time ,count(1)  as total " +
            "from yp_user " +
            "WHERE DATE_FORMAT(create_time,'%Y-%m-%d %H:%i:%S') >= (:startTime) " +
            "and DATE_FORMAT(create_time,'%Y-%m-%d %H:%i:%S' ) <= (:endTime)  GROUP BY time")
    List<Map<String,Object>> getYpUserByDay(@Param("startTime") String startTime, @Param("endTime") String endTime);

    //交易额
    @Query(nativeQuery = true, value = "Select sum(total_fee) total From yp_user_order " +
            "Where DATE_FORMAT(time_start,'%Y-%m-%d %H:%i:%S') >= (:startTime) " +
            "and DATE_FORMAT(time_start,'%Y-%m-%d %H:%i:%S' ) <= (:endTime)")
    Long getYpUserOrderCountMoney(@Param("startTime") String startTime, @Param("endTime") String endTime);

    @Query(nativeQuery = true, value ="SELECT DISTINCT(DATE_FORMAT(time_start,'%m-%d')) as time ,sum(total_fee)  as total " +
            "from yp_user_order " +
            "WHERE DATE_FORMAT(time_start,'%Y-%m-%d %H:%i:%S') >= (:startTime) " +
            "and DATE_FORMAT(time_start,'%Y-%m-%d %H:%i:%S' ) <= (:endTime)  GROUP BY time")
    List<Map<String,Object>> getYpUserOrderDayMoney(@Param("startTime") String startTime, @Param("endTime") String endTime);



}
