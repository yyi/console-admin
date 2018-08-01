package com.founder.console.web.config;

import com.founder.console.web.captcha.LoginFormAuthenticationFilter;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.Collection;
import java.util.LinkedHashMap;

@Slf4j
public class MyFormAuthenticationFilter extends LoginFormAuthenticationFilter {
    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject,
                                     ServletRequest request, ServletResponse response) throws Exception {

        log.debug("setting users attributes");
        //  new SuccessURLDAO().setUserAttribute(subject);

//        if (((LoggedInUser) subject.getSession().getAttribute("userinfo")).getIsAccountLocked() == 1) {
//            subject.logout();
//            return onLoginFailure(token, new DisabledAccountException(), request, response);
//        } else


        issueSuccessRedirect(request, response); //we handled the success redirect directly, prevent the chain from continuing: return false; }

        return false;
    }

    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
          /*  SuccessURLDAO dao = new SuccessURLDAO();
            String userid =token.getPrincipal().toString();

            if((!(e instanceof org.apache.shiro.authc.UnknownAccountException))){
                if( dao.isAccountLocked(userid))
                { setFailureAttribute( request,new DisabledAccountException()); }
                else

                { setFailureAttribute(request, e); new SuccessURLDAO().setUserFailedAttempt(token.getPrincipal().toString()); }
            }else

            { setFailureAttribute(request, e); }*/
        return true;
    }

    @Override
    protected boolean executeLogin(final ServletRequest request, final ServletResponse response) throws Exception {
        final AuthenticationToken token = createToken(request, response);
        if (token == null) {
            String msg = "createToken method implementation returned null. A valid non-null AuthenticationToken " + "must be created in order to execute a login attempt.";
            throw new IllegalStateException(msg);
        }
        try {

            final Subject subject = getSubject(request, response);


            subject.login(token);

            Session session = subject.getSession();
            String old_id = (String) session.getId();
            final LinkedHashMap<Object, Object> attributes = new LinkedHashMap<>();
            final Collection<Object> keys = session.getAttributeKeys();
            for (Object key : keys) {
                final Object value = session.getAttribute(key);
                if (value != null) {
                    attributes.put(key, value);
                }
            }
            session.stop();

            session = subject.getSession();
            log.debug("OWASP session fixation from " + old_id + " to " + session.getId());
            for (final Object key : attributes.keySet()) {
                session.setAttribute(key, attributes.get(key));
            }
            return onLoginSuccess(token, subject, request, response);
        } catch (AuthenticationException e) {
            return onLoginFailure(token, e, request, response);
        }
    }
}