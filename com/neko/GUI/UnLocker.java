package com.neko.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class UnlockerGUI extends JFrame {
    private JTextField inputPathField;
    private JButton inputBrowseButton;
    private JTextField outputPathField;
    private JButton outputBrowseButton;
    private JButton runButton;
    private JLabel statusLabel;
    private JPanel mainPanel;
    private static final Logger LOGGER = Logger.getLogger(UnlockerGUI.class.getName());
    private static final Path LOG_PATH = Path.of("logs/unLocker.log");

    static {
        try {
            if (!Files.exists(LOG_PATH.getParent())) {
                Files.createDirectories(LOG_PATH.getParent());
            }
            // 设置 append 参数为 false 以清空日志文件
            FileHandler fileHandler = new FileHandler(LOG_PATH.toString(), false);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public UnlockerGUI() {
        // 窗体基本设置
        setTitle("文件解锁工具");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(mainPanel);
        setSize(600, 250);
        setLocationRelativeTo(null); // 居中

        // 事件绑定
        inputBrowseButton.addActionListener(this::browseInputPath);
        outputBrowseButton.addActionListener(this::browseOutputPath);
        runButton.addActionListener(this::runUnlocker);
    }

    private void browseInputPath(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            inputPathField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void browseOutputPath(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            outputPathField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void runUnlocker(ActionEvent e) {
        String input = inputPathField.getText().trim();
        String output = outputPathField.getText().trim();

        if (input.isEmpty() || output.isEmpty()) {
            showMessage("请先选择输入和输出路径！", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Path inputPath = Path.of(input).toAbsolutePath();
        Path outputPath = Path.of(output).toAbsolutePath();

        if (validatePaths(inputPath, outputPath)) {
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() {
                    runButton.setEnabled(false);
                    updateStatus("运行中...", Color.BLUE);

                    try {
                        UnLocker.startDecrypt(inputPath, outputPath);
                        updateStatus("处理完成！", Color.GREEN);
                    } catch (Exception ex) {
                        updateStatus("错误: " + ex.getMessage(), Color.RED);
                        LOGGER.severe("错误: " + ex.getMessage());
                        ex.printStackTrace();
                    } finally {
                        runButton.setEnabled(true);
                        showLog();
                    }
                    return null;
                }
            }.execute();
        }
    }

    private boolean validatePaths(Path inputPath, Path outputPath) {
        if (Files.notExists(inputPath) || !Files.isDirectory(inputPath)) {
            showMessage("输入路径不存在或不是目录！有效路径示例：\n" + Path.of(".").toAbsolutePath(), "路径错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            if (!Files.exists(outputPath)) {
                Files.createDirectories(outputPath);
            }
        } catch (IOException ex) {
            showMessage("无法创建输出目录！请检查权限或路径合法性。\n错误详情：" + ex.getMessage(), "路径错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    private void updateStatus(String message, Color color) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(message);
            statusLabel.setForeground(color);
        });
    }

    private void showLog() {
        SwingUtilities.invokeLater(() -> {
            try {
                List<String> logLines = Files.readAllLines(LOG_PATH);
                JTextArea textArea = new JTextArea(String.join("\n", logLines));
                textArea.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(600, 400));

                JOptionPane.showMessageDialog(this, scrollPane, "日志内容", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                showMessage("无法读取日志文件！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            UnlockerGUI gui = new UnlockerGUI();
            gui.setVisible(true);
        });
    }
}