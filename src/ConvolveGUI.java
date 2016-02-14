import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileView;

public class ConvolveGUI extends JFrame implements ActionListener, ImageObserver
{
	private final int[] verticalSobel = {-1, 2, -1, 0, 0, 0, 1, 2, 1};
	int width;
	int height;
	String title;
	
	ImageBox mainImageBox;
	ImageBox newImageBox;
	MatrixBox matrix;
	
	Button openImgBtn;
	Button convolve;
	
	Image originalImg;
	Image convolutedImg;
	
	
	JFileChooser fileDialog;
	FileFilter imgFilter;
	FileView imgView;
	
	private class ImageBox extends JPanel
	{
		Image img;
		public ImageBox(Image i)
		{
			super();
			this.img = i;
		}
		
		@Override
		public void paint(Graphics g) {
			super.paintComponent(g);
			g.drawImage(img, 0, 0, null);
		}
	}
	
	private class MatrixBox extends JPanel
	{
		JTextField[] matrixFields;
		public MatrixBox()
		{
			setPreferredSize(new Dimension(100,100));
			GridLayout grid = new GridLayout(3,3);

			grid.setHgap(5);
			grid.setVgap(5);
			
			this.setLayout(grid);
	
			matrixFields = new JTextField[9];
			for(int i = 0; i < matrixFields.length; i++)
			{
				matrixFields[i] = new JTextField();
				matrixFields[i].setColumns(10);
				matrixFields[i].setText("" + verticalSobel[i]);
				this.add(matrixFields[i]);
			}
		}
	}
	
	public ConvolveGUI(int width, int height, String title)
	{
		super(title);
		this.setPreferredSize(new Dimension(width, height));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		matrix = new MatrixBox();
		mainImageBox = new ImageBox(originalImg);
		newImageBox = new ImageBox(convolutedImg);
		
		mainImageBox.setPreferredSize(new Dimension(500,500));
		newImageBox.setPreferredSize(new Dimension(500,500));
		
		openImgBtn = new Button("Open Image");
		openImgBtn.addActionListener(this);
		
		convolve = new Button("Convolve");
		convolve.addActionListener(this);
		convolve.setMaximumSize(new Dimension(100,30));

		this.add(mainImageBox, BorderLayout.WEST);
		this.add(newImageBox, BorderLayout.EAST);
		
		//this.add(matrix, BorderLayout.CENTER);
		//this.add(openImgBtn, BorderLayout.SOUTH);
		//this.add(convolve, BorderLayout.SOUTH);
		this.add(new JPanel()
		{
			{
				add(matrix, BorderLayout.NORTH);
				add(openImgBtn, BorderLayout.WEST);
				add(convolve, BorderLayout.EAST);
			}
		}, BorderLayout.SOUTH);
		imgFilter = new FileFilter() {

			@Override
			public boolean accept(File f) {
				if(f.isDirectory())
					return true;
				
				return ImageExtensionUtils.isValidImage(ImageExtensionUtils.getExtension(f));
			}

			@Override
			public String getDescription() {
				
				return "Image files (*.jpg, *.jpeg, *.png, *.tif, *.tiff)";
			}
			
		};
		
		fileDialog = new JFileChooser();
		fileDialog.setFileFilter(imgFilter);
		
		this.pack();
		this.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource() == openImgBtn)
		{
			loadImage();
		}
		
		if(e.getSource() == convolve)
		{
			doConvolution();
		}
	}
	
	private void loadImage()
	{
		int returnCode = fileDialog.showOpenDialog(this);
		
		if(returnCode == JFileChooser.APPROVE_OPTION)
		{
			File file = fileDialog.getSelectedFile();
		
			try {
				originalImg = ImageIO.read(file);
				mainImageBox.repaint();
			} catch (IOException ex) {
				JOptionPane.showOptionDialog(this, "ERROR: Unable to load image", "Image read error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, null, null);
				ex.printStackTrace();
			}
		}
	}
	
	private void doConvolution()
	{
		
	}
}
