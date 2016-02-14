import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.ImageObserver;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileView;

public class ConvolveGUI extends JFrame implements ActionListener, ImageObserver
{
	int width;
	int height;
	String title;
	
	JPanel imageBox;
	Button openImgBtn;
	
	Image originalImg;
	
	
	JFileChooser fileDialog;
	FileFilter imgFilter;
	FileView imgView;
	
	public ConvolveGUI(int width, int height, String title)
	{
		super(title);
		this.setPreferredSize(new Dimension(width, height));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		imageBox = new JPanel(){

			@Override
			protected void paintComponent(Graphics g) {
				g.drawImage(originalImg, 0, 0, this);
				super.paintComponent(g);
			}
			
		};
		imageBox.setSize(new Dimension(500,500));

		
		openImgBtn = new Button("Open Image");
		openImgBtn.addActionListener(this);
		
		this.add(openImgBtn, BorderLayout.SOUTH);
		
		imgFilter = new FileFilter() {

			@Override
			public boolean accept(File f) {
				if(f.isDirectory())
					return true;
				
				return ImageExtensionUtils.isValidImage(ImageExtensionUtils.getExtension(f));
			}

			@Override
			public String getDescription() {
				
				return ".jpg\n.jpeg\n.png\n.tif\n.tiff";
			}
			
		};
		
		fileDialog = new JFileChooser();
		fileDialog.addChoosableFileFilter(imgFilter);
		
		this.pack();
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == openImgBtn)
		{
			int returnCode = fileDialog.showOpenDialog(this);
			
			if(returnCode == JFileChooser.APPROVE_OPTION)
			{
				File file = fileDialog.getSelectedFile();
				
			}
		}
	}
	
	
}
