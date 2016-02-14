import javax.swing.SwingUtilities;

public class Main 
{
	private static void buildGUI()
	{
		new ConvolveGUI(1024,800, "Convolution Tester");
	}
	
	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable(){

			@Override
			public void run() {
				buildGUI();
			}
		});
	}
}
