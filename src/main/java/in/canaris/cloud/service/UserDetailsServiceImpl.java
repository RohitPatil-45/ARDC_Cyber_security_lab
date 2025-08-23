package in.canaris.cloud.service;
 
import java.util.ArrayList;
import java.util.List;

import javax.mail.internet.MimeMessage;

import in.canaris.cloud.dao.AppUserDAO;
import in.canaris.cloud.entity.AppUser;
import in.canaris.cloud.dao.AppRoleDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
 
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
 
    @Autowired
    private AppUserDAO appUserDAO;
 
    @Autowired
    private AppRoleDAO appRoleDAO;
    
    @Autowired
	private JavaMailSender mailsender;

	@Value("${spring.mail.username}")
	private String sender;
 
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        AppUser appUser = this.appUserDAO.findUserAccount(userName);
 
        if (appUser == null) {
            System.out.println("User not found! " + userName);
            throw new UsernameNotFoundException("User " + userName + " was not found in the database");
        }
 
        System.out.println("Found User: " + appUser);
 
        // [ROLE_USER, ROLE_ADMIN,..]
        List<String> roleNames = this.appRoleDAO.getRoleNames(appUser.getUserId());
 
        List<GrantedAuthority> grantList = new ArrayList<GrantedAuthority>();
        if (roleNames != null) {
            for (String role : roleNames) {
                // ROLE_USER, ROLE_ADMIN,..
                GrantedAuthority authority = new SimpleGrantedAuthority(role);
                grantList.add(authority);
            }
        }
 
        UserDetails userDetails = (UserDetails) new User(appUser.getUserName(), //
                appUser.getEncrytedPassword(), grantList);
 
        return userDetails;
    }
    
	public String sendSimpleMail(String user, String password, String receiverMail) {

		try {

			MimeMessage message = mailsender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);
			helper.setTo(receiverMail);
			helper.setSubject("CMP Portal - Your Credentials");
			helper.setText("<p>Dear " + user + ",</p>" + "<p>Your account has been successfully created.</p>"
					+ "<p><strong>Username:</strong> " + user + "</p>" + "<p><strong>Password:</strong> " + password
					+ "</p>" + "<p>For security reasons, please change your password after logging in.</p>"
					+ "<br><p>Best regards,<br>Canaris Team</p>", true);

			mailsender.send(message);
			System.out.println("Email sent successfully to: " + receiverMail);

			return "Mail Sent Successfully...";
		}

		catch (Exception e) {
			return "Error while Sending Mail";
		}
	}
 
}