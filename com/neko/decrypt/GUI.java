package com.neko.decrypt;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_INPUT = 1;
    private static final int REQUEST_CODE_OUTPUT = 2;

    private EditText inputPathField;
    private EditText outputPathField;
    private CheckBox setOutputAsInputCheckBox;
    private Button runButton;
    private TextView console;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputPathField = findViewById(R.id.inputPathField);
        outputPathField = findViewById(R.id.outputPathField);
        setOutputAsInputCheckBox = findViewById(R.id.setOutputAsInputCheckBox);
        runButton = findViewById(R.id.runButton);
        console = findViewById(R.id.console);

        findViewById(R.id.inputButton).setOnClickListener(v -> selectInputFolder());
        findViewById(R.id.outputButton).setOnClickListener(v -> selectOutputFolder());
        runButton.setOnClickListener(v -> runCommand());
    }

    private void selectInputFolder() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, REQUEST_CODE_INPUT);
    }

    private void selectOutputFolder() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, REQUEST_CODE_OUTPUT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                String path = uri.getPath();
                if (requestCode == REQUEST_CODE_INPUT) {
                    inputPathField.setText(path);
                    if (setOutputAsInputCheckBox.isChecked()) {
                        outputPathField.setText(path);
                    }
                } else if (requestCode == REQUEST_CODE_OUTPUT) {
                    outputPathField.setText(path);
                }
            }
        }
    }

    private void runCommand() {
        // 禁用按钮并更改文本
        runButton.setText("运行中");
        runButton.setEnabled(false);

        // 清空控制台
        console.setText("");

        String inputDir = inputPathField.getText().toString();
        String outputDir = outputPathField.getText().toString();

        if (inputDir.isEmpty()) {
            showError("无输入文件夹");
            resetRunButton();
            return;
        }

        if (!setOutputAsInputCheckBox.isChecked() && outputDir.isEmpty()) {
            showError("未选择输出文件夹");
            resetRunButton();
            return;
        }

        // 运行解密任务
        new Thread(() -> {
            try {
                processFiles(inputDir, outputDir);
                runOnUiThread(() -> console.setText("处理结束\n"));
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> console.setText("处理文件时出错: " + e.getMessage()));
            } finally {
                runOnUiThread(this::resetRunButton);
            }
        }).start();
    }

    private void resetRunButton() {
        runButton.setText("运行");
        runButton.setEnabled(true);
    }

    private void showError(String message) {
        new AlertDialog.Builder(this)
                .setTitle("错误")
                .setMessage(message)
                .setPositiveButton("确定", null)
                .show();
    }

    private void processFiles(String inputDir, String outputDir) {
        // 这里实现文件处理逻辑
    }
}