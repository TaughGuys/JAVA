import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.sql.*;
import java.sql.Connection;

public class ReadWebPage {
	

	public static void main(String[] args)   {
		Connection connection = null;
		 Statement stat = null;
		 int[] result = null;
		 String query = "select h.suplier , h.phone,  replace(replace(trim(h.suplier),'-','_'),'.','_') link1 , replace(replace(replace(replace(replace(trim(h.phone),' ','_'),'+',''),'-','_'),'.','_'),'(','') link2  from Hayk_test_phones h";
		 		
		 try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e1) {
			System.out.println("Class not found");
		}
		 
		 try {
	           connection = DriverManager.getConnection("jdbc:oracle:thin:@some_ip:some_port:SID",
	            		                                  "Hayk_manukyan", "Some_pass");
	        } catch (SQLException e) {
	            System.out.println("Connection Failed! ");	          
	            return;
	        }
		 
		 if (connection != null) {
	            System.out.println("Done!");
	        } else {
	            System.out.println("Failed to make connection!");
	        }
		 
		 
		// oppening result set 
		 	try {
		 		//connection.setAutoCommit(false);
				stat=connection.createStatement();
				ResultSet rs = stat.executeQuery(query);
				
				 while (rs.next()) {
					float price=0;	 
					 try {
					 String link1 = rs.getString("LINK1");					 
					 String link2 = rs.getString("LINK2");
					 String phone= rs.getString("PHONE");
					 String suplier = rs.getString("SUPLIER");					 
					 try {
						 price = getPrice(link1,link2);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					 catch (NullPointerException e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
							System.out.println("Null_point !");
							
						}
					 
					 //stat.execute("Update Hayk_test_phones set price= "+price +" where SUPLIER='"+suplier+"' and phone='"+phone+"'" );
					String update="Update Hayk_test_phones set price= "+price +" where SUPLIER='"+suplier+"' and phone='"+phone+"'";
					stat.addBatch(update);
					System.out.println(link1 + " " + link2 + " -> " + price   );
					 }catch (NullPointerException e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
							System.out.println("Null_point !!!!");
							
						}
				 }
				
				 result = stat.executeBatch();
				 //connection.commit();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				System.out.println("SQL exeption !");
			} 

		 	
		 	// closeing 
             try {
		        if (stat != null) { stat.close(); }
		    } catch(SQLException e) {   System.out.println("Unable to close");   }
		 
		 
		/*
		 * try { System.out.println( getPrice("Asus","ZenFone_4_Max_ZC554KL")); } catch
		 * (NullPointerException | IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */
    
	
	}
	
	
	private static float getPrice(String comp, String model) throws IOException , NullPointerException  {
		
	    Elements links=null; Document doc=null; float price=0;
		
		doc = Jsoup.connect("https://imei24.com/price/"+comp+"/"+model).get();
		links = doc.select("div.container-fluid > div.row > div.col-sm-8 > div.well > div.row >"+
		" div.col-sm-12 > p:contains(Average price for a phone) > b > font[color=\"red\"]:contains(EUR)");	  
	    price= Float.parseFloat(links.first().text().replaceAll("EUR","  ").trim());
				
		return price;
				
	}
}
