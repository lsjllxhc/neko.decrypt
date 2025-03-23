package com.neko.decrypt;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.*;
import java.nio.charset.StandardCharsets;

import com.neko.decrypt.UnLocker.*;

import static com.neko.decrypt.UnLocker.processFiles;

public class GUI extends JFrame {
    private JTextField inputPathField;
    private JTextField outputPathField;
    private JTextArea console;

    public GUI() {
        setTitle("UnLocker");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        // 设置窗口图标
        File iconFile = new File("logo.ico");
        if (iconFile.exists()) {
            setIconImage(Toolkit.getDefaultToolkit().getImage("logo.ico"));
        } else {
            JOptionPane.showMessageDialog(this, "未找到ico文件", "警告", JOptionPane.WARNING_MESSAGE);
        }

        // 创建菜单栏
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        // 创建“文件”菜单
        JMenu fileMenu = new JMenu("文件");
        menuBar.add(fileMenu);

        // 关于软件菜单项
        JMenuItem aboutMenuItem = new JMenuItem("关于软件");
        aboutMenuItem.addActionListener(e -> showAboutDialog());
        fileMenu.add(aboutMenuItem);

        // 打开输入文件夹菜单项
        JMenuItem openInputMenuItem = new JMenuItem("打开输入文件夹");
        openInputMenuItem.addActionListener(e -> openInputFolder());
        fileMenu.add(openInputMenuItem);

        // 打开输出文件夹菜单项
        JMenuItem openOutputMenuItem = new JMenuItem("打开输出文件夹");
        openOutputMenuItem.addActionListener(e -> openOutputFolder());
        fileMenu.add(openOutputMenuItem);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 输入文件夹选择器
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("输入文件夹:"), gbc);

        gbc.gridx = 1;
        inputPathField = new JTextField(30);
        add(inputPathField, gbc);

        gbc.gridx = 2;
        JButton inputButton = new JButton("选择");
        inputButton.addActionListener(e -> selectInputFolder());
        add(inputButton, gbc);

        // 输出文件夹选择器
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("输出文件夹:"), gbc);

        gbc.gridx = 1;
        outputPathField = new JTextField(30);
        add(outputPathField, gbc);

        gbc.gridx = 2;
        JButton outputButton = new JButton("选择");
        outputButton.addActionListener(e -> selectOutputFolder());
        add(outputButton, gbc);

        // 运行按钮
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        JButton runButton = new JButton("运行");
        runButton.addActionListener(e -> runCommand());
        add(runButton, gbc);

        // 控制台
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        console = new JTextArea();
        console.setFont(new Font("Monospaced", Font.PLAIN, 12)); // 设置字体，确保支持中文字符
        console.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(console);
        add(scrollPane, gbc);

        // 显示欢迎对话框
        showWelcomeDialog();
    }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(this, "2025.3.23-jdk21-windows-v0.6.0", "关于软件", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showWelcomeDialog() {
        JOptionPane.showMessageDialog(this, "欢迎使用Neko.UnLocker.Decrypt,制作者Histrem Rakik。", "欢迎", JOptionPane.INFORMATION_MESSAGE);
    }

    private void selectInputFolder() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            inputPathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
        } else {
            JOptionPane.showMessageDialog(this, "未选择输入文件夹", "警告", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void selectOutputFolder() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            outputPathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
        } else {
            JOptionPane.showMessageDialog(this, "未选择输出文件夹", "警告", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void openInputFolder() {
        String inputDir = inputPathField.getText();
        if (inputDir.isEmpty()) {
            JOptionPane.showMessageDialog(this, "未选择输入文件夹", "警告", JOptionPane.WARNING_MESSAGE);
        } else {
            // 打开输入文件夹的逻辑
        }
    }

    private void openOutputFolder() {
        String outputDir = outputPathField.getText();
        if (outputDir.isEmpty()) {
            JOptionPane.showMessageDialog(this, "未选择输出文件夹", "警告", JOptionPane.WARNING_MESSAGE);
        } else {
            // 打开输出文件夹的逻辑
        }
    }

    private void runCommand() {
        String inputDir = inputPathField.getText();
        String outputDir = outputPathField.getText();

        if (inputDir.isEmpty()) {
            JOptionPane.showMessageDialog(this, "无输入文件夹", "警告", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 清除 UnLock.log 文件内容
        clearLogFile();

        try {
            if (!outputDir.isEmpty()) {
                processFiles(inputDir, outputDir);
                // 显示 UnLock.log 文件内容
                displayLogFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "处理文件时出错: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearLogFile() {
        try (PrintWriter writer = new PrintWriter("UnLock.log")) {
            writer.print("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void displayLogFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("UnLock.log"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                console.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            GUI app = new GUI();
            app.setVisible(true);
        });
    }
}