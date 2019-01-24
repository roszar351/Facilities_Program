public class Facility
{
	private int facilityID;
	private String facilityName;
	private double pricePerHour;
	private String decommissionedUntilDate;
	private boolean active;
	
	public Facility(int ID, String name, double pricePH)
	{
		facilityID = ID;
		facilityName = name;
		pricePerHour = pricePH;
		decommissionedUntilDate = "0000-00-00";
		active = true;
	}
	
	public int getFacilityID()
	{
		return facilityID;
	}
	
	public String getName()
	{
		return facilityName;
	}
	
	public double getPricePerHour()
	{
		return pricePerHour;
	}
	
	public String getDecommissionUntilDate()
	{
		return decommissionedUntilDate;
	}
	
	public boolean getActive()
	{
		return active;
	}
	
	public void setDecommissionUntilDate(String untilDate)
	{
		decommissionedUntilDate = untilDate;
	}
	
	public void setActive(boolean a)
	{
		active = a;
	}
	
	public String toString()
	{
		String s = facilityID + "," + facilityName + "," + pricePerHour + "," + decommissionedUntilDate + "," + active;
		return s;
	}
}