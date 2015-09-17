public class RouteOptimizerTest
{
	public static void main(String args[])throws Exception
	{
		RouteOptimizer optimizer = new RouteOptimizer();
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

	}
}