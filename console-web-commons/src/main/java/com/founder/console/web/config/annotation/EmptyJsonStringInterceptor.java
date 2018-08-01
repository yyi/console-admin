package com.founder.console.web.config.annotation;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ClassUtils;
import org.springframework.http.MediaType;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

import static com.founder.console.web.utils.WebUtils.isAcceptJson;
import static com.founder.console.web.utils.WebUtils.isAjaxRequest;
import static com.founder.console.web.utils.WebUtils.isHttpStatusSucessful;

@Slf4j
public class EmptyJsonStringInterceptor extends HandlerInterceptorAdapter {

    private String emptyJsonString = "{}";
    private String primitiveVoidString = "void";

    /* @Override
         public boolean preHandle(HttpServletRequest request, //2
                                  HttpServletResponse response, Object handler) throws Exception {
             HandlerMethod method = (HandlerMethod) handler;
             RequiresPermissions requiresAuthorization =
                     method.getMethodAnnotation(RequiresPermissions.class);

             if (requiresAuthorization != null) {
                 Subject currentUser = SecurityUtils.getSubject();

                 for (String requiredPermission : requiresAuthorization.value()) {
                     PermissionResolver permissionResolver = new
                             WildcardPermissionResolver();
                     Permission permission =
                             permissionResolver.resolvePermission(requiredPermission);
                     if (!currentUser.isPermitted(permission)) {
                         throw new OperationException(SysadminError.LackOfPermission);
                     }
                 }
             }
             return true;
         }

         @Override
         public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
             super.postHandle(request, response, handler, modelAndView);
         }
     */
    @Override
    @SneakyThrows
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (isCompensateEmptyJson(request, response, handler, ex)) {
            writeEmptyJsonString(response);
        }
        super.afterCompletion(request, response, handler, ex);
    }

    @SneakyThrows
    private void writeEmptyJsonString(HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        IOUtils.write(emptyJsonString, response.getWriter());
    }

    private boolean isCompensateEmptyJson(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        return isHandlerMethodClass(handler) && isPrimitiveVoid((HandlerMethod) handler) && isHttpStatusSucessful(response.getStatus()) && isAjaxRequest(request) && isAcceptJson(request) && Objects.isNull(ex);
    }

    private boolean isPrimitiveVoid(HandlerMethod handler) {
        return Objects.equals(primitiveVoidString, handler.getMethod().getReturnType().getCanonicalName());
    }

    private boolean isHandlerMethodClass(Object handler) {
        return ClassUtils.isAssignable(HandlerMethod.class, handler.getClass());
    }
}
