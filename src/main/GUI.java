package main;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GUI extends JFrame implements ActionListener, MouseListener,
		MouseMotionListener {
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
	
	String path = null;

	private ColorPanel colorPanel = null;
	private BufferedImage image;

	private JButton reset = new JButton("Reset Image");

	public GUI(String path) {

		this.image = this.open(path);
		this.colorPanel = new ColorPanel(image);
		this.path = path;
		this.setLayout(new BorderLayout());

		this.reset.addActionListener(this);

		this.colorPanel.addMouseMotionListener(this);
		this.colorPanel.addMouseListener(this);

		this.add(colorPanel, BorderLayout.CENTER);
		this.setSize(1000, 1000);
		this.setLocation(200, 200);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setTitle("DCM file viewer");
		this.setVisible(true);

	}

	private BufferedImage open(String path) {
		try {

			return ImageIO.read(new File(path));

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private BufferedImage increaseImageBrightness(BufferedImage image) {

		RescaleOp op = new RescaleOp(1.1f, 0.0f, null);
		BufferedImage brighter = op.filter(image, null);
		return brighter;
	}

	private BufferedImage resetImage(String filename) {
		return open(filename);
	}

	private BufferedImage decreaseImageBrightness(BufferedImage image) {

		RescaleOp op = new RescaleOp(.9f, 0.0f, null);
		BufferedImage darker = op.filter(image, null);
		return darker;
	}

	public static void main(String[] args) {

		GUI main = new GUI(
				"/home/alexr/Desktop/files/small/1.3.12.2.1107.5.1.4.57283.30000010082911211628100002319");
	}

	

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == reset) {
			colorPanel.img = this.resetImage(this.path);
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

	}

	@Override
	public void mouseReleased(java.awt.event.MouseEvent e) {

	}

	@Override
	public void mouseDragged(java.awt.event.MouseEvent e) {
		if ((e.getModifiers() & InputEvent.BUTTON1_MASK) == InputEvent.BUTTON1_MASK)
			colorPanel.img = increaseImageBrightness(colorPanel.img);
		if ((e.getModifiers() & InputEvent.BUTTON3_MASK) == InputEvent.BUTTON3_MASK)
			colorPanel.img = decreaseImageBrightness(colorPanel.img);
		
		
		this.repaint();

	}

	@Override
	public void mouseMoved(java.awt.event.MouseEvent e) {

	}

}
