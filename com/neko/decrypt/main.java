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

    public GUI() {
        setTitle("UnLocker");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // 设置为不进行任何操作
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

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

        // 创建“控制台”菜单
        JMenu consoleMenu = new JMenu("控制台");
        menuBar.add(consoleMenu);

        // 清空控制台菜单项
        JMenuItem clearConsoleMenuItem = new JMenuItem("清空");
        clearConsoleMenuItem.addActionListener(e -> clearConsole());
        consoleMenu.add(clearConsoleMenuItem);

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
        inputButton.setPreferredSize(new Dimension(60, 30)); // 设置为合适的大小
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
        outputButton.setPreferredSize(new Dimension(60, 30)); // 设置为合适的大小
        outputButton.addActionListener(e -> selectOutputFolder());
        add(outputButton, gbc);

        // 设置输出文件夹为输入文件夹复选框
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        setOutputAsInputCheckBox = new JCheckBox("设置输出文件夹为输入文件夹");
        setOutputAsInputCheckBox.addActionListener(e -> toggleOutputAsInput());
        add(setOutputAsInputCheckBox, gbc);

        // 运行按钮
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        runButton = new JButton("运行");
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
        JDialog aboutDialog = new JDialog(this, "关于软件", true);
        aboutDialog.setSize(400, 300);
        aboutDialog.setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Neko.UnLocker.Decrypt");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(titleLabel);

        JLabel versionLabel = new JLabel("版本: 2025.3.23-jdk21-windows-v0.6.0");
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

    private void toggleOutputAsInput() {
        if (setOutputAsInputCheckBox.isSelected()) {
            outputPathField.setText(inputPathField.getText());
            outputPathField.setEnabled(false);
        } else {
            outputPathField.setEnabled(true);
        }
    }

    private void runCommand() {
        // 禁用按钮并更改文本
        runButton.setText("运行中");
        runButton.setEnabled(false);

        // 清空控制台
        clearConsole();

        String inputDir = inputPathField.getText();
        String outputDir = outputPathField.getText();

        if (inputDir.isEmpty()) {
            JOptionPane.showMessageDialog(this, "无输入文件夹", "警告", JOptionPane.WARNING_MESSAGE);
            resetRunButton();
            return;
        }

        if (!setOutputAsInputCheckBox.isSelected() && outputDir.isEmpty()) {
            JOptionPane.showMessageDialog(this, "未选择输出文件夹", "警告", JOptionPane.WARNING_MESSAGE);
            resetRunButton();
            return;
        }

        // 清除 UnLock.log 文件内容
        clearLogFile();

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    processFiles(inputDir, outputDir);
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

package com.neko.decrypt;

import java.nio.file.Path;
import java.nio.file.Files;
import java.util.stream.Stream;
import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import static com.neko.decrypt.MMK.*;

public class UnLocker {

    private static final Logger logger = Logger.getLogger(UnLocker.class.getName());

    static {
        try {
            FileHandler fh = new FileHandler("UnLock.log");
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
        } catch (IOException e) {
            logger.severe("设置logger失败: " + e.getMessage());
        }
    }

    public static void processFiles(String inputDir, String outputDir) throws IOException {
        logger.info("程序开始");
        Path srcPath = Path.of(inputDir);
        Path outPath = Path.of(outputDir);

        try (Stream<Path> lines = Files.list(srcPath)) {
            var iterator = lines.iterator();
            while (iterator.hasNext()) {
                Path path = iterator.next();
                try {
                    SecretKey secretKey = getSecretKey(path);
                    if (secretKey == null)
                        continue;
                    logger.info("已处理: " + path + " " + secretKey);
                    handleDiv(path, outPath, secretKey);
                } catch (Exception e) {
                    logger.severe("错误: " + path + " -> " + e.getMessage());
                }
            }
        }
        logger.info("程序结束");
    }

    public static void handleDiv(Path srcDiv, Path targetDiv, SecretKey secretKey) throws IOException {
        Files.walkFileTree(srcDiv, new FileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path relativize = srcDiv.getParent().relativize(dir);
                Path target = targetDiv.resolve(relativize);
                if (!Files.exists(target))
                    Files.createDirectory(target);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path relativize = srcDiv.getParent().relativize(file);
                Path target = targetDiv.resolve(relativize);

                if (!Files.exists(target))
                    switch (file.getFileName().toString()) {
                        case "fileMap.json", "meta.mko" -> {
                            Files.copy(file, target);
                        }
                        default -> {
                            byte[] data = secretKey.aes(Files.readAllBytes(file));
                            if (data == null) {
                                logger.warning("警告: " + file + "无法解密此文件");
                                break;
                            }
                            Files.write(target, data);
                        }
                    }

                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public static MMK.SecretKey getSecretKey(Path rootPath) throws IOException {
        final Path[] files = new Path[3];
        Files.walkFileTree(rootPath, new FileVisitor<>() {
            @Override public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().endsWith(".json")) {
                    if (isJson(Files.readAllBytes(file)))
                        return FileVisitResult.CONTINUE;
                    files[0] = file;
                    return FileVisitResult.TERMINATE;
                }
                if (file.toString().endsWith(".png") || file.toString().endsWith(".jpg")) {
                    if (isImage(Files.readAllBytes(file)))
                        return FileVisitResult.CONTINUE;
                    files[1] = file;
                    return FileVisitResult.TERMINATE;
                }
                if (file.toString().endsWith(".mp4")) {
                    if (isMp4(Files.readAllBytes(file)))
                        return FileVisitResult.CONTINUE;
                    files[2] = file;
                    return FileVisitResult.TERMINATE;
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }
        });

        if (files[0] != null) {
            return MMK.SecretKey.getFromJson(Files.readAllBytes(files[0]));
        } else if (files[1] != null) {
            return MMK.SecretKey.getFromImage(Files.readAllBytes(files[1]));
        } else if (files[2] != null) {
            return MMK.SecretKey.getFromMp4(Files.readAllBytes(files[2]));
        } else {
            throw new RuntimeException("无法判断");
        }
    }
}

package com.neko.decrypt;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


public class MMK {
    private static final Gson gson = new Gson();

    public static boolean isJson(byte[] json) {
        try {
            gson.fromJson(new String(json, StandardCharsets.UTF_8), JsonElement.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isImage(byte[] image) {
        try {
            return ImageIO.read(new ByteArrayInputStream(image)).getWidth() != 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isMp4(byte[] mp4) {
        if (mp4 == null) return false;
        return  mp4[4] == 0x66 & mp4[5] == 0x74 & mp4[6] == 0x79 & mp4[7] == 0x70;
    }

    public enum SecretKey {
        PE("mimikkouiaeskey2", "AES/CTR/NoPadding"),
        PC("mimikkopcaeskey2", "AES/CTR/NoPadding"),
        OLD_PE("mimikkouiaeskey2", "AES/CBC/PKCS5Padding");
        private final byte[] key;
        private final Cipher cipher;

        SecretKey(String key, String transformation) {
            this.key = key.getBytes(StandardCharsets.UTF_8);
            try {
                this.cipher = Cipher.getInstance(transformation);

                this.cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(this.key,  this.cipher.getAlgorithm().split("/",  2)[0]), new IvParameterSpec(new byte[16]));
            } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                     InvalidAlgorithmParameterException e) {
                throw new RuntimeException(e);
            }
        }

        public static SecretKey getFromJson(byte[] json) {
            for (SecretKey secretKey : SecretKey.values()) {
                if (isJson(secretKey.aes(json)))
                    return secretKey;
            }
            throw new RuntimeException("没有符合的SecretKey");
        }

        public static SecretKey getFromImage(byte[] image) {
            for (SecretKey secretKey : SecretKey.values()) {
                if (isImage(secretKey.aes(image)))
                    return secretKey;
            }
            throw new RuntimeException("没有符合的SecretKey");
        }

        public static SecretKey getFromMp4(byte[] mp4) {
            for (SecretKey secretKey : SecretKey.values()) {
                if (isMp4(secretKey.aes(mp4)))
                    return secretKey;
            }
            throw new RuntimeException("没有符合的SecretKey");
        }

        public byte[] aes(byte[] data) {
            try {
                return cipher.doFinal(data, 0, data.length);
            } catch (IllegalBlockSizeException | BadPaddingException e) {
                return null;
            }
        }
    }

}