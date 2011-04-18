//package test;
//
//import java.awt.BorderLayout;
//import java.awt.Color;
//import java.awt.Dimension;
//import java.awt.Graphics;
//import java.awt.Graphics2D;
//import java.awt.GridLayout;
//import java.awt.SystemColor;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.event.InputEvent;
//import java.awt.event.MouseEvent;
//import java.awt.event.MouseListener;
//import java.awt.event.MouseMotionListener;
//import java.awt.image.BufferedImage;
//import java.awt.image.RescaleOp;
//import java.io.File;
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.Arrays;
//import java.util.Calendar;
//
//import javax.imageio.ImageIO;
//import javax.swing.JApplet;
//import javax.swing.JFileChooser;
//import javax.swing.JFrame;
//import javax.swing.JLabel;
//import javax.swing.JMenu;
//import javax.swing.JMenuBar;
//import javax.swing.JMenuItem;
//import javax.swing.JOptionPane;
//import javax.swing.JPanel;
//import javax.swing.JScrollPane;
//import javax.swing.JSlider;
//import javax.swing.JTree;
//import javax.swing.KeyStroke;
//import javax.swing.UIManager;
//import javax.swing.UnsupportedLookAndFeelException;
//import javax.swing.UIManager.LookAndFeelInfo;
//import javax.swing.event.ChangeEvent;
//import javax.swing.event.ChangeListener;
//import javax.swing.event.TreeSelectionEvent;
//import javax.swing.event.TreeSelectionListener;
//import javax.swing.filechooser.FileFilter;
//import javax.swing.filechooser.FileNameExtensionFilter;
//import javax.swing.plaf.SliderUI;
//import javax.swing.tree.DefaultMutableTreeNode;
//import javax.swing.tree.DefaultTreeCellRenderer;
//
//public class GUI extends JApplet implements ActionListener, MouseListener,
//		MouseMotionListener, TreeSelectionListener
//{
//
//	private static final long serialVersionUID = -4710340542609513214L;
//
//	private final double maxBr = 8.0f;
//	private final double lowBr = 0.3f;
//	private final static String folder = "Data";
//
//	private double crtBr = 0.0f;
//
//	public static int speedValue = -299;
//	public static boolean isPlaying = false;
//
//	private String path = null;
//
//	private static String crtFile = "";
//
//	private main.ColorPanel colorPanel = null;
//	private JTree tree = null;
//	private JLabel label = new JLabel("Pos");
//	private JLabel info = new JLabel("Set play speed: ");
//	private BufferedImage image;
//
//	private JPanel top = new JPanel();
//
//	private JPanel leftPanel;// = new JPanel(new BorderLayout(10, 0));
//	private JScrollPane left = null;
//	private JSlider speed = new JSlider(-1000, -25);
//
//	private JFileChooser fileChooser = new JFileChooser();
//
//	private JMenuBar menuBar = new JMenuBar();
//
//	private JMenuItem openAction = null;
//	private JMenuItem saveAction = null;
//	private JMenuItem resetAction = null;
//	private JMenuItem exitAction = null;
//
//	private JMenuItem incBrAction = null;
//	private JMenuItem decBrAction = null;
//	private JMenuItem rotCWAction = null;
//	private JMenuItem rotCCWAction = null;
//	private JMenuItem flipHAction = null;
//	private JMenuItem flipVAction = null;
//	private JMenuItem playAction = null;
//	JPanel content;//  = new JPanel();
//	public GUI()
//	{
//		//super();
//		init();
//	
//
//	}
//
//	private JMenuBar makeMenu()
//	{
//
//		JMenuBar menuBar = new JMenuBar();
//		JMenu fileMenu = new JMenu("File");
//		JMenu imageMenu = new JMenu("Image");
//
//		openAction = new JMenuItem("Open DCM");
//		openAction.setAccelerator(KeyStroke.getKeyStroke(
//				java.awt.event.KeyEvent.VK_O, java.awt.Event.CTRL_MASK));
//
//		saveAction = new JMenuItem("Save image");
//		saveAction.setAccelerator(KeyStroke.getKeyStroke(
//				java.awt.event.KeyEvent.VK_S, java.awt.Event.CTRL_MASK));
//
//		exitAction = new JMenuItem("Exit");
//		exitAction.setAccelerator(KeyStroke.getKeyStroke(
//				java.awt.event.KeyEvent.VK_Q, java.awt.Event.CTRL_MASK));
//
//		openAction.addActionListener(this);
//		saveAction.addActionListener(this);
//		exitAction.addActionListener(this);
//
//		fileMenu.add(openAction);
//		fileMenu.add(saveAction);
//		fileMenu.addSeparator();
//		fileMenu.add(exitAction);
//
//		menuBar.add(fileMenu);
//
//		playAction = new JMenuItem("Play");
//		playAction.setAccelerator(KeyStroke.getKeyStroke(
//				java.awt.event.KeyEvent.VK_P, java.awt.Event.SHIFT_MASK));
//		incBrAction = new JMenuItem("Increase Brightness (+)");
//		incBrAction.setAccelerator(KeyStroke.getKeyStroke(
//				java.awt.event.KeyEvent.VK_EQUALS, java.awt.Event.CTRL_MASK));
//
//		decBrAction = new JMenuItem("Decrease Brightness (-)");
//		decBrAction.setAccelerator(KeyStroke.getKeyStroke(
//				java.awt.event.KeyEvent.VK_MINUS, java.awt.Event.CTRL_MASK));
//
//		rotCWAction = new JMenuItem("Rotate Clockwise (CW)");
//		rotCWAction.setAccelerator(KeyStroke.getKeyStroke(
//				java.awt.event.KeyEvent.VK_L, java.awt.Event.CTRL_MASK));
//
//		rotCCWAction = new JMenuItem("Rotate Counterclockwise (CCW)");
//		rotCCWAction.setAccelerator(KeyStroke.getKeyStroke(
//				java.awt.event.KeyEvent.VK_R, java.awt.Event.CTRL_MASK));
//
//		flipHAction = new JMenuItem("Flip image horizontally");
//		flipHAction.setAccelerator(KeyStroke.getKeyStroke(
//				java.awt.event.KeyEvent.VK_H, java.awt.Event.CTRL_MASK
//						+ java.awt.Event.SHIFT_MASK));
//
//		flipVAction = new JMenuItem("Flip image vertically");
//		flipVAction.setAccelerator(KeyStroke.getKeyStroke(
//				java.awt.event.KeyEvent.VK_V, java.awt.Event.CTRL_MASK
//						+ java.awt.Event.SHIFT_MASK));
//
//		resetAction = new JMenuItem("Reset Image");
//		resetAction.setAccelerator(KeyStroke.getKeyStroke(
//				java.awt.event.KeyEvent.VK_R, java.awt.Event.SHIFT_MASK
//						+ java.awt.Event.CTRL_MASK));
//
//		incBrAction.addActionListener(this);
//		decBrAction.addActionListener(this);
//		rotCWAction.addActionListener(this);
//		rotCCWAction.addActionListener(this);
//		flipHAction.addActionListener(this);
//		flipVAction.addActionListener(this);
//		resetAction.addActionListener(this);
//		playAction.addActionListener(this);
//
//		imageMenu.add(playAction);
//		imageMenu.add(incBrAction);
//		imageMenu.add(decBrAction);
//		imageMenu.add(flipHAction);
//		imageMenu.add(flipVAction);
//		imageMenu.add(rotCWAction);
//		imageMenu.add(rotCCWAction);
//		imageMenu.addSeparator();
//		imageMenu.add(resetAction);
//
//		menuBar.add(imageMenu);
//		return menuBar;
//	}
//
//	public BufferedImage rotate(BufferedImage img, int angle)
//	{
//		int w = img.getWidth();
//		int h = img.getHeight();
//		BufferedImage dimg = new BufferedImage(w, h, img.getType());
//		Graphics2D g = dimg.createGraphics();
//		g.rotate(Math.toRadians(angle), w / 2, h / 2);
//		g.drawImage(img, null, 0, 0);
//		return dimg;
//	}
//
//	private BufferedImage verticalFlip(BufferedImage img)
//	{
//		int w = img.getWidth();
//		int h = img.getHeight();
//		BufferedImage dimg = new BufferedImage(w, h, img.getType());
//		Graphics2D g = dimg.createGraphics();
//		g.drawImage(img, 0, 0, w, h, w, 0, 0, h, null);
//		g.dispose();
//		return dimg;
//	}
//
//	private BufferedImage horizontalflip(BufferedImage img)
//	{
//		int w = img.getWidth();
//		int h = img.getHeight();
//		BufferedImage dimg = new BufferedImage(w, h, img.getColorModel()
//				.getTransparency());
//		Graphics2D g = dimg.createGraphics();
//		g.drawImage(img, 0, 0, w, h, 0, h, w, 0, null);
//		g.dispose();
//		return dimg;
//	}
//
//	private JTree loadJTree()
//	{
//
//		String[] elem = listFile(folder);
//		System.out.println(elem.toString());
//		Arrays.sort(elem);
//
//		DefaultMutableTreeNode root = new DefaultMutableTreeNode("DICOM folder");
//
//		for (String strings : elem)
//		{
//			root.add(new DefaultMutableTreeNode(strings));
//		}
//
//		DefaultTreeCellRenderer rend = new DefaultTreeCellRenderer();
//		JTree arbore = new JTree(root);
//		arbore.setCellRenderer(rend);
//		return arbore;
//
//	}
//
//	private void playPause()
//	{
//
//		Thread animation = new Thread(new PlayRunnable(this.colorPanel,
//				this.folder));
//		if (!isPlaying)
//		{
//
//			if (animation.getState() == Thread.State.NEW)
//			{
//				animation.start();
//			}
//		}
//
//		isPlaying = !isPlaying;
//
//	}
//
//	public static String[] listFile(String path)
//	{
//		File f = new File(path);
//		System.out.println(f.getAbsolutePath());
//		return f.list();
//	}
//
//	public static BufferedImage open(String path)
//	{
//		crtFile = path;
//		try
//		{
//
//			// System.out.println(crtFile);
//
//			return increaseImageBrightness(ImageIO.read(new File(path)), 20.0f);
//
//		} catch (IOException e)
//		{
//			e.printStackTrace();
//		}
//		return null;
//	}
//
//	private static BufferedImage increaseImageBrightness(BufferedImage image,
//			float scaleFactor)
//	{
//		RescaleOp op = new RescaleOp(scaleFactor, 0.0f, null);
//		BufferedImage brighter = op.filter(image, null);
//		return brighter;
//	}
//
//	private BufferedImage decreaseImageBrightness(BufferedImage image,
//			float scaleFactor)
//	{
//		RescaleOp op = new RescaleOp(scaleFactor, 0.0f, null);
//		BufferedImage darker = op.filter(image, null);
//		return darker;
//	}
//
//	private BufferedImage resetImage(String filename)
//	{
//		return open(filename);
//	}
//
//	public void saveImage(BufferedImage img, String ref)
//	{
//		try
//		{
//			ref = "export/" + ref;
//			System.out.println(ref);
//			String format = (ref.endsWith(".png")) ? "png" : "jpg";
//			boolean ok = ImageIO.write(img, format, new File(ref));
//			if (ok)
//			{
//				JOptionPane.showMessageDialog(this, "Image saved in " + ref);
//			}
//		} catch (IOException e)
//		{
//			e.printStackTrace();
//		}
//	}
//
//	// public static void main(String[] args)
//	public void init()
//	{
//
////		try
////		{
////			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
////			{
////				if ("Nimbus".equals(info.getName()))
////				{
////					UIManager.setLookAndFeel(info.getClassName());
////					break;
////				}
////			}
////		} catch (UnsupportedLookAndFeelException e)
////		{
////			// handle exception
////		} catch (ClassNotFoundException e)
////		{
////			// handle exception
////		} catch (InstantiationException e)
////		{
////			// handle exception
////		} catch (IllegalAccessException e)
////		{
////		}
//
//		
//		initialize();
//		this.setSize(1000,1000);
//		this.setContentPane(content);
//		
//		
//		
//	}
//	
//	private void initialize()
//		{crtFile = folder
//			+ "/1.3.12.2.1107.5.1.4.57283.30000010082911211628100002319";
//		
//		String path = folder+ "/1.3.12.2.1107.5.1.4.57283.30000010082911211628100002319";
//		menuBar = makeMenu();
//		
//		// menuBar.addMouseMotionListener(new MouseMotionListener()
//		// {
//		//			
//		// @Override
//		// public void mouseMoved(MouseEvent e)
//		// {
//		// // TODO Auto-generated method stub
//		//				
//		// }
//		//			
//		// @Override
//		// public void mouseDragged(MouseEvent e)
//		// {
//		// menuBar.getParent().getParent().setLocation(e.getXOnScreen(),
//		// e.getYOnScreen());
//		// menuBar.getParent().getParent().update(menuBar.getParent().getParent().getGraphics());
//		// // TODO Auto-generated method stub
//		//				
//		// }
//		// });
//		leftPanel = new JPanel(new BorderLayout(10, 0));
//		this.tree = loadJTree();
//		this.left = new JScrollPane(tree,
//				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
//				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
//		this.left.setPreferredSize(new Dimension(200, 650));
//		this.left.getViewport().add(tree);
//
//		this.image = open(path);
//
//		this.colorPanel = new ColorPanel(image);
//		this.colorPanel.setFocusable(true);
//
//		this.path = path;
//
////		this.setLayout(new BorderLayout());
//
//		this.colorPanel.addMouseMotionListener(this);
//		this.colorPanel.addMouseListener(this);
//
//		this.tree.addTreeSelectionListener(this);
//
//		this.speed.setToolTipText("Set the speed value");
//		this.speed.addChangeListener(new ChangeListener()
//		{
//
//			@Override
//			public void stateChanged(ChangeEvent e)
//			{
//				speedValue = speed.getValue();
//				info.setText("Set play speed:"
//						+ Math.round((double) 1000. / Math.abs(speedValue))
//						+ " | " + Math.abs(speedValue));
//
//			}
//		});
//
//		if (content == null)
//			content  = new JPanel();
////		content.setLayout(new BorderLayout());
//		
//
//		this.leftPanel.add(left, BorderLayout.NORTH);
//		this.leftPanel.add(info, BorderLayout.CENTER);
//		this.leftPanel.add(speed, BorderLayout.SOUTH);
//		
////		menuBar.setPreferredSize(new Dimension(100,10));
////		colorPanel.setPreferredSize(new Dimension(100,100));
////		label.setPreferredSize(new Dimension(20,20));
////		leftPanel.setPreferredSize(new Dimension(200,200));
//		
//	//	content.add(menuBar, BorderLayout.NORTH);
//		//content.add(colorPanel, BorderLayout.CENTER);
////		content.add(label, BorderLayout.SOUTH);
//		//content.add(leftPanel, BorderLayout.WEST);
//
////		this.getContentPane().setPreferredSize(new Dimension(1200, 800));
////		this.getContentPane().setSize(new Dimension(1200, 800));
////		this.setLocation(0, 0);
//
//		// this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//		// this.setTitle("DCM file viewer");
//		// this.setVisible(true);
//
//		// System.out.println(crtFile);
//		//GUI main = new GUI(crtFile);
//		//main.crtBr = 0;
//		// main.setUndecorated(true);
//		// main.setResizable(true);
//		//main.setVisible(true);
////		this.setSize(900, 1000);
//	}
//
//
//	@Override
//	public void actionPerformed(ActionEvent e)
//	{
//		if (e.getSource() == resetAction)
//		{
//			System.out.println("CF " + crtFile);
//			colorPanel.img = this.resetImage(crtFile);
//			this.repaint();
//		}
//		if (e.getSource() == incBrAction)
//		{
//			colorPanel.img = increaseImageBrightness(colorPanel.img, 1.1f);
//			this.repaint();
//		}
//		if (e.getSource() == decBrAction)
//		{
//			colorPanel.img = decreaseImageBrightness(colorPanel.img, 0.9f);
//			this.repaint();
//		}
//		if (e.getSource() == openAction)
//		{
//
//			fileChooser.setDialogTitle("Alege fisierul");
//			FileFilter filtru = new FileNameExtensionFilter("DICOM files",
//					"dicom", "dcm");
//			fileChooser.addChoosableFileFilter(filtru);
//
//			int ret = fileChooser.showDialog(null, "Submit");
//			if (ret == JFileChooser.APPROVE_OPTION)
//			{
//				File f = fileChooser.getSelectedFile();
//				colorPanel.img = open(f.getAbsolutePath());
//				this.repaint();
//
//			}
//		}
//		if (e.getSource() == saveAction)
//		{
//			Calendar cal = Calendar.getInstance();
//			SimpleDateFormat sdf = new SimpleDateFormat("_dd-MM.H.mm.ss");
//
//			saveImage(colorPanel.img, "image" + sdf.format(cal.getTime())
//					+ ".png");
//		}
//
//		if (e.getSource() == rotCCWAction)
//		{
//			colorPanel.img = rotate(colorPanel.img, -90);
//			this.repaint();
//		}
//		if (e.getSource() == rotCWAction)
//		{
//			colorPanel.img = rotate(colorPanel.img, 90);
//			this.repaint();
//		}
//
//		if (e.getSource() == flipHAction)
//		{
//			colorPanel.img = horizontalflip(colorPanel.img);
//			this.repaint();
//		}
//
//		if (e.getSource() == flipVAction)
//		{
//			colorPanel.img = verticalFlip(colorPanel.img);
//			this.repaint();
//		}
//
//		if (e.getSource() == playAction)
//		{
//			playPause();
//		}
//		if (e.getSource() == exitAction)
//		{
//			System.exit(0);
//		}
//
//	}
//
//	@Override
//	public void mouseClicked(java.awt.event.MouseEvent e)
//	{
//		if ((e.getModifiers() & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK)
//		{
//
//			int xBorder = (colorPanel.getWidth() - colorPanel.img.getWidth()) / 2;
//			int yBorder = (colorPanel.getHeight() - colorPanel.img.getHeight()) / 2;
//
//			// g1.setColor(Color.RED);
//			// g1.drawLine(e.getX() , e.getY(), (colorPanel.getWidth() -
//			// colorPanel.img.getWidth())/2,(colorPanel.getHeight() -
//			// colorPanel.img.getHeight())/2);
//			//			
//			// Graphics2D g2 = (Graphics2D) colorPanel.getGraphics();
//			// g2.drawImage(img1, null, 0,0 );
//
//			Graphics2D g = (Graphics2D) colorPanel.img.getGraphics();
//			g.setColor(Color.white);
//			g.drawLine(0, 0, e.getX() - xBorder, e.getY() - yBorder);
//			// colorPanel.img.
//			// colorPanel.repaint();
//			// Graphics2D g2d =(Graphics2D) colorPanel.getGraphics();
//			// g2d.setColor(Color.black);
//			// g2d.fillRect(0, 0, getWidth(), getHeight());
//			// g2d.drawImage(colorPanel.img , null, (getWidth() -
//			// colorPanel.img.getWidth()) / 2,
//			// (getHeight() - colorPanel.img.getHeight()) / 2);
//			//		
//			// g2d.setColor(Color.green);
//			//			
//			// g2d.drawRect((getWidth() - colorPanel.img.getWidth()) / 2,
//			// (getHeight() - colorPanel.img
//			// .getHeight()) / 2, colorPanel.img.getWidth(),
//			// colorPanel.img.getHeight());
//			//			
//			// g2d.drawString("info", 10, 20);
//			//			
//			//			
//			//
//			// g2d.setColor(Color.RED);
//			//			
//			// // Graphics2D g2 =(Graphics2D) colorPanel.getGraphics();
//			// g2d.setColor(Color.RED);
//			//			
//			// g2d.drawLine(e.getX() , e.getY(), (colorPanel.getWidth() -
//			// colorPanel.img.getWidth())/2,(colorPanel.getHeight() -
//			// colorPanel.img.getHeight())/2);
//			// //colorPanel.img = increaseImageBrightness(colorPanel.img, 1.1f);
//
//		}
//
//		if ((e.getModifiers() & InputEvent.BUTTON2_MASK) == InputEvent.BUTTON2_MASK)
//			colorPanel.img = this.resetImage(crtFile);
//		this.repaint();
//	}
//
//	@Override
//	public void mouseEntered(java.awt.event.MouseEvent e)
//	{
//
//	}
//
//	@Override
//	public void mouseExited(java.awt.event.MouseEvent e)
//	{
//
//	}
//
//	@Override
//	public void mousePressed(java.awt.event.MouseEvent e)
//	{
//
//	}
//
//	@Override
//	public void mouseReleased(java.awt.event.MouseEvent e)
//	{
//
//	}
//
//	@Override
//	public void mouseDragged(java.awt.event.MouseEvent e)
//	{
//		if ((e.getModifiers() & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK)
//		{
//			colorPanel.img = increaseImageBrightness(colorPanel.img, 1.1f);
//		}
//		if ((e.getModifiers() & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK)
//			colorPanel.img = decreaseImageBrightness(colorPanel.img, 0.9f);
//
//		this.repaint();
//
//	}
//
//	@Override
//	public void mouseMoved(java.awt.event.MouseEvent e)
//	{
//		label.setText((e.getX() - (colorPanel.getWidth() - colorPanel.img
//				.getWidth()) / 2)
//				+ " "
//
//				+ (e.getY() - (colorPanel.getHeight() - colorPanel.img
//						.getHeight()) / 2) + " | " + e.getX() + " " + e.getY());
//
//	}
//
//	@Override
//	public void valueChanged(TreeSelectionEvent e)
//	{
//		String[] splitted = e.getPath().toString().split(",\\ ");
//		if (splitted.length >= 2)
//		{
//			splitted[1] = splitted[1].substring(0, splitted[1].length() - 1);
//			colorPanel.img = open(folder + "/" + splitted[1]);
//			this.repaint();
//		}
//	}
//
//}

