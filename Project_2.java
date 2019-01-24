import java.util.*;
import javax.swing.JOptionPane;
import java.io.*;
import java.time.*;
import java.time.format.*;

public class Project_2
{
	public static int userID;
	public static boolean admin;
	public static ArrayList<User> users;
	public static ArrayList<Facility> currentFacilities;
	public static ArrayList<Booking> bookings;
	
	/**main() calls  loadFilesIntoMemory() method and if it returns true it then calls the 
	 * checkForRecommission() and updateBookings() methods to make sure the files are updated
	 * then it calls the login() method to let the user login, then depending on user type
	 * it shows a drop down menu for the user to select which method to call.
	 */
	public static void main(String[] args) throws IOException
	{
		if(loadFilesIntoMemory())
		{
			checkForRecommission();
			updateBookings();
			if(login())
			{
				String selection;
				if(admin)
				{
					String[] options = {"Register a new user.", "Add new facility.", "Make booking.", "View availability of a facility.", 
							   "View bookings for a facility.", "Remove a facility.", "Decommission a facility.",
							   "Recommission a facility.", "Record a payment.", "View account statements.", "Exit."};
					selection = (String) JOptionPane.showInputDialog(null, "Select what you want to do: ",
										"Welcome", 1, null, options, options[0]);
					while(selection != null && !selection.equals("Exit."))
					{
						switch(selection)
						{
							case "Register a new user.": createUser(); break;
							case "Add new facility.": addNewFacility(); break;
							case "Make booking.": makeBooking(); break;                      //CHECK SLOTS
							case "View availability of a facility.": viewAvailabilityOfFacility(); break;
							case "View bookings for a facility.": viewYourBookings(); break; //ADD DATE
							case "Remove a facility.": deleteFacility(); break;
							case "Decommission a facility.": decommission(); break;
							case "Recommission a facility.": recommission(); break;
							case "Record a payment.": makePayment(); break;
							case "View account statements.": checkAccounts(); break;
							default: break;
						}
						selection = (String) JOptionPane.showInputDialog(null, "Select what you want to do: ",
										"Welcome", 1, null, options, options[0]);
					}
				}
				else
				{
					String[] options = {"View your bookings.", "View your statement of account.", "Exit."};
					selection = (String) JOptionPane.showInputDialog(null, "Select what you want to do: ",
										"Welcome", 1, null, options, options[0]);
					while(selection != null && !selection.equals("Exit."))
					{
						switch(selection)
						{
							case "View your bookings.": viewYourBookings(); break;
							case "View your statement of account.": checkAccounts(); break;
							default: break;
						}
						selection = (String) JOptionPane.showInputDialog(null, "Select what you want to do: ",
										"Welcome", 1, null, options, options[0]);
					}
				}
				updateFiles();
			}
		}
	}
	
	/**the login() method promts the user to login, it sets the userID and admin global variables
	 * @return returns true if logged in successfully else returns false
	 */
	public static boolean login() throws IOException{
	
		int attempts = 1, userType = -1;
		String userName = "", userPassword = "";
		String pattern = ".+@.+(\\.)[A-Za-z]{1,}";
		boolean found = false, login = false;
		admin = false;
		
		for( int i = 0; i < 3 && !found; i++){
			
			userName = JOptionPane.showInputDialog(null, "Enter your Email: \nEmail is not case sensitive", "Attempts: " + attempts, 1);
			if( userName == null )
				break;
			userPassword = JOptionPane.showInputDialog(null, "Enter your Password: ", "Attempts: " + attempts, 1);
			attempts++;
			for(int j = 0; j < users.size() && !found;j++){
				
				if(users.get(j).getEmail().equalsIgnoreCase(userName) && users.get(j).getPassword().equals(userPassword)){
					
					found = true;
					userType = users.get(j).getUserType();
					userID = users.get(j).getUserID();
				}	
			}
			if(!found && attempts<=3)
				JOptionPane.showMessageDialog(null, "Error: username or password doesn't match");
		}
		if(found && userType == 0){
			
			JOptionPane.showMessageDialog(null, "Admin acces granted, welcome back");
			admin = true;
			login = true;
		}else if(found  && userType == 1){
			JOptionPane.showMessageDialog(null, "Welcome back user");
			login = true;
		}else
			JOptionPane.showMessageDialog(null, "Access denied: too many attempts");
		
		return login;
	}
		
