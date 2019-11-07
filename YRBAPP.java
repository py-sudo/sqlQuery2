
/*============================================================================
Author:Peiyi Guan
Student NO.: 215328917
============================================================================*/

import java.util.*;
import java.net.*;
import java.text.*;
import java.lang.*;
import java.io.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;

/*============================================================================
CLASS YrbApp
============================================================================*/

public class YRBAPP {
    private Connection conDB; // Connection to the database system.
    private final String url = "jdbc:db2:c3421a";// URL: Which database?
    private static ArrayList<String> customerUserInfo;
    private static ArrayList<String> recordClubs;

    // Constructor
    public YRBAPP() {
        // Set up the DB connection.
        try {
            // Register the driver with DriverManager.
            Class.forName("com.ibm.db2.jcc.DB2Driver").newInstance();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.exit(0);
        } catch (InstantiationException e) {
            e.printStackTrace();
            System.exit(0);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            System.exit(0);
        }

        // Initialize the connection.
        try {
            // Connect with a fall-thru id & password
            conDB = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.print("\nSQL: database connection error.\n");
            System.out.println(e.toString());
            System.exit(0);
        }
        // auto commit turned off
        try {
            conDB.setAutoCommit(false);
        } catch (SQLException e) {
            System.out.print("\nFailed trying to turn autocommit off.\n");
            e.printStackTrace();
            System.exit(0);
        }

    }

