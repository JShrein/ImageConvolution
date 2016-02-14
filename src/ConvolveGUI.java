import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
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
	private final int FILTER_SIZE = 3*3;
	private final int[] verticalSobel = {-1, -2, -1, 0, 0, 0, 1, 2, 1};
	int width;
	int height;
	String title;
	
	ImageBox mainImageBox;
	ImageBox newImageBox;
	MatrixBox matrix;
	
	Button openImgBtn;
	Button convolveBtn;
	
	BufferedImage originalImg = null;
	BufferedImage convolutedImg = null;
	
	
	JFileChooser fileDialog;
	FileFilter imgFilter;
	FileView imgView;
	
	private class ImageBox extends JPanel
	{
		Image img;
		public ImageBox()
		{

		}
		
		public void setImage(Image i)
		{
			this.img = i;
		}
		
		@Override
		public void paint(Graphics g) {
			super.paintComponent(g);
			g.drawImage(img, 0, 0, this);
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
		
		public int[] getFilter()
		{
			int[] filter = new int[FILTER_SIZE];
			
			for(int i = 0; i < FILTER_SIZE; i++)
			{
				filter[i] = Integer.parseInt(matrixFields[i].getText());
			}
			
			return filter;
		}
		
		public int getNormConstant()
		{
			int[] filter = getFilter();
			int sum = 0;
			
			for(int i = 0; i < FILTER_SIZE; i++)
			{
				sum += filter[i];
			}
			
			return sum == 0 ? 1 : sum;
		}
	}
	
	public ConvolveGUI(int width, int height, String title)
	{
		super(title);
		this.setPreferredSize(new Dimension(width, height));
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		matrix = new MatrixBox();
		mainImageBox = new ImageBox();
		newImageBox = new ImageBox();
		
		mainImageBox.setPreferredSize(new Dimension(500,500));
		newImageBox.setPreferredSize(new Dimension(500,500));
		
		openImgBtn = new Button("Open Image");
		openImgBtn.addActionListener(this);
		
		convolveBtn = new Button("Convolve");
		convolveBtn.addActionListener(this);
		convolveBtn.setMaximumSize(new Dimension(100,30));
		convolveBtn.setEnabled(false);

		this.add(mainImageBox, BorderLayout.CENTER);
		this.add(newImageBox, BorderLayout.EAST);
		
		//this.add(matrix, BorderLayout.CENTER);
		//this.add(openImgBtn, BorderLayout.SOUTH);
		//this.add(convolveBtn, BorderLayout.SOUTH);
		this.add(new JPanel()
		{
			{
				add(matrix, BorderLayout.NORTH);
				add(openImgBtn, BorderLayout.WEST);
				add(convolveBtn, BorderLayout.EAST);
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
		
		if(e.getSource() == convolveBtn)
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
			} catch (IOException ex) {
				JOptionPane.showOptionDialog(this, "ERROR: Unable to load image", "Image read error", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, null, null);
				ex.printStackTrace();
			}
			
			mainImageBox.setImage(originalImg);
			mainImageBox.repaint();
			convolveBtn.setEnabled(true);
		}
	}
	
	private void doConvolution()
	{
		int imgWidth = originalImg.getWidth();
		int imgHeight = originalImg.getHeight();
		
		int[] filter = new int[9];
		

		String s = originalImg.getType() == BufferedImage.TYPE_3BYTE_BGR ? "Yes" : "No";
		//System.out.println(s);

		
		// Setup grayscale work Image
		BufferedImage workImage = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_3BYTE_BGR);
		
		for(int i = 0; i < imgWidth; i++)
		{
			for(int j = 0; j < imgHeight; j++)
			{
				int rgb = originalImg.getRGB(i, j);
				workImage.setRGB(i, j, rgb);
			}
		}
		
		filter = matrix.getFilter();
		
		for(int i = 1; i < imgWidth-1; i++)
		{
			for(int j = 1; j < imgHeight-1; j++)
			{
				/*if(isEdgePixel(i,j,imgWidth,imgHeight))
				{
					if(isCornerPixel(i,j,imgWidth,imgHeight))
					{
						
						
					}
				}
				*/
				int bgr = originalImg.getRGB(i, j);

				byte b = (byte) ((Channels.getChannels(originalImg.getRGB(Neighborhood.TL.x + i, Neighborhood.TL.y + j))[0]*filter[FILTER_SIZE-1]) +
						  (Channels.getChannels(originalImg.getRGB(Neighborhood.TM.x + i, Neighborhood.TM.y + j))[0]*filter[FILTER_SIZE-2]) +
						  (Channels.getChannels(originalImg.getRGB(Neighborhood.TR.x + i, Neighborhood.TR.y + j))[0]*filter[FILTER_SIZE-3]) +
						  (Channels.getChannels(originalImg.getRGB(Neighborhood.ML.x + i, Neighborhood.ML.y + j))[0]*filter[FILTER_SIZE-4]) +
						  (Channels.getChannels(originalImg.getRGB(Neighborhood.MM.x + i, Neighborhood.MM.y + j))[0]*filter[FILTER_SIZE-5]) +
						  (Channels.getChannels(originalImg.getRGB(Neighborhood.MR.x + i, Neighborhood.MR.y + j))[0]*filter[FILTER_SIZE-6]) +
						  (Channels.getChannels(originalImg.getRGB(Neighborhood.BL.x + i, Neighborhood.BL.y + j))[0]*filter[FILTER_SIZE-7]) +
						  (Channels.getChannels(originalImg.getRGB(Neighborhood.BM.x + i, Neighborhood.BM.y + j))[0]*filter[FILTER_SIZE-8]) +
						  (Channels.getChannels(originalImg.getRGB(Neighborhood.BR.x + i, Neighborhood.BR.y + j))[0]*filter[FILTER_SIZE-9]));

				byte g = (byte) ((Channels.getChannels(originalImg.getRGB(Neighborhood.TL.x + i, Neighborhood.TL.y + j))[1]*filter[FILTER_SIZE-1]) +
						  (Channels.getChannels(originalImg.getRGB(Neighborhood.TM.x + i, Neighborhood.TM.y + j))[1]*filter[FILTER_SIZE-2]) +
						  (Channels.getChannels(originalImg.getRGB(Neighborhood.TR.x + i, Neighborhood.TR.y + j))[1]*filter[FILTER_SIZE-3]) +
						  (Channels.getChannels(originalImg.getRGB(Neighborhood.ML.x + i, Neighborhood.ML.y + j))[1]*filter[FILTER_SIZE-4]) +
						  (Channels.getChannels(originalImg.getRGB(Neighborhood.MM.x + i, Neighborhood.MM.y + j))[1]*filter[FILTER_SIZE-5]) +
						  (Channels.getChannels(originalImg.getRGB(Neighborhood.MR.x + i, Neighborhood.MR.y + j))[1]*filter[FILTER_SIZE-6]) +
						  (Channels.getChannels(originalImg.getRGB(Neighborhood.BL.x + i, Neighborhood.BL.y + j))[1]*filter[FILTER_SIZE-7]) +
						  (Channels.getChannels(originalImg.getRGB(Neighborhood.BM.x + i, Neighborhood.BM.y + j))[1]*filter[FILTER_SIZE-8]) +
						  (Channels.getChannels(originalImg.getRGB(Neighborhood.BR.x + i, Neighborhood.BR.y + j))[1]*filter[FILTER_SIZE-9]));
				
				byte r = (byte) ((Channels.getChannels(originalImg.getRGB(Neighborhood.TL.x + i, Neighborhood.TL.y + j))[2]*filter[FILTER_SIZE-1]) +
						  (Channels.getChannels(originalImg.getRGB(Neighborhood.TM.x + i, Neighborhood.TM.y + j))[2]*filter[FILTER_SIZE-2]) +
						  (Channels.getChannels(originalImg.getRGB(Neighborhood.TR.x + i, Neighborhood.TR.y + j))[2]*filter[FILTER_SIZE-3]) +
						  (Channels.getChannels(originalImg.getRGB(Neighborhood.ML.x + i, Neighborhood.ML.y + j))[2]*filter[FILTER_SIZE-4]) +
						  (Channels.getChannels(originalImg.getRGB(Neighborhood.MM.x + i, Neighborhood.MM.y + j))[2]*filter[FILTER_SIZE-5]) +
						  (Channels.getChannels(originalImg.getRGB(Neighborhood.MR.x + i, Neighborhood.MR.y + j))[2]*filter[FILTER_SIZE-6]) +
						  (Channels.getChannels(originalImg.getRGB(Neighborhood.BL.x + i, Neighborhood.BL.y + j))[2]*filter[FILTER_SIZE-7]) +
						  (Channels.getChannels(originalImg.getRGB(Neighborhood.BM.x + i, Neighborhood.BM.y + j))[2]*filter[FILTER_SIZE-8]) +
						  (Channels.getChannels(originalImg.getRGB(Neighborhood.BR.x + i, Neighborhood.BR.y + j))[2]*filter[FILTER_SIZE-9]));
				
				//rgb2 /= matrix.getNormConstant();
				int bgrResult = Channels.getBGR(b, g, r);
				workImage.setRGB(i, j, bgrResult);
				
			}
		}
		
		newImageBox.setImage(workImage);
		newImageBox.repaint();
	}
	
	private boolean isEdgePixel(int x, int y, int width, int height)
	{
		return x == 0 || x == (width - 1) ? true : y == 0 || y == (height-1) ? true : false;
	}
	
	private boolean isCornerPixel(int x, int y, int width, int height)
	{
		if(x == 0 || x == (width - 1))
		{
			if(y == 0 || y == (width - 1))
			{
				return true;
			}
		}
		return false;
	}
}