	/**the createUser() method allows for creation of new user, it calls the generatePassword() method
	 * to generate a password, and calls the updateFiles() method to update the user file.
	 */
	public static void createUser() throws IOException{
		
		String userName, password, pattern = ".+@.+(\\.)[A-Za-z]{1,}";
		boolean found = false, valid = false;
		int userPos = -1;
		ArrayList<Integer> ids = new ArrayList<>();
		User temp;
		for(int i = 0; i < users.size(); i++)
			ids.add(users.get(i).getUserID());
				 
		for(int j = 1; j <= ids.size() && !found;j++){
			if(!(ids.contains(j))){
				userPos = j - 1;
				found = true;
			}
		}
		if(!found)
			userPos = ids.size();
		userName = JOptionPane.showInputDialog(null, "Enter new users email: ");
		
		while(userName != null && !valid){
			if(!(userName.matches(pattern)))
				userName = JOptionPane.showInputDialog(null, "Email not valid, please try again");
			else
				valid = true;
		}
		if(valid){
			password = generatePassword();
			temp = new User((userPos + 1), userName, password, 1);
			users.add(userPos, temp);
			JOptionPane.showMessageDialog(null, "Your password is: " + password);
			updateFiles(1);
		}else
			JOptionPane.showMessageDialog(null, "User creation failed");
	}
	
	/**the generatePassword() method generates and returns a string with random letters and numbers.
	 * @return string with random letters and numbers.
	 */
	public static String generatePassword(){
		
		String password = "";
		String alpha = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		
		for(int i = 0; i <6;i++)
			password = password + alpha.charAt((int) (Math.random()*alpha.length()));
		
		return password;
	}
	
	/**the loadFilesIntoMemory() method loads files into three global ArrayLists.
	 * @return true if succesfull else false.
	 */
	public static boolean loadFilesIntoMemory() throws IOException
	{
		File file_1 = new File("DataFiles/Users.txt");
		File file_2 = new File("DataFiles/Facilities.txt");
		File file_3 = new File("DataFiles/Bookings.txt");
		String[] fileElements;
		users = new ArrayList<>();
		currentFacilities = new ArrayList<>();
		bookings = new ArrayList<>();
		
		if(file_1.exists() && file_2.exists() && file_3.exists())
		{
			User tempUser;
			Facility tempFacility;
			Booking tempBooking;
			Scanner in = new Scanner(file_1);
			while(in.hasNext())
			{
				fileElements = in.nextLine().split(",");
				tempUser = new User(Integer.parseInt(fileElements[0]), fileElements[1], fileElements[2], Integer.parseInt(fileElements[3]));
				users.add(tempUser);
			}
			in.close();
			in = new Scanner(file_2);
			while(in.hasNext())
			{
				fileElements = in.nextLine().split(",");
				tempFacility = new Facility(Integer.parseInt(fileElements[0]), fileElements[1], Double.parseDouble(fileElements[2]));
				tempFacility.setDecommissionUntilDate(fileElements[3]);
				tempFacility.setActive(Boolean.parseBoolean(fileElements[4]));
				currentFacilities.add(tempFacility);
			}
			in.close();
			in = new Scanner(file_3);
			while(in.hasNext())
			{
				fileElements = in.nextLine().split(",");
				tempBooking = new Booking(Integer.parseInt(fileElements[0]), Integer.parseInt(fileElements[1]), Integer.parseInt(fileElements[2]),
										  fileElements[3], Integer.parseInt(fileElements[4]), Boolean.parseBoolean(fileElements[5]));
				bookings.add(tempBooking);
			}
			in.close();
			return true;
		}
		else
			JOptionPane.showMessageDialog(null, "Error: \"Users.txt\", \"Bookings.txt\", \"Facilities.txt\". One or more files missing.");
		return false;
	}
	
