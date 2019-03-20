import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.Image;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.SwingConstants;

public class PixelIt {

	private JFrame frame;
	private JComboBox<String> comboBoxNumberOfFrames;
	private JTextField textFieldOfImage;
	private JTextField locationPath;
	private JTextField output_name;
	private JTextField textField;
	private JButton btnSaveLocation;
	private JButton btnToSaveJPGImg;
	private JButton btnNewButton;
	private JButton btnPixelItTo;
	private JLabel lblProgress;
	private JLabel ImagePlace;
	private JLabel savedLbl;
	private JLabel label;

	/**
	 * Launch the application.
	 */



	//Test
	//The important Thread, 
	//make button invisible 
	//take the picture pixel item number of times and create the GIF output.
	class PixelAndGifThread extends Thread {
		public void run(){

			try {

				btnNewButton.setVisible(false);	
				btnToSaveJPGImg.setVisible(false);
				
				String imagePath = textFieldOfImage.getText();
				//String fileFormat = username.split(".")[1];
				if (!imagePath.endsWith("jpg") && !imagePath.endsWith("jpeg")) {
					textField.setText("File format should be jpg");
					btnNewButton.setVisible(true);
					return;
				}

				textField.setText("Job Started! 3 Steps away.");
				File file = new File(imagePath);
				int numofpics = Integer.parseInt((String)comboBoxNumberOfFrames.getSelectedItem());

				File directory = new File("output");
				directory.mkdirs();
				BufferedImage image;
				Color pixel;

				for (int size = 1; size <= numofpics; size++) {

					image = ImageIO.read(file);

					int num = size*size;
					long sumr = 0, sumg = 0, sumb = 0;
					for (int i = 0; i < image.getHeight(); i=i+size) {
						for (int j = 0; j < image.getWidth(); j=j+size) {

							for (int k = i; k < i+size && k < image.getHeight(); k++) {
								for (int l = j; l < j+size && l < image.getWidth(); l++) {
									pixel = new Color(image.getRGB(l, k));
									sumr += pixel.getRed();
									sumg += pixel.getGreen();
									sumb += pixel.getBlue();
								}
							}
							for (int k = i; k < i+size && k < image.getHeight(); k++) {
								for (int l = j; l < j+size && l < image.getWidth(); l++) {
									image.setRGB(l, k, new Color((int)(sumr / num), (int)(sumg / num), (int)(sumb / num)).getRGB());
								}
							}
							sumr = 0;
							sumg = 0;
							sumb = 0;
						}
					}
					File outputfile = new File("output\\image"+ size +".jpg");
					ImageIO.write(image, "jpg", outputfile);
				}

				
				textField.setText("Step One Done");
				BufferedImage firstImage = ImageIO.read(new File("output\\image1.jpg"));   
				// create a new BufferedOutputStream with the last argument
				String outputName = "";
				if (!locationPath.getText().equals("")) {
					outputName+=locationPath.getText();   
				}
				if (!output_name.getText().equals("")) {
					outputName += "\\"+output_name.getText()+".gif";
				}else {
					if (outputName.equals("")){
						outputName = "output.gif";
					}else {
					outputName += "\\output.gif";
					}
				}
				
				ImageOutputStream output = new FileImageOutputStream(new File(outputName));  
				
				// create a gif sequence with the type of the first image, 5 second    
				// between frames, which loops continuously    
				GifSequenceWriter writer =  new GifSequenceWriter(output, firstImage.getType(), 5, false);    

				// write out the first image to our sequence...
				writer.writeToSequence(firstImage);    
				for(int i=1; i<=numofpics; i++) 
				{      
					BufferedImage nextImage = ImageIO.read(new File("output\\image"+ i +".jpg"));      
					writer.writeToSequence(nextImage);    
				}
				textField.setText("Step Two Done");
				//progressBar.setString("75");
				for(int i=numofpics; i>=1; i--) 
				{
					File nxtImage = new File("output\\image"+ i +".jpg");
					BufferedImage nextImage = ImageIO.read(nxtImage);      
					writer.writeToSequence(nextImage); 
					nxtImage.delete();
				} 
				directory.delete();
				writer.close();    
				output.close();

			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Bug!");
				System.exit(1);
				e.printStackTrace();
			}

			btnNewButton.setVisible(true);
			textField.setText("Step Three Done. Image Ready!");
			locationPath.setText("");

		}
	}

