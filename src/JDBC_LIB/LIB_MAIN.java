package JDBC_LIB;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

import com.mysql.jdbc.PreparedStatement;
import com.mysql.jdbc.Statement;

public class LIB_MAIN {

	public static void main(String[] args) throws SQLException {
		System.out.println("\t\t\tWELCOME TO THE");
		System.out.println("\n\t\t\tBOOKLAND LIBRARY");
		System.out.println("                              Once upon a book... :)");
		System.out.println("\n HELLO SIR/MA'AM\n");
		
		
		char cont='y';
		int opt;
		do {
		
		System.out.println(" Choose an option\n" + " 1.Add a book\n 2.Add a member(Enter either 'student' or 'faculty') ");
		System.out.println(" 3.Issue a book to a member if book is available in the library (student-max 3 books, faculty-max 5 books)");
		System.out.println(" 4.Return a book. For students, after due date, fine of rs.02 per day. No fine for faculty.");
		System.out.println(" 5.Generate report of all books in the library along with their issue status.");
		Scanner input=new Scanner(System.in);
		opt=input.nextInt();
				
		switch(opt)
		{
		case 1: add_book();
			break;
		case 2: add_member();
			break;
		case 3: issue_book();
			break;
		case 4: return_book();
			break;
		case 5: 
			System.out.println("BOOK ID" +"\t" +" BOOK NAME" +"\t" + "TOTAL" +"\t" +"COPIES_ISSUED");
			show_books();
			
			break;
		default: System.out.println("Wrong input!Enter a proper option!");
		
		}
		System.out.println("Continue?");
		cont=input.next().charAt(0);
		}while(cont=='y');
		System.out.println("Thank you :)");
	}
	public static int daysBetween(java.util.Date date, java.util.Date date2)
	   {
	      return (int)( (date2.getTime() - date.getTime()) / (1000 * 60 * 60 * 24));
	   }
	public static int numberOfDays(String fromDate,String toDate)
	   {    
	       java.util.Calendar cal1 = new java.util.GregorianCalendar();
	       java.util.Calendar cal2 = new java.util.GregorianCalendar();

	       //split year, month and days from the date using StringBuffer.
	       StringBuffer sBuffer = new StringBuffer(fromDate);
	       String yearFrom = sBuffer.substring(6,10);
	       String monFrom = sBuffer.substring(0,2);
	       String ddFrom = sBuffer.substring(3,5);
	       int intYearFrom = Integer.parseInt(yearFrom);
	       int intMonFrom = Integer.parseInt(monFrom);
	       int intDdFrom = Integer.parseInt(ddFrom);

	       // set the fromDate in java.util.Calendar
	       cal1.set(intYearFrom, intMonFrom, intDdFrom);

	       //split year, month and days from the date using StringBuffer.
	       StringBuffer sBuffer1 = new StringBuffer(toDate);
	       String yearTo = sBuffer1.substring(6,10);
	       String monTo = sBuffer1.substring(0,2);
	       String ddTo = sBuffer1.substring(3,5);
	       int intYearTo = Integer.parseInt(yearTo);
	       int intMonTo = Integer.parseInt(monTo);
	       int intDdTo = Integer.parseInt(ddTo);

	       // set the toDate in java.util.Calendar
	       cal2.set(intYearTo, intMonTo, intDdTo);

	       //call method daysBetween to get the number of days between two dates
	       int days = daysBetween(cal1.getTime(),cal2.getTime());
	       return days;
	   }
public static void return_book() throws SQLException{
	
	System.out.println("Enter member id number:");
	Scanner input =new Scanner (System.in);
	int id=input.nextInt();
	String type=null;
	int num_books=0;
	int val=0;
	 ResultSet rs=null;
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			Connection con=(Connection)DriverManager.getConnection("jdbc:mysql://localhost:3306/lib_sys?verifyServerCertificate=false&useSSL=true","root","isnam123");
			
			String selectSQL="select mem_type,mem_num_books from member where mem_id = ?";
			PreparedStatement stmt= (PreparedStatement) con.prepareStatement(selectSQL);
			stmt.setInt(1, id);
			rs=stmt.executeQuery();
			while(rs.next())
			{  
				val=1;
				type=rs.getString("mem_type") ;
				num_books=rs.getInt("mem_num_books") ;
				
			}
			if(val==0)
			{
				System.out.println("Wrong member number!!!");
			}
			else
			{ 
			if(type.equals("student"))
			{ 
				if(num_books<3)
				{	 
					int bk_val=0;
					System.out.println("Enter book id to return:");
					int bk_id=input.nextInt();
					stmt= (PreparedStatement) con.prepareStatement("select issue_date from books_issued where mem_id=? and book_id=?");
			     	stmt.setInt(1, id);
			     	stmt.setInt(2, bk_id);
					rs=stmt.executeQuery();
					String date_ret=null;
					while(rs.next())
					{
						 bk_val=1;
						 date_ret=rs.getString("issue_date");
						 
					}
					if(bk_val==0)
					{
						System.out.println("The given member id hasn't issued this book!");	
					}
					else
					{
						System.out.println("Enter date on which the book is returned:");
						String s=input.next();
						
						int days= numberOfDays(date_ret,s);
						System.out.println("Overdue days:" +days );
						System.out.println("Fine= " + 2*days);
						
						
						
							stmt= (PreparedStatement) con.prepareStatement("delete from books_issued where mem_id=? and book_id=?");
					     	stmt.setInt(1, id);
					     	stmt.setInt(2, bk_id);
					
					     	int i=stmt.executeUpdate();  
					     	System.out.println(i+" records deleted from table BOOKS_ISSUED");
					     	
					     	stmt= (PreparedStatement) con.prepareStatement("update book set copies_issued=copies_issued-1 where bk_id=?");
					     	
					     	stmt.setInt(1, bk_id);
					     	
					        i=stmt.executeUpdate(); 
					        System.out.println(i+" records updated in table BOOK");
					        
					        stmt= (PreparedStatement) con.prepareStatement("update member set mem_num_books=mem_num_books-1 where mem_id=?");
					     	
					     	stmt.setInt(1,id);
					     	
					        i=stmt.executeUpdate(); 
					        System.out.println(i+" records updated in table MEMBER");
					        
							System.out.println("Book returned successfully!");
							
						
						
					}
				}
				else
					System.out.println("Student already has 3 books issued!");
			     	
					
				
			}
			else
			{
				if(num_books<5)
				{
						
					int bk_val=0;
					System.out.println("Enter book id to return:");
					int bk_id=input.nextInt();
					stmt= (PreparedStatement) con.prepareStatement("select issue_date from books_issued where mem_id=? and book_id=?");
			     	stmt.setInt(1, id);
			     	stmt.setInt(2, bk_id);
					rs=stmt.executeQuery();
					String date_ret=null;
					while(rs.next())
					{
						 bk_val=1;
						 date_ret=rs.getString("issue_date");
					}
					if(bk_val==0)
					{
						System.out.println("The given member id hasn't issued this book!");	
					}
					else
					{
						System.out.println("No fine for faculty!");
							stmt= (PreparedStatement) con.prepareStatement("delete from books_issued where mem_id=? and book_id=?");
					     	stmt.setInt(1, id);
					     	stmt.setInt(2, bk_id);
					
					     	int i=stmt.executeUpdate();  
					     	System.out.println(i+" records deleted from table BOOKS_ISSUED");
					     	
					     	stmt= (PreparedStatement) con.prepareStatement("update book set copies_issued=copies_issued-1 where bk_id=?");
					     	
					     	stmt.setInt(1, bk_id);
					     	
					        i=stmt.executeUpdate(); 
					        System.out.println(i+" records updated in table BOOK");
					        
					        stmt= (PreparedStatement) con.prepareStatement("update member set mem_num_books=mem_num_books-1 where mem_id=?");
					     	
					     	stmt.setInt(1,id);
					     	
					        i=stmt.executeUpdate(); 
					        System.out.println(i+" records updated in table MEMBER");
					        
							System.out.println("Book returned successfully!");
							
				}
				}
				else
					System.out.println("Faculty already has 5 books issued!");
					
		}
		}
	}
		catch(ClassNotFoundException e)
		{
			e.printStackTrace();
		}
	
}
public static void issue_book() throws SQLException{
	
	System.out.println("Enter member id number:");
	Scanner input =new Scanner (System.in);
	int id=input.nextInt();
	String type=null;
	int num_books=0;
	int val=0;
	 ResultSet rs=null;
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			Connection con=(Connection)DriverManager.getConnection("jdbc:mysql://localhost:3306/lib_sys?verifyServerCertificate=false&useSSL=true","root","isnam123");
			
			String selectSQL="select mem_type,mem_num_books from member where mem_id = ?";
			PreparedStatement stmt= (PreparedStatement) con.prepareStatement(selectSQL);
			stmt.setInt(1, id);
			rs=stmt.executeQuery();
			while(rs.next())
			{  
				val=1;
				type=rs.getString("mem_type") ;
				num_books=rs.getInt("mem_num_books") ;
				
			}
			if(val==0)
			{
				System.out.println("Wrong member number!!!");
			}
			else
			{ 
			if(type.equals("student"))
			{ 
				if(num_books<3)
				{	 
					int bk_val=0;
					int t_c=0;
					int c_i=0;
					System.out.println("Enter book id to issue:");
					int bk_id=input.nextInt();
					stmt= (PreparedStatement) con.prepareStatement("select total_copies,copies_issued from book where bk_id=?");
			     	stmt.setInt(1, bk_id);
					rs=stmt.executeQuery();
					while(rs.next())
					{
						bk_val=1;
						t_c=rs.getInt("total_copies") ;
						c_i=rs.getInt("copies_issued") ;
						
					}
					if(bk_val==0)
					{
						System.out.println("Wrong book id!");	
					}
					else
					{
						int avail_copies=t_c-c_i;
						if(avail_copies<0)
						{
							System.out.println("All copies are already issued!!!");
						}
						else
						{
							String date;
							System.out.println("Enter date of issue:");
							date=input.next();
							stmt= (PreparedStatement) con.prepareStatement("insert into books_issued values(?,?,?)");
					     	stmt.setInt(1, id);
					     	stmt.setInt(2, bk_id);
					     	stmt.setString(3, date);
					     	
					     	int i=stmt.executeUpdate();  
					     	System.out.println(i+" records inserted in table BOOKS_ISSUED");  
					     	stmt= (PreparedStatement) con.prepareStatement("update book set copies_issued=copies_issued+1 where bk_id=?");
					     	
					     	stmt.setInt(1, bk_id);
					     	
					        i=stmt.executeUpdate(); 
					        System.out.println(i+" records updated in table BOOK");
					        
					        stmt= (PreparedStatement) con.prepareStatement("update member set mem_num_books=mem_num_books+1 where mem_id=?");
					     	
					     	stmt.setInt(1,id);
					     	
					        i=stmt.executeUpdate(); 
					        System.out.println(i+" records updated in table MEMBER");
					        
							System.out.println("Book issued successfully!");
							
						}
						
					}
				}
				else
					System.out.println("Student already has 3 books issued!");
			}
			else
			{
				if(num_books<5)
				{
					int bk_val=0;
					int t_c=0;
					int c_i=0;
					System.out.println("Enter book id to issue:");
					int bk_id=input.nextInt();
					stmt= (PreparedStatement) con.prepareStatement("select total_copies,copies_issued from book where bk_id=?");
			     	stmt.setInt(1, bk_id);
					rs=stmt.executeQuery();
					while(rs.next())
					{
						bk_val=1;
						t_c=rs.getInt("total_copies") ;
						c_i=rs.getInt("copies_issued") ;
						
					}
					if(bk_val==0)
					{
						System.out.println("Wrong book id!");	
					}
					else
					{
						int avail_copies=t_c-c_i;
						if(avail_copies<0)
						{
							System.out.println("All copies are already issued!!!");
						}
						else
						{
							String date;
							System.out.println("Enter date of issue:");
							date=input.next();
							stmt= (PreparedStatement) con.prepareStatement("insert into books_issued values(?,?,?)");
					     	stmt.setInt(1, id);
					     	stmt.setInt(2, bk_id);
					     	stmt.setString(3, date);
					     	
					     	int i=stmt.executeUpdate();  
					     	System.out.println(i+" records inserted in table BOOKS_ISSUED");  
					     	stmt= (PreparedStatement) con.prepareStatement("update book set copies_issued=copies_issued+1 where bk_id=?");
					     	
					     	stmt.setInt(1, bk_id);
					     	
					        i=stmt.executeUpdate(); 
					        System.out.println(i+" records updated in table BOOK");
					        
					        stmt= (PreparedStatement) con.prepareStatement("update member set mem_num_books=mem_num_books+1 where mem_id=?");
					     	
					     	stmt.setInt(1,id);
					     	
					        i=stmt.executeUpdate(); 
					        System.out.println(i+" records updated in table MEMBER");
					        
							System.out.println("Book issued successfully!");
							
						}
						
					}
				}
	
				else
					System.out.println("Faculty already has 5 books issued!");
				
				
			}
			}
		}
		catch(ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		
	
}
	
