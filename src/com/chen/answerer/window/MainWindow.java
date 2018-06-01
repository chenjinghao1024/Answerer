package com.chen.answerer.window;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chen.answerer.listener.ClosedLitener;
import com.chen.answerer.util.CommonUtil;
import com.chen.answerer.util.HttpClientUtil;
import com.chen.answerer.util.KeyListener;
import com.chen.answerer.util.OcrRecognition;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MainWindow extends JFrame {

    private final static String VERSION = "0.0.2 Beta";
    private final static String AUTHOR = "Mr.D,Hong";

    private final int screenWidth;
    private final int screenHeight;
    private int windowHeight;


    private JLabel question;
    private final JLabel logLabel;
    private final JLabel versionLabel;
    private final JLabel authorLabel;
    private JLabel answerTitle;

    private JPanel mainPanel;
    private JPanel answerPanel;

    private static KeyListener keyListener;

    private static MainWindow mainWindow;
    private static HttpClientUtil httpClientUtil;

    private final String url = "https://nsh.leanapp.cn/main/data";


    public static void main(String[] args){
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        EventQueue.invokeLater(() -> {
            try{
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                System.out.println("运行程序");
                MainWindow frame = new MainWindow();
                frame.setVisible(true);
                frame.setResizable(false);
                new Thread(() -> keyListener = new KeyListener(mainWindow)).start();
                httpClientUtil = new HttpClientUtil();
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }


    private MainWindow() throws HeadlessException {
        super("答题");
        mainWindow = this;
        CommonUtil.initGlobalFont();
        CommonUtil.addMouseMotionListener(this);

        this.setUndecorated(true);

        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        screenWidth = (int)screensize.getWidth();
        screenHeight = (int)screensize.getHeight();

        setDefaultCloseOperation (WindowConstants.EXIT_ON_CLOSE);
        windowHeight = 300;
        setBounds(screenWidth - 500, 0, 500, windowHeight);
        setAlwaysOnTop(true);
        setResizable(false);
        setFocusable(true);

        mainPanel = new JPanel();
        mainPanel.setBackground(Color.white);
        mainPanel.setBorder(new LineBorder(new Color(157,157,157),1));
        setContentPane(mainPanel);
        mainPanel.setLayout(null);


        // 窗口顶部蓝色头
        JLabel titleLabel = new JLabel();
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(50,219,230));
        titleLabel.setBounds(1, 1, 498, 5);
        mainPanel.add(titleLabel);

        // 关闭按钮
        JButton closeButton = new JButton(
                new ImageIcon(this.getClass().getResource("../icon/X_close_32px.png")));
        closeButton.setContentAreaFilled(false);
        closeButton.setBorderPainted(false);//不绘制边框
        closeButton.setBounds(500-32, 6, 32, 32);
        ClosedLitener closedLitener = new ClosedLitener();
        closeButton.addMouseListener(closedLitener);
        closeButton.addActionListener(closedLitener);
        mainPanel.add(closeButton);

        // 标题
        JLabel titleText = new JLabel("逆水寒科举答题工具",JLabel.CENTER);
        titleText.setFont(CommonUtil.getTitleFont(30));
        titleText.setForeground(new Color(255,121,121));
        titleText.setBounds(0, 20, 500, 50);
        mainPanel.add(titleText);

        // 日志窗口
        logLabel = new JLabel("[]~(￣▽￣)~*");
        logLabel.setBorder(new LineBorder(new Color(157,157,157),2));
        logLabel.setBounds(70, 100, 360, 30);
        mainPanel.add(logLabel);

        JLabel questionTitle = new JLabel("识别图片【~】");
        questionTitle.setFont(CommonUtil.getTitleFont(20));
        questionTitle.setForeground(new Color(157,157,157));
        questionTitle.setBounds(70, 150, 360, 30);
        mainPanel.add(questionTitle);

        versionLabel = new JLabel("Version:"+VERSION,JLabel.CENTER);
        versionLabel.setFont(CommonUtil.getTitleFont(20));
        versionLabel.setForeground(new Color(255,121,121));
        versionLabel.setBounds(0, 200, 500, 50);
        mainPanel.add(versionLabel);

        authorLabel = new JLabel("By:"+AUTHOR,JLabel.CENTER);
        authorLabel.setFont(CommonUtil.getTitleFont(20));
        authorLabel.setForeground(new Color(255,121,121));
        authorLabel.setBounds(0, 230, 500, 50);
        mainPanel.add(authorLabel);
    }

    public void getQuestion() throws Exception {
        setLog("获取图片啦!");
        String path = CommonUtil.getImageFromClipboard();
        setLog("识别图片啦!");
        String questionText=OcrRecognition.imageToText(path);
        setLog("识别完啦!");
        if(questionText == null){
            setLog("截图!截图!截图!说三遍!(◦`~´◦)");
            return;
        }
        addQuestion();
        question.setText("<html><body>"+questionText+"</body></html>");
        setLog("获取答案中...");
        getAnswer(questionText);
    }

    private void addQuestion() {
        if(question==null){
            question = new JLabel();
            question.setBounds(70, 180, 360, 100);
            question.setBorder(new LineBorder(new Color(157,157,157),2));
            mainPanel.add(question);
            resetWindowHigh(100);
        }

    }

    private void setLog(String log){
        logLabel.setText(log);
    }

    private void getAnswer(String questionText) {
        Map<String,String> params = new HashMap<>();
        params.put("content",questionText);
        params.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        String result = httpClientUtil.doPost(url,params,"utf-8");

        setAnswerToWindow(JSON.parseArray(result));
    }
    private void setAnswerToWindow(JSONArray answers){
        if(answers.size() == 0){

            setLog("查无此题!生死由命!嗯!(ー`´ー)");
            return;
        }
//        setBounds(screenWidth - 500, 0, 500, 1000);

        answerTitle = new JLabel("题库答案");
        answerTitle.setFont(CommonUtil.getTitleFont(20));
        answerTitle.setForeground(new Color(157,157,157));
        answerTitle.setBounds(70, 300, 360, 50);
        answerTitle.setBorder(new LineBorder(new Color(157,157,157),2));
        mainPanel.add(answerTitle);

        answers.forEach(anwser -> setAnswerToWindow((JSONObject) anwser));


        setLog("搞定啦啦啦啦啦~(◦˙▽˙◦)");
    }

    private void setAnswerToWindow(JSONObject answer){
        System.out.println(answer.toString());
    }

    private void resetWindowHigh(int high){
        setBounds(getX(), getY(), getWidth(), windowHeight+high);
        versionLabel.setBounds(versionLabel.getX(), versionLabel.getY()+high,
                versionLabel.getWidth(), versionLabel.getHeight());
        authorLabel.setBounds(authorLabel.getX(), authorLabel.getY()+high,
                authorLabel.getWidth(), authorLabel.getHeight());
        windowHeight += high;
        mainPanel.updateUI();
    }
}
