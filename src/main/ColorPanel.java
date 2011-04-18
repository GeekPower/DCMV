package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class ColorPanel extends JPanel
{
	private static final long serialVersionUID = -5362510372793557392L;

	BufferedImage img;

	public ColorPanel(BufferedImage image)
	{
		img = image;

	}

	public BufferedImage getImage()
	{
		return this.img;
	}

	public void update()
	{
	}

	public void paint(Graphics g)
	{

		int X = (getWidth() - img.getWidth()) / 2;
		int Y = (getHeight() - img.getHeight()) / 2;
		Graphics2D g2d = (Graphics2D) g;

		g2d.setColor(Color.black);
		g2d.fillRect(0, 0, getWidth(), getHeight());
		g2d.drawImage(img, null, X, Y);

		g2d.setColor(Color.green);
//		g2d.drawLine(0, 0, 100, 100);
		g2d.drawRect(X, Y, img.getWidth(), img.getHeight());

		g2d.drawString("info", 10, 20);

	}
}