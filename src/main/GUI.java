package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

public class GUI extends JFrame implements ActionListener, MouseListener,
		MouseMotionListener, TreeSelectionListener {
	private static final long serialVersionUID = -4710340542609513214L;

	class ColorPanel extends JPanel {
		BufferedImage img;

		public ColorPanel(BufferedImage image) {
			img = image;
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			g2d.drawImage(img, null, 50, 50);
		}
	}

	private JPanel top = new JPanel();
	private String path = null;
	private ColorPanel colorPanel = null;
	private JTree tree = null;
	private JLabel label = new JLabel("");
	private BufferedImage image;
	private JButton reset = new JButton("Reset Image");
	private JButton openBtn = new JButton("Open series");
	private JButton incBr = new JButton("+");
	private JButton decBr = new JButton("-");
	private JScrollPane left = null;
	private JFileChooser fileChooser = new JFileChooser();
	
	public GUI(String path) {

		
		this.tree = loadJTree();
		this.left = new JScrollPane(tree, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		this.left.setPreferredSize(new Dimension(200, 900));
		this.left.getViewport().add( tree );

		
		this.image = this.open(path);
		this.colorPanel = new ColorPanel(image);
		this.colorPanel.setBackground(Color.black);
		this.path = path;
		
		this.top.setLayout(new GridLayout(0,6,20,5));
		
		this.setLayout(new BorderLayout());

		this.colorPanel.addMouseMotionListener(this);
		this.colorPanel.addMouseListener(this);
		this.tree.addTreeSelectionListener(this);
		this.reset.addActionListener(this);
		this.incBr.addActionListener(this);
		this.decBr.addActionListener(this);
		
		this.top.add(openBtn);
		this.top.add(reset);
		this.top.add(incBr);
		this.top.add(decBr);
		this.top.add(new JButton("T"));
		
		
		this.add(top, BorderLayout.NORTH);
		this.add(colorPanel, BorderLayout.CENTER);
		this.add(label, BorderLayout.SOUTH);
		this.add(left, BorderLayout.WEST);

		
		this.setSize(1200, 800);
		this.setLocation(200, 200);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("DCM file viewer");
		this.setVisible(true);

	}

	private JTree loadJTree() {

		String[] elem = this.listFile("dcmvf");

		Arrays.sort(elem);

		DefaultMutableTreeNode root = new DefaultMutableTreeNode("DICOM folder");
		DefaultMutableTreeNode UID = new DefaultMutableTreeNode();

		for (String strings : elem) {
			root.add(new DefaultMutableTreeNode(strings));
		}

		DefaultTreeCellRenderer rend = new DefaultTreeCellRenderer();
		JTree arbore = new JTree(root);
		arbore.setCellRenderer(rend);
		return arbore;

	}

	private String[] listFile(String path) {
		File f = new File(path);
		return f.list();
	}

	private BufferedImage open(String path) {
		try {

			return increaseImageBrightness(ImageIO.read(new File(path)), 20.0f);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private BufferedImage increaseImageBrightness(BufferedImage image,
			float scaleFactor) {

		RescaleOp op = new RescaleOp(scaleFactor, 0.0f, null);
		BufferedImage brighter = op.filter(image, null);
		return brighter;
	}

	private BufferedImage resetImage(String filename) {
		return open(filename);
	}

	private BufferedImage decreaseImageBrightness(BufferedImage image,
			float scaleFactor) {

		RescaleOp op = new RescaleOp(scaleFactor, 0.0f, null);
		BufferedImage darker = op.filter(image, null);
		return darker;
	}

	public static void main(String[] args) {

		GUI main = new GUI(
				"dcmvf/1.3.12.2.1107.5.1.4.57283.30000010082911211628100002319");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == reset) {
			colorPanel.img = this.resetImage(this.path);
			this.repaint();
		}
		if (e.getSource() == incBr) {
			colorPanel.img = increaseImageBrightness(colorPanel.img, 1.1f);
			this.repaint();
		}
		if (e.getSource() == decBr) {
			colorPanel.img = decreaseImageBrightness(colorPanel.img, 0.9f);
			this.repaint();
		}
	}

	@Override
	public void mouseClicked(java.awt.event.MouseEvent e) {
		if ((e.getModifiers() & InputEvent.BUTTON2_MASK) == InputEvent.BUTTON2_MASK)
			colorPanel.img = this.resetImage(this.path);
		this.repaint();
	}

	@Override
	public void mouseEntered(java.awt.event.MouseEvent e) {

	}

	@Override
	public void mouseExited(java.awt.event.MouseEvent e) {

	}

	@Override
	public void mousePressed(java.awt.event.MouseEvent e) {
		label.setText(e.getX() + " " + e.getY());
	}

	@Override
	public void mouseReleased(java.awt.event.MouseEvent e) {

	}

	@Override
	public void mouseDragged(java.awt.event.MouseEvent e) {
		if ((e.getModifiers() & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK)
			colorPanel.img = increaseImageBrightness(colorPanel.img, 1.1f);
		if ((e.getModifiers() & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK)
			colorPanel.img = decreaseImageBrightness(colorPanel.img, 0.9f);

		this.repaint();

	}

	@Override
	public void mouseMoved(java.awt.event.MouseEvent e) {
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		String[] splitted = e.getPath().toString().split(",\\ ");
		System.out.println(splitted[0]);
		if (splitted.length >=2) {
			splitted[1] = splitted[1].substring(0, splitted[1].length() - 1);
			colorPanel.img = open("dcmvf/" + splitted[1]);
			this.repaint();
		}
	}
}
