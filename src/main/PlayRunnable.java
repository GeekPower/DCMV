package main;

import java.util.Arrays;

public class PlayRunnable implements Runnable
{
	ColorPanel colorPanel = null;
	String folder = null;
	
	public PlayRunnable(ColorPanel colorPanel, String folder)
	{
		this.colorPanel = colorPanel;
		this.folder = folder;
	}

	@Override
	public void run()
	{
		String[] elem = GUI.listFile(folder+"/");

		Arrays.sort(elem);
		int speed = 0;
		while (GUI.isPlaying)
			for (String file : elem)
			{

				if (GUI.isPlaying)
				{
					try
					{
						speed = Math.abs(GUI.speedValue);
						colorPanel.img = GUI.open(folder +"/" + file);
						this.colorPanel.repaint();
						Thread.sleep(speed, 0);

					} catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}

			}

	}

}