	/**the updateFiles() method updates all files with the current ArrayLists.
	 */
	public static void updateFiles() throws IOException
	{
		File file_1 = new File("DataFiles/Users.txt");
		File file_2 = new File("DataFiles/Facilities.txt");
		File file_3 = new File("DataFiles/Bookings.txt");
		PrintWriter out = new PrintWriter(file_1);
		for(int i = 0; i < users.size(); i++)
			out.println(users.get(i));
		out.close();
		out = new PrintWriter(file_2);
		for(int i = 0; i < currentFacilities.size(); i++)
			out.println(currentFacilities.get(i));
		out.close();
		out = new PrintWriter(file_3);
		for(int i = 0; i < bookings.size(); i++)
			out.println(bookings.get(i));
		out.close();
	}
	
	/**the updateFiles(int whichFile) updates a specif file.
	 * @param whichFile - specifies which file to update '1' for Users.txt, '2' for Facilities.txt, '3' for Bookings.txt
	 */
	public static void updateFiles(int whichFile) throws IOException
	{
		File aFile;
		PrintWriter out;
		if(whichFile == 1)
		{
			aFile = new File("DataFiles/Users.txt");
			out = new PrintWriter(aFile);
			for(int i = 0; i < users.size(); i++)
				out.println(users.get(i));
			out.close();
		}
		else if(whichFile == 2)
		{
			aFile = new File("DataFiles/Facilities.txt");
			out = new PrintWriter(aFile);
			for(int i = 0; i < currentFacilities.size(); i++)
				out.println(currentFacilities.get(i));
			out.close();
		}
		else if(whichFile == 3)
		{
			aFile = new File("DataFiles/Bookings.txt");
			out = new PrintWriter(aFile);
			for(int i = 0; i < bookings.size(); i++)
				out.println(bookings.get(i));
			out.close();
		}
	}
	
	/**the addNewFacility() method allows for adding of new facilities with unique names,
	 * it calls the updateFiles() method to update the facilities file.
	 */
	public static void addNewFacility() throws IOException
	{
		ArrayList<Integer> facilityIDs = new ArrayList<>();
		ArrayList<String> facilityNames = new ArrayList<>();
		Facility fac;
		String pattern = "[0-9]{1,}|[0-9]{1,}(\\.)[0-9]{1,2}";
		String input = "";
		String name = "";
		double price = -1;
		boolean valid = false;
		boolean found = false;
		
		for(int i = 0; i < currentFacilities.size(); i++)
		{
			facilityIDs.add(currentFacilities.get(i).getFacilityID());
			facilityNames.add(currentFacilities.get(i).getName());
		}
		input = JOptionPane.showInputDialog(null, "Input facility name: ");
		if(input != null && input.length() != 0)
		{
			if(facilityNames.contains(input.toUpperCase()))
				JOptionPane.showMessageDialog(null, "\"" + input + "\" :- Facility already exists.");
			else
			{
				name = input;
				input = JOptionPane.showInputDialog(null, "Input price per hour(integer or double value greater than 0): ");
				while(input != null && !valid)
				{
					if(input.matches(pattern))
					{
						price = Double.parseDouble(input);
						valid = true;
						if(price == 0)
						{
							input = JOptionPane.showInputDialog(null, "Value greater than 0 expected.\nInput price per hour: ");
							price = -1;
							valid = false;
						}
					}
					else
						input = JOptionPane.showInputDialog(null, "Incorrect format, an integer or double value expected.\nInput price per hour: ");
				}
				if(price != -1)
				{
					int index = 0;
					while(index <= facilityIDs.size() && !found)
					{
						index++;
						if(!(facilityIDs.contains(index)))
							found = true;
					}
					fac = new Facility(index, name, price);
					currentFacilities.add((index - 1), fac);
					updateFiles(2);
					JOptionPane.showMessageDialog(null, "Facility added successfully.");
				}
			}
		}
	}
	
