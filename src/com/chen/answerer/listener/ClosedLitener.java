package com.chen.answerer.listener;

import com.chen.answerer.window.MainWindow;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ClosedLitener implements MouseListener,ActionListener {

    @Override
    public void mouseClicked(MouseEvent e) {
        System.exit(0);
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        JButton button = (JButton) e.getSource();
        button.setIcon(new ImageIcon(MainWindow.class.getResource("/com/chen/answerer/icon/X_close_active.png")));
    }

    @Override
    public void mouseExited(MouseEvent e) {
        JButton button = (JButton) e.getSource();
        button.setIcon(new ImageIcon(MainWindow.class.getResource("/com/chen/answerer/icon/X_close_32px.png")));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.exit(0);
    }
}
