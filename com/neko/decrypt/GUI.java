package com.neko.decrypt;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.neko.decrypt.UnLocker.*;

import static com.neko.decrypt.UnLocker.processFiles;

public class GUI extends JFrame {
    private JTextField inputPathField;
    private JTextField outputPathField;
    private JTextArea console;
    private JButton runButton;
    private JCheckBox setOutputAsInputCheckBox;
    private JCheckBox overwriteCheckBox;
    private boolean isCoverage = false;

    public GUI() {
        setTitle("UnLocker");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // 设置为不进行任何操作
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        // 设置程序图标
        setIconImage(Toolkit.getDefaultToolkit().getImage("logo.ico"));

        // 添加窗口监听器
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (JOptionPane.showConfirmDialog(GUI.this,
                        "您确定要关闭窗口吗?", "确认关闭",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });

        // 创建菜单栏
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        // 创建“文件”菜单
        JMenu fileMenu = new JMenu("文件");
        menuBar.add(fileMenu);

        // 打开输入文件夹菜单项
        JMenuItem openInputMenuItem = new JMenuItem("打开输入文件夹");
        openInputMenuItem.addActionListener(e -> openInputFolder());
        fileMenu.add(openInputMenuItem);

        // 打开输出文件夹菜单项
        JMenuItem openOutputMenuItem = new JMenuItem("打开输出文件夹");
        openOutputMenuItem.addActionListener(e -> openOutputFolder());
        fileMenu.add(openOutputMenuItem);

        // 创建“控制台”菜单
        JMenu consoleMenu = new JMenu("控制台");
        menuBar.add(consoleMenu);

        // 清空控制台菜单项
        JMenuItem clearConsoleMenuItem = new JMenuItem("清空");
        clearConsoleMenuItem.addActionListener(e -> clearConsole());
        consoleMenu.add(clearConsoleMenuItem);

        // 创建“帮助”菜单
        JMenu helpMenu = new JMenu("帮助");
        menuBar.add(helpMenu);

        // 关于软件菜单项
        JMenuItem aboutMenuItem = new JMenuItem("关于软件");
        aboutMenuItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutMenuItem);

