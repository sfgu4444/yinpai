package com.yinpai.server.exception;

import com.yinpai.server.domain.dto.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityNotFoundException;
import java.util.List;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2019-06-21 20:03
 */
@Slf4j
@ControllerAdvice
public class ProjectExceptionHandler {

    @Value("${yinpai.url}")
    private String url;

    @ResponseBody
    @ExceptionHandler(value = NotAcceptableException.class)
    public CommonResponse handleNotAcceptableException(NotAcceptableException e) {
        CommonResponse commonResponse = new CommonResponse();
        commonResponse.setCode(HttpStatus.NOT_ACCEPTABLE.value());
        commonResponse.setMsg(e.getMessage());
        return commonResponse;
    }

    @ExceptionHandler(value = AdminAuthorizeException.class)
    public ModelAndView handerAuthorizeException(){
        return new ModelAndView("redirect:".concat(url).concat("/admin/admin/login"));
    }

    /**
     * 项目异常捕捉
     *
     * @param e
     * @return
     * @author weilai
     */
    @ExceptionHandler(value = ProjectException.class)
    @ResponseBody
    public CommonResponse handleProjectException(ProjectException e) {
        log.warn(e.getMessage());
        CommonResponse commonResponse = new CommonResponse();
        commonResponse.setCode(e.getCode());
        commonResponse.setMsg(e.getMessage());
        return commonResponse;
    }

    @ExceptionHandler(value = AuthRefusedException.class)
    @ResponseBody
    public CommonResponse handleAuthRefusedException(
            AuthRefusedException e) {
        CommonResponse commonResponse = new CommonResponse();
        commonResponse.setCode(HttpStatus.FORBIDDEN.value());
        commonResponse.setMsg(e.getMessage());
        return commonResponse;
    }

    @ExceptionHandler(value = NotLoginException.class)
    @ResponseBody
    public CommonResponse handleNotLoginException(
            NotLoginException e) {
        CommonResponse commonResponse = new CommonResponse();
        commonResponse.setCode(HttpStatus.UNAUTHORIZED.value());
        commonResponse.setMsg(e.getMessage());
        return commonResponse;
    }

    /**
     * method not allowed exception.
     *
     * @param e
     * @return
     * @author weilai
     */
    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public CommonResponse handleMethodNotSupportedException(
        HttpRequestMethodNotSupportedException e) {
        CommonResponse commonResponse = new CommonResponse();
        commonResponse.setCode(HttpStatus.METHOD_NOT_ALLOWED.value());
        commonResponse.setMsg(e.getMessage());
        return commonResponse;
    }

    @ExceptionHandler(value = EntityNotFoundException.class)
    @ResponseBody
    public CommonResponse handleEntityNotFoundException(
            EntityNotFoundException e) {
        CommonResponse commonResponse = new CommonResponse();
        commonResponse.setCode(HttpStatus.NOT_FOUND.value());
        commonResponse.setMsg(e.getMessage());
        return commonResponse;
    }

    /**
     * method not allowed exception.
     */
    @ExceptionHandler(value = MethodArgumentCheckException.class)
    @ResponseBody
    public CommonResponse handleMethodArgumentCheckException(MethodArgumentCheckException e) {
        CommonResponse commonResponse = new CommonResponse();
        commonResponse.setCode(HttpStatus.BAD_REQUEST.value());
        commonResponse.setMsg(e.getMessage());
        return commonResponse;
    }


    /**
     * 捕获参数校验异常
     */
    @ResponseBody
    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public CommonResponse handleValidException(MissingServletRequestParameterException e) {
        CommonResponse commonResponse = new CommonResponse();
        commonResponse.setCode(HttpStatus.BAD_REQUEST.value());
        commonResponse.setMsg("缺少必填参数" + e.getParameterName());
        return commonResponse;
    }

    /**
     * 捕获参数校验异常
     */
    @ResponseBody
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public CommonResponse handleValidException(MethodArgumentNotValidException e) {
        CommonResponse commonResponse = new CommonResponse();
        commonResponse.setCode(HttpStatus.BAD_REQUEST.value());
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        String message = !CollectionUtils.isEmpty(fieldErrors) ? fieldErrors.get(0).getDefaultMessage() : e.getMessage();
        commonResponse.setMsg(message);
        log.warn(message);
        return commonResponse;
    }

    @ResponseBody
    @ExceptionHandler(value = DataSaveException.class)
    public CommonResponse handlerDataSaveException(DataSaveException t) {
        log.error("数据操作异常", t);
        CommonResponse commonResponse = new CommonResponse();
        commonResponse.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        commonResponse.setMsg("数据操作异常");
        return commonResponse;
    }

    /**
     * 通用错误
     */
    @ResponseBody
    @ExceptionHandler(value = Throwable.class)
    public CommonResponse handlerProjectException(Throwable t) {
        log.error("系统异常", t);
        CommonResponse commonResponse = new CommonResponse();
        commonResponse.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        commonResponse.setMsg("服务器开小差了");
        return commonResponse;
    }
}
