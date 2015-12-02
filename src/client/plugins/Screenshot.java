package client.plugins;
import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import client.Rs2007;
import javax.imageio.ImageIO;

//
// All Information regarding Screenshots are found here.
//

public class Screenshot {
	
	private static String homePath = System.getProperty("user.home");
	static int mainFrameX = Rs2007.mainFrame.getX();
	static int mainFrameY = Rs2007.mainFrame.getY() + 54;
	static int mainFrameWidth = Rs2007.mainFrame.getWidth();
	static int mainFrameHight = Rs2007.mainFrame.getHeight() - 54;
	
	
	public static void screenshot() throws
    AWTException, IOException, ParseException {
		
	     // capture mainframe
	     BufferedImage screencapture = new Robot().createScreenCapture(
	    		 new Rectangle( mainFrameX, mainFrameY, 
	    				 mainFrameWidth, mainFrameHight ) );

	     	//searches for Old School Runescape folder. If it is not found creates folder.
			File theDir1 = new File(homePath + "/Old School RuneScape");
			if(!theDir1.exists()){
				theDir1.mkdir();
			}
			//Searches for Screenshots Folder within Old School Runescape Folder. If it is not found creates folder.
			theDir1 = new File(theDir1 + "/Screenshots");
			if(!theDir1.exists()){
				theDir1.mkdir();
			}
			/**try{
				//SAVES AS PNG, IF folders above are found.
				File file = new File(homePath + "/Old School RuneScape/Screenshots/" +  + ".png");
			    ImageIO.write(screencapture, "png", file);
			}catch(IOException a){
				//If folders cannot be found or can not save file, Prints ln. 
				System.out.println("Failed to save Screenshot.");
			}**/
	  }
}