	//Thread thats gets the file Path and put it in the path bar
	class ChoosePhotoThread extends Thread {
		public void run(){
			JFileChooser fileChooser = new JFileChooser();
			if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				File openedFile = fileChooser.getSelectedFile();
				textFieldOfImage.setText(openedFile.getPath());
			} 
		}
	}

	//Thread thats gets the file Path to save and put it in the path bar
	class SaveLocationThread extends Thread {
		public void run(){
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				File openedFile = fileChooser.getSelectedFile();
				locationPath.setText(openedFile.getPath());
			} 
		}
	}

	//Thread pixelate an image and show it on the screen
	class PixelAndShowThread extends Thread {
		public void run(){

			btnPixelItTo.setVisible(false);
			String imagePath = textFieldOfImage.getText();
			//String fileFormat = username.split(".")[1];
			if (!imagePath.endsWith("jpg") && !imagePath.endsWith("jpeg")) {
				textField.setText("File format should be jpg");
				btnPixelItTo.setVisible(true);
				return;
			}

			try {
			File file = new File(imagePath);
			BufferedImage image = ImageIO.read(file);
			Color pixel;
			int size = 10 , doubleSize = size * size;
			long sumr = 0, sumg = 0, sumb = 0;
			for (int i = 0; i < image.getHeight(); i=i+size) {
				for (int j = 0; j < image.getWidth(); j=j+size) {

					for (int k = i; k < i+size && k < image.getHeight(); k++) {
						for (int l = j; l < j+size && l < image.getWidth(); l++) {
							pixel = new Color(image.getRGB(l, k));
							sumr += pixel.getRed();
							sumg += pixel.getGreen();
							sumb += pixel.getBlue();
						}
					}
					for (int k = i; k < i+size && k < image.getHeight(); k++) {
						for (int l = j; l < j+size && l < image.getWidth(); l++) {
							image.setRGB(l, k, new Color((int)(sumr / doubleSize), (int)(sumg / doubleSize), (int)(sumb / doubleSize)).getRGB());
						}
					}
					sumr = 0;
					sumg = 0;
					sumb = 0;
				}
			}
			File outputfile = new File("TempImage.jpg");
			ImageIO.write(image, "jpg", outputfile);
		
			
			
			//load image on screen
			BufferedImage img;
			
				img = ImageIO.read(new File("TempImage.jpg"));
				double percentageWidth = 445/(double)img.getWidth();
				double percentageHeight = 410/(double)img.getHeight();
				Image newImage;
				if (percentageWidth*img.getHeight()<=410) {
					newImage = img.getScaledInstance((int)(percentageWidth * (double)img.getWidth()), 410, Image.SCALE_DEFAULT);
				}else {
					newImage = img.getScaledInstance(445, (int)(percentageHeight * (double)img.getHeight()), Image.SCALE_DEFAULT);
				}
				ImageIcon icon = new ImageIcon(newImage);
				ImagePlace.setIcon(icon);
			} catch (IOException e) {
				ImagePlace.setText("Wrong image");
				e.printStackTrace();
			}
			btnToSaveJPGImg.setVisible(true);
			btnPixelItTo.setVisible(true);
		}
	}

	//Thread saved image in the wanted location and remove the button.
	class SaveAndRemove extends Thread {
		public void run(){
			
			try {
				
				btnToSaveJPGImg.setVisible(false);
				BufferedImage imageToSave = ImageIO.read(new File("TempImage.jpg")); 
				ImageOutputStream output;
				// create a new BufferedOutputStream with the last argument
				String outputName = "";

				if (!locationPath.getText().equals("")) {
					outputName+=locationPath.getText();   
				}
				if (!output_name.getText().equals("")) {
					outputName += "\\"+output_name.getText()+".jpg";
				} else {
					outputName += "\\output.jpg";
				}
				output = new FileImageOutputStream(new File(outputName));
				ImageIO.write(imageToSave, "jpg", output);
				
				output.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 			
			btnToSaveJPGImg.setVisible(false);
			savedLbl.setVisible(true);
			
			} 
		}

	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PixelIt window = new PixelIt();
					window.frame.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public PixelIt() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */

	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 912, 485);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		btnNewButton = new JButton("Pixel It To Gif!");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				savedLbl.setVisible(false);
				PixelAndGifThread myThread = new PixelAndGifThread();
				myThread.start();
				
			}
		});

		btnNewButton.setBackground(new Color(51, 204, 102));
		btnNewButton.setFont(new Font("David", Font.BOLD, 17));
		btnNewButton.setBounds(12, 313, 186, 28);
		frame.getContentPane().add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("Choose Picture:");
		btnNewButton_1.setFont(new Font("David", Font.BOLD, 15));
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				savedLbl.setVisible(false);
				ChoosePhotoThread myThread = new ChoosePhotoThread();
				myThread.start();

			}
		});
		btnNewButton_1.setBounds(143, 13, 139, 25);
		frame.getContentPane().add(btnNewButton_1);

		btnSaveLocation = new JButton("Save At:");
		btnSaveLocation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				savedLbl.setVisible(false);
				SaveLocationThread myThread = new SaveLocationThread();
				myThread.start();

			}
		});
		btnSaveLocation.setFont(new Font("David", Font.BOLD, 15));
		btnSaveLocation.setBounds(143, 120, 139, 25);
		frame.getContentPane().add(btnSaveLocation);
		
		btnPixelItTo = new JButton("Pixel It To JPG!");
		btnPixelItTo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				savedLbl.setVisible(false);
				PixelAndShowThread myThread = new PixelAndShowThread();
				myThread.start();

			}
		});
		btnPixelItTo.setFont(new Font("David", Font.BOLD, 17));
		btnPixelItTo.setBackground(new Color(51, 204, 102));
		btnPixelItTo.setBounds(225, 313, 195, 28);
		frame.getContentPane().add(btnPixelItTo);
		
		btnToSaveJPGImg = new JButton("Save it?");
		btnToSaveJPGImg.setVisible(false);
		btnToSaveJPGImg.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				SaveAndRemove myThread = new SaveAndRemove();
				myThread.start();
				
			}
		});
		btnToSaveJPGImg.setFont(new Font("David", Font.BOLD, 17));
		btnToSaveJPGImg.setBackground(new Color(51, 204, 102));
		btnToSaveJPGImg.setBounds(225, 272, 195, 28);
		frame.getContentPane().add(btnToSaveJPGImg);
		

		textFieldOfImage = new JTextField();
		textFieldOfImage.setHorizontalAlignment(SwingConstants.CENTER);
		textFieldOfImage.setFont(new Font("David", Font.BOLD, 17));
		textFieldOfImage.setBounds(12, 50, 408, 22);
		frame.getContentPane().add(textFieldOfImage);
		textFieldOfImage.setColumns(10);

		comboBoxNumberOfFrames = new JComboBox<String>();
		comboBoxNumberOfFrames.setFont(new Font("David", Font.BOLD, 19));
		comboBoxNumberOfFrames.setModel(new DefaultComboBoxModel<String>(new String[] {"10", "20", "30", "40", "50"}));
		comboBoxNumberOfFrames.setBounds(109, 85, 49, 22);
		frame.getContentPane().add(comboBoxNumberOfFrames);

		JLabel lblNumberOfFrames = new JLabel("Number Of Frames");
		lblNumberOfFrames.setHorizontalAlignment(SwingConstants.CENTER);
		lblNumberOfFrames.setFont(new Font("David", Font.BOLD, 15));
		lblNumberOfFrames.setBounds(179, 85, 139, 28);
		frame.getContentPane().add(lblNumberOfFrames);

		textField = new JTextField();
		textField.setHorizontalAlignment(SwingConstants.CENTER);
		textField.setFont(new Font("David", Font.BOLD, 21));
		textField.setBounds(17, 383, 408, 40);
		frame.getContentPane().add(textField);
		textField.setColumns(10);

		lblProgress = new JLabel("Status");
		lblProgress.setHorizontalAlignment(SwingConstants.CENTER);
		lblProgress.setFont(new Font("David", Font.BOLD, 15));
		lblProgress.setBounds(190, 354, 49, 16);
		frame.getContentPane().add(lblProgress);
		
		locationPath = new JTextField();
		locationPath.setHorizontalAlignment(SwingConstants.CENTER);
		locationPath.setFont(new Font("David", Font.BOLD, 17));
		locationPath.setColumns(10);
		locationPath.setBounds(12, 158, 408, 22);
		frame.getContentPane().add(locationPath);

		output_name = new JTextField();
		output_name.setHorizontalAlignment(SwingConstants.CENTER);
		output_name.setFont(new Font("David", Font.BOLD, 17));
		output_name.setColumns(10);
		output_name.setBounds(12, 222, 408, 22);
		frame.getContentPane().add(output_name);

		JLabel lblOutputName = new JLabel("Output Name");
		lblOutputName.setHorizontalAlignment(SwingConstants.CENTER);
		lblOutputName.setFont(new Font("David", Font.BOLD, 15));
		lblOutputName.setBounds(143, 193, 139, 28);
		frame.getContentPane().add(lblOutputName);

		ImagePlace = new JLabel("");
		ImagePlace.setHorizontalAlignment(SwingConstants.CENTER);
		ImagePlace.setBounds(437, 13, 445, 410);
		frame.getContentPane().add(ImagePlace);
		
		savedLbl = new JLabel("Saved");
		savedLbl.setVisible(false);
		savedLbl.setFont(new Font("David", Font.BOLD, 16));
		savedLbl.setHorizontalAlignment(SwingConstants.CENTER);
		savedLbl.setBounds(366, 257, 56, 16);
		frame.getContentPane().add(savedLbl);
		
		label = new JLabel("*");
		label.setForeground(new Color(255, 0, 0));
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setFont(new Font("David", Font.BOLD, 18));
		label.setBounds(118, 13, 25, 28);
		frame.getContentPane().add(label);
	}
}
