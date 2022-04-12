/*
 * Copyright [2020] [MaxKey of copyright http://www.maxkey.top]
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 

package org.maxkey.web.contorller;

import org.maxkey.authn.AbstractAuthenticationProvider;
import org.maxkey.authn.LoginCredential;
import org.maxkey.authn.jwt.AuthJwt;
import org.maxkey.authn.jwt.AuthJwtService;
import org.maxkey.configuration.ApplicationConfig;
import org.maxkey.entity.Institutions;
import org.maxkey.entity.Message;
import org.maxkey.web.WebConstants;
import org.maxkey.web.WebContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.http.MediaType;


/**
 * @author Crystal.Sea
 *
 */
@Controller
public class LoginEntryPoint {
	private static Logger _logger = LoggerFactory.getLogger(LoginEntryPoint.class);
	
	@Autowired
  	@Qualifier("authJwtService")
	AuthJwtService authJwtService;
	
	@Autowired
  	@Qualifier("applicationConfig")
  	protected ApplicationConfig applicationConfig;
 	

	@Autowired
	@Qualifier("authenticationProvider")
	AbstractAuthenticationProvider authenticationProvider ;
	
	/**
	 * init login
	 * @return
	 */
 	@RequestMapping(value={"/login"})
	public ModelAndView login() {
		_logger.debug("LoginController /login.");
		
		boolean isAuthenticated= false;//WebContext.isAuthenticated();
		//for normal login
		if(isAuthenticated){
			return WebContext.redirect("/main");
		}

		ModelAndView modelAndView = new ModelAndView();
		Institutions inst = (Institutions)WebContext.getAttribute(WebConstants.CURRENT_INST);
		modelAndView.addObject("isRemeberMe", applicationConfig.getLoginConfig().isRemeberMe());
		modelAndView.addObject("captchaSupport", inst.getCaptchaSupport());
		modelAndView.addObject("captchaType", inst.getCaptchaType());
		modelAndView.addObject("sessionid", WebContext.getSession().getId());
		Object loginErrorMessage=WebContext.getAttribute(WebConstants.LOGIN_ERROR_SESSION_MESSAGE);
        modelAndView.addObject("loginErrorMessage", loginErrorMessage==null?"":loginErrorMessage);
        WebContext.removeAttribute(WebConstants.LOGIN_ERROR_SESSION_MESSAGE);
		modelAndView.setViewName("login");
		return modelAndView;
	}
 	
 	@RequestMapping(value={"/signin"}, produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> signin( @RequestBody LoginCredential loginCredential) {
 		Authentication  authentication  = authenticationProvider.authenticate(loginCredential);
 		String jwt = authJwtService.generateToken(authentication);
 		return new Message<AuthJwt>(new AuthJwt(jwt, authentication)).buildResponse();
 	}
 	
}
