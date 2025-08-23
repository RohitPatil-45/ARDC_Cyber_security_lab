package in.canaris.cloud.config;

import java.io.IOException;
import java.security.Principal;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import in.canaris.cloud.entity.AppUser;
import in.canaris.cloud.repository.AppUserRepository;
import in.canaris.cloud.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsServiceImpl userDetailsService;

	@Autowired
	private DataSource dataSource;

	@Autowired
	private AppUserRepository appUserRepository;

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		return bCryptPasswordEncoder;
	}

//    @Autowired
//    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
// 
//        // Setting Service to find User in the database.
//        // And Setting PassswordEncoder
//        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
// 
//    }
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}

//	private Principal principal22;

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.csrf().disable();

		// The pages does not require login
		http.authorizeRequests().antMatchers("/", "/logout").permitAll();

		// /userInfo page requires login as ROLE_USER or ROLE_ADMIN.
		// If no login, it will redirect to /login page.
		http.authorizeRequests().antMatchers("/userInfo")
				.access("hasAnyRole('ROLE_USER', 'ROLE_ADMIN', 'ROLE_SUPERADMIN')");
		http.authorizeRequests().antMatchers("/vpc/*")
				.access("hasAnyRole('ROLE_USER', 'ROLE_ADMIN', 'ROLE_SUPERADMIN')");

		// For ADMIN only.
		http.authorizeRequests().antMatchers("/admin", "/discount/*", "/	/*")
				.access("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPERADMIN')");

		// When the user has logged in as XX.
		// But access a page that requires role YY,
		// AccessDeniedException will be thrown.
		http.authorizeRequests().and().exceptionHandling().accessDeniedPage("/403");
//		String landingpageURl = "/cloud_instance/view";

		// Custom Success Handler
		SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler() {
			@Override
			public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
					Authentication authentication) throws IOException, ServletException {
				String landingpageURl = "/cloud_instance/view"; // Default landing page
				String user = authentication.getName();
				AppUser userObj = appUserRepository.findByuserName(user);
				Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
				
				if(userObj.getIsFirstTimeLogin())
				{
					landingpageURl = "/users/resetloginpassword";
				}
				else if (userObj.isPasswordExpired()) {
			        request.getSession().setAttribute("resetPasswordMessage", "Your password has expired. Please reset your password.");
			        getRedirectStrategy().sendRedirect(request, response, "/users/resetloginpassword");
			        return; 
			    }

				else if (roles.contains("ROLE_SUPERADMIN")) {
					landingpageURl = "/cloud_instance/view";
				} else if (roles.contains("ROLE_ADMIN")) {
					landingpageURl = "/cloud_instance/view";
				} else if (roles.contains("ROLE_USER")) {
					landingpageURl = "/cloud_instance/view";
				}

				getRedirectStrategy().sendRedirect(request, response, landingpageURl);
			}
		};

		// Config for Login Form
		http.authorizeRequests().and().formLogin()//
				// Submit URL of login page.
				.loginProcessingUrl("/j_spring_security_check") // Submit URL
				.loginPage("/")//
				.successHandler(successHandler) // Use inline success handler
//				.defaultSuccessUrl(landingpageURl)//
				.failureUrl("/?error=true")//
				.usernameParameter("username")//
				.passwordParameter("password")

				// Config for Logout Page
				.and().logout().invalidateHttpSession(true) // This will invalidate the session
				.clearAuthentication(true) // This will clear the authentication
				.deleteCookies("dummyCookie").logoutUrl("/logout").logoutSuccessUrl("/").and().sessionManagement()
				.invalidSessionUrl("/?session-timeout=true");
//				.maximumSessions(100).expiredUrl("/?invalid-session=true")
//				.maxSessionsPreventsLogin(true);

	}

	@Bean
	public SessionRegistry sessionRegistry() {
		SessionRegistry sessionRegistry = new SessionRegistryImpl();
		return sessionRegistry;
	}

	@Bean
	public HttpSessionEventPublisher httpSessionEventPublisher() {
		return new HttpSessionEventPublisher();
	}

	@Bean
	public PersistentTokenRepository persistentTokenRepository() {
		JdbcTokenRepositoryImpl db = new JdbcTokenRepositoryImpl();
		db.setDataSource(dataSource);
		return db;
	}
}