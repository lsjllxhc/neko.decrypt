package com.neko.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class UnlockerGUI extends JFrame {
    // ==== 组件声明 ==== (确保与设计器中变量名一致！)
    private JTextField inputPathField;
    private JButton inputBrowseButton;
    private JTextField outputPathField;
    private JButton outputBrowseButton;
    private JButton runButton;
    private JLabel statusLabel;
    private JPanel mainPanel; // Designer生成的主面板必须存在此变量！

    public UnlockerGUI() {
        // 1. 窗体基本设置
        setTitle("文件解锁工具");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(mainPanel); // 绑定Designer生成的布局面板
        setSize(600, 250);
        setLocationRelativeTo(null); // 居中

        // 2. 事件绑定（正确的作用域范围）
        inputBrowseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                if (chooser.showOpenDialog(UnlockerGUI.this) == JFileChooser.APPROVE_OPTION) {
                    inputPathField.setText(chooser.getSelectedFile().getAbsolutePath());
                }
            }
        });

        outputBrowseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                if (chooser.showOpenDialog(UnlockerGUI.this) == JFileChooser.APPROVE_OPTION) {
                    outputPathField.setText(chooser.getSelectedFile().getAbsolutePath());
                }
            }
        });

        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String input = inputPathField.getText().trim();
                String output = outputPathField.getText().trim();

                if (input.isEmpty() || output.isEmpty()) {
                    JOptionPane.showMessageDialog(UnlockerGUI.this,
                            "请先选择输入和输出路径！",
                            "错误",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // 转换为绝对路径
                Path inputPath = Path.of(input).toAbsolutePath();
                Path outputPath = Path.of(output).toAbsolutePath();

                // 校验输入路径有效性
                if (Files.notExists(inputPath) || !Files.isDirectory(inputPath)) {
                    JOptionPane.showMessageDialog(
                            UnlockerGUI.this,
                            "输入路径不存在或不是目录！有效路径示例：\n" + Path.of(".").toAbsolutePath(),
                            "路径错误",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }

                // 尝试创建输出目录（若不存在）
                try {
                    if (!Files.exists(outputPath)) {
                        Files.createDirectories(outputPath);
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(
                            UnlockerGUI.this,
                            "无法创建输出目录！请检查权限或路径合法性。\n错误详情：" + ex.getMessage(),
                            "路径错误",
                            JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }
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
                            ex.printStackTrace();
                        } finally {
                            runButton.setEnabled(true);
                        }
                        return null;
                    }
                }.execute();
            }
        });
    }

    // 更新状态栏的辅助方法
    private void updateStatus(String message, Color color) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(message);
            statusLabel.setForeground(color);
        });
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            UnlockerGUI gui = new UnlockerGUI();
            gui.setVisible(true);
        });
    }
}
