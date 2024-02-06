package myPackages;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonObject;


public class MyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MyServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		// API setup
		String apiKey = "d84cb3a62c4320755db3239434a284c6";
		
		// Get city from the input
		String city = request.getParameter("city");
		
		// Setting API URl
		String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q="+city+"&appid="+apiKey;
		
		// API integration
		URL url = new URL(apiUrl);
	    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	    
	    // Set the request method to GET
	    connection.setRequestMethod("GET");
	    
	    // Reading the data from network
	    InputStream inputStream = connection.getInputStream();
	    InputStreamReader reader = new InputStreamReader(inputStream);

	    // Want to store in string
	    StringBuilder responseContent = new StringBuilder();
	    
	    // To get the input from reader, will create scanner object
	    Scanner sc = new Scanner(reader);
	    while(sc.hasNext()) {
	    	responseContent.append(sc.nextLine());
	    }
	    sc.close();
	    
	    //TypeCasting = Parsing the data into JSON
	    Gson gson = new Gson();
	    JsonObject jsonObject = gson.fromJson(responseContent.toString(), JsonObject.class);
	    
	    //Date & Time
        long dateTimeStamp = jsonObject.get("dt").getAsLong() * 1000;
        String date = new Date(dateTimeStamp).toString();
        
        //Temperature
        double temperatureKelvin = jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
        int temperatureCelsius = (int) (temperatureKelvin - 273.15);
       
        //Humidity
        int humidity = jsonObject.getAsJsonObject("main").get("humidity").getAsInt();
        
        //Wind Speed
        double windSpeed = jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();
        
        //Weather Condition
        String weatherCondition = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString();
        
        // Set the data as request attributes (for sending to the jsp page)
        request.setAttribute("date", date);
        request.setAttribute("city", city);
        request.setAttribute("temperature", temperatureCelsius);
        request.setAttribute("weatherCondition", weatherCondition); 
        request.setAttribute("humidity", humidity);    
        request.setAttribute("windSpeed", windSpeed);
        request.setAttribute("weatherData", responseContent.toString());
        
        connection.disconnect();
        
        // Forward the request to the weather.jsp page for rendering
        request.getRequestDispatcher("index.jsp").forward(request, response);
        
	}

}