	/**the deleteFacility() method allows for the deletion of a facility,
	 * it calls the updateFiles() method to update the facilities file.
	 */
	public static void deleteFacility() throws IOException
	{
		Facility[] options;
		Facility selection;
		ArrayList<Facility> removableFacilities = new ArrayList<>();
		Facility temp;
		int facID = 0;
		boolean removable = true;
		boolean deleted = false;
		for(int i = 0; i < currentFacilities.size(); i++)
		{
			removable = true;
			temp = currentFacilities.get(i);
			for(int j = 0; j < bookings.size() && removable; j++)
			{
				if(temp.getFacilityID() == bookings.get(j).getFacilityID())
					removable = false;
			}
			if(removable)
				removableFacilities.add(temp);
		}
		if(removableFacilities.size() != 0)
		{
			options = new Facility[removableFacilities.size()];
			for(int i = 0; i < options.length; i++)
				options[i] = removableFacilities.get(i);
			selection = (Facility) JOptionPane.showInputDialog(null, "Choose facility to delete: ", "Removable facilities", 1, null, options, options[0]);
			facID = selection.getFacilityID();
			if(selection != null)
			{
				currentFacilities.remove(selection);
				updateFiles(2);
			}
		}
	}
	
	/**the viewYourBookings() method allows the user to view their bookings, and allows the admin
	 * to see the bookings on a given day or days in given range of dates,
	 * it calls the validDate() method to validate the given date.
	 */
	public static void viewYourBookings()
	{
		Facility [] options = new Facility[currentFacilities.size()];
		Facility input;						
		int inputID;
		String message = "", startDate, endDate;
		String message1 = "Current bookings: \n";
		LocalDate sDate, eDate, temp, tempDate; 
		
		
		
		if(admin){
			
			for(int i = 0; i < currentFacilities.size();i++)
				options[i] = currentFacilities.get(i);								
			input = (Facility) JOptionPane.showInputDialog(null, "Choose a facility", "input", 1, null, options, options[0]);
			
			if(input != null){
				
				inputID = input.getFacilityID();
				startDate = JOptionPane.showInputDialog(null, "Input start date to check bookings: ");
				
				if(startDate != null){
					
					if(validDate(startDate))
					{
						
						sDate = LocalDate.parse(startDate);
						endDate = JOptionPane.showInputDialog(null, "Input end date to check bookings(just press 'ok' or cancel to check only the start date): ");
						if(endDate == null || endDate.length() == 0){
							
							for(int j = 0; j < bookings.size(); j++){
								
								if(inputID == bookings.get(j).getFacilityID()){
								
									temp = LocalDate.parse(bookings.get(j).getDate());
									if(sDate.equals(temp))
										message1 += "Booking ID: " + bookings.get(j).getBookingID() + " is registered by user: " + bookings.get(j).getUserID() + " for date: "
										+ bookings.get(j).getDate() + " and slot number: " + bookings.get(j).getSlotNum() + "\nPayment status = " + bookings.get(j).getPaymentStatus();
								}
							}
						}else if(validDate(endDate)){
							
							
							eDate = LocalDate.parse(endDate);
							for(int j = 0; j < bookings.size(); j++){
								
								if(inputID == bookings.get(j).getFacilityID()){
									
									tempDate = LocalDate.parse(bookings.get(j).getDate());
									if((tempDate.isAfter(sDate) || tempDate.equals(sDate)) && (tempDate.isBefore(eDate) || tempDate.equals(eDate)))
										message1 += "Booking ID: " + bookings.get(j).getBookingID() + " is registered by user: " + bookings.get(j).getUserID() + " for date: "
										+ bookings.get(j).getDate() + " and slot number: " + bookings.get(j).getSlotNum() + "\nPayment status = " + bookings.get(j).getPaymentStatus();
								}
							}
						}else
							JOptionPane.showMessageDialog(null, "Error: Invalid date.");
					}else
						JOptionPane.showMessageDialog(null, "Error: Invalid date.");
				}else
					JOptionPane.showMessageDialog(null, "Error: Invalid date.");
			}		
		}else{

			for(int i = 0; i < bookings.size(); i++){
	
				if(bookings.get(i).getUserID() == userID){
				
					message += "Booking ID: " + bookings.get(i).getBookingID() + " is registered for date: " 
								+ bookings.get(i).getDate() + " Payment status = " + bookings.get(i).getPaymentStatus() + "\n";
				}
			}
				JOptionPane.showMessageDialog(null, "Your bookings are:\n" + message);
		}
		
	}
	
	
	/**the updateBookings() method checks all bookings to see if they are all still valid,
	 * and removes any invalid ones and calls the updateFiles() method to update bookings file.
	 */
	public static void updateBookings() throws IOException{
		boolean payStat;
		String date;
		Booking find;
		for(int i=0;i<bookings.size();i++){
			find=bookings.get(i);
			payStat=find.getPaymentStatus();
			date=bookings.get(i).getDate();
		
			if(!checkDate(date)&&payStat){
				bookings.remove(i);			
			}
		}
		updateFiles(3);
	}
	