public static void add_member() throws SQLException{
	System.out.println("Enter member id number:");
	Scanner input=new Scanner(System.in);
	int id=input.nextInt();
	System.out.println("Enter member name:");
	input.nextLine();
	String name=input.nextLine();
	System.out.println("Enter member type(student/faculty):");
	
	String type=input.nextLine();
	System.out.println("Enter address:");
	String address=input.nextLine();
	
	
		

	try
	{
		Class.forName("com.mysql.jdbc.Driver");
		Connection con=(Connection)DriverManager.getConnection("jdbc:mysql://localhost:3306/lib_sys?verifyServerCertificate=false&useSSL=true","root","isnam123");
		
		PreparedStatement stmt= (PreparedStatement) con.prepareStatement("insert into member values(?,?,?,?,?)");  
		stmt.setInt(1, id);
		stmt.setString(2, name);
		stmt.setString(3, type);
		stmt.setString(4, address);
		stmt.setInt(5, 0);
		int i=stmt.executeUpdate();  
		System.out.println(i+" records inserted!"); 
		System.out.println("New member added successfully!");
	}
	catch(ClassNotFoundException e)
	{
		e.printStackTrace();
	}
	
	}
 public static void show_books() throws SQLException{
	 
	 ResultSet rs=null;
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			Connection con=(Connection)DriverManager.getConnection("jdbc:mysql://localhost:3306/lib_sys?verifyServerCertificate=false&useSSL=true","root","isnam123");
			
			Statement stmt=(Statement) con.createStatement();  
			rs=((java.sql.Statement) stmt).executeQuery("select * from book");
			while(rs.next())
			{
				System.out.println(rs.getInt(1) + "\t" + rs.getString(2) + "\t" + rs.getInt(3) + "\t" + rs.getInt(4));
				
			}
		}
		catch(ClassNotFoundException e)
		{
			e.printStackTrace();
		}
		
 }
 public static void add_book() throws SQLException{
	 
	System.out.println("Enter book id number:");
	Scanner input=new Scanner(System.in);
	int id=input.nextInt();
	System.out.println("Enter book name:");
	input.nextLine();
	String name=input.nextLine();
	System.out.println("Enter total number of copies:");
	int tot_copies=input.nextInt();
	
	
	
	try
	{
		Class.forName("com.mysql.jdbc.Driver");
		Connection con=(Connection)DriverManager.getConnection("jdbc:mysql://localhost:3306/lib_sys?verifyServerCertificate=false&useSSL=true","root","isnam123");
		
		PreparedStatement stmt= (PreparedStatement) con.prepareStatement("insert into book values(?,?,?,?)");  
		stmt.setInt(1, id);
		stmt.setString(2, name);
		stmt.setInt(3, tot_copies);
		stmt.setInt(4, 0);
		int i=stmt.executeUpdate();  
		System.out.println(i+" records inserted!"); 
		System.out.println("New book added successfully!");
	}
	catch(ClassNotFoundException e)
	{
		e.printStackTrace();
	}
	 
	 
 }
	}


