package com.yinpai.server.controller.app;

import com.yinpai.server.domain.dto.LoginUserInfoDto;
import com.yinpai.server.domain.dto.PageResponse;
import com.yinpai.server.domain.dto.fiter.BaseFilterDto;
import com.yinpai.server.exception.NotLoginException;
import com.yinpai.server.service.UserDepositService;
import com.yinpai.server.thread.threadlocal.LoginUserThreadLocal;
import com.yinpai.server.vo.PayDepositListVo;
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
 * @date 2020/10/21 6:46 下午
 */
@RestController
@RequestMapping("/user/deposit")
@Api(tags = "用户充值记录")
public class UserDepositController {

    private final UserDepositService userDepositService;

    @Autowired
    public UserDepositController(UserDepositService userDepositService) {
        this.userDepositService = userDepositService;
    }

    @ApiOperation("充值记录")
    @GetMapping("/list")
    public PageResponse<PayDepositListVo> list(BaseFilterDto baseFilterDto) {
        LoginUserInfoDto userInfoDto = LoginUserThreadLocal.get();
        if (userInfoDto == null) {
            throw new NotLoginException("请先登陆");
        }
        return userDepositService.list(userInfoDto.getUserId(), baseFilterDto);
    }
}
