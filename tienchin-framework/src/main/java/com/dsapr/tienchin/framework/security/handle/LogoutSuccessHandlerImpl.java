package com.dsapr.tienchin.framework.security.handle;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dsapr.tienchin.common.constant.Constants;
import com.dsapr.tienchin.common.constant.HttpStatus;
import com.dsapr.tienchin.common.core.domain.AjaxResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import com.alibaba.fastjson2.JSON;
import com.dsapr.tienchin.common.core.domain.model.LoginUser;
import com.dsapr.tienchin.common.utils.ServletUtils;
import com.dsapr.tienchin.common.utils.StringUtils;
import com.dsapr.tienchin.framework.manager.AsyncManager;
import com.dsapr.tienchin.framework.manager.factory.AsyncFactory;
import com.dsapr.tienchin.framework.web.service.TokenService;

/**
 * 自定义退出处理类 返回成功
 *
 * @author tienchin
 */
@Configuration
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {
    @Autowired
    private TokenService tokenService;

    /**
     * 退出处理
     *
     * @return
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        LoginUser loginUser = tokenService.getLoginUser(request);
        if (StringUtils.isNotNull(loginUser)) {
            String userName = loginUser.getUsername();
            // 删除用户缓存记录
            tokenService.delLoginUser(loginUser.getToken());
            // 记录用户退出日志
            AsyncManager.me().execute(AsyncFactory.recordLogininfor(userName, Constants.LOGOUT, "退出成功"));
        }
        ServletUtils.renderString(response, JSON.toJSONString(AjaxResult.error(HttpStatus.SUCCESS, "退出成功")));
    }
}
