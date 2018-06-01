package com.chen.answerer.util;

import com.chen.answerer.window.MainWindow;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Enumeration;

public class CommonUtil {

    private static Point origin = new Point();

    public static void initGlobalFont(){
        FontUIResource fontUIResource = new FontUIResource(new Font("微软雅黑",Font.PLAIN, 18));
        for (Enumeration keys = UIManager.getDefaults().keys(); keys.hasMoreElements();) {
            Object key = keys.nextElement();
            Object value= UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, fontUIResource);
            }
        }
    }


    public static Font getTitleFont(float size) {
        Font definedFont = null;
        InputStream is = null;
        BufferedInputStream bis = null;
        try {

            is=MainWindow.class.getResourceAsStream("../font/title_text.ttf");//Class类的一个方法,将本地的资源加载成一个输入流
            bis=new BufferedInputStream(is);
            definedFont=Font.createFont(Font.TRUETYPE_FONT,bis);//使用TRUETYPE类型的字体来创建新的字体
            definedFont=definedFont.deriveFont(size);//复制当前字体对象并应用新的大小来创建一个新的字体

        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != bis)
                    bis.close();
                if (null != is)
                    is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return definedFont;
    }
    public static void addMouseMotionListener(JFrame frame){
//        AWTUtilities.setWindowOpaque(frame, false);
        frame.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                origin.x = e.getX();
                origin.y = e.getY();
            }
        });
        frame.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point p = frame.getLocation();
                frame.setLocation(p.x + e.getX() - origin.x, p.y + e.getY()- origin.y);
            }
        });
    }

    public static String getImageFromClipboard() throws Exception{
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
            if(!path.exists() && !path.mkdirs()){
                    return null;
            }
            Date dt= new Date();
            Long time= dt.getTime();
            File file = new File(path, time + ".png");
            ImageIO.write(bi, "png", file);
            return file.getPath();
        }
        return null;
    }

}
