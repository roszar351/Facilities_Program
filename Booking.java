public class Booking
{
	private int bookingID;
	private int facilityID;
	private int userID;
	private String date;
	private int slotNum;
	private boolean paymentStatus;
	
	public Booking(int bID, int fID, int uID, String d, int sNum, boolean paymentStatus)
	{
		bookingID = bID;
		facilityID = fID;
		userID = uID;
		date = d;
		slotNum = sNum;
		this.paymentStatus = paymentStatus;
	}
	
	public int getBookingID()
	{
		return bookingID;
	}
	
	public int getFacilityID()
	{
		return facilityID;
	}
	
	public int getUserID()
	{
		return userID;
	}
	
	public String getDate()
	{
		return date;
	}
	
	public int getSlotNum()
	{
		return slotNum;
	}
	
	public boolean getPaymentStatus()
	{
		return paymentStatus;
	}
	
	public void setPaymentStatus(boolean status)
	{
		paymentStatus = status;
	}
	
	public String toString()
	{
		return bookingID + "," + facilityID + "," + userID + "," + date + "," + slotNum + "," + paymentStatus;
	}
}