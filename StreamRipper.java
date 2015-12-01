/* @Author: John McCain
 * @Date:7-18-15
 * @Version 0.1 
 * @Description:  Records KJHK mp3 stream at 128kbs in one hour increments.  Labels and manages files.
 * 
 */

import java.net.URLConnection;
import java.net.URL;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;


public class StreamRipper 
{
	static boolean firstHour;
	static DateFormat dateFormat = new SimpleDateFormat("MM-dd-yy HHmm");
	static DateFormat topOfHourFormat = new SimpleDateFormat("mm");

	public static void main(String[] args)
	{
		final long MINUTE_IN_MS = 1000 * 60 * 60;
		boolean errorOccurred = false;
		Date errorDate = new Date();

		while(true) //Master while loop, should only loop if there is an error
		{
            System.out.println("Starting the KJHK Stream Ripper...");

            //Used to cut first export file short as to start on hour
			firstHour = true;
			
			if(errorOccurred)
			{
				Date compareDate = new Date(System.currentTimeMillis()-5*MINUTE_IN_MS);
				if(errorDate.compareTo(compareDate)>=0)
				{
					Date nowDate = new Date();
					System.out.println("An error occurred! This has happened recently, Stream Rip will wait 5 minutes before trying again.  TIME: " + dateFormat.format(nowDate));
					try
					{
						Thread.sleep(1000*60*5);
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				else
				{
					System.out.println("An error occurred! We still gucci tho");
				}
				errorDate = new Date();
				errorOccurred = false;
			}
			
			try
			{
				Thread.currentThread().setPriority(Thread.MAX_PRIORITY-2); //Sets priority to just below the maximum (Maximum is real time, which would be bad and starve everything else) (Java priority values range from 1 to 10)
				//Connects to the stream
				URLConnection myConnection = new URL("http://173.45.231.37:8000/stream_low").openConnection();
				
				//Creates input stream to process the stream
				InputStream instream = myConnection.getInputStream();
				
				while(true)
				{
					//Get current date/time for timestamp in filename
					Date date = new Date();
		
					//Set output filename
					String filename = "KJHK" + dateFormat.format(date) + ".mp3";
					String filepath = "Y:/Stream Rip/" + filename;

					//Creates output steam to export the stream to an mp3 file
					OutputStream outstream = new FileOutputStream(new File(filepath));
		
					//Buffer
					byte[] buffer = new byte[16384];
		
					//Stores length of the buffer in bytes
					int len;
		
					//Iterator to limit length of the exported file in bytes.  
					long t = 0;
		
					//Make sure the buffer isn't empty
					while(!(instream.read(buffer)>0))
					{
						//Chill
					}

					Thread.sleep(10);
					
					//Writes the buffer to the output file when the buffer size is>0 and the length of the output file is < the specified limit and it is not both the top of the hour and the first export file in this succesful run
					//Note: To calculate bit limit from time, take 128kbps/8bpB to get the number of Bytes per second.  Multiply by the number of seconds desired. 57600000 is one hour.
					while (((len = instream.read(buffer)) > 0 && ((t+=len)<57600000)) && !(firstHour && isOnHour()))
					{
						Thread.sleep(10);
						outstream.write(buffer, 0, len);
					}
					outstream.close();
					
					System.out.println("Just exported a file. It is " + t + " bytes and the file name is " + filename);
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				errorOccurred = true;
			}
		}
	}


	//Pre: topOfHourFormat is a SimpleDateFormat of style "mm", firstHour is a declared boolean
	//Post: firstHour is set to false if it is the top of the hour
	//Returns: boolean, true if it is the top of the hour, false if it is not
	static boolean isOnHour()
	{
		Date currentTime = new Date();
		if(topOfHourFormat.format(currentTime).equals("00"))
		{
			firstHour = false;
            System.out.println("Exporting a file early in order to start on the hour");
			return true;
		}
		return false;
	}

}
