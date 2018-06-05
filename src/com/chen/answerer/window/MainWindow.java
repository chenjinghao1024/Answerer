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

/**
 * 答题器主体
 * @author MR.D
 */
public class MainWindow extends JFrame {
    // 版本信息及作者信息
    private final static String VERSION = "v0.1.0";
    private final static String AUTHOR = "Mr.D,Hong";

    // 屏幕宽高
    private final int screenWidth;
    private final int screenHeight;

    // 窗口基础高度
    private int windowHeight = 300;

    // 问题识别结果
    private JLabel question;
    // 日志
    private final JLabel logLabel;
    // 版本
    private final JLabel versionLabel;
    // 作者
    private final JLabel authorLabel;
    // 答案组
    private JLabel answersLabel;
    // 答案标题
    private JLabel answerTitle;

    // 主窗体容器
    private JPanel mainPanel;

    //全局键盘监听
    private static KeyListener keyListener;

    // 主窗体
    private static MainWindow mainWindow;
    // http请求工具
    private static HttpClientUtil httpClientUtil;
    // 数据接口url
    private final String url = "https://nsh.leanapp.cn/main/data";


    public static void main(String[] args){
        // 设置字体渲染 : windows渲染
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        EventQueue.invokeLater(() -> {
            try{

                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                MainWindow frame = new MainWindow();
                frame.setVisible(true);
                // 不可修改大小
                frame.setResizable(false);
                new Thread(() -> keyListener = new KeyListener(mainWindow)).start();
                httpClientUtil = new HttpClientUtil();
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }


    private MainWindow() throws HeadlessException {

        mainWindow = this;
        CommonUtil.initGlobalFont();
        CommonUtil.addMouseMotionListener(this);

        // 设置无边框
        this.setUndecorated(true);
        // 获取屏幕宽高
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        screenWidth = (int)screensize.getWidth();
        screenHeight = (int)screensize.getHeight();
        // 设置默认关闭时间
        setDefaultCloseOperation (WindowConstants.EXIT_ON_CLOSE);
        // 初始高度
        setBounds(screenWidth - 500, 0, 500, windowHeight);
        setAlwaysOnTop(true);
        setResizable(false);
        setFocusable(true);
        // 主窗体
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
                new ImageIcon(this.getClass().getResource("/com/chen/answerer/icon/X_close_32px.png")));
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
        // 文字
        JLabel questionTitle = new JLabel("识别图片【~】");
        questionTitle.setFont(CommonUtil.getTitleFont(20));
        questionTitle.setForeground(new Color(157,157,157));
        questionTitle.setBounds(70, 150, 360, 30);
        mainPanel.add(questionTitle);
        // 版本信息
        versionLabel = new JLabel("Version:"+VERSION,JLabel.CENTER);
        versionLabel.setFont(CommonUtil.getTitleFont(20));
        versionLabel.setForeground(new Color(255,121,121));
        versionLabel.setBounds(0, 200, 500, 50);
        mainPanel.add(versionLabel);
        // 作者信息
        authorLabel = new JLabel("By:"+AUTHOR,JLabel.CENTER);
        authorLabel.setFont(CommonUtil.getTitleFont(20));
        authorLabel.setForeground(new Color(255,121,121));
        authorLabel.setBounds(0, 230, 500, 50);
        mainPanel.add(authorLabel);
    }

    /**
     * 主逻辑进程
     * @throws Exception 子方法异常
     */
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

    /**
     * 添加识别后问题文字
     */
    private void addQuestion() {
        if(question==null){
            question = new JLabel();
            question.setBounds(70, 180, 360, 100);
            question.setBorder(new LineBorder(new Color(157,157,157),2));
            mainPanel.add(question);
            resetWindowHigh(question.getHeight());
        }

    }

    /**
     * 设置log
     * @param log log信息
     */
    private void setLog(String log){
        logLabel.setText(log);
    }

    /**
     * 请求接口获取答案
     * @param questionText 题目文本
     */
    private void getAnswer(String questionText) {
        Map<String,String> params = new HashMap<>();
        params.put("content",questionText);
        params.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        String result = httpClientUtil.doPost(url,params,"utf-8");
        setAnswerToWindow(JSON.parseArray(result));
    }

    /**
     * 根据返回结果 处理数据并在页面上做出对应处理
     * @param answers 匹配到的题目对
     */
    private void setAnswerToWindow(JSONArray answers){

        if(answersLabel != null){
            resetWindowHigh(0-answersLabel.getHeight());
            mainPanel.remove(answersLabel);
            answersLabel = null;
        }

        if(answers.size() == 0){
            setLog("查无此题!生死由命!嗯!(ー`´ー)");
            return;
        }

//        setBounds(screenWidth - 500, 0, 500, 1000);
        // 答案标题
        if (answerTitle == null){
            answerTitle = new JLabel("题库答案");
            answerTitle.setFont(CommonUtil.getTitleFont(20));
            answerTitle.setForeground(new Color(157,157,157));
            answerTitle.setBounds(70, 280, 360, 50);
            mainPanel.add(answerTitle);
            resetWindowHigh(answerTitle.getHeight());
        }

        StringBuilder answerStr = new StringBuilder();
        answerStr.append("<html><body style = \"border: 2px solid #9d9d9d;\">\n" );
        for (Object answer1 : answers) {
            JSONObject answer = (JSONObject) answer1;
            answerStr.append("<div style=\"border: 2px dashed #cccc;padding: 10px 20px 10px 20px;width:100%;height:100px\">\n" + "<p>Q：").append(answer.getString("q")).append("</p>\n").append("<p>A：").append(answer.getString("a")).append("</p>\n").append("</div>\n");
        }
        answerStr.append("</body><html>");


        answersLabel = new JLabel(answerStr.toString());
//        answersLabel.setBackground(Color.blue);
//        answersLabel.setOpaque(true);
        answersLabel.setBounds(70, 330, 360, (120)*answers.size()+50);
        mainPanel.add(answersLabel);
        resetWindowHigh(answersLabel.getHeight());
        setLog("搞定啦啦啦啦啦~(◦˙▽˙◦)");
    }


    /**
     * 重置窗口大小 待修改
     * @param high 待定`
     */
    private void resetWindowHigh(int high){
        setBounds(getX(), getY(), getWidth(), getHeight()+high);

        versionLabel.setBounds(versionLabel.getX(), versionLabel.getY()+high,
                versionLabel.getWidth(), versionLabel.getHeight());
        authorLabel.setBounds(authorLabel.getX(), authorLabel.getY()+high,
                authorLabel.getWidth(), authorLabel.getHeight());
        mainPanel.updateUI();
    }
}
