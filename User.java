public class User
{
	private int userID;
	private String email;
	private String password;
	private int userType;
	
	public User(int ID, String em, String pw, int uT)
	{
		userID = ID;
		email = em;
		password = pw;
		userType = uT;
	}
	
	public int getUserID()
	{
		return userID;
	}
	
	public String getEmail()
	{
		return email;
	}
	
	public String getPassword()
	{
		return password;
	}
	
	public int getUserType()
	{
		return userType;
	}
	
	public String toString()
	{
		return userID + "," + email + "," + password + "," + userType;
	}
}