    private static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }

    private static int intVal(String s) {
        try {
           Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return -1;
        } catch (NullPointerException e) {
            return -1;
        }
    
        return Integer.parseInt(s);
    }

    private static int fetchID(Scanner sc) {
        String input = sc.nextLine();
        if (input.isEmpty()) {
            System.out.println("Thank you for using York book store app, see you next time.");
            System.exit(0);
        }
        while (!isInteger(input)) {
            System.out.print(input + "  is not a valid integer,Please retry: ");
            input = sc.nextLine();
            if (input.isEmpty()) {
                System.out.println("bye");
                System.exit(0);
            }
        }

        return Integer.parseInt(input);
    }

    public boolean ifUpdate(Scanner sc) {
        boolean opt = false;
        System.out.print("Do you want to update this customer(Yes/No):");
        String input = sc.nextLine();
        
        while (isInteger(input) || input.isEmpty() || !( input.toLowerCase().equals("yes") || input.toLowerCase().equals("no"))) {
            
                System.out.print("invalid option,Enter again: ");
                input = sc.nextLine();
        }
        if (input.toLowerCase().equals("yes")) {
            opt = true;
            return opt;
        } 
        if (input.toLowerCase().equals("no")) {
            opt = false;
            return opt;
        }
        
        
        return opt;
    }

    public int updateCustomerOptions(Scanner sc) {
        System.out.println("Select following update options");
        System.out.println("1 : update name");
        System.out.println("2 : update city");
        System.out.println("3 : update  name and city");

        String input = sc.nextLine();
        int val = intVal(input);
        while (val == -1 || (val != 1 && val != 2 && val != 3)) {
            System.out.print("Please enter an valiad input: ");
            // String input = sc.nextLine();
            val = intVal(sc.nextLine());
        }
        return val;
    }
    public String getCategory(Scanner sc) {
        System.out.println("-----------------------------------------");
        // display all cat
        HashMap<Integer, String> allCats = this.fetchCategories();

        System.out.print("Choose one of the following Categories in the database : ");
        int key = this.getCategoryKey(sc);
        while (allCats.get(key) == null) {
            System.out.print("Key selected is not valid, retry: ");
            key = this.getCategoryKey(sc);
        }
        String category = allCats.get(key);
        return category;
    }

    public String obtainBookTitle(Scanner sc) {
        System.out.print("Please Enter a book title : ");
        String input = sc.nextLine();
        while (input.isEmpty()) {
            System.out.print("Please Enter a book title : ");
            input = sc.nextLine();
        }
        return input;
    }

    public int getCategoryKey(Scanner sc) {
        String input = sc.nextLine();
        ;
        while (!isInteger(input)) {
            System.out.println(input + " is not a valid key");
            System.out.print("Please retry : ");
            input = sc.nextLine();
        }
        return Integer.parseInt(input);
    }
    private ArrayList<String> storeCustomerInfo(int customerID){
		ArrayList<String> customerInfo = new ArrayList<>();
		String custQuery = "SELECT * FROM YRB_CUSTOMER, YRB_MEMBER WHERE YRB_CUSTOMER.CID=" + customerID + " AND YRB_CUSTOMER.CID = YRB_MEMBER.CID";
		PreparedStatement psCust = null;
		ResultSet answers = null;
		
		try
		{   psCust = conDB.prepareStatement(custQuery);
            answers = psCust.executeQuery();
            if (answers.next())
			{
				customerInfo.add(Integer.toString(answers.getInt("cid")));
				customerInfo.add(answers.getString("name"));
				customerInfo.add(answers.getString("city"));
				customerInfo.add(answers.getString("club"));
			}
			while (answers.next())
			{
				customerInfo.add(answers.getString("club"));
			}
            answers.close();
            psCust.close();
		}
		catch (SQLException e)
		{
			System.out.println("The query could not be executed.");
			System.exit(0);
		}
		return customerInfo;
	}
	

    public boolean checkCustomer(int id) {
        String queryText = ""; // The SQL text.
        PreparedStatement querySt = null; // The query handle.
        ResultSet answers = null; // A cursor.
        boolean inDB = false; // Return.
        queryText = "SELECT name  " + "FROM yrb_customer " + "WHERE cid = ?     ";
        // Prepare the query.
        try {
            querySt = conDB.prepareStatement(queryText);
            querySt.setInt(1, id);
            answers = querySt.executeQuery();
         
            if (answers.next()) {
                inDB = true;
            } else {
                inDB = false;
            }
            answers.close();
            querySt.close();
        } catch (SQLException e) {
            System.out.println("SQL#1 failed in prepare");
            System.out.println(e.toString());
            System.exit(0);
        }
        return inDB;
    }

    public void getCustomerInfo(int id) {
        String queryText = ""; // The SQL text.
        PreparedStatement querySt = null; // The query handle.
        ResultSet answers = null; // A cursor.
        queryText = "   SELECT C.cid,C.name,C.city      FROM yrb_customer C WHERE C.cid=?";

        try {
            querySt = conDB.prepareStatement(queryText);
            querySt.setInt(1, id);
            answers = querySt.executeQuery();
            if (answers.next()) {
                System.out.print("Cid : "+answers.getInt("cid")+"    ");
                System.out.print("Name : "+answers.getString("name")+"    ");
                System.out.println("City : "+answers.getString("city"));
            }
            answers.close();
            querySt.close();
        } catch (SQLException e) {
            System.out.println(e.toString());
            System.exit(0);
        }

           
        customerUserInfo = this.storeCustomerInfo(id);

    }

    public HashMap<Integer, String> fetchCategories() {
        HashMap<Integer, String> hmap = new HashMap<Integer, String>();
        int index = 1;
        String queryText = ""; // The SQL text.
        PreparedStatement querySt = null; // The query handle.
        ResultSet answers = null; // A cursor.
        queryText = "SELECT *	" + "	FROM yrb_category ";

        try {
            querySt = conDB.prepareStatement(queryText);
            answers = querySt.executeQuery();
            while (answers.next()) {
                String val = answers.getString("cat");
                hmap.put(index, val);
                index++;
            }
            answers.close();
            querySt.close();
        Set set = hmap.entrySet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            Map.Entry mentry = (Map.Entry) iterator.next();
            System.out.print(mentry.getKey() + "   ");
            System.out.println(mentry.getValue());
        }

        } catch (SQLException e) {
            System.out.println("SQL#2 failed in prepare");
            System.out.println(e.toString());
            System.exit(0);
        }
        
        return hmap;
    }

    public TreeMap<Integer, ArrayList<String>> findBook(String title, String cat) {
        int index = 1;
        String queryText = ""; // The SQL text.
        PreparedStatement querySt = null; // The query handle.
        ResultSet answers = null;
        ArrayList<String> bookInfo = new ArrayList<String>();
        TreeMap<Integer, ArrayList<String>> result = new TreeMap<Integer, ArrayList<String>>();
        queryText = "SELECT B.title,B.year,B.language,B.weight	FROM yrb_book B	WHERE B.cat = ? and B.title = ?  AND B.title IN (SELECT TITLE from YRB_OFFER, YRB_MEMBER WHERE YRB_OFFER.CLUB = YRB_MEMBER.CLUB AND CID =?)";
        System.out.println(title+","+cat+","+customerUserInfo.get(0));
        try {
            querySt = conDB.prepareStatement(queryText);            
            querySt.setString(1, cat);
            querySt.setString(2, title);
            querySt.setInt(3,Integer.parseInt(customerUserInfo.get(0)));
            answers = querySt.executeQuery();
            if (!answers.next()) {
                System.out.println("There is no such book in the database");
                return result;
                
            } else {
                 do {
                     // get title, year, language, weight
                    bookInfo.add(answers.getString("title"));
                    bookInfo.add(Integer.toString(answers.getInt("year")));
                    bookInfo.add(answers.getString("language"));
                    bookInfo.add(Integer.toString(answers.getInt("weight")));
                    result.put(index,bookInfo);
                    index++;
                } while(answers.next());
            }
            answers.close();
            querySt.close();

        } catch (SQLException e) {
            System.out.println("SQL#1 failed in cursor.");
            System.out.println(e.toString());
            System.exit(0);
        }
        return result;
    }
    public void updateCustomer(int cusId,int opt,String name,String city){
        String queryText = ""; // The SQL text.
        PreparedStatement querySt = null; // The query handle.
        int answers = -1; // A cursor.
        
        if(opt==1){
            queryText = "UPDATE yrb_customer SET name=? where cid = ?";
            try {
                querySt = conDB.prepareStatement(queryText);
                querySt.setString(1,name);
                querySt.setInt(2,cusId);
                answers = querySt.executeUpdate();
                querySt.close();
                
            } catch (SQLException e) {
                System.out.println("SQL failed");
                System.out.println(e.toString());
                System.exit(0);
            }
            
        }
        if(opt==2){
            queryText = " UPDATE yrb_customer SET city=? where cid = ?";
            try {
                querySt = conDB.prepareStatement(queryText);
                querySt.setString(1,city);
                querySt.setInt(2,cusId);
                answers = querySt.executeUpdate();
                querySt.close();
           
                
            } catch (SQLException e) {
                System.out.println("SQL failed");
                System.out.println(e.toString());
                System.exit(0);
            }
        }
        if(opt==3){
            queryText = " UPDATE yrb_customer SET name=?,city=? where cid = ?";
            try {
                querySt = conDB.prepareStatement(queryText);
                querySt.setString(1,name);
                querySt.setString(2,city);
                querySt.setInt(3, cusId);
                answers = querySt.executeUpdate();
                querySt.close();
             
                
            } catch (SQLException e) {
                System.out.println("SQL failed");
                System.out.println(e.toString());
                System.exit(0);
            }
        }
        
    }
  
    private static void printBooksOptions( TreeMap<Integer, ArrayList<String>> books){
        System.out.println("");
        System.out.println("Slect a book you want to purchase from the following books");
        System.out.println("---------------------------------------------------------------");
        System.out.println("key"+"   "+"title"+"   "+"year"+"   "+"language"+"   "+"weight");
        System.out.println("---------------------------------------------------------------");
        for(int i=0;i<books.size();i++){
            int key = i+1;
            ArrayList<String> thisBook = books.get(i+1);
            System.out.println(key+"   "+thisBook.get(0)+"   "+thisBook.get(1)+"   "+thisBook.get(2)+"   "+thisBook.get(3));
            System.out.println("---------------------------------------------------------------");
        }

    }
    public int purchaseId(TreeMap<Integer, ArrayList<String>> books,Scanner sc){
        printBooksOptions(books);
        System.out.print("Enter the key of the book that you want to purchase (eg 1,2,3...) :" );
        int i = intVal(sc.nextLine());
        while(i<1||i>books.size()){
            printBooksOptions(books);
            System.out.print("input is not valid, please enter a valid key for the book : ");
            i = intVal(sc.nextLine());
        }
        return i;
    }
    
    public double minPrice(ArrayList<String> books,String cat,int id){
        String queryText = "Select min(price) as price from yrb_offer where title =? AND year =? and club in (select club FROM yrb_member where cid =?)";
        PreparedStatement querySt = null; // The query handle. 
        ResultSet answers = null;
        double min=0.0;
        recordClubs = new ArrayList<String>();
        // cat title year cid
        try {
                querySt = conDB.prepareStatement(queryText);
                querySt.setString(1,books.get(0));
                querySt.setInt(2,Integer.parseInt(books.get(1)));
                querySt.setInt(3,id);

                answers = querySt.executeQuery();
                if(answers.next()){
                    min = answers.getDouble("price");
                }
                querySt.close();
                answers.close();
                
                
            } catch (SQLException e) {
                System.out.println("SQL failed");
                System.out.println(e.toString());
                System.exit(0);
            }


        //get min price cat title year cid
        return min;
    }
    public boolean ifPurchaseBook(Scanner sc){
        boolean opt = false;
        System.out.print("Enter Yes to confirm purchase or Enter No to quit the app: ");
        String input = sc.nextLine();        
        while (isInteger(input) || input.isEmpty() || !( input.toLowerCase().equals("yes") || input.toLowerCase().equals("no"))) {
            
                System.out.print("invalid option,Enter again: ");
                input = sc.nextLine();
        }
        if (input.toLowerCase().equals("yes")) {
            opt = true;
            
        } 
        if (input.toLowerCase().equals("no")) {
            opt = false;
        }
        return opt;
    }
    public void insert_purchase(int cid, String club, String titles, int years, int qnty) {
        String queryText = ""; // The SQL text.
        PreparedStatement querySt = null; // The query handle.

        Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss");
        String finalStamp = dateFormat.format(timeStamp);

        queryText = "Insert into yrb_purchase values (?,?,?,?,?,?) ";
        try {
            querySt = conDB.prepareStatement(queryText);
            querySt.setInt(1, cid);
            querySt.setString(2, club);
            querySt.setString(3, titles);
            querySt.setInt(4, years);
            querySt.setString(5, finalStamp);
            querySt.setInt(6, qnty);
            querySt.executeUpdate();
            querySt.close();
        } catch (SQLException e) {
            System.out.println(e.toString());
            System.exit(0);
        }

    }
 
    private String fetch_club(double price,ArrayList<String> books,int id) {
        String queryText = ""; // The SQL text.
        PreparedStatement querySt = null; // The query handle.
        ResultSet answers = null; // A cursor.
        String club ="";
        queryText = "Select o.club FROM yrb_member m, yrb_offer o where o.club = m.club AND o.year = ? and m.cid = ? and o.title = ? and o.price = ? ";
        // cat title year cid
        try {
            querySt = conDB.prepareStatement(queryText);
            querySt.setInt(1,Integer.parseInt(books.get(1)));
            querySt.setInt(2,id);
            querySt.setString(3,books.get(0));
            querySt.setDouble(4,price);

            answers = querySt.executeQuery();
            if(answers.next()){
                club = answers.getString("club");
            }
            querySt.close();
            answers.close();
            
            
        } catch (SQLException e) {
            System.out.println("SQL failed");
            System.out.println(e.toString());
            System.exit(0);
        }

        return club;

             }



    public static void main(String[] args) {
        System.out.print("Welcome to bookstore,please enter a Customer ID  or (Press enter to quit):");
        Scanner sc = new Scanner(System.in);
        // get valid input val for customer id
        int custId = fetchID(sc); // customer id obtained
        YRBAPP yrbApp = new YRBAPP(); // initialize app class

        // check if customer exit in the database
        while (!yrbApp.checkCustomer(custId)) {
            System.out.print("There is no customer #");
            System.out.print(custId);
            System.out.print(" in the database.  Please retry:");
            custId = fetchID(sc);
        }
        //
        System.out.println("Customer selected is :");
        yrbApp.getCustomerInfo(custId);
        boolean updateOpt = yrbApp.ifUpdate(sc);
        if(updateOpt){  
            int opt = yrbApp.updateCustomerOptions(sc);
            if (opt == 1) {
                // update name
                System.out.println("Please enter a new name: ");
                String name = sc.nextLine();
                yrbApp.updateCustomer(custId,1,name,"");
                
            } 
            if (opt == 2) {
                // update city
                String city = sc.nextLine();
                yrbApp.updateCustomer(custId, 2, "", city);
            }
            if (opt == 3) {
                // update name and city
                String name = sc.nextLine();
                String city = sc.nextLine();
                yrbApp.updateCustomer(custId, 3, name, city);
            }
            yrbApp.getCustomerInfo(custId);
            System.out.println("\nTransaction Complete!\n");
            System.out.println("Here are the new information");
            yrbApp.getCustomerInfo(custId);
            
        }

        // find books
            String cat = yrbApp.getCategory(sc);
            String title = yrbApp.obtainBookTitle(sc);
            TreeMap<Integer, ArrayList<String>> booksFound = yrbApp.findBook(title, cat);
            while(booksFound.isEmpty()){
                 cat = yrbApp.getCategory(sc);
                 title = yrbApp.obtainBookTitle(sc);
                 booksFound = yrbApp.findBook(title, cat);
            }
            // books here
            int purchaseId =  yrbApp.purchaseId(booksFound,sc);
           ArrayList<String> bookToPurchase = booksFound.get(purchaseId);
          
           //book title and year to be purchased
           String booksTitle = bookToPurchase.get(0);
           int year = Integer.parseInt(bookToPurchase.get(1));

          
           double minPrice = yrbApp.minPrice(bookToPurchase,cat,custId);
           String club = yrbApp.fetch_club(minPrice, bookToPurchase,custId);
           System.out.print("Please enter the quantity you want to purchase: ");     

           int quantity = intVal(sc.nextLine());
           while(quantity==-1){
            System.out.println("The quantity you entered is not valid");
            System.out.print("Please enter a valid quantity: ");
            quantity = intVal(sc.nextLine());
           }
           double totalPrice = quantity*minPrice;
           System.out.println("The total price for this purchase is: "+totalPrice);

           if(yrbApp.ifPurchaseBook(sc)){
                yrbApp.insert_purchase(custId,club,booksTitle,year,quantity);
                System.out.println("\nTransaction Complete!\n");
                System.out.printf("Here are the details: \nCustomer ID: %d        Club: %s        Title: %s        Year: %d       Quantity: %d       Price: $%.2f", custId, club, booksTitle, year, quantity, totalPrice);
                System.out.println();
                System.out.println("Have a nice day and please visit us again");
                System.exit(0);
           }else{
            System.out.println("Thank you for using York book store app, see you next time.");
            System.exit(0);
           }
        

}
}
