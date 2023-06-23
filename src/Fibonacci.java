import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.JButton;
import java.awt.GridLayout;
import java.awt.Font;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.util.List;
import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class Fibonacci extends JFrame {

	private JPanel contentPane;
	private JPanel pnInput;
	private JPanel pnStatus;
	private JLabel lblRow;
	private JTextField textRow;
	private JButton btnGetsum;
	private JButton btnCancel;
	private JProgressBar progressBar;
	private JLabel lblStatus;
	private JScrollPane pnList;
	private JTextArea textArea;
	private SwingWorker worker; //define worker as global variable, so that it can be used everywhere.
	int sum = 0; //define sum to calculate sum of fibonacci

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Fibonacci frame = new Fibonacci();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	//method to calculate the fibonacci sequence
	public int fiboCalculate(int number) {
		if (number == 1)
			return 0;
		else if (number == 2)
			return 1;
		return fiboCalculate(number - 1) + fiboCalculate(number - 2);
	}

	/**
	 * Create the frame.
	 */
	//GUI using window builder Design
	public Fibonacci() {
		setTitle("Finding Fibonacci series sum");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 860, 410);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		pnInput = new JPanel();
		contentPane.add(pnInput, BorderLayout.NORTH);
		pnInput.setLayout(new GridLayout(0, 4, 0, 0));
		
		lblRow = new JLabel("Number of row in Fibonacci series:");
		lblRow.setFont(new Font("Arial", Font.BOLD, 12));
		pnInput.add(lblRow);
		
		textRow = new JTextField();
		pnInput.add(textRow);
		textRow.setColumns(10);
		
		btnGetsum = new JButton("Get Sum of Fibonacci series");
		btnGetsum.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				try {
					//get user input value from textRow field
		            int inputValue = Integer.parseInt(textRow.getText());
		            start();
		            btnGetsum.setEnabled(false); //disable btnGetsum after start 
		            btnCancel.setEnabled(true); //enable btnCancel after start
		        } catch (NumberFormatException ex) {
		            //exception handling when a non-numeric value is entered
		            JOptionPane.showMessageDialog(null, "Enter valid input!");
		        }
				
			}
		});
		btnGetsum.setFont(new Font("Arial", Font.BOLD, 12));
		pnInput.add(btnGetsum);
		
		btnCancel = new JButton("Cancel");
		btnCancel.setEnabled(false); //disable btnCancel by default (when first starting)
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				worker.cancel(true); //stop calculation using cancel method
				btnGetsum.setEnabled(true); //enable btnGetsum when stopped
				btnCancel.setEnabled(false); //disable btnCancel when stopped
				
			}
		});
		btnCancel.setFont(new Font("Arial", Font.BOLD, 12));
		pnInput.add(btnCancel);
		
		pnStatus = new JPanel();
		contentPane.add(pnStatus, BorderLayout.SOUTH);
		pnStatus.setLayout(new GridLayout(0, 2, 0, 0));
		
		progressBar = new JProgressBar();
		pnStatus.add(progressBar);
		
		lblStatus = new JLabel("Sum = 0");
		lblStatus.setFont(new Font("Arial Black", Font.BOLD, 12));
		pnStatus.add(lblStatus);
		
		textArea = new JTextArea();
		
		pnList = new JScrollPane(textArea);
		contentPane.add(pnList, BorderLayout.CENTER);
	}
	
	private void start() {
		worker = new SwingWorker<Void, Integer>() {

			@Override
			protected Void doInBackground() throws Exception {
				
				int UserInput = Integer.parseInt(textRow.getText());
				textArea.setText(""); //make clean textArea each execution
				sum = 0; //make sum value 0 each execution
				
				for (int i = 1; i <= UserInput; i++) {
					if (isCancelled()) //check if cancel is true
						break;
					Thread.sleep(50);
					sum += fiboCalculate(i); //calculate sum
					
					
					
					//publish user input, current i value, current sum value
					publish(UserInput);
					publish(i);
					publish(sum);
				}
				
				btnGetsum.setEnabled(true); //enable btnGetsum when calculation is done or break
				btnCancel.setEnabled(false); //disable btnCancel when calculation is done or break
				return null;
				
			}
			
			@Override
			protected void process(List<Integer> chunks) {
				
				Integer UserInput = chunks.get(chunks.size() - 3); //user input value
				Integer i = chunks.get(chunks.size() - 2); //current i value
				Integer sum = chunks.get(chunks.size() - 1); //current sum value
				
				textArea.append(fiboCalculate(i)+"\n"); //output the fibonacci value of each step
				progressBar.setStringPainted(true);
				progressBar.setValue((i)*100 / UserInput); //percentage of progress
				lblStatus.setText("Sum = " + sum); //print sum in lblStatus field
				
			}
			
			@Override
			protected void done() {
				
				btnGetsum.setEnabled(true); //enable btnGetsum when execution is done
				btnCancel.setEnabled(false); //disable btnCancel when execution is done
				
				//write textArea as array form in file.txt
				//contents of the file are initialized on each run.
				try {
				    FileOutputStream fileStream = new FileOutputStream("file.txt", false);
				    PrintWriter writer = new PrintWriter(fileStream);

				    String[] lines = textArea.getText().split("\\r?\\n");
				    writer.print("[");
				    for (int i = 0; i < lines.length; i++) {
				        writer.print(lines[i]);
				        if (i != lines.length - 1) {
				            writer.print(", ");
				        }
				    }
				    writer.println("]");
				    writer.close();
				} catch (FileNotFoundException e1) {
				    //catch FileNotFoundException
					e1.printStackTrace();
				}
				
			}
		};
		
		worker.execute(); //execute worker	
	}
	
}