//==========
//
// package main;
//
// import java.awt.BorderLayout;
// import java.awt.Color;
// import java.awt.Dimension;
// import java.awt.Graphics;
// import java.awt.Graphics2D;
// import java.awt.GridLayout;
// import java.awt.SystemColor;
// import java.awt.event.ActionEvent;
// import java.awt.event.ActionListener;
// import java.awt.event.InputEvent;
// import java.awt.event.MouseEvent;
// import java.awt.event.MouseListener;
// import java.awt.event.MouseMotionListener;
// import java.awt.image.BufferedImage;
// import java.awt.image.RescaleOp;
// import java.io.File;
// import java.io.IOException;
// import java.text.SimpleDateFormat;
// import java.util.Arrays;
// import java.util.Calendar;
//
// import javax.imageio.ImageIO;
// import javax.swing.JApplet;
// import javax.swing.JFileChooser;
// import javax.swing.JFrame;
// import javax.swing.JLabel;
// import javax.swing.JMenu;
// import javax.swing.JMenuBar;
// import javax.swing.JMenuItem;
// import javax.swing.JOptionPane;
// import javax.swing.JPanel;
// import javax.swing.JScrollPane;
// import javax.swing.JSlider;
// import javax.swing.JTree;
// import javax.swing.KeyStroke;
// import javax.swing.UIManager;
// import javax.swing.UnsupportedLookAndFeelException;
// import javax.swing.UIManager.LookAndFeelInfo;
// import javax.swing.event.ChangeEvent;
// import javax.swing.event.ChangeListener;
// import javax.swing.event.TreeSelectionEvent;
// import javax.swing.event.TreeSelectionListener;
// import javax.swing.filechooser.FileFilter;
// import javax.swing.filechooser.FileNameExtensionFilter;
// import javax.swing.plaf.SliderUI;
// import javax.swing.tree.DefaultMutableTreeNode;
// import javax.swing.tree.DefaultTreeCellRenderer;
//
// public class GUI extends JApplet implements ActionListener, MouseListener,
// MouseMotionListener, TreeSelectionListener
// {
//
// private static final long serialVersionUID = -4710340542609513214L;
//
// private final double maxBr = 8.0f;
// private final double lowBr = 0.3f;
// private final static String folder = "Data";
//
// private double crtBr = 0.0f;
//
// public static int speedValue = -299;
// public static boolean isPlaying = false;
//
// private String path = null;
//
// private static String crtFile = "";
//
// private ColorPanel colorPanel = null;
// private JTree tree = null;
// private JLabel label = new JLabel("Pos");
// private JLabel info = new JLabel("Set play speed: ");
// private BufferedImage image;
//
// private JPanel top = new JPanel();
//
// private JPanel leftPanel = new JPanel(new BorderLayout(10, 0));
// private JScrollPane left = null;
// private JSlider speed = new JSlider(-1000, -25);
//
// private JFileChooser fileChooser = new JFileChooser();
//
// private JMenuBar menuBar = new JMenuBar();
//
// private JMenuItem openAction = null;
// private JMenuItem saveAction = null;
// private JMenuItem resetAction = null;
// private JMenuItem exitAction = null;
//
// private JMenuItem incBrAction = null;
// private JMenuItem decBrAction = null;
// private JMenuItem rotCWAction = null;
// private JMenuItem rotCCWAction = null;
// private JMenuItem flipHAction = null;
// private JMenuItem flipVAction = null;
// private JMenuItem playAction = null;
//
// public GUI()
// {
// super();
// init();
//	
// String path = folder+
// "/1.3.12.2.1107.5.1.4.57283.30000010082911211628100002319";
// menuBar = makeMenu();
// setJMenuBar(menuBar);
// // menuBar.addMouseMotionListener(new MouseMotionListener()
// // {
// //
// // @Override
// // public void mouseMoved(MouseEvent e)
// // {
// // // TODO Auto-generated method stub
// //
// // }
// //
// // @Override
// // public void mouseDragged(MouseEvent e)
// // {
// // menuBar.getParent().getParent().setLocation(e.getXOnScreen(),
// // e.getYOnScreen());
// //
// menuBar.getParent().getParent().update(menuBar.getParent().getParent().getGraphics());
// // // TODO Auto-generated method stub
// //
// // }
// // });
//
// this.tree = loadJTree();
// this.left = new JScrollPane(tree,
// JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
// JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
// this.left.setPreferredSize(new Dimension(200, 650));
// this.left.getViewport().add(tree);
//
// this.image = open(path);
//
// this.colorPanel = new ColorPanel(image);
// this.colorPanel.setFocusable(true);
//
// this.path = path;
//
// this.setLayout(new BorderLayout());
//
// this.colorPanel.addMouseMotionListener(this);
// this.colorPanel.addMouseListener(this);
//
// this.tree.addTreeSelectionListener(this);
//
// this.speed.setToolTipText("Set the speed value");
// this.speed.addChangeListener(new ChangeListener()
// {
//
// @Override
// public void stateChanged(ChangeEvent e)
// {
// speedValue = speed.getValue();
// info.setText("Set play speed:"
// + Math.round((double) 1000. / Math.abs(speedValue))
// + " | " + Math.abs(speedValue));
//
// }
// });
//
// this.leftPanel.add(left, BorderLayout.NORTH);
// this.leftPanel.add(info, BorderLayout.CENTER);
// this.leftPanel.add(speed, BorderLayout.SOUTH);
//
// this.getContentPane().add(colorPanel, BorderLayout.CENTER);
// this.getContentPane().add(label, BorderLayout.SOUTH);
// this.getContentPane().add(leftPanel, BorderLayout.WEST);
//
// this.getContentPane().(1200, 800);
// this.setLocation(0, 0);
// // this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
// // this.setTitle("DCM file viewer");
// // this.setVisible(true);
//
// }
//
// private JMenuBar makeMenu()
// {
//
// JMenuBar menuBar = new JMenuBar();
// JMenu fileMenu = new JMenu("File");
// JMenu imageMenu = new JMenu("Image");
//
// openAction = new JMenuItem("Open DCM");
// openAction.setAccelerator(KeyStroke.getKeyStroke(
// java.awt.event.KeyEvent.VK_O, java.awt.Event.CTRL_MASK));
//
// saveAction = new JMenuItem("Save image");
// saveAction.setAccelerator(KeyStroke.getKeyStroke(
// java.awt.event.KeyEvent.VK_S, java.awt.Event.CTRL_MASK));
//
// exitAction = new JMenuItem("Exit");
// exitAction.setAccelerator(KeyStroke.getKeyStroke(
// java.awt.event.KeyEvent.VK_Q, java.awt.Event.CTRL_MASK));
//
// openAction.addActionListener(this);
// saveAction.addActionListener(this);
// exitAction.addActionListener(this);
//
// fileMenu.add(openAction);
// fileMenu.add(saveAction);
// fileMenu.addSeparator();
// fileMenu.add(exitAction);
//
// menuBar.add(fileMenu);
//
// playAction = new JMenuItem("Play");
// playAction.setAccelerator(KeyStroke.getKeyStroke(
// java.awt.event.KeyEvent.VK_P, java.awt.Event.SHIFT_MASK));
// incBrAction = new JMenuItem("Increase Brightness (+)");
// incBrAction.setAccelerator(KeyStroke.getKeyStroke(
// java.awt.event.KeyEvent.VK_EQUALS, java.awt.Event.CTRL_MASK));
//
// decBrAction = new JMenuItem("Decrease Brightness (-)");
// decBrAction.setAccelerator(KeyStroke.getKeyStroke(
// java.awt.event.KeyEvent.VK_MINUS, java.awt.Event.CTRL_MASK));
//
// rotCWAction = new JMenuItem("Rotate Clockwise (CW)");
// rotCWAction.setAccelerator(KeyStroke.getKeyStroke(
// java.awt.event.KeyEvent.VK_L, java.awt.Event.CTRL_MASK));
//
// rotCCWAction = new JMenuItem("Rotate Counterclockwise (CCW)");
// rotCCWAction.setAccelerator(KeyStroke.getKeyStroke(
// java.awt.event.KeyEvent.VK_R, java.awt.Event.CTRL_MASK));
//
// flipHAction = new JMenuItem("Flip image horizontally");
// flipHAction.setAccelerator(KeyStroke.getKeyStroke(
// java.awt.event.KeyEvent.VK_H, java.awt.Event.CTRL_MASK
// + java.awt.Event.SHIFT_MASK));
//
// flipVAction = new JMenuItem("Flip image vertically");
// flipVAction.setAccelerator(KeyStroke.getKeyStroke(
// java.awt.event.KeyEvent.VK_V, java.awt.Event.CTRL_MASK
// + java.awt.Event.SHIFT_MASK));
//
// resetAction = new JMenuItem("Reset Image");
// resetAction.setAccelerator(KeyStroke.getKeyStroke(
// java.awt.event.KeyEvent.VK_R, java.awt.Event.SHIFT_MASK
// + java.awt.Event.CTRL_MASK));
//
// incBrAction.addActionListener(this);
// decBrAction.addActionListener(this);
// rotCWAction.addActionListener(this);
// rotCCWAction.addActionListener(this);
// flipHAction.addActionListener(this);
// flipVAction.addActionListener(this);
// resetAction.addActionListener(this);
// playAction.addActionListener(this);
//
// imageMenu.add(playAction);
// imageMenu.add(incBrAction);
// imageMenu.add(decBrAction);
// imageMenu.add(flipHAction);
// imageMenu.add(flipVAction);
// imageMenu.add(rotCWAction);
// imageMenu.add(rotCCWAction);
// imageMenu.addSeparator();
// imageMenu.add(resetAction);
//
// menuBar.add(imageMenu);
// return menuBar;
// }
//
// public BufferedImage rotate(BufferedImage img, int angle)
// {
// int w = img.getWidth();
// int h = img.getHeight();
// BufferedImage dimg = new BufferedImage(w, h, img.getType());
// Graphics2D g = dimg.createGraphics();
// g.rotate(Math.toRadians(angle), w / 2, h / 2);
// g.drawImage(img, null, 0, 0);
// return dimg;
// }
//
// private BufferedImage verticalFlip(BufferedImage img)
// {
// int w = img.getWidth();
// int h = img.getHeight();
// BufferedImage dimg = new BufferedImage(w, h, img.getType());
// Graphics2D g = dimg.createGraphics();
// g.drawImage(img, 0, 0, w, h, w, 0, 0, h, null);
// g.dispose();
// return dimg;
// }
//
// private BufferedImage horizontalflip(BufferedImage img)
// {
// int w = img.getWidth();
// int h = img.getHeight();
// BufferedImage dimg = new BufferedImage(w, h, img.getColorModel()
// .getTransparency());
// Graphics2D g = dimg.createGraphics();
// g.drawImage(img, 0, 0, w, h, 0, h, w, 0, null);
// g.dispose();
// return dimg;
// }
//
// private JTree loadJTree()
// {
//
// String[] elem = listFile(folder);
// System.out.println(elem.toString());
// Arrays.sort(elem);
//
// DefaultMutableTreeNode root = new DefaultMutableTreeNode("DICOM folder");
//
// for (String strings : elem)
// {
// root.add(new DefaultMutableTreeNode(strings));
// }
//
// DefaultTreeCellRenderer rend = new DefaultTreeCellRenderer();
// JTree arbore = new JTree(root);
// arbore.setCellRenderer(rend);
// return arbore;
//
// }
//
// private void playPause()
// {
//
// Thread animation = new Thread(new PlayRunnable(this.colorPanel,
// this.folder));
// if (!isPlaying)
// {
//
// if (animation.getState() == Thread.State.NEW)
// {
// animation.start();
// }
// }
//
// isPlaying = !isPlaying;
//
// }
//
// public static String[] listFile(String path)
// {
// File f = new File(path);
// System.out.println(f.getAbsolutePath());
// return f.list();
// }
//
// public static BufferedImage open(String path)
// {
// crtFile = path;
// try
// {
//
// // System.out.println(crtFile);
//
// return increaseImageBrightness(ImageIO.read(new File(path)), 20.0f);
//
// } catch (IOException e)
// {
// e.printStackTrace();
// }
// return null;
// }
//
// private static BufferedImage increaseImageBrightness(BufferedImage image,
// float scaleFactor)
// {
// RescaleOp op = new RescaleOp(scaleFactor, 0.0f, null);
// BufferedImage brighter = op.filter(image, null);
// return brighter;
// }
//
// private BufferedImage decreaseImageBrightness(BufferedImage image,
// float scaleFactor)
// {
// RescaleOp op = new RescaleOp(scaleFactor, 0.0f, null);
// BufferedImage darker = op.filter(image, null);
// return darker;
// }
//
// private BufferedImage resetImage(String filename)
// {
// return open(filename);
// }
//
// public void saveImage(BufferedImage img, String ref)
// {
// try
// {
// ref = "export/" + ref;
// System.out.println(ref);
// String format = (ref.endsWith(".png")) ? "png" : "jpg";
// boolean ok = ImageIO.write(img, format, new File(ref));
// if (ok)
// {
// JOptionPane.showMessageDialog(this, "Image saved in " + ref);
// }
// } catch (IOException e)
// {
// e.printStackTrace();
// }
// }
//
// // public static void main(String[] args)
// public void init()
// {
//
// try
// {
// for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
// {
// if ("Nimbus".equals(info.getName()))
// {
// UIManager.setLookAndFeel(info.getClassName());
// break;
// }
// }
// } catch (UnsupportedLookAndFeelException e)
// {
// // handle exception
// } catch (ClassNotFoundException e)
// {
// // handle exception
// } catch (InstantiationException e)
// {
// // handle exception
// } catch (IllegalAccessException e)
// {
// // handle exception
// }
//
// crtFile = folder
// + "/1.3.12.2.1107.5.1.4.57283.30000010082911211628100002319";
// // System.out.println(crtFile);
// //GUI main = new GUI(crtFile);
// //main.crtBr = 0;
// // main.setUndecorated(true);
// // main.setResizable(true);
// //main.setVisible(true);
// this.setSize(900, 1000);
//		
// }
//
//
// @Override
// public void actionPerformed(ActionEvent e)
// {
// if (e.getSource() == resetAction)
// {
// System.out.println("CF " + crtFile);
// colorPanel.img = this.resetImage(crtFile);
// this.repaint();
// }
// if (e.getSource() == incBrAction)
// {
// colorPanel.img = increaseImageBrightness(colorPanel.img, 1.1f);
// this.repaint();
// }
// if (e.getSource() == decBrAction)
// {
// colorPanel.img = decreaseImageBrightness(colorPanel.img, 0.9f);
// this.repaint();
// }
// if (e.getSource() == openAction)
// {
//
// fileChooser.setDialogTitle("Alege fisierul");
// FileFilter filtru = new FileNameExtensionFilter("DICOM files",
// "dicom", "dcm");
// fileChooser.addChoosableFileFilter(filtru);
//
// int ret = fileChooser.showDialog(null, "Submit");
// if (ret == JFileChooser.APPROVE_OPTION)
// {
// File f = fileChooser.getSelectedFile();
// colorPanel.img = open(f.getAbsolutePath());
// this.repaint();
//
// }
// }
// if (e.getSource() == saveAction)
// {
// Calendar cal = Calendar.getInstance();
// SimpleDateFormat sdf = new SimpleDateFormat("_dd-MM.H.mm.ss");
//
// saveImage(colorPanel.img, "image" + sdf.format(cal.getTime())
// + ".png");
// }
//
// if (e.getSource() == rotCCWAction)
// {
// colorPanel.img = rotate(colorPanel.img, -90);
// this.repaint();
// }
// if (e.getSource() == rotCWAction)
// {
// colorPanel.img = rotate(colorPanel.img, 90);
// this.repaint();
// }
//
// if (e.getSource() == flipHAction)
// {
// colorPanel.img = horizontalflip(colorPanel.img);
// this.repaint();
// }
//
// if (e.getSource() == flipVAction)
// {
// colorPanel.img = verticalFlip(colorPanel.img);
// this.repaint();
// }
//
// if (e.getSource() == playAction)
// {
// playPause();
// }
// if (e.getSource() == exitAction)
// {
// System.exit(0);
// }
//
// }
//
// @Override
// public void mouseClicked(java.awt.event.MouseEvent e)
// {
// if ((e.getModifiers() & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK)
// {
//
// int xBorder = (colorPanel.getWidth() - colorPanel.img.getWidth()) / 2;
// int yBorder = (colorPanel.getHeight() - colorPanel.img.getHeight()) / 2;
//
// // g1.setColor(Color.RED);
// // g1.drawLine(e.getX() , e.getY(), (colorPanel.getWidth() -
// // colorPanel.img.getWidth())/2,(colorPanel.getHeight() -
// // colorPanel.img.getHeight())/2);
// //
// // Graphics2D g2 = (Graphics2D) colorPanel.getGraphics();
// // g2.drawImage(img1, null, 0,0 );
//
// Graphics2D g = (Graphics2D) colorPanel.img.getGraphics();
// g.setColor(Color.white);
// g.drawLine(0, 0, e.getX() - xBorder, e.getY() - yBorder);
// // colorPanel.img.
// // colorPanel.repaint();
// // Graphics2D g2d =(Graphics2D) colorPanel.getGraphics();
// // g2d.setColor(Color.black);
// // g2d.fillRect(0, 0, getWidth(), getHeight());
// // g2d.drawImage(colorPanel.img , null, (getWidth() -
// // colorPanel.img.getWidth()) / 2,
// // (getHeight() - colorPanel.img.getHeight()) / 2);
// //
// // g2d.setColor(Color.green);
// //
// // g2d.drawRect((getWidth() - colorPanel.img.getWidth()) / 2,
// // (getHeight() - colorPanel.img
// // .getHeight()) / 2, colorPanel.img.getWidth(),
// // colorPanel.img.getHeight());
// //
// // g2d.drawString("info", 10, 20);
// //
// //
// //
// // g2d.setColor(Color.RED);
// //
// // // Graphics2D g2 =(Graphics2D) colorPanel.getGraphics();
// // g2d.setColor(Color.RED);
// //
// // g2d.drawLine(e.getX() , e.getY(), (colorPanel.getWidth() -
// // colorPanel.img.getWidth())/2,(colorPanel.getHeight() -
// // colorPanel.img.getHeight())/2);
// // //colorPanel.img = increaseImageBrightness(colorPanel.img, 1.1f);
//
// }
//
// if ((e.getModifiers() & InputEvent.BUTTON2_MASK) == InputEvent.BUTTON2_MASK)
// colorPanel.img = this.resetImage(crtFile);
// this.repaint();
// }
//
// @Override
// public void mouseEntered(java.awt.event.MouseEvent e)
// {
//
// }
//
// @Override
// public void mouseExited(java.awt.event.MouseEvent e)
// {
//
// }
//
// @Override
// public void mousePressed(java.awt.event.MouseEvent e)
// {
//
// }
//
// @Override
// public void mouseReleased(java.awt.event.MouseEvent e)
// {
//
// }
//
// @Override
// public void mouseDragged(java.awt.event.MouseEvent e)
// {
// if ((e.getModifiers() & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK)
// {
// colorPanel.img = increaseImageBrightness(colorPanel.img, 1.1f);
// }
// if ((e.getModifiers() & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK)
// colorPanel.img = decreaseImageBrightness(colorPanel.img, 0.9f);
//
// this.repaint();
//
// }
//
// @Override
// public void mouseMoved(java.awt.event.MouseEvent e)
// {
// label.setText((e.getX() - (colorPanel.getWidth() - colorPanel.img
// .getWidth()) / 2)
// + " "
//
// + (e.getY() - (colorPanel.getHeight() - colorPanel.img
// .getHeight()) / 2) + " | " + e.getX() + " " + e.getY());
//
// }
//
// @Override
// public void valueChanged(TreeSelectionEvent e)
// {
// String[] splitted = e.getPath().toString().split(",\\ ");
// if (splitted.length >= 2)
// {
// splitted[1] = splitted[1].substring(0, splitted[1].length() - 1);
// colorPanel.img = open(folder + "/" + splitted[1]);
// this.repaint();
// }
// }
//
// }
