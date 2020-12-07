package com.yinpai.server.utils;

import com.yinpai.server.vo.admin.ResultVO;

public class ResultUtil {

    public static ResultVO success() {
        ResultVO resultVO = new ResultVO();
        resultVO.setCode(200);
        resultVO.setMsg("请求成功");
        return resultVO;
    }

    public static ResultVO success(String msg) {
        ResultVO resultVO = new ResultVO();
        resultVO.setCode(200);
        resultVO.setMsg(msg);
        return resultVO;
    }

    public static <T> ResultVO<T> successData(T object){
        ResultVO<T> resultVO = new ResultVO<>();
        resultVO.setCode(200);
        resultVO.setMsg("请求成功");
        resultVO.setData(object);
        return resultVO;
    }


    public static ResultVO error(Integer code, String msg) {
        ResultVO resultVO = new ResultVO();
        resultVO.setCode(code);
        resultVO.setMsg(msg);
        return resultVO;
    }

    public static ResultVO error() {
        ResultVO resultVO = new ResultVO();
        resultVO.setCode(500);
        resultVO.setMsg("请求失败");
        return resultVO;
    }

}
