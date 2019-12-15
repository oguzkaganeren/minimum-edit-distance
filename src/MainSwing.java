import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MainSwing extends JFrame {

	private JPanel contentPane;
	private static ArrayList<String> dictionary;
	private static HashMap<String, Integer> listOfSimilarity=new HashMap<>();
	private static final int ITEMCOUNT=10;//count of showing item on list
	private JTextField textField;
	private JList<String> list;
	private JLabel lblResultTime;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainSwing frame = new MainSwing();
					frame.setVisible(true);
					readFile("tr_dictionary.txt");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	//return us minimum value
	public static int minimum(int a, int b, int c)
	{
		return Integer.min(a, Integer.min(b, c));
	}
	//inspired by https://www.techiedelight.com/levenshtein-distance-edit-distance-problem/
	public static int editDist(String word, String string,int sizeOfWord, int sizeOfString)
	{
		//create a matrix
		int[][] matrix = new int[sizeOfWord + 1][sizeOfString + 1];
		for (int i = 1; i <= sizeOfWord; i++)
			matrix[i][0] = i;				// add numbers for word to matrix

		for (int j = 1; j <= sizeOfString; j++)
			matrix[0][j] = j;				// add numbers for string to matrix

		//we made the matrix
		int cost;

		// start to fill the matrix
		for (int i = 1; i <= sizeOfWord; i++)
		{
			for (int j = 1; j <= sizeOfString; j++)
			{
				if (word.charAt(i-1) == string.charAt(j-1))		
					cost = 0;//its same character, so cost is 0		
				else
					cost = 1;//not same character			
				//matrix'i üstten başlayarak gittiğimizi düşündüğümüzde
				//sol üst, sol ve üst kısmında kalan cost değerlerinin min. değere sahip
				//değeri yeni eleman olarak matrixe ekliyoruz
				matrix[i][j] = minimum(matrix[i - 1][j] + 1,	// deletion
						matrix[i][j - 1] + 1,			// insertion
						matrix[i - 1][j - 1] + cost);	// replace
			}
		}
		//köşedeki sayıyı geri döndürüyoruz
		return matrix[sizeOfWord][sizeOfString];
	}

	// readFile function helps to read txt file
	// it gets one parameter that is name of the file
	// it puts the words in global dictionary variable
	public static void readFile(String fileName) {
		dictionary = new ArrayList<String>();
		try {
			// Some uni-character problems will fix by Windows-1254, if it occurs.
			BufferedReader in = new BufferedReader(
					new InputStreamReader(MainSwing.class.getResourceAsStream("tr_dictionary.txt"), "WINDOWS-1254"));

			String str;
			while ((str = in.readLine()) != null) {
				dictionary.add(str);
			}

			in.close();

		} catch (UnsupportedEncodingException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Create the frame.
	 */
	public MainSwing() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 400, 500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblSearch = new JLabel("Search:");
		lblSearch.setBounds(48, 28, 70, 15);
		contentPane.add(lblSearch);

		textField = new JTextField();
		textField.setBounds(109, 26, 166, 19);
		contentPane.add(textField);
		textField.setColumns(10);

		list = new JList();
		list.setBounds(63, 152, 253, 292);
		contentPane.add(list);

		lblResultTime = new JLabel("Result Time:");
		lblResultTime.setBounds(60, 101, 250, 15);
		contentPane.add(lblResultTime);
		
		JButton btnSearch = new JButton("Search");
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(textField.getText().length()>0) {
					long startTime = System.currentTimeMillis();
					String word=textField.getText();
					//clear hashMap
					listOfSimilarity.clear();
					for (String string : dictionary) {
						int distance = editDist(word, string, word.length(), string.length());
						if(listOfSimilarity.size()<=ITEMCOUNT) {
							listOfSimilarity.put(string, distance);
						}else {
							String tempKey="";
							for (Map.Entry<String, Integer> entry : listOfSimilarity.entrySet()) {
								if(distance<entry.getValue()) {
									//if there is a bigger distance in hashmap, we will remove it
									//and add new smaller one.
									tempKey=entry.getKey();
								}
							}
							if(tempKey.length()>0) {
								listOfSimilarity.remove(tempKey);
								listOfSimilarity.put(string, distance);
							}
							
						}
					}

					DefaultListModel<String> listModel = new DefaultListModel<String>();
					for (Map.Entry<String, Integer> entry : listOfSimilarity.entrySet()) {
						listModel.addElement(entry.getKey()+"-"+entry.getValue());
					}
					
					list.setModel(listModel);
					long resultTime = System.currentTimeMillis() - startTime;
					lblResultTime.setText("Result Time: "+resultTime+" ms");
				}
				
			}
		});
		btnSearch.setBounds(130, 60, 117, 25);
		contentPane.add(btnSearch);

		
	}
}