	/**the checkForRecommission() method checks all facilities to see if they have to be recommissioned,
	 * and recommissions them if needed then it calls the updateFiles() method to update the facilities file.
	 */
	public static void checkForRecommission() throws IOException
	{
		boolean isBefore;
		String[] t;
		LocalDate compare;
		for(int i = 0;i < currentFacilities.size();i++)
		{
			if(currentFacilities.get(i).getActive() == false)
			{
				t = currentFacilities.get(i).getDecommissionUntilDate().split("-");
				compare = LocalDate.of(Integer.parseInt(t[0]), Integer.parseInt(t[1]), Integer.parseInt(t[2]));
				if(compare.isBefore(LocalDate.now()) || compare.equals(LocalDate.now()))
				{
					currentFacilities.get(i).setActive(true);
					currentFacilities.get(i).setDecommissionUntilDate("0000-00-00");
				}
			}
		}
		updateFiles(2);
	}
	
	/**the validDate() method checks if given string is a valid date and correct format.
	 * @param date - the date string to be tested/checked.
	 * @return true if given date is valid, false if invalid.
	 */
	public static boolean validDate(String date)
	{	
		boolean valid = false;
		LocalDate compareDate;
		String pattern = "[0-9]{4}-[0-9]{1,2}-[0-9]{1,2}";	
		if(!(date.matches(pattern)))
			valid = false;
		else
		{
			int[] days = {31,28,31,30,31,30,31,31,30,31,30,31};
			String[] t = date.split("-");
			if(Integer.parseInt(t[1]) > 12 || Integer.parseInt(t[1]) == 0)
			{
				return false;
			}
			int position = Integer.parseInt(t[1]);
			if(Integer.parseInt(t[2]) != 0)
			{
				compareDate = LocalDate.of(Integer.parseInt(t[0]), 1, 1);
				if(compareDate.isLeapYear())
				{
					days[1] = 29;
				}
				if(!(Integer.parseInt(t[2]) <= days[position - 1]))
				{
					return false;
				}
				else
					valid = true;
			}
			
		}
		return valid;
	}
	
	/**the checkDate() method checks if the given date is valid by calling the validDate() method
	 * if it is valid it checks if it is on or after today.
	 * @return true if given date is valid and is today's or after today's date, false if date is invalid or is before today's date
	 */
	public static boolean checkDate(String date)
	{		
		boolean valid = false;
		if(!validDate(date))
		{
			valid = false;
		}
		else
		{
			LocalDate now;
			LocalDate compareDate;
			DateTimeFormatter formatter;
			String FormatDateTime,dateAsString;
			now = LocalDate.now();
			formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			FormatDateTime = now.format(formatter);
			String[] t = date.split("-");
			compareDate = LocalDate.of(Integer.parseInt(t[0]), Integer.parseInt(t[1]), Integer.parseInt(t[2]));
			boolean isBefore = compareDate.isBefore(now);
			if(isBefore)
			{
				valid = false;
			}
			else
			{
				valid = true;
			}
		}
		return valid;
	}
	
	/**the makePayment() method allows the admin to change the status of a booking to paid,
	 * it calls the payStat() method to show the options, and updateFiles() method to update bookings file.
	 */
	public static void makePayment() throws IOException
	{	
		ArrayList<Booking> temp = new ArrayList<Booking>();
		Booking toAdd;
		Booking selection;
		int size = 0;
		for(int i = 0; i < bookings.size(); i++)
		{
			toAdd = bookings.get(i);
			if(!toAdd.getPaymentStatus())
				temp.add(toAdd);

			size = temp.size();
		}
		if(temp.size() != 0)
		{
			Booking[] options = new Booking[size];
			for(int i = 0; i < options.length; i++)
				options[i] = temp.get(i);
		
			selection = (Booking) JOptionPane.showInputDialog(null, "Select booking to make payment: ", "Select Booking", 1, null, options, options[0]);
			
			if(selection == null)
				JOptionPane.showMessageDialog(null,"No booking selected");
			else
				selection.setPaymentStatus(payStat());
			
			updateFiles(3);
		}
		else
			JOptionPane.showMessageDialog(null, "No unpaid bookings.");
	}
	
