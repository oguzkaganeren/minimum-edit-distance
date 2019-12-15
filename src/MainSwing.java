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
	private static HashMap<Integer, String> listOfSimilarity=new HashMap<>();
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

	static int min(int x, int y, int z) {
		if (x <= y && x <= z)
			return x;
		if (y <= x && y <= z)
			return y;
		else
			return z;
	}

	// inspired by https://www.geeksforgeeks.org/edit-distance-dp-5/
	static int editDist(String str1, String str2, int m, int n) {
		// If first string is empty, the only option is to
		// insert all characters of second string into first
		if (m == 0)
			return n;

		// If second string is empty, the only option is to
		// remove all characters of first string
		if (n == 0)
			return m;

		// If last characters of two strings are same, nothing
		// much to do. Ignore last characters and get count for
		// remaining strings.
		if (str1.charAt(m - 1) == str2.charAt(n - 1))
			return editDist(str1, str2, m - 1, n - 1);

		// If last characters are not same, consider all three
		// operations on last character of first string, recursively
		// compute minimum cost for all three operations and take
		// minimum of three values.
		return 1 + min(editDist(str1, str2, m, n - 1), // Insert
				editDist(str1, str2, m - 1, n), // Remove
				editDist(str1, str2, m - 1, n - 1) // Replace
		);
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
							listOfSimilarity.put(distance, string);
						}else {
							int tempKey=-1;
							for (Map.Entry<Integer, String> entry : listOfSimilarity.entrySet()) {
								if(entry.getKey()>distance) {
									//if there is a bigger distance in hashmap, we will remove it
									//and add new smaller one.
									tempKey=entry.getKey();
									break;
								}
							}
							if(tempKey!=-1) {
								listOfSimilarity.remove(tempKey);
								listOfSimilarity.put(distance, string);
							}
							
						}
					}

					DefaultListModel<String> listModel = new DefaultListModel<String>();
					for (Map.Entry<Integer, String> entry : listOfSimilarity.entrySet()) {
						listModel.addElement(entry.getValue()+"-"+entry.getKey());
						System.out.println("here");
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
