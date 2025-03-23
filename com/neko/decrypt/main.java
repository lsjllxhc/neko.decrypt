package com.util.regex.*;

public class GUI extends JFrame {
    private JTextField inputPathField;
    private JTextField outputPathField;
    private JTextArea console;
    private JButton runButton;
    private JCheckBox setOutputAsInputCheckBox;

    public GUI() {
        initializeUI();
        setWindowListener();
        createMenu();
        createComponents();
        showWelcomeDialog();
    }

    private void initializeUI() {
        setTitle("UnLocker");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
    }

    private void setWindowListener() {
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
    }

    private void createMenu() {
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("文件");
        menuBar.add(fileMenu);

        JMenuItem aboutMenuItem = new JMenuItem("关于软件");
        aboutMenuItem.addActionListener(e -> showAboutDialog());
        fileMenu.add(aboutMenuItem);

        JMenuItem openInputMenuItem = new JMenuItem("打开输入文件夹");
        openInputMenuItem.addActionListener(e -> openInputFolder());
        fileMenu.add(openInputMenuItem);

        JMenuItem openOutputMenuItem = new JMenuItem("打开输出文件夹");
        openOutputMenuItem.addActionListener(e -> openOutputFolder());
        fileMenu.add(openOutputMenuItem);

        JMenu consoleMenu = new JMenu("控制台");
        menuBar.add(consoleMenu);

        JMenuItem clearConsoleMenuItem = new JMenuItem("清空");
        clearConsoleMenuItem.addActionListener(e -> clearConsole());
        consoleMenu.add(clearConsoleMenuItem);
    }

    private void createComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addComponent(new JLabel("输入文件夹:"), gbc, 0, 0);
        inputPathField = new JTextField(30);
        addComponent(inputPathField, gbc, 1, 0);
        JButton inputButton = createButton("选择", e -> selectInputFolder());
        addComponent(inputButton, gbc, 2, 0);

        addComponent(new JLabel("输出文件夹:"), gbc, 0, 1);
        outputPathField = new JTextField(30);
        addComponent(outputPathField, gbc, 1, 1);
        JButton outputButton = createButton("选择", e -> selectOutputFolder());
        addComponent(outputButton, gbc, 2, 1);

        setOutputAsInputCheckBox = new JCheckBox("设置输出文件夹为输入文件夹");
        setOutputAsInputCheckBox.addActionListener(e -> toggleOutputAsInput());
        addComponent(setOutputAsInputCheckBox, gbc, 0, 2, 3);

        runButton = createButton("运行", e -> runCommand());
        addComponent(runButton, gbc, 0, 3, 3);

        console = new JTextArea();
        console.setFont(new Font("Monospaced", Font.PLAIN, 12));
        console.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(console);
        addComponent(scrollPane, gbc, 0, 4, 3, GridBagConstraints.BOTH, 1, 1);
    }

    private JButton createButton(String text, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(60, 30));
        button.addActionListener(actionListener);
        return button;
    }

    private void addComponent(Component component, GridBagConstraints gbc, int x, int y) {
        addComponent(component, gbc, x, y, 1, GridBagConstraints.HORIZONTAL, 0, 0);
    }

    private void addComponent(Component component, GridBagConstraints gbc, int x, int y, int width) {
        addComponent(component, gbc, x, y, width, GridBagConstraints.HORIZONTAL, 0, 0);
    }

    private void addComponent(Component component, GridBagConstraints gbc, int x, int y, int width, int fill, double weightx, double weighty) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.fill = fill;
        gbc.weightx = weightx;
        gbc.weighty = weighty;
        add(component, gbc);
    }

    private void showAboutDialog() {
        JDialog aboutDialog = new JDialog(this, "关于软件", true);
        aboutDialog.setSize(400, 300);
        aboutDialog.setLayout(new BorderLayout());

        JPanel contentPanel = createContentPanel();
        aboutDialog.add(contentPanel, BorderLayout.CENTER);
        aboutDialog.setLocationRelativeTo(this);
        aboutDialog.setVisible(true);
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        contentPanel.add(createLabel("Neko.UnLocker.Decrypt", new Font("Serif", Font.BOLD, 18)));
        contentPanel.add(createLabel("版本: 2025.3.23-jdk21-windows-v0.6.0", new Font("Serif", Font.PLAIN, 14)));
        contentPanel.add(createLabel("制作者: Histrem Rakik", new Font("Serif", Font.PLAIN, 14)));
        contentPanel.add(createLabel("<html><body style='text-align: center;'>Neko.UnLocker.Decrypt 是一个用于解密文件的专业工具，<br>支持多种格式的文件解密。</body></html>", new Font("Serif", Font.PLAIN, 14)));

        JButton closeButton = new JButton("关闭");
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeButton.addActionListener(e -> ((Window) SwingUtilities.getWindowAncestor(e.getSource())).dispose());
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(closeButton);

        return contentPanel;
    }

    private JLabel createLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    private void showWelcomeDialog() {
        JOptionPane.showMessageDialog(this, "欢迎使用Neko.UnLocker.Decrypt, 制作者Histrem Rakik。", "欢迎", JOptionPane.INFORMATION_MESSAGE);
    }

    private void selectInputFolder() {
        selectFolder(inputPathField, setOutputAsInputCheckBox.isSelected() ? outputPathField : null);
    }

    private void selectOutputFolder() {
        selectFolder(outputPathField, null);
    }

    private void selectFolder(JTextField pathField, JTextField linkedField) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            String selectedPath = fileChooser.getSelectedFile().getAbsolutePath();
            pathField.setText(selectedPath);
            if (linkedField != null) {
                linkedField.setText(selectedPath);
            }
        } else {
            JOptionPane.showMessageDialog(this, "未选择文件夹", "警告", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void openInputFolder() {
        openFolder(inputPathField.getText());
    }

    private void openOutputFolder() {
        openFolder(outputPathField.getText());
    }

    private void openFolder(String path) {
        if (path.isEmpty()) {
            JOptionPane.showMessageDialog(this, "未选择文件夹", "警告", JOptionPane.WARNING_MESSAGE);
        } else {
            try {
                Desktop.getDesktop().open(new File(path));
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "无法打开文件夹", "错误", JOptionPane.ERROR_MESSAGE);
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
        runButton.setText("运行中");
        runButton.setEnabled(false);

        clearConsole();

        String inputDir = inputPathField.getText();
        String outputDir = outputPathField.getText();

        if (validatePaths(inputDir, outputDir)) {
            clearLogFile();

            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() {
                    processFiles(inputDir, outputDir);
                    displayLogFile();
                    return null;
                }

                @Override
                protected void done() {
                    summarizeConsoleOutput();
                    resetRunButton();
                }
            };
            worker.execute();
        } else {
            resetRunButton();
        }
    }

    private boolean validatePaths(String inputDir, String outputDir) {
        if (inputDir.isEmpty()) {
            JOptionPane.showMessageDialog(this, "无输入文件夹", "警告", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (!setOutputAsInputCheckBox.isSelected() && outputDir.isEmpty()) {
            JOptionPane.showMessageDialog(this, "未选择输出文件夹", "警告", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
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
                console.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void summarizeConsoleOutput() {
        String consoleText = console.getText();
        if (consoleText.contains("程序结束")) {
            int errorCount = countOccurrences(consoleText, "错误");
            int warningCount = countOccurrences(consoleText, "警告");
            int processedCount = countOccurrences(consoleText, "已处理");

            console.append("处理结束\n错误数量: ").append(errorCount).append("\n警告数量: ").append(warningCount).append("\n已处理数量: ").append(processedCount).append("\n");
        }
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