	/**the payStat() method gives a choice if booking is paid or not.
	 * @return true if paid, false if unpaid
	 */
	public static boolean payStat()
	{
		boolean result = false;
		String[] payment = {"paid", "unpaid"};
		String paymentStat = (String) JOptionPane.showInputDialog(null, "Select payment status","Payment Status", 1, null, payment, payment[0]);
		if(paymentStat != null)
		{
			if(paymentStat.equals("paid"))
				result = true;
			else
				result = false;
		}
		return result;
	}
	
	
	/**the makeBooking() method allows the admin to make a new booking,
	 * it calls the slot() method to convert time to slot number,
	 * also calls the checkDate() method to validate the date,
	 * and updateFiles() method to update the bookings file.
	 */
	public static void makeBooking() throws IOException
	{ 		
		ArrayList<Integer> bookingID = new ArrayList<Integer>();
		ArrayList<Facility> temp = new ArrayList<>();
		Facility aFacility;
		String[] times = {"09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00"};
		String[] userOptions = new String[users.size() - 1];
		String user;
		
		for(int i = 1; i < users.size(); i++)
			userOptions[i-1] = users.get(i).getUserID() + "," + users.get(i).getEmail();
		user = (String) JOptionPane.showInputDialog(null, "Choose a user: ", "USERS", 1, null, userOptions, userOptions[0]);
		if(user != null)
		{
			for(int i = 0; i < currentFacilities.size(); i++)
			{
				aFacility = currentFacilities.get(i);
				if(aFacility.getDecommissionUntilDate().equals("0000-00-00"))
					temp.add(aFacility);
			}
			if(temp.size() != 0)
			{
				Facility[] options = new Facility[temp.size()];
				for(int i = 0; i < temp.size(); i++)
					options[i] = temp.get(i);
				aFacility = (Facility) JOptionPane.showInputDialog(null, "Select a facility to book: ", "Booking", 1, null, options, options[0]);
				if(aFacility != null)
				{
					String date = JOptionPane.showInputDialog(null, "Enter the date for bookings in the format YYYY-MM-DD");
					if(checkDate(date))
					{
						for(int i = 0; i < bookings.size(); i++)
							bookingID.add(bookings.get(i).getBookingID());
						
						String time = (String) JOptionPane.showInputDialog(null,"Select the hour to book","Booking time", 1, null, times, times[0]);
						boolean validBooking=true;
						int slot = slots(time);
						if(time != null && validBooking)
						{
							slot = slots(time);
							
							for(int j=0;j<bookings.size();j++)
							{
								if((bookings.get(j).getDate()).equals(date))
								{
									if(bookings.get(j).getSlotNum()==slot)
									{
										if(bookings.get(j).getFacilityID()==aFacility.getFacilityID())
										{
											String result="A "+aFacility.getName()+" is already booked for slot "+slot+" on "+date;
											JOptionPane.showMessageDialog(null,result);
											validBooking=false;
											break;
										}
									}
								}
							}
						}
									
						if(validBooking){
							boolean found = false;
							int i = 0;
							while(i <= bookingID.size() && !found)
							{
								i++;
								if(!bookingID.contains(i))
									found = true;
							}
							boolean status = payStat();
							Booking toBook = new Booking(i, aFacility.getFacilityID(), Integer.parseInt(user.substring(0, user.indexOf(","))), date, slot, status);
							
							bookings.add(i-1, toBook);
							updateFiles(3);
							
						}
					}
				}
					else
					JOptionPane.showMessageDialog(null,"Invalid date input","Error",1);
			}
		}
			else
				JOptionPane.showMessageDialog(null, "No facility available.");
	}

