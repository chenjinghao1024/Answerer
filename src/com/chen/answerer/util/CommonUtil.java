package com.chen.answerer.util;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.util.Enumeration;

public class CommonUtil {

    public static void initGlobalFont(){
        FontUIResource fontUIResource = new FontUIResource(new Font("宋体",Font.PLAIN, 18));
        for (Enumeration keys = UIManager.getDefaults().keys(); keys.hasMoreElements();) {
            Object key = keys.nextElement();
            Object value= UIManager.get(key);
            if (value instanceof FontUIResource) {
                UIManager.put(key, fontUIResource);
            }
        }
    }

}
