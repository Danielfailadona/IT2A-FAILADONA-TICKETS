package it2a.failadona.tickets;
import java.util.Scanner;

public class Tickets{
	
	public void tTransaction{

        Scanner sc = new Scanner(System.in);
        String response;
    do{
        System.out.println("\n------------------------------");
        System.out.println("Ticket DETAIL");
        System.out.println("1. ADD TICKET DETAIL");               
        System.out.println("2. VIEW TICKET DETAIL");        
        System.out.println("3. UPDATE TICKET DETAIL");        
        System.out.println("4. DELETE TICKET DETAIL");        
        System.out.println("5. EXIT");      

//_________________________________________                
        System.out.print("Enter Selection: ");
        int action = -1;

        while (action < 1 || action > 5) {
              System.out.print("Enter Action: ");
        
        if (sc.hasNextInt()) {
           action = sc.nextInt();   
           
        if (action < 1 || action > 5) {        
            System.out.println("Invalid input. Please choose a number between 1 and 5.");
        }
            
        } else {
            System.out.println("Invalid input. Please enter a number.");
            sc.next();
        }
        
  }
//_________________________________________    
 Tickets tk = new Tickets();
                                
        switch(action){
            
            case 1:           
            tk.addTicket();
            tk.viewTickets();
                break;
                
            case 2:
          
                break;
            
            case 3:
            tk.viewTickets(); 
            tk.updateTickets();
            tk.viewTickets();    
               break;
                
            case 4:
            tk.viewTickets(); 
            tk.deleteTickets();
            tk.viewTickets();   
                break;
                
            case 5:              
                break;
                
                
        }
        
        System.out.println("Do you want to continue? yes/no: ");
        response = sc.next();
        
    }while(response.equalsIgnoreCase("yes"));  
    }
		

	
		
//<<<<<<<Add Tickets Inputs<<<<<<<
public void addTickets(){
Scanner sc = new Scanner(System.in); 
config conf = new config();
Tickets t = new Tickets();
  
System.out.print("Enter Movie Name: "); 
String movie_n = sc.next();

System.out.print("Enter Seats Available "); 
int m_seats = (int) t.numValid();

System.out.print("Enter Movie Price: "); 
double m_price = (double) t.numValid();

System.out.print("Enter Morning Discount "); 
double Mdiscount =(double) t.numValid();

System.out.print("Enter Afternoon Discount "); 
double Adiscount = (double) t.numValid();

System.out.print("Enter Evening Discount "); 
double Ediscount = (double) t.numValid();

String qry = "INSERT INTO tbl_tickets (movie_name, max_seats, t_price, morning_discount, afternoon_discount, evening_discount) VALUES (?, ?, ?, ?, ?, ?)";

conf.addRecord(qry, movie_n, m_seats, m_price, Mdiscount, Adiscount, Ediscount);	
    }
    
    
    
    



//<<<<<<<View Ticket Inputs<<<<<<
public void viewTickets() {
        String qry = "SELECT * FROM tbl_tickets";
        String[] hrds = {"ID", "Movie", "Seats Available", "Price", "Morning discount", "Afternoon discount", "Evening discount"};
        String[] clms = {"t_id, movie_name", "max_seats", "t_price, morning_discount", "afternoon_discount", "evening_discount"};
config conf = new config();        
        conf.viewRecords(qry, hrds, clms);
    }









//<<<<<<<Update Ticket Inputs<<<<<<
public void updateTickets(){      
Scanner sc = new Scanner(System.in);
config conf = new config(); 
Tickets t = new Tickets();  

System.out.print("Enter ID to Update");
int id = sc.nextInt();


//validation if key exists
while(conf.getSingleValue("SELECT t_id FROM tbl_tickets WHERE t_id = ?", id)==0){
 System.out.println("Selected ID doesn't exist");   
 System.out.println("Select customer ID again:");
 id = sc.nextInt(); 
    }
//end of validation    


System.out.print("Enter New Movie Name: "); 
String movie_n = sc.next();

System.out.print("Enter New Seats Available "); 
int m_seats = (int) t.numValid();

System.out.print("Enter New Movie Price: "); 
double m_price = (double) t.numValid();

System.out.print("Enter New Morning Discount "); 
double Mdiscount = (double) t.numValid();

System.out.print("Enter New Afternoon Discount "); 
double Adiscount = (double) t.numValid();

System.out.print("Enter New Evening Discount "); 
double Ediscount = (double) t.numValid();
        
    
String qry = "UPDATE tbl_tickets SET movie_name = ?, max_seats = ?, t_price = ?, morning_discount = ?, afternoon_discount = ?, evening_discount  = ? WHERE t_id = ?";

conf.updateRecord(qry, movie_n, m_seats, m_price, Mdiscount, Adiscount, Ediscount, id);             
                }









//<<<<<<Delete Ticket Inputs<<<<<<<    

private void deleteTickets(){
Scanner sc = new Scanner(System.in);
config conf = new config();

System.out.print("Enter ID to Delete");
int id = sc.nextInt();     

 
  //validation if key exists
while(conf.getSingleValue("SELECT t_id FROM tbl_tickets WHERE t_id = ?", id)==0){
 System.out.println("Selected ID doesn't exist");   
 System.out.println("Select customer ID again:");
int id = sc.nextInt(); 
    }
//end of validation   
 

String qry = "DELETE FROM tbl_tickets WHERE t_id = ?";
conf.deleteRecord(qry, id);
        }

//<<<<<< Validation For Numbers <<<<<<<
    public double numValid() {
        Scanner sc = new Scanner(System.in);
        double value;
        while (true) {
            if (sc.hasNextDouble()) { // Check if the next input is a double
                value = sc.nextDouble();
                break; // Exit the loop if input is valid
            } else {
                System.out.print("Invalid input. Please enter a number: ");
                sc.next(); // Clear invalid input
            }
        }
        return value;
    /*end validation void*/}



}