	/**the slots() method converts a time to a slot number.
	 * @param input - time given as a string in the format hh:mm
	 * @returns the equivalent slot number to given time.
	 */
	public static int slots(String input){
		int index = 0;
		String[] times = {"09:00","10:00","11:00","12:00","13:00","14:00","15:00","16:00","17:00"};
		
		for(int i = 0; i < times.length; i++)
		{
			if(times[i].equals(input))
			{
				index = i;
				break;
			}
		}
		return (index + 1);
	}
	
	/**the checkAccounts() method shows the amount the user owns for his/her bookings,
	 * also allows the admin to see a users account statement.
	 */
	public static void checkAccounts()
	{
		User options[] = new User[users.size()];
		User input;
		int inputID;
		int facID;
		double amountOwed = 0;
		String message1 = "";
		String message2 = "Account Balance: ";
		if(admin)
		{
			for(int i = 0; i < users.size();i++)
			{
				options[i] = users.get(i);
			}
			input = (User) JOptionPane.showInputDialog(null, "Choose a User", "input", 1, null, options, options[0]);
			if(input != null)
			{
				inputID = input.getUserID();
				for(int i = 0;i < bookings.size();i++)
				{
					if(inputID == bookings.get(i).getUserID())
					{
						if(bookings.get(i).getPaymentStatus() == false)
						{
							facID = bookings.get(i).getFacilityID();
							for(int j = 0; j < currentFacilities.size();j++)
							{
								if(currentFacilities.get(j).getFacilityID() == facID)
								{
									amountOwed += currentFacilities.get(j).getPricePerHour();
								}
							}
						}
					}
				}
				message1 = "Amounted Due: " + "\t" + amountOwed;
				JOptionPane.showMessageDialog(null,message1);
			}
		}
		else
		{
			for(int i = 0; i < bookings.size();i++)
			{
				if(bookings.get(i).getUserID() == userID)
				{
					if(bookings.get(i).getPaymentStatus() == false)
					{
						facID = bookings.get(i).getFacilityID();
						for(int j = 0;j < currentFacilities.size();j++)
						{
							if(currentFacilities.get(j).getFacilityID() == facID)
							{
								amountOwed += currentFacilities.get(j).getPricePerHour();
							}
						}
					}
				}
			}
			message2 = "Amount Due: " + "\t" + amountOwed;
			JOptionPane.showMessageDialog(null,message2);
		}	
	}
	
	/**the decommission() method allows for a facility to be decommissioned,
	 * it calls the checkDate() method to validate the date,
	 * and then calls the updateFiles() method to update the facilities file.
	 */
	public static void decommission() throws IOException
	{
		ArrayList<Facility> temp = new ArrayList<>();
		Facility aFacility;
		boolean canBeDecommissioned = true;
		for(int j = 0; j < currentFacilities.size(); j++)
		{
			aFacility = currentFacilities.get(j);
			canBeDecommissioned = true;
			for(int i = 0; i < bookings.size() && canBeDecommissioned;i++)
			{
				if(bookings.get(i).getFacilityID() == aFacility.getFacilityID())
					canBeDecommissioned = false;
			}	
			if(canBeDecommissioned)
				temp.add(aFacility);
		}
		
		if(temp.size() != 0)
		{
			Facility[] options = new Facility[temp.size()];
			for(int i = 0; i < temp.size(); i++)
				options[i] = temp.get(i);
			aFacility = (Facility) JOptionPane.showInputDialog(null, "Select facility to decommission: ", "Decommission", 1, null, options, options[0]);
			if(aFacility != null)
			{
				boolean valid = false;
				String date = JOptionPane.showInputDialog(null,"Enter a date to decommission the facility","Format: yyyy-mm-dd",1);
				while(date != null && !valid)
				{
					if(!checkDate(date))
					{
						JOptionPane.showMessageDialog(null,"Not a valid date");
						date = JOptionPane.showInputDialog(null,"Enter a date to decommission the facility","Format: yyyy-mm-dd",1);
					}
					else
					{
						valid = true;
						aFacility.setActive(false);
						aFacility.setDecommissionUntilDate(date);
					}	
				}
				updateFiles(2);
			}
		}
		else
			JOptionPane.showMessageDialog(null,"Facility has booking, cannot decommission");
	}
	
