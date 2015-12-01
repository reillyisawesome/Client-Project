package client;
import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

import javax.swing.*;

import java.io.IOException;
import java.text.ParseException;

public class Rs2007 extends JFrame implements AppletStub {

	private static final long serialVersionUID = 1L;
	private static final HashMap<String, String> parameters = new HashMap<String, String>();
	private static Class<?> clientClass;
	private static Object client;
	private static String homePath = System.getProperty("user.home");
	static JFrame mainFrame;
	//private JFrame worldMapFrame;
	private JPanel loadingPanel, gamePanel;
	private ImageIcon icon;
	private Point start;
	private int x, y;
	private String worldID, onTop, resize;
	//private boolean worldMapInitiated = false;
	private boolean gameInitiated = false;
	private boolean alwaysOnTop = false;
	private boolean resizable = false;
	private ClassLoader classLoader;
	
	public Rs2007(){
		mainFrame = new JFrame("Old School RuneScape");
		mainFrame.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		mainFrame.setResizable(resizable);
		icon = new ImageIcon(this.getClass().getResource("/images/icon/icon.png"));
		confirmOnClose();
		loadSettings();
		buildMenuBar();
		boolean selectedWorld = false;
		boolean startGame = false;
		while(!selectedWorld){
			selectedWorld = true;
			worldID = JOptionPane.showInputDialog(mainFrame, "OldSchool server: ", "Server select.", JOptionPane.PLAIN_MESSAGE);
			String pattern = "[0-9]{1,3}";
			try{
				if(worldID.matches(pattern)){
					if(Integer.parseInt(worldID) > 300){
						worldID = Integer.parseInt(worldID) - 300 + "";
					}
					if(Integer.parseInt(worldID) > 0 && Integer.parseInt(worldID) < 6 ||
							Integer.parseInt(worldID) > 8 && Integer.parseInt(worldID) < 95){
						startGame = true;
					}else{
						selectedWorld = true;
						int result = JOptionPane.showConfirmDialog(null,
								new JLabel("World does not exist. Try again?", JLabel.CENTER),
								"Invalid world.",
								JOptionPane.YES_NO_OPTION,
								JOptionPane.PLAIN_MESSAGE,
								null);
						if (result == JOptionPane.YES_OPTION) {
							selectedWorld = false;
						}
					}
				}else{
					selectedWorld = true;
					int result = JOptionPane.showConfirmDialog(null,
							new JLabel("You may only enter numbers, up to a maximum of 3. Try again?", JLabel.CENTER),
							"Invalid world.",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.PLAIN_MESSAGE,
							null);
					if (result == JOptionPane.YES_OPTION) {
						selectedWorld = false;
					}
				}
			}catch(Exception e1){}
		}
		if(startGame){
			loadingScreen();
			parse();
			buildGame();
		}else{
			System.exit(0);
		}
	}
	
	
	
