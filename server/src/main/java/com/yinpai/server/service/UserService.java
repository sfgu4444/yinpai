package com.yinpai.server.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.yinpai.server.domain.dto.LoginUserInfoDto;
import com.yinpai.server.domain.entity.User;
import com.yinpai.server.domain.entity.UserLoginLog;
import com.yinpai.server.domain.repository.UserLoginLogRepository;
import com.yinpai.server.domain.repository.UserRepository;
import com.yinpai.server.exception.NotAcceptableException;
import com.yinpai.server.exception.NotLoginException;
import com.yinpai.server.exception.ProjectException;
import com.yinpai.server.log.WebLog;
import com.yinpai.server.thread.threadlocal.LoginUserThreadLocal;
import com.yinpai.server.utils.ProjectUtil;
import com.yinpai.server.utils.TokenUtil;
import com.yinpai.server.vo.PersonalCenterVo;
import com.yinpai.server.vo.UserProfileVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/9/28 11:26 上午
 */
@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;

    private final UserFollowService userFollowService;

    private final UserCollectionService userCollectionService;

    private final UserLoginLogRepository userLoginLogRepository;

    @Autowired
    public UserService(UserRepository userRepository, @Lazy UserFollowService userFollowService, @Lazy UserCollectionService userCollectionService, UserLoginLogRepository userLoginLogRepository) {
        this.userRepository = userRepository;
        this.userFollowService = userFollowService;
        this.userCollectionService = userCollectionService;
        this.userLoginLogRepository = userLoginLogRepository;
    }

    @Value("${userinfo.logo}")
    private String user_logo;


    public User findById(Integer userId) {
        return userRepository.findById(userId).orElseGet(User::new);
    }

    public Page<User> findFilterAll(Map<String, String> map, Pageable pageable) {
        return userRepository.findAll(ProjectUtil.getSpecification(map), pageable);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    /**
     * 通过token获取用户登陆信息
     */
    public LoginUserInfoDto tokenToUserInfo(String token, boolean exception) {
        if (exception && StringUtils.isEmpty(token)) {
            throw new NotLoginException("请先登陆");
        }

        if (!exception && (StringUtils.isEmpty(token))) {
            return null;
        }
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256("secret")).build();
        DecodedJWT jwt;
        try {
            jwt = jwtVerifier.verify(token);
        } catch (JWTVerificationException e) {
            if (!exception) {
                return null;
            }
            throw new NotLoginException("请重新登录");
        }
        Integer userId = jwt.getClaim("userId").asInt();
        return LoginUserInfoDto.builder().userId(userId).build();
    }
    public String userLoginPassword(String username, String password) {
        User user = userRepository.findByUsernameAndPassword(username, DigestUtils.md5Hex("yin" + password + "pai"));
        if (user == null) {
            throw new EntityNotFoundException("用户名或密码不存在");
        }
        UserLoginLog userLoginLog = new UserLoginLog();
        userLoginLog.setUsername(user.getUsername());
        userLoginLogRepository.save(userLoginLog);
        return TokenUtil.getToken(user);
    }

    public String userLoginPhone(String phone) {
        User user = userRepository.findByPhone(phone);
        if (user == null) {
            throw new EntityNotFoundException("用户不存在");
        }
        UserLoginLog userLoginLog = new UserLoginLog();
        userLoginLog.setUsername(user.getUsername());
        userLoginLogRepository.save(userLoginLog);
        return TokenUtil.getToken(user);
    }

    public void register(String phone, String password) {
        User user = new User();
        user.setUsername(phone);
        user.setPassword(DigestUtils.md5Hex("yin" + password + "pai"));
        user.setPhone(phone);
        user.setMoney(0);
        user.setStatus(1);
        user.setAvatarUrl(user_logo);
        userRepository.save(user);
    }

    public void forgetPassword(String phone, String password) {
        User user = userRepository.findByPhone(phone);
        if (user == null) {
            throw new EntityNotFoundException("用户不存在");
        }
        user.setPassword(DigestUtils.md5Hex("yin" + password + "pai"));
        userRepository.save(user);
    }

    public User findByIdNotNull(Integer userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ProjectException("用户不存在"));
    }

    public Integer changeUserStatus(Integer id) {
        User user = findByIdNotNull(id);
        Integer status = user.getStatus();
        Integer result = status == 1 ? 0 : 1;
        user.setStatus(result);
        userRepository.save(user);
        return result;
    }

    public void delete(Integer id) {
        User user = findByIdNotNull(id);
        userRepository.delete(user);
    }

    public UserProfileVo userProfile() {
        LoginUserInfoDto userInfoDto = LoginUserThreadLocal.get();
        if (userInfoDto == null) {
            throw new NotLoginException("请先登陆");
        }
        User user = findByIdNotNull(userInfoDto.getUserId());
        UserProfileVo vo = new UserProfileVo();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }

    public void editUserProfile(UserProfileVo vo) {
        LoginUserInfoDto userInfoDto = LoginUserThreadLocal.get();
        if (userInfoDto == null) {
            throw new NotLoginException("请先登陆");
        }
        User user = findByIdNotNull(userInfoDto.getUserId());
        BeanUtils.copyProperties(vo, user);
        userRepository.save(user);
    }

    public PersonalCenterVo personalCenter() {
        LoginUserInfoDto userInfoDto = LoginUserThreadLocal.get();
        if (userInfoDto == null) {
            throw new NotLoginException("请先登陆");
        }
        User user = findByIdNotNull(userInfoDto.getUserId());
        PersonalCenterVo vo = new PersonalCenterVo();
        BeanUtils.copyProperties(user, vo);
        vo.setFollowCount(userFollowService.userFollowCount(user.getId()));
        vo.setCollectionCount(userCollectionService.userCollectionCount(user.getId()));
        return vo;
    }

    public void editPhone(String phone, String newPhone) {
        LoginUserInfoDto userInfoDto = LoginUserThreadLocal.get();
        if (userInfoDto == null) {
            throw new NotLoginException("请先登陆");
        }
        User user = findByIdNotNull(userInfoDto.getUserId());
        if (!user.getPhone().equals(phone)) {
            throw new NotAcceptableException("原手机号不正确");
        }
        user.setPhone(newPhone);
        user.setUsername(newPhone);
        userRepository.save(user);
    }

    public List<User> findByNicknameList(String nickName){
        return userRepository.findByNickNameContaining(nickName);
    }

    public List<Integer> findByNicknameListId(String nickName){
        List<Integer> userIdList = new ArrayList<>();
        List<User> userList  = userRepository.findByNickNameContaining(nickName);
        if(null == userList ||userList.size() == 0){
            return userIdList;
        }
        userList.forEach(u -> {
            userIdList.add(u.getId());
        });
        return userIdList;
    }
}
