package com.yinpai.server.controller.app;

import com.yinpai.server.domain.dto.LoginUserInfoDto;
import com.yinpai.server.domain.dto.PageResponse;
import com.yinpai.server.domain.dto.fiter.BaseFilterDto;
import com.yinpai.server.domain.dto.fiter.UserFollowFilterDto;
import com.yinpai.server.domain.dto.fiter.WorksFilterDto;
import com.yinpai.server.exception.NotLoginException;
import com.yinpai.server.service.UserFollowService;
import com.yinpai.server.thread.threadlocal.LoginUserThreadLocal;
import com.yinpai.server.vo.IndexWorksVo;
import com.yinpai.server.vo.UserFollowListVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/10/5 10:52 下午
 */
@RestController
@RequestMapping("/users/follow")
@Api(tags = "用户关注相关")
public class UserFollowController {

    private final UserFollowService userFollowService;

    @Autowired
    public UserFollowController(UserFollowService userFollowService) {
        this.userFollowService = userFollowService;
    }

    @GetMapping("/list")
    @ApiOperation("用户关注列表")
    public PageResponse<UserFollowListVo> userFollowList(BaseFilterDto baseFilterDto) {
        LoginUserInfoDto userInfoDto = LoginUserThreadLocal.get();
        if (userInfoDto == null) {
            throw new NotLoginException("请先登陆");
        }
        UserFollowFilterDto userFollowFilterDto = new UserFollowFilterDto();
        userFollowFilterDto.setUserId(userInfoDto.getUserId());
        userFollowFilterDto.setPageable(baseFilterDto.getSetPageable());
        return userFollowService.userFollowList(userFollowFilterDto);
    }

    @PostMapping("/{adminId}")
    @ApiOperation("商家作者关注与取消")
    public Integer userFollowAdmin(@PathVariable Integer adminId) {
        LoginUserInfoDto userInfoDto = LoginUserThreadLocal.get();
        if (userInfoDto == null) {
            throw new NotLoginException("请先登陆");
        }
        return userFollowService.userFollowAdmin(adminId, userInfoDto.getUserId());
    }

    @GetMapping("/works")
    @ApiOperation("用户关注作品列表")
    public PageResponse<IndexWorksVo> follow(BaseFilterDto baseFilterDto, @ApiParam("类型筛选：1图片 2视频") @RequestParam("type") Integer type) {
        LoginUserInfoDto userInfoDto = LoginUserThreadLocal.get();
        if (userInfoDto == null) {
            throw new NotLoginException("请先登陆");
        }
        WorksFilterDto dto = new WorksFilterDto();
        dto.setPageable(baseFilterDto.getSetPageable());
        dto.setType(type);
        return userFollowService.followWorksList(userInfoDto.getUserId(), dto);
    }
}