        // 许可证菜单项
        JMenuItem licenseMenuItem = new JMenuItem("许可证");
        licenseMenuItem.addActionListener(e -> showLicenseDialog());
        helpMenu.add(licenseMenuItem);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 输入文件夹选择器
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("输入目录:"), gbc);

        gbc.gridx = 1;
        inputPathField = new JTextField(30);
        add(inputPathField, gbc);

        gbc.gridx = 2;
        JButton inputButton = new JButton("选择");
        inputButton.setPreferredSize(new Dimension(60, 30)); // 设置为合适的大小
        inputButton.addActionListener(e -> selectInputFolder());
        add(inputButton, gbc);

        // 输出文件夹选择器
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("输出目录:"), gbc);

        gbc.gridx = 1;
        outputPathField = new JTextField(30);
        add(outputPathField, gbc);

        gbc.gridx = 2;
        JButton outputButton = new JButton("选择");
        outputButton.setPreferredSize(new Dimension(30, 30)); // 设置为合适的大小
        outputButton.addActionListener(e -> selectOutputFolder());
        add(outputButton, gbc);

        // 输出目录与源相同复选框
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        setOutputAsInputCheckBox = new JCheckBox("输出目录与源相同");
        setOutputAsInputCheckBox.addActionListener(e -> toggleOutputAsInput());
        add(setOutputAsInputCheckBox, gbc);

        // 是否覆盖复选框
        gbc.gridy = 3;
        overwriteCheckBox = new JCheckBox("是否覆盖");
        overwriteCheckBox.setEnabled(false); // 初始化为禁用状态
        overwriteCheckBox.addActionListener(e -> {
            isCoverage = overwriteCheckBox.isSelected();
        });
        add(overwriteCheckBox, gbc);

        gbc.gridy = 4;
        openOutputFolderCheckBox = new JCheckBox("结束时是否打开输出文件夹");
        add(openOutputFolderCheckBox, gbc);

        // 运行按钮
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 3;
        runButton = new JButton("运行");
        runButton.addActionListener(e -> runCommand());
        add(runButton, gbc);

        // 控制台
        gbc.gridy = 6;
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
        JDialog aboutDialog = new JDialog(this, "关于软件", true);
        aboutDialog.setSize(400, 300);
        aboutDialog.setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel iconLabel = new JLabel(new ImageIcon("logo.ico"));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(iconLabel);

        JLabel titleLabel = new JLabel("Neko.UnLocker.Decrypt");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(titleLabel);

        JLabel versionLabel = new JLabel("版本: 2025.3.24-jdk21-windows-v0.8.0");
        versionLabel.setFont(new Font("Serif", Font.PLAIN, 14));
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(versionLabel);

        JLabel authorLabel = new JLabel("制作者: Histrem Rakik");
        authorLabel.setFont(new Font("Serif", Font.PLAIN, 14));
        authorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(authorLabel);

        JLabel descriptionLabel = new JLabel("<html><body style='text-align: center;'>Neko.UnLocker.Decrypt 是一个用于解密文件的专业工具，<br>支持多种格式的文件解密。</body></html>");
        descriptionLabel.setFont(new Font("Serif", Font.PLAIN, 14));
        descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(descriptionLabel);

        JButton closeButton = new JButton("关闭");
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeButton.addActionListener(e -> aboutDialog.dispose());
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(closeButton);

        aboutDialog.add(contentPanel, BorderLayout.CENTER);
        aboutDialog.setLocationRelativeTo(this);
        aboutDialog.setVisible(true);
    }

    private void showLicenseDialog() {
        JDialog licenseDialog = new JDialog(this, "许可证", true);
        licenseDialog.setSize(600, 400);
        licenseDialog.setLayout(new BorderLayout());

        JTextArea licenseTextArea = new JTextArea();
        licenseTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        licenseTextArea.setEditable(false);

        try (BufferedReader reader = new BufferedReader(new FileReader("LICENSE"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                licenseTextArea.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            licenseTextArea.setText("无法加载许可证文件。");
        }

        JScrollPane scrollPane = new JScrollPane(licenseTextArea);
        licenseDialog.add(scrollPane, BorderLayout.CENTER);

        JButton closeButton = new JButton("关闭");
        closeButton.addActionListener(e -> licenseDialog.dispose());
        licenseDialog.add(closeButton, BorderLayout.SOUTH);

        licenseDialog.setLocationRelativeTo(this);
        licenseDialog.setVisible(true);
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
            if (setOutputAsInputCheckBox.isSelected()) {
                outputPathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
            }
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
            try {
                Desktop.getDesktop().open(new File(inputDir));
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "无法打开输入文件夹", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openOutputFolder() {
        String outputDir = outputPathField.getText();
        if (outputDir.isEmpty()) {
            JOptionPane.showMessageDialog(this, "未选择输出文件夹", "警告", JOptionPane.WARNING_MESSAGE);
        } else {
            try {
                Desktop.getDesktop().open(new File(outputDir));
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "无法打开输出文件夹", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // 修改toggleOutputAsInput方法
    private void toggleOutputAsInput() {
        if (setOutputAsInputCheckBox.isSelected()) {
            outputPathField.setText(inputPathField.getText());
            outputPathField.setEnabled(false);
            overwriteCheckBox.setEnabled(true); // 启用“是否覆盖”复选框
        } else {
            outputPathField.setEnabled(true);
            overwriteCheckBox.setEnabled(false); // 禁用“是否覆盖”复选框
            overwriteCheckBox.setSelected(false); // 取消选中状态
            isCoverage = false;
        }
    }

    private void runOver(int errorCount, int warningCount, int processedCount){
        String message = String.format("处理结束\n错误数量: " + errorCount + "\n警告数量: " + warningCount + "\n已处理数量: " + processedCount + "\n");
        JOptionPane.showMessageDialog(this, message, "运行结束", JOptionPane.INFORMATION_MESSAGE);
    }

    private void runCommand() {
        // 获取输入目录、输出目录和复选框的状态
        String inputDir = inputPathField.getText();
        String outputDir = outputPathField.getText();
        boolean isOutputAsInput = setOutputAsInputCheckBox.isSelected();
        boolean isOverwrite = overwriteCheckBox.isSelected();

        // 显示确认对话框
        String message = String.format("请确认以下设置:\n输入目录: %s\n输出目录: %s\n输出目录与源相同: %s\n是否覆盖: %s",
                inputDir, outputDir, isOutputAsInput ? "是" : "否", isOverwrite ? "是" : "否");
        int confirm = JOptionPane.showConfirmDialog(this, message, "确认设置", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);

        // 如果用户点击“取消”按钮，则取消运行
        if (confirm != JOptionPane.OK_OPTION) {
            return;
        }

        // 禁用按钮并更改文本
        runButton.setText("运行中");
        runButton.setEnabled(false);

        // 清空控制台
        clearConsole();

        if (inputDir.isEmpty()) {
            JOptionPane.showMessageDialog(this, "无输入文件夹", "警告", JOptionPane.WARNING_MESSAGE);
            resetRunButton();
            return;
        }

        if (!isOutputAsInput && outputDir.isEmpty()) {
            JOptionPane.showMessageDialog(this, "未选择输出文件夹", "警告", JOptionPane.WARNING_MESSAGE);
            resetRunButton();
            return;
        }

        // 清除 UnLock.log 文件内容
        clearLogFile();

        // 获取是否覆盖的状态
        isCoverage = isOverwrite;

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    processFiles(inputDir, outputDir, isCoverage);
                    // 显示 UnLock.log 文件内容
                    displayLogFile();
                } catch (Exception e) {
                    e.printStackTrace();
                    console.append("处理文件时出错: " + e.getMessage() + "\n");
                }
                return null;
            }

            @Override
            protected void done() {
                // 查找“程序结束”字样
                String consoleText = console.getText();
                if (consoleText.contains("程序结束")) {
                    int errorCount = countOccurrences(consoleText, "错误");
                    int warningCount = countOccurrences(consoleText, "警告");
                    int processedCount = countOccurrences(consoleText, "已处理");

                    console.append("处理结束\n错误数量: " + errorCount + "\n警告数量: " + warningCount + "\n已处理数量: " + processedCount + "\n");
                    runOver(errorCount, warningCount, processedCount);

                    if (!isOverwrite){
                        outputDir = outputDir + "_unlocked";
                    }

                    if (openOutputFolderCheckBox.isSelected()) {
                        openOutputFolder();
                    }
                }
                resetRunButton();
            }
        };
        worker.execute();
    }

    private int countOccurrences(String text, String word) {
        Pattern pattern = Pattern.compile(word);
        Matcher matcher = pattern.matcher(text);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }

    private void resetRunButton() {
        runButton.setText("运行");
        runButton.setEnabled(true);
    }

    private void clearConsole() {
        console.setText("");
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