	private void confirmOnClose(){
		mainFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				int result = JOptionPane.showConfirmDialog(mainFrame,
						new JLabel("Are you sure you want to exit the application?", JLabel.CENTER),
						"Exit.",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.PLAIN_MESSAGE,
						null);
				if (result == JOptionPane.YES_OPTION) {
					saveAndExit();
				}
			}
		});
	}
	private void saveAndExit(){
		if (gameInitiated){
			Dimension screenSize = getToolkit().getScreenSize();
			Insets scnMax = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
			int taskBarSize = scnMax.bottom;
			int screenWidth = screenSize.width;
			int screenHeight = screenSize.height;
			x = mainFrame.getX();
			y = mainFrame.getY();
			if(x < 0){
				x = 0;
			}
			if(x > screenWidth - mainFrame.getWidth()){
				x = screenWidth - mainFrame.getWidth();
			}
			if(y < 0){
				y = 0;
			}
			if(y > screenHeight - taskBarSize - mainFrame.getHeight()){
				y = screenHeight - taskBarSize - mainFrame.getHeight();
			}
		}
		if (resizable){
			resize = "true";
		}else{
			resize = "false";
		}
		if (alwaysOnTop){
			onTop = "true";
		}else{
			onTop = "false";
		}
		File theDir = new File(homePath + "/Old School RuneScape");
		if(!theDir.exists()){
			theDir.mkdir();
		}
		theDir = new File(theDir + "/Settings");
		if(!theDir.exists()){
			theDir.mkdir();
		}
		try{
			FileWriter fw = new FileWriter(homePath + "/Old School RuneScape/Settings/Settings.txt");
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter write = new PrintWriter(bw);
			write.println(x);
			write.println(y);
			write.println(alwaysOnTop);
			write.println(resizable);
			write.close();
			System.out.println("(X.Y) set to (" + x + "."  + y + "), and client on top saved as " + onTop + ". Resizable is "+ resize +".");
		}catch(IOException a){
			System.out.println("Failed to save settings.");
		}
		System.exit(0);
	}
	private void loadSettings(){
		try{
			FileReader fr = new FileReader(homePath + "/Old School RuneScape/Settings/Settings.txt");
			BufferedReader read = new BufferedReader(fr);
			x = Integer.parseInt(read.readLine());
			y = Integer.parseInt(read.readLine());
			onTop = read.readLine();
			resize = read.readLine();
			read.close();
			System.out.println("(X.Y) set to (" + x + "."  + y + "), and client on top set as " + onTop + ". Resizable is "+ resize +".");
		}
		catch(IOException e2){
			x = 0;
			y = 0;
			onTop = "false";
			resize = "false";
			System.out.println("No settings found.");
			System.out.println("(X.Y) set to (0.0), and client on top is set to false. Resizeable is set to false.");
		}
		if(onTop.equals("true")){
			mainFrame.setAlwaysOnTop(true);
			alwaysOnTop = true;
		}
		//if(resize.equals("true")){
		//	mainFrame.setResizable(true);
		//	resizable = true;
		//}
	}
	private void buildMenuBar(){
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("Menu");
		JMenu resizeMenu = new JMenu("Resizeable?");
		JMenu onTopMenu = new JMenu("Always on top?");
		JMenu siteMenu = new JMenu("Homepage");
		//JMenu mapMenu = new JMenu("Map");
		JRadioButtonMenuItem resizeon = new JRadioButtonMenuItem("On");
		JRadioButtonMenuItem resizeoff = new JRadioButtonMenuItem("Off");
		if(resizable){
			resizeon.setSelected(true);
		}else{
			resizeoff.setSelected(true);
		}
		JRadioButtonMenuItem onTopOn = new JRadioButtonMenuItem("On");
		JRadioButtonMenuItem onTopOff = new JRadioButtonMenuItem("Off");
		if(alwaysOnTop){
			onTopOn.setSelected(true);
		}else{
			onTopOff.setSelected(true);
		}
		JMenuItem exitItem = new JMenuItem("Exit");
		JMenuItem screenShot = new JMenuItem("View Screenshots");
		JMenuItem siteMain = new JMenuItem("Main page");
		JMenuItem sitePolls = new JMenuItem("Polls");
		JMenuItem siteForums = new JMenuItem("Forums");
		JMenuItem siteHiscores = new JMenuItem("Hiscores");
		JMenuItem sitePlayerLookup = new JMenuItem("Look up player");
		//JMenuItem mapMenuItem = new JMenuItem("World map");
		JButton shotMenu = new JButton("Screenshot");
		JMenuItem about = new JMenuItem("About");
		//Resize Group
		ButtonGroup resizeGroup = new ButtonGroup();
		resizeGroup.add(resizeon);
		resizeGroup.add(resizeoff);
		resizeMenu.add(resizeon);
		resizeMenu.add(resizeoff);
		//On top Menu
		ButtonGroup onTopGroup = new ButtonGroup();
		onTopGroup.add(onTopOn);
		onTopGroup.add(onTopOff);
		onTopMenu.add(onTopOn);
		onTopMenu.add(onTopOff);
		
		fileMenu.add(screenShot);
		fileMenu.add(onTopMenu);
		fileMenu.add(resizeMenu);
		fileMenu.add(about);
		fileMenu.add(exitItem);
		siteMenu.add(siteMain);
		siteMenu.add(sitePolls);
		siteMenu.add(siteForums);
		siteMenu.add(siteHiscores);
		siteMenu.add(sitePlayerLookup);
		//mapMenu.add(mapMenuItem);
		menuBar.add(fileMenu);
		menuBar.add(siteMenu);
		//menuBar.add(mapMenu);
		menuBar.add(shotMenu);
		mainFrame.setJMenuBar(menuBar);
		
		shotMenu.setSize(12,12);
		shotMenu.setIcon(new ImageIcon(this.getClass().getResource("/images/icon/camera.png")));
		screenShot.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().open(new File(homePath + "/Old School RuneScape/Screenshots/"));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		shotMenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Screenshot.screenshot();
				} catch (AWTException | IOException | ParseException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		resizeon.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainFrame.setResizable(true);
				resizable = true;
				mainFrame.setPreferredSize(new Dimension(765, 580));
				mainFrame.pack();
			}
		});
		
		resizeoff.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainFrame.setResizable(false);
				resizable = false;
				mainFrame.setPreferredSize(new Dimension(765, 558));
				mainFrame.pack();
			}
		});
		
		onTopOn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainFrame.setAlwaysOnTop(true);
				alwaysOnTop = true;
				mainFrame.pack();
			}
		});
		
		onTopOff.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mainFrame.setAlwaysOnTop(false);
				alwaysOnTop = false;
				mainFrame.pack();
			}
		});
		exitItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int exit = JOptionPane.showConfirmDialog(mainFrame,
						new JLabel("Are you sure?", JLabel.CENTER),
						"Exit.",
						JOptionPane.YES_NO_OPTION,
						JOptionPane.PLAIN_MESSAGE,
						null);
				if (exit == JOptionPane.YES_OPTION) {
					saveAndExit();
				}
			}
		});

		siteMain.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					java.awt.Desktop.getDesktop().browse(java.net.URI.create("http://oldschool.runescape.com/oldschool_index"));
				}catch (Exception e1){
					System.out.println(e1);
				}
			}
		});
		sitePolls.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					java.awt.Desktop.getDesktop().browse(java.net.URI.create("http://services.runescape.com/m=poll/oldschool/index.ws"));
				}catch (Exception e1){
					System.out.println(e1);
				}
			}
		});
		siteForums.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					java.awt.Desktop.getDesktop().browse(java.net.URI.create("http://services.runescape.com/m=forum/forums.ws#group63"));
				}catch (Exception e1){
					System.out.println(e1);
				}
			}
		});
		siteHiscores.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					java.awt.Desktop.getDesktop().browse(java.net.URI.create("http://services.runescape.com/m=hiscore_oldschool/overall.ws"));
				}catch (Exception e1){
					System.out.println(e1);
				}
			}
		});
		sitePlayerLookup.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String rsn = JOptionPane.showInputDialog(mainFrame, "RuneScape Name: ", "Input username", JOptionPane.PLAIN_MESSAGE);
				String pattern= "[0-9A-Za-z][0-9A-Za-z _-]{1,11}";
				rsn = rsn.replace(" ", "_").replace("-", "_");
				try{
					if(rsn.matches(pattern)){
						try{
							java.awt.Desktop.getDesktop().browse(java.net.URI.create("http://services.runescape.com/m=hiscore_oldschool/hiscorepersonal.ws?user1=" + rsn));
						}catch (Exception e1){
							System.out.println(e1);
						}
					}else{
						JOptionPane.showMessageDialog(null, String.format("<html><center>Usernames must begin with 0-9 or a-Z, <br>can only contain characters 0-9, a-Z, spaces, underscores, and hyphens, <br>and may be a maximum of 12 characters long.</center></html>"), "Invalid username.", JOptionPane.PLAIN_MESSAGE);
					}
				}catch(Exception e1){}
			}
		});
		/*mapMenuItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				buildWorldMap();
			}
		});*/

	}
	
	
	private void loadingScreen(){
		ImageIcon loadingIcon = new ImageIcon(this.getClass().getResource("/images/loading/loading.gif"));
		JLabel loadingLabel = new JLabel();
		loadingLabel.setIcon(loadingIcon);
		loadingPanel = new JPanel(new GridBagLayout());
		loadingPanel.setMinimumSize(new Dimension(407, 305));
		loadingPanel.setMaximumSize(new Dimension(407, 305));
		loadingPanel.add(loadingLabel);
		mainFrame.getContentPane().add(loadingPanel);
		mainFrame.pack();;
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setVisible(true);
		start = mainFrame.getLocationOnScreen();
	}
	/*private void buildWorldMap(){
		if (!worldMapInitiated){
			worldMapFrame = new JFrame("World Map");
			worldMapFrame.setIconImage(icon.getImage());
			JPanel worldMapPanel = new JPanel(new BorderLayout());
			JScrollPane worldMapScrollPane = new JScrollPane();
			ImageIcon worldMapIcon = new ImageIcon(this.getClass().getResource("/images/map/world.png"));
			worldMapScrollPane.setViewportView(new JLabel(worldMapIcon));
			worldMapScrollPane.getVerticalScrollBar().setUnitIncrement(20);
			worldMapPanel.add(worldMapScrollPane);
			worldMapFrame.add(worldMapPanel, BorderLayout.CENTER);
			worldMapFrame.setSize(new Dimension(500, 350));
			worldMapFrame.setVisible(true);
			worldMapFrame.setLocationRelativeTo(null);
		}else{
			worldMapFrame.setVisible(true);
		}
		worldMapInitiated = true;
	}*/
	private void parse() {
		boolean loaded = false;
		try {
			URL url = new URL("http://oldschool" + worldID + ".runescape.com/plugin.js?param=o0,a1,s0");
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
			String raw;
			String param_name = null;
			String value = null;
			while ((raw = br.readLine()) != null) {
				loaded = true;
				if (!raw.contains("param name") || !raw.contains("value") || raw.contains("haveie6"))
					continue;
				raw = raw.replace("(", "");
				raw = raw.replace(")", "");
				raw = raw.replaceAll("document.write", "").replaceAll("\"", "").replaceAll("'", "").replaceAll("<", "").replaceAll(">", "").replaceAll(";", "");
				if (raw.indexOf("param name") != -1 && raw.indexOf("value") != -1) {
					param_name = raw.trim().substring("param_name=".length(), raw.indexOf("value") - 2);
					raw = raw.substring(raw.indexOf(param_name) + (param_name.length() + 1), raw.length());
					value = raw.trim().substring("value=".length(), raw.length());
					parameters.put(param_name, value);
				}
			}
			parameters.put("haveie6", "0");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		if (!loaded){
			JOptionPane.showMessageDialog(null, "Error connecting to the server.");
		}
	}
	public void buildGame(){
		try{
			String loaderURL = "http://oldschool" + worldID +".runescape.com/gamepack_7537067.jar";
			classLoader = new URLClassLoader(new URL[] { new URL(loaderURL) });
			clientClass = classLoader.loadClass("client");
			client = clientClass.newInstance();
		}catch (Exception e){
			System.out.println(e);
		}
		Applet applet = (Applet) (client);
		applet.setStub(this);
		applet.init();
		applet.start();

		gamePanel = new JPanel(new BorderLayout());
		gamePanel.setMinimumSize(new Dimension(765, 503));
		mainFrame.setMinimumSize(new Dimension(765, 558));
		mainFrame.remove(loadingPanel);
		gamePanel.add(applet);
		mainFrame.getContentPane().add(gamePanel, BorderLayout.CENTER);
		mainFrame.pack();
		if (start.equals(mainFrame.getLocationOnScreen())){
			mainFrame.setLocation(x, y);
		}
		gameInitiated = true;
	}

	public static void main(String[] args) {
		new Rs2007();
	}

	public void appletResize(int width, int height) {}
	public final URL getCodeBase() {
		try {
			return new URL("http://oldschool" + worldID + ".runescape.com/");
		} catch (Exception e) { return null; }
	}
	public final URL getDocumentBase() {
		return getCodeBase();
	}
	public final String getParameter(String name) {
		return parameters.get(name);
	}
	public final AppletContext getAppletContext() {
		return null;
	}
	
}