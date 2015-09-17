// Implemented for two routes only now. Will modify accordingly.
//Please check the mail for the kind of input you should give.

import java.io.*;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


public class RouteOptimizer
{
	
	private String[] Origins;
	private String[] Destinations; 
	private final String API_KEY = "AIzaSyCUD_jTqsuwlZTk5XzQbnoV4aYJw6moqjE";
	private final String USER_AGENT = "Mozilla/5.0";
	private double[] Overlapping; 
	private double[] Deviation;
	private double sumOfABCD;
	private JSONParser parser;
	private double[] total_distArray;
	private int counter;

	public RouteOptimizer()
	{
		Origins = new String[2];
		Destinations = new String[2];
		//Waypoints = null;
		Overlapping = new double[4];
		Deviation = new double[4];
		parser = new JSONParser();
		total_distArray = new double[4];
		counter = 0;		
	}

	/*public static void main(String args[]) throws Exception
	{		
		boolean proceed = true; // Will continue till the number of routes get over.
		
		while(proceed) 
		{
			RouteOptimizer optimize = new RouteOptimizer();
			System.out.println("Populating Addresses");
			optimize.populateAddresses();
			System.out.println("Addresses Populated");
			
			System.out.println("Starting calculations for all the routes");
			optimize.calcAllRoutes();			
			System.out.println("All calculations done!");
			System.out.println("YAY!");
			optimize.showMeDetails();
			optimize.chooseRoute();

			proceed = false;			
		}
	}*/



	public void showMeDetails()
	{
		System.out.println("Total Distance = "+sumOfABCD);
		System.out.println("OVERLAPPING for all routes");

		for (double i :Overlapping )
			System.out.print(i+"\t");
		System.out.println();
		System.out.println();
		System.out.println("DEVIATION for all routes");

		for(int i =0; i< Deviation.length; i++)
		{
			Deviation[i] = sumOfABCD - total_distArray[i];
		}
		for (double i :Deviation )
			System.out.print(i+"\t");
		System.out.println();
		System.out.println("------------------AND ALRIGHT!--------------");

	}

	public void calcAllRoutes() throws Exception
	{
		System.out.println("Inside calcAllRoutes");
		//A--C--B--D
		eachRoute(1,'C','B');
		//A--C--D--B
		eachRoute(2,'C','D');
		//C--A--B--D
		eachRoute(3,'A','B');
		//C--A--D--B
		eachRoute(4,'A','D');

	}

	public void eachRoute(int no,char a, char b) throws Exception 
	{
		

		System.out.println("Route "+no+" starting");
		String[] tempWaypoints = getWayPointsArr(a,b);
		for(String i: tempWaypoints)
			System.out.print(i+"\t");
		System.out.println();
		String response = getResponse(Origins[0].replace(' ','+'),Destinations[1].replace(' ','+'),tempWaypoints);
		decodeJSON(no,response);
	}

	public void decodeJSON(int route_number,String json)
	{
		
		long total_distance = 0;
        long overlapping = 0;
        try
        {
            Object main_obj = parser.parse(json);
            JSONObject object = (JSONObject)main_obj;
            JSONArray routes = (JSONArray)object.get("routes");
            JSONObject routes_object = (JSONObject)routes.get(0);
            JSONArray legs = (JSONArray)routes_object.get("legs");
            for(int i =0; i<legs.size(); i++)
            {
            	JSONObject legs_i = (JSONObject)legs.get(i);
                JSONObject distance = (JSONObject)legs_i.get("distance");
                //System.out.println(distance.toString());
                Long leg_distance = (Long)distance.get("value");
                total_distance += leg_distance;

                if(i == 1)
                    overlapping = leg_distance;
                if((route_number == 2 && i == 1) || (route_number == 3 && i==1))
                	sumOfABCD += (double)leg_distance;
            }
            total_distArray[counter++] = total_distance; 
        }
        catch(ParseException pe)
        {
            System.out.println("position: " + pe.getPosition());
            System.out.println(pe);
        }
        System.out.println("Total DISTANCE FOR ROUTE "+route_number+" = "+total_distance);
        Overlapping[route_number-1] = (double)overlapping;
        Deviation[route_number-1] = sumOfABCD - (double)total_distance;

	}
	public String[] getWayPointsArr(char a,char b)
	{
		String[] local = new String[2];
		if(a == 'A')
			local[0] = Origins[0];
		else if(a == 'B')
			local[0] = Destinations[0];
		else if(a == 'C')
			local[0] = Origins[1];
		else if(a == 'D')
			local[0] = Destinations[1];

		if(b == 'A')
			local[1] = Origins[0];
		else if(b == 'B')
			local[1] = Destinations[0];
		else if(b == 'C')
			local[1] = Origins[1];
		else if(b == 'D')
			local[1] = Destinations[1];

		return local;
		
	}	
	
	
	public void populateAddresses()
	{
		Scanner scan = new Scanner(System.in);
		System.out.println("Enter Origin of AB");
		Origins[0] = scan.nextLine();
		System.out.println("Enter Destination of AB");
		Destinations[0] = scan.nextLine();
		System.out.println("Enter Origin of CD");
		Origins[1] = scan.nextLine();
		System.out.println("Enter Destination of CD");
		Destinations[1] = scan.nextLine();	
		for (String i :Origins )
			System.out.print(i+"\t");
		System.out.println();

		for (String i :Destinations )
			System.out.print(i+"\t");
		System.out.println();
	}


	public String getResponse(String origin,String dest,String[] waypts) throws Exception
	{
		String waypoints = build_waypoints(waypts);
		//System.out.println("Waypoints String "+waypoints);
		String url = "https://maps.googleapis.com/maps/api/directions/json?origin="+origin+"&destination="+dest+"&waypoints=optimize:true|"+waypoints+"&key="+API_KEY;
		String response = makeRequest(url);
		return response;
	}


	public String build_waypoints(String[] waypts)
	{
		String waypoints = waypts[0].replace(' ','+');
		for(int i = 1; i < waypts.length; i++)
		{
			waypts[i] = waypts[i].replace(' ','+');
			waypoints += "|"+waypts[i];
		}
		return waypoints;
	}


	public String makeRequest(String url) throws Exception
	{
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", USER_AGENT);
		int responseCode = con.getResponseCode();
		System.out.println("-----------------------");
		System.out.println("Sending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);
		System.out.println("--------------------------------");
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		return response.toString();
	}

	public void chooseRoute()
	{
		double min = Deviation[0];
		int pos = 0;
		for(int i=0; i < Deviation.length; i++ )
			if(Deviation[i] < min)
			{
				min = Deviation[i];
				pos = i;
			}
		System.out.println("Chosen route is"+ (pos+1));
		int route_number = pos+1;
		System.out.println("Overlapping for the chosen route is "+Overlapping[pos]);
		System.out.println("Deviation for the chosen route is "+Deviation[pos]);

		if(route_number == 1)
			System.out.println(Origins[0]+"----->"+Origins[1]+"----->"+Destinations[0]+"----->"+Destinations[1]);
		else if(route_number == 2)
			System.out.println(Origins[0]+"----->"+Origins[1]+"----->"+Destinations[0]+"----->"+Destinations[1]);
		else if(route_number == 3)
			System.out.println(Origins[1]+"----->"+Origins[0]+"----->"+Destinations[0]+"----->"+Destinations[1]);
		else if(route_number == 4)
			System.out.println(Origins[1]+"----->"+Origins[0]+"----->"+Destinations[1]+"----->"+Destinations[0]);
	}		
}
	
