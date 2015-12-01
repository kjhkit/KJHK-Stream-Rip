/* @Author: John McCain
 * @Date:7-18-15
 * @Version 0.1 
 * @Description:  Manages the files created by the KJHK Stream Ripper, deleting after 2 weeks.
 * 
 */

import java.io.File;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;


public class StreamRipManager 
{
	public static void main(String[] args) throws InterruptedException
	{
		while(true)
		{
			try
			{
				File folder = new File("Y:/Stream Rip");
				File[] listOfFiles = folder.listFiles();
				
				for(int i=0; i<listOfFiles.length; i++)
				{
					String filename = listOfFiles[i].getName();
					if(filename.substring(0, 4).equals("KJHK"))
					{
						if(TooOld(filename))
						{
							listOfFiles[i].delete();
						}
					}	
				}
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			Thread.sleep(900000);
		}
	}
	
	public static boolean TooOld(String filename)
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd-yy HHmm");
		
		try 
		{
			Date fileDate = dateFormat.parse(filename.substring(5));
			final long DAY_IN_MS = 1000 * 60 * 60 * 24;
			Date compareDate = new Date(System.currentTimeMillis() - (14 * DAY_IN_MS));
			
			return(fileDate.compareTo(compareDate)<0);
		} 
		catch (ParseException e) 
		{
			e.printStackTrace();
		}
		return false;
	}
}
