package codm.neko.decrypt;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class FixMotions {

    public static void FIX(String path) {
        // 获取文件夹目录路径
        String folderPath = path;

        File folder = new File(folderPath);
        processFolder(folder);
    }

    private static void processFolder(File folder) {
        // 遍历文件夹中的所有文件
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    processFolder(file);
                } else {
                    if (file.getName().endsWith(".json") && !file.getName().contains("idle") && !file.getName().contains("photo") && file.getName().contains("motion3")) {
                        processFile(file);
                    }
                }
            }
        }
    }

    private static void processFile(File file) {
        try (FileReader reader = new FileReader(file);
             FileWriter writer = new FileWriter(file)) {
            // 读取JSON文件
            JSONTokener tokener = new JSONTokener(reader);
            JSONObject data = new JSONObject(tokener);

            // 查找id为LipSync的segment
            JSONArray curves = data.getJSONArray("Curves");
            JSONArray lipsyncSegment = null;
            for (int i = 0; i < curves.length(); i++) {
                JSONObject curve = curves.getJSONObject(i);
                if (curve.getString("Id").equals("LipSync")) {
                    lipsyncSegment = curve.getJSONArray("Segments");
                    break;
                }
            }

            // 替换id为MouseOpenY的segment
            for (int i = 0; i < curves.length(); i++) {
                JSONObject curve = curves.getJSONObject(i);
                if (curve.getString("Id").equals("ParamMouthOpenY")) {
                    curve.put("Segments", lipsyncSegment);
                    break;
                }
            }

            // 删除id为LipSync的数据组
            Iterator<Object> iterator = curves.iterator();
            while (iterator.hasNext()) {
                JSONObject curve = (JSONObject) iterator.next();
                if (curve.getString("Id").equals("LipSync")) {
                    iterator.remove();
                }
            }

            // 写回修改后的JSON文件
            writer.write(data.toString(4));

            System.out.println("Segments replaced and LipSync data group deleted successfully in " + file.getName() + ".");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}