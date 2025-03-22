package GUISCR;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

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

        // 版本按钮
        gbc.gridy = 3;
        JButton versionButton = new JButton("版本");
        versionButton.addActionListener(e -> showVersion());
        add(versionButton, gbc);

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
    }

    private void selectInputFolder() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            inputPathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void selectOutputFolder() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            outputPathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void runCommand() {
        String inputDir = inputPathField.getText();
        String outputDir = outputPathField.getText();

        if (inputDir.isEmpty()) {
            JOptionPane.showMessageDialog(this, "无输入文件夹", "警告", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String command = "java -jar ./UnLocker.jar -i " + inputDir;
        if (!outputDir.isEmpty()) {
            command += " -o " + outputDir;
        }

        executeCommand(command);
    }

    private void showVersion() {
        String command = "java -jar ./UnLocker.jar";
        executeCommand(command);
    }

    private void executeCommand(String command) {
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8));

            String s;
            console.append("Standard output:\n\n");
            while ((s = stdInput.readLine()) != null) {
                console.append(s + "\n\n");
            }

            console.append("Standard error:\n\n");
            while ((s = stdError.readLine()) != null) {
                console.append(s + "\n\n");
            }
        } catch (Exception e) {
            console.append("Error: " + e.getMessage() + "\n\n");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GUI app = new GUI();
            app.setVisible(true);
        });
    }
}
