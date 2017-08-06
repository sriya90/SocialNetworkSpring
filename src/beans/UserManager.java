package beans;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class UserManager {
	
	String userId;
	String password;
	SessionFactory sessionFactory;
	
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String validateUser()
	{
		FacesContext faces=FacesContext.getCurrentInstance();
		Session session;
		session=sessionFactory.openSession();
		session.beginTransaction();
		try
		{
		User user=(User)session.get(User.class, userId);
		if(user != null) {
			if(!user.getPassword().equals(password))
			{
				FacesMessage message=new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login Failed!", "Then Password specified is not correct");
				faces.addMessage(null, message);
				return null;
			}
		faces.getExternalContext().getSessionMap().put("user",user);
		return "success";
		}
		else
		{
			FacesMessage message=new FacesMessage(FacesMessage.SEVERITY_ERROR, "Login Failed!","User Id not valid");
			faces.addMessage(null, message);
			return null;
		}
		}
		finally
		{
			session.close();
		}
	}
}
