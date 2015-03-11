import twitter4j.*;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.util.Map;
import java.util.HashMap;

public class Twit {
	//public static final String HOST = "104.131.150.198:5984";
	//public static final String DB_NAME = "104.131.150.198:5984";
	//public static final String STANBOL = "http://swent1linux.asu.edu:8080/enhancer";
	//public static final String COUCHDB = "http://swent1linux.asu.edu:5984/";
	
	private static String STANBOL;
	private static String COUCHDB;
	private static String MYSQL;
	private static String MYSQLUSER;
	private static String MYSQLPASSWORD;
	
	public static void main(String[] args) {		
	
		Properties properties = new Properties();
		InputStream is = null;
	
  		Twitter twitter = null;
  		Query query = null;
  		QueryResult result = null;

  		Connection dbConn = null;

  		Statement statementMakes = null;
  		Statement statementModels = null;
  		Statement statementAlternates = null;

  		ResultSet resultSetMakes = null;
  		ResultSet resultSetModels = null;
  		ResultSet resultSetAlternates = null;

  		try { 
			is = new FileInputStream("build.properties");
			properties.load(is);
			
			STANBOL = properties.getProperty("stanbol");
			COUCHDB = properties.getProperty("couchdb");
			MYSQL = properties.getProperty("mysql");
			MYSQLUSER = properties.getProperty("mysql-user");
			MYSQLPASSWORD = properties.getProperty("mysql-password");
			
			System.out.println(STANBOL);
			System.out.println(COUCHDB);
			System.out.println(MYSQL);
			System.out.println(MYSQLUSER);
			System.out.println(MYSQLPASSWORD);
			
   			Class.forName("com.mysql.jdbc.Driver").newInstance();
			dbConn = DriverManager.getConnection(MYSQL, MYSQLUSER, MYSQLPASSWORD);
   			//dbConn = DriverManager.getConnection("jdbc:mysql://localhost/testGM", "root", "");
   			//dbConn = DriverManager.getConnection("jdbc:mysql://localhost/testGM", "root", "digiocean2@");
			
   			statementMakes = dbConn.createStatement();
   			resultSetMakes = statementMakes.executeQuery("SELECT * FROM filterquerymakes WHERE filterid = 0");

   			statementModels = dbConn.createStatement();
   			resultSetModels = statementModels.executeQuery("SELECT * FROM filterquerymodels");

   			statementAlternates = dbConn.createStatement();
   			resultSetAlternates = statementAlternates.executeQuery("SELECT * FROM filterqueryalternates");
  		}
  		catch(Exception e) {
  			e.printStackTrace();
  		}
		finally {
			if(is != null){
				try {
					is.close();
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		}
 
  		List<Status> tweets = new ArrayList<Status>();

		try {
			// MongoClient mongoClient = new MongoClient("localhost", 27017);
		    TwitterStream stream = new TwitterStreamFactory().getInstance();
		    StatusListener listener = new StatusListener() {
		    	int count = 0;
		    	
		    	public void onStatus(Status status) {
		    		// TODO
		    		System.out.println("#: " + ++count + " @" + status.getUser().getScreenName() + ": " + status.getText());
		    		// NNED TO PARSE STUFF HERE
		    		// DONT KEEP IF:
		    		// beings with "http://" or "www" sean: regex is only removing them from text right now
					
					
					// sean: I'm not very good with regular expressions so someone might want to fix this mess
					String regexAtUser = "@\\w+";
					String regexBreakLines = "[(\\r)(\\n)]";
					String regexLink = "(\\S+\\.(\\w+)(\\/\\S+)?)";
					String regexRemaining = "[^a-zA-Z0-9.,!\\s]";
					boolean ignoreTweet = false;
		    		// removes start with "@" sean: regex covers this
		    		// remove all non-letter characters sean: regex covers this
					
					String text = status.getText();
					
					Pattern p = Pattern.compile(regexAtUser);
					Matcher m = p.matcher(text); 
					text = m.replaceAll("");
					p = Pattern.compile(regexBreakLines);
					m = p.matcher(text); 
					text = m.replaceAll("");
					p = Pattern.compile(regexLink);
					if( !text.equals(m.replaceAll("")) ) ignoreTweet = true;
					
					
					m = p.matcher(text); 
					text = m.replaceAll("");
					p = Pattern.compile(regexRemaining);
					m = p.matcher(text); 
					text = m.replaceAll("");
					
					
					String[] textArray = text.split(" ");
					Map<String, Integer> wordCount = new HashMap<>();
					for (String word: textArray) {
						if (wordCount.containsKey(word)) {
							wordCount.put(word, wordCount.get(word) + 1);
						} else {
							wordCount.put(word, 1);
						}
					}
					
					
					
		    		JSONObject injectObj = new JSONObject();

		    		boolean hasLocation = false;
		    		String location = "";

		    		if(status.getGeoLocation() != null)
		    		{
		    			location = status.getGeoLocation().toString();
		    			hasLocation = true;
		    		}
		    		String time = status.getCreatedAt().toString();
					Long timeLong = status.getCreatedAt().getTime();
		    		String numFaves = String.valueOf(status.getFavoriteCount());
		    		String numRetweets = String.valueOf(status.getRetweetCount());
		    		String length = String.valueOf(status.getText().length());
					String language = String.valueOf(status.getLang());
					StanbolConnection sc = new StanbolConnection(STANBOL);
					double sentiment = sc.singleSentiment(text);
					
					if(sentiment == 404) ignoreTweet = true;
					
					if("en".equals(language) && !ignoreTweet){
						try {
							//injectObj.put("source", String.valueOf(status.getSource()));
							injectObj.put("text", text);
							if(hasLocation) {
								injectObj.put("location", location);
							}
							injectObj.put("time", time);
							injectObj.put("timeLong", timeLong);
							//injectObj.put("numFaves", numFaves);
							//injectObj.put("numRetweets", numRetweets);
							//injectObj.put("length", length);
							for (Map.Entry<String, Integer> entry: wordCount.entrySet()) {
								injectObj.put(entry.getKey(), entry.getValue());
							}  
							
							injectObj.put("sentiment", sentiment);
							//sean: it's only injecting one at time right now, will change this later
							CouchConnection cc = new CouchConnection(COUCHDB, "gm/");						
							cc.createDocuments(injectObj, false);    		
							
							count++;
							
						if(count == 50) System.exit(0);
						}
						catch(JSONException je) {
							je.printStackTrace();
						}
						for(int i = 0; i < 30; ++i)System.out.println("");
						System.out.print("OBJECT: " + injectObj.toString());
						for(int i = 0; i < 30; ++i)System.out.println("");
					}
					
					
		    	}

		    	public void onStallWarning(StallWarning warning) {
		    		System.out.println("StallWarning: " + warning);
		    	}

		    	public void onScrubGeo(long userId, long upToStatusId) {
	                System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
	            }

	            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
	                System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
	            }

	            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
	                System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
	            }

	            public void onException(Exception ex) {
	                ex.printStackTrace();
	            }
		    };
		    //listener.filter("#chevy OR #generalmotors OR #gmc OR #buick");
		    stream.addListener(listener);

			// Filter
		    FilterQuery filter = new FilterQuery();
		    ArrayList<String> paramsAsList = new ArrayList<String>();

		    // Add makes to filter list
		    ArrayList<String> makes = new ArrayList<String>();
		    while(resultSetMakes.next()) {
		    	String[] splitMakes = resultSetMakes.getString("makes").split(",");
				for(int i = 0; i < splitMakes.length; i++) {
					makes.add(splitMakes[i]);
				}
		    } 

		    // Add models to filter list
		    ArrayList<String> models = new ArrayList<String>();
			while(resultSetModels.next()) {
		    	String[] splitModels = resultSetModels.getString("models").split(",");
				for(int i = 0; i < splitModels.length; i++) {
					models.add(splitModels[i]);
				}
		    } 

		    // Add altenates to filter list
		    ArrayList<String> alternates = new ArrayList<String>();
			while(resultSetAlternates.next()) {
				String[] splitAlternates = resultSetAlternates.getString("alternates").split(",");
				for(int i = 0; i < splitAlternates.length; i++) {
			    	alternates.add(splitAlternates[i]);
				}
		    } 

		    for(String make : makes) {
		    	paramsAsList.add(make);
		    }

		    for(String model : models) {
		    	paramsAsList.add(model);
		    }
		    for(String alternate : alternates) {
		    	paramsAsList.add(alternate);
		    }
		    
		    String[] filterTags = paramsAsList.toArray(new String[paramsAsList.size()]);
		    for(String tag : filterTags) {
		    	System.out.println(tag);
		    }
		    filter.track(filterTags);
		    stream.filter(filter);
		}
		catch(Exception e) {
			e.printStackTrace();
			System.out.println("Gathered: " + tweets.size() + " tweets..");
			System.exit(0);
		}
		
	}
}
