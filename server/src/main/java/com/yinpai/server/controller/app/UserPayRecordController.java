package com.yinpai.server.controller.app;

import com.yinpai.server.domain.dto.LoginUserInfoDto;
import com.yinpai.server.domain.dto.PageResponse;
import com.yinpai.server.domain.dto.fiter.BaseFilterDto;
import com.yinpai.server.exception.NotLoginException;
import com.yinpai.server.service.UserPayRecordService;
import com.yinpai.server.thread.threadlocal.LoginUserThreadLocal;
import com.yinpai.server.vo.PayRecordListVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/10/21 6:28 下午
 */
@RestController
@Api(tags = "用户支付记录相关")
@RequestMapping("/users/payRecord")
public class UserPayRecordController {

    private final UserPayRecordService userPayRecordService;

    @Autowired
    public UserPayRecordController(UserPayRecordService userPayRecordService) {
        this.userPayRecordService = userPayRecordService;
    }

    @ApiOperation("打赏记录")
    @GetMapping("/list")
    public PageResponse<PayRecordListVo> list(BaseFilterDto baseFilterDto) {
        LoginUserInfoDto userInfoDto = LoginUserThreadLocal.get();
        if (userInfoDto == null) {
            throw new NotLoginException("请先登陆");
        }
        return userPayRecordService.list(userInfoDto.getUserId(), baseFilterDto);
    }
}
