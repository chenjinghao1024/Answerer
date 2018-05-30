package com.chen.answerer.window;

import com.chen.answerer.util.CommonUtil;
import com.chen.answerer.util.KeyListener;
import com.chen.answerer.util.OcrRecognition;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Date;

public class MainWindow extends JFrame {

    private static JLabel cutImage;
    private final JLabel question;
    private final JLabel logLabel;
    private JPanel mainPanel;

    private static KeyListener keyListener;

    private static MainWindow mainWindow;
    public static void main(String[] args){

        EventQueue.invokeLater(new Runnable(){
            @Override
            public void run(){
                try{
                    System.out.println("运行程序");
                    MainWindow frame = new MainWindow();
                    frame.setVisible(true);
                    frame.setResizable(false);
                    new Thread(() -> keyListener = new KeyListener(mainWindow)).start();

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }


    public MainWindow() throws HeadlessException {
        super("答题");
        mainWindow = this;
        CommonUtil.initGlobalFont();

        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = (int)screensize.getWidth();
        int height = (int)screensize.getHeight();

        setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);

        setBounds(width - 500, 0, 500, height);
        setAlwaysOnTop(true);
        setResizable(false);
        mainPanel = new JPanel();
        mainPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(mainPanel);
        mainPanel.setLayout(null);

        logLabel = new JLabel("[○･｀Д´･ ○]");
        logLabel.setBorder(new LineBorder(Color.black,1));
        logLabel.setBounds(20, 20, 460, 30);
        mainPanel.add(logLabel);

        cutImage = new JLabel();
        cutImage.setBounds(20, 70, 460, 300);
        cutImage.setBorder(BorderFactory.createLineBorder(Color.blue,1));
        this.add(cutImage);

        question = new JLabel();
        question.setBounds(20, 370, 460, 100);
        question.setBorder(BorderFactory.createLineBorder(Color.blue,1));
        question.setFont(new Font("宋体",Font.PLAIN, 14));
        this.add(question);
    }

    public void getQuestion() throws Exception {
        setLog("获取图片啦!");
        String path = getImageFromClipboard();
        setLog("识别图片啦!");
        String questionText=OcrRecognition.imageToText(path);
        setLog("识别完啦!");
        System.out.println(questionText);
        question.setText("<html><body>"+questionText+"</body></html>");
        setLog("获取答案中...");
    }

    private String getImageFromClipboard() throws Exception{
        Clipboard sysc = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable cc = sysc.getContents(null);
        if (cc == null){
            return null;
        }else if(cc.isDataFlavorSupported(DataFlavor.imageFlavor)){
            Image image = (Image) cc.getTransferData(DataFlavor.imageFlavor);
            int width = image.getWidth(null);
            int height = image.getHeight(null);
            BufferedImage bi = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
            Graphics g = bi.getGraphics();
            g.drawImage(image,0,0,width,height,null);
            g.dispose();
            File path = new File("d:/image");
            if(!path.exists()){
                path.mkdirs();
            }
            Date dt= new Date();
            Long time= dt.getTime();
            File file = new File(path, time + ".png");
            ImageIO.write(bi, "png", file);


            ImageIcon imageIcon = new ImageIcon(file.getPath());
            Image img = imageIcon.getImage();
            img = img.getScaledInstance(width,height, Image.SCALE_DEFAULT);
            imageIcon.setImage(img);
            cutImage.setIcon(imageIcon);
            return file.getPath();
        }
        return null;
    }

    public void setLog(String log){
        logLabel.setText(log);
    }
}
