package com.yinpai.server.controller.app;

import com.yinpai.server.domain.dto.LoginUserInfoDto;
import com.yinpai.server.domain.dto.PageResponse;
import com.yinpai.server.domain.dto.fiter.BaseFilterDto;
import com.yinpai.server.domain.dto.fiter.UserCollectionFilterDto;
import com.yinpai.server.exception.NotLoginException;
import com.yinpai.server.log.WebLog;
import com.yinpai.server.service.UserCollectionService;
import com.yinpai.server.thread.threadlocal.LoginUserThreadLocal;
import com.yinpai.server.vo.IndexWorksVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/10/5 10:04 下午
 */
@RestController
@RequestMapping("/users/collection")
@Api(tags = "用户收藏")
public class UserCollectionController {

    private final UserCollectionService userCollectionService;

    @Autowired
    public UserCollectionController(UserCollectionService userCollectionService) {
        this.userCollectionService = userCollectionService;
    }

    @PostMapping("/{workId}")
    @ApiOperation("用户收藏与取消")
    @WebLog(description = "购买作品")
    public Integer collection(@PathVariable Integer workId) {
        return userCollectionService.collectionWork(workId);
    }

    @GetMapping("/list")
    @ApiOperation("收藏列表")
    public PageResponse<IndexWorksVo> userCollectionList(BaseFilterDto baseFilterDto,
                                                         @ApiParam("类型筛选：1图片 2视频") @RequestParam("type") Integer type) {
        UserCollectionFilterDto dto = new UserCollectionFilterDto();
        LoginUserInfoDto userInfoDto = LoginUserThreadLocal.get();
        if (userInfoDto == null) {
            throw new NotLoginException("请先登陆");
        }
        dto.setUserId(userInfoDto.getUserId());
        dto.setType(type);
        dto.setPageable(baseFilterDto.getSetPageable());
        return userCollectionService.userCollectionList(dto);
    }
}