	/**the recommission() method allows to recommission a decommissioned facility,
	 * it calls the updateFiles() method to update the facilities file.
	 */
	public static void recommission() throws IOException
	{
		ArrayList<Facility> temp = new ArrayList<>();
		Facility aFacility;
		for(int i = 0; i < currentFacilities.size(); i++)
		{
			aFacility = currentFacilities.get(i);
			if(!(aFacility.getDecommissionUntilDate().equals("0000-00-00")))
				temp.add(aFacility);
		}
		if(temp.size() != 0)
		{
			Facility[] options = new Facility[temp.size()];
			for(int i = 0; i < temp.size(); i++)
				options[i] = temp.get(i);
			aFacility = (Facility) JOptionPane.showInputDialog(null, "Select a facility to recommission: ", "Recommission", 1, null, options, options[0]);
			if(aFacility != null)
			{
				aFacility.setActive(true);
				aFacility.setDecommissionUntilDate("0000-00-00");
				updateFiles(2);
			}
		}
		else
			JOptionPane.showMessageDialog(null, "No facilities to recommission.");
	}
	
	/**the viewAvailabilityOfFacility() method shows the avaible facilities for the given date or range of dates,
	 * it calls the validDate() method to validate the date.
	 */
	public static void viewAvailabilityOfFacility()
	{
		if(currentFacilities.size() != 0)
		{
			String[] options = new String[currentFacilities.size()];
			String[] temp;
			String selection;
			String startDate, endDate;
			LocalDate sDate, eDate, tempDate;
			int selectedID, index;
			String result = "Facility is: \n";
			for(int i = 0; i < currentFacilities.size(); i++)
				options[i] = (i+1) + "." + currentFacilities.get(i).getName();
			selection = (String) JOptionPane.showInputDialog(null, "Select facility: ", "Availability", 1, null, options, options[0]);
			if(selection != null)
			{
				index = Integer.parseInt(selection.substring(0, selection.indexOf(".")));
				selectedID = currentFacilities.get(index - 1).getFacilityID();
				startDate = JOptionPane.showInputDialog(null, "Input start date to check availability: ");
				if(startDate != null)
				{
					if(validDate(startDate))
					{
						temp = startDate.split("-");
						sDate = LocalDate.of(Integer.parseInt(temp[0]), Integer.parseInt(temp[1]), Integer.parseInt(temp[2]));
						endDate = JOptionPane.showInputDialog(null, "Input end date to check availability(just press 'ok' or cancel to check only the start date): ");
						if(endDate == null || endDate.length() == 0)
						{
							if(!(currentFacilities.get(index - 1).getActive()))
							{
								temp = currentFacilities.get(index - 1).getDecommissionUntilDate().split("-");
								tempDate = LocalDate.of(Integer.parseInt(temp[0]), Integer.parseInt(temp[1]), Integer.parseInt(temp[2]));
								if(sDate.equals(tempDate))
									result += selection + "Is not active(decommissioned) on " + startDate;
							}
							else
							{
								result += selection + "Is available on " + startDate;
							}
						}
						else if(validDate(endDate))
						{
							temp = endDate.split("-");
							eDate = LocalDate.of(Integer.parseInt(temp[0]), Integer.parseInt(temp[1]), Integer.parseInt(temp[2]));
							if(!(currentFacilities.get(index - 1).getActive()))
							{
								temp = currentFacilities.get(index - 1).getDecommissionUntilDate().split("-");
								tempDate = LocalDate.of(Integer.parseInt(temp[0]), Integer.parseInt(temp[1]), Integer.parseInt(temp[2]));
								if((tempDate.isAfter(sDate) || tempDate.equals(sDate)) && (tempDate.isBefore(eDate) || tempDate.equals(eDate)))
									result += selection + "Is not active(decommissioned) between " + startDate + " -> " + endDate;
							}
							else
							{
								result += selection + "Is available between " + startDate + " -> " + endDate;
							}
						}
						else
							JOptionPane.showMessageDialog(null, "Error: Invalid date.");
					}
					else
						JOptionPane.showMessageDialog(null, "Error: Invalid date.");
				}
				else
					JOptionPane.showMessageDialog(null, "Error: Invalid date.");
				
			}
			JOptionPane.showMessageDialog(null, result);
		}
		else
			JOptionPane.showMessageDialog(null, "No facilities found.");
	}
}