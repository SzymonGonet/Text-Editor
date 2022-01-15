import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Scanner;

public class TextEditor extends JFrame implements ActionListener {
    
    JTextArea textArea;
    JScrollPane scrollPane;
    JSpinner fontSizeSpinner;
    JButton fontColorButton;
    JComboBox fontBox;
    JButton plainButton;
    JButton boldButton;
    JButton italicButton;
    JButton underlineButton;
    JMenuBar menuBar;
    JMenu fileMenu;
    JMenuItem openItem;
    JMenuItem saveItem;
    JMenuItem exitItem;
    JMenuItem exportPng;

    TextEditor() {
        
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Edytor Tekstu");
        this.setSize(600, 600);
        this.setLayout(new FlowLayout());
        this.setLocationRelativeTo(null);

        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Arial", Font.PLAIN,20));

        plainButton = new JButton("Plain");
        plainButton.addChangeListener(changeEvent -> textArea.setFont(new Font(textArea.getFont().getName(), Font.PLAIN,(int) fontSizeSpinner.getValue())));

        boldButton = new JButton("Bold");
        boldButton.setFont(new Font(boldButton.getFont().getName(),Font.BOLD, boldButton.getFont().getSize()));
        boldButton.addChangeListener(changeEvent -> textArea.setFont(new Font(textArea.getFont().getName(), Font.BOLD, (int) fontSizeSpinner.getValue())));

        italicButton = new JButton("Italic");
        italicButton.setFont(new Font(italicButton.getFont().getName(),Font.ITALIC, italicButton.getFont().getSize()));
        italicButton.addChangeListener(changeEvent -> textArea.setFont(new Font(textArea.getFont().getName(), Font.ITALIC, (int) fontSizeSpinner.getValue())));

        underlineButton = new JButton("Underline text");
        underlineButton.addChangeListener(changeEvent -> {
            Font font = textArea.getFont();
            Map attributes = font.getAttributes();
            attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
            textArea.setFont(font.deriveFont(attributes));
        });

        scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(550, 450));
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        fontSizeSpinner = new JSpinner();
        fontSizeSpinner.setPreferredSize(new Dimension(50,25));
        fontSizeSpinner.setValue(20);
        fontSizeSpinner.addChangeListener(e -> textArea.setFont(new Font(textArea.getFont().getFamily(),Font.PLAIN,(int) fontSizeSpinner.getValue())));
        
        fontColorButton = new JButton("Color");
        fontColorButton.addActionListener(this);

        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        
        fontBox = new JComboBox(fonts);
        fontBox.addActionListener(this);
        fontBox.setSelectedItem("Arial");

        //MENU BAR
        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        openItem = new JMenuItem("Open");
        saveItem = new JMenuItem("Save as...");
        exitItem = new JMenuItem("Exit");
        exportPng = new JMenuItem("Export to PNG");

        openItem.addActionListener(this);
        saveItem.addActionListener(this);
        exportPng.addActionListener(this);
        exitItem.addActionListener(this);

        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        fileMenu.add(exportPng);
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);

        this.setJMenuBar(menuBar);
        this.add(plainButton);
        this.add(boldButton);
        this.add(italicButton);
        this.add(underlineButton);
        this.add(fontSizeSpinner);
        this.add(fontColorButton);
        this.add(fontBox);
        this.add(scrollPane);
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getSource()==fontColorButton) {
            Color color = JColorChooser.showDialog(null, "Choose a color", Color.black);
            textArea.setForeground(color);
        }

        if(e.getSource()==fontBox) {
            textArea.setFont(new Font((String)fontBox.getSelectedItem(),Font.PLAIN,textArea.getFont().getSize()));
        }

        if(e.getSource()==openItem) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File("."));
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Text files", "txt");
            fileChooser.setFileFilter(filter);

            int response = fileChooser.showOpenDialog(null);

            if(response == JFileChooser.APPROVE_OPTION) {
                File file = new File(fileChooser.getSelectedFile().getAbsolutePath());

                try (Scanner fileIn = new Scanner(file)) {
                    if (file.isFile()) {
                        while (fileIn.hasNextLine()) {
                            String line = fileIn.nextLine() + "\n";
                            textArea.append(line);
                        }
                    }
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
            }
        }

        if(e.getSource()==saveItem) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File("."));
            fileChooser.setFileFilter(new FileNameExtensionFilter("Text files (*.txt)", "*.txt"));

            int response = fileChooser.showSaveDialog(null);

            if(response == JFileChooser.APPROVE_OPTION) {
                File file;

                file = new File(fileChooser.getSelectedFile().getAbsolutePath());
                try (PrintWriter fileOut = new PrintWriter(file)) {
                    fileOut.println(textArea.getText());
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
            }
        }

        if (e.getSource()==exportPng) {
            String text = textArea.getText();
            int height = textArea.getFont().getSize();
            //int width = textArea.getText().length();
            BufferedImage bufferedImage = new BufferedImage(textArea.getWidth(), 2*height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = bufferedImage.createGraphics();
            graphics2D.setColor(Color.WHITE);
            graphics2D.fillRect(0,0,bufferedImage.getWidth(),bufferedImage.getHeight());
            graphics2D.setColor(textArea.getForeground());
            graphics2D.setFont(new Font((String) fontBox.getSelectedItem(), Font.PLAIN, (int) fontSizeSpinner.getValue()));
            graphics2D.drawString(text, 10, (4*height)/3);
            try {
                ImageIO.write(bufferedImage, "jpg", new File(
                        "./image.jpg"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            System.out.println("Image Created");
        }

        if(e.getSource()==exitItem) {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        new TextEditor();
    }
}
