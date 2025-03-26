import json
import os

# 遍历文件夹中的所有文件
for root, dirs, files in os.walk('path_to_your_folder'):
    for file_name in files:
        if file_name.endswith('.motion'):
            file_path = os.path.join(root, file_name)
            
            # 读取JSON文件
            with open(file_path, 'r') as file:
                data = json.load(file)
            
            # 查找id为LipSync的segment
            lipsync_segment = None
            for curve in data['Curves']:
                if curve['Id'] == 'LipSync':
                    lipsync_segment = curve['Segments']
                    break
            
            # 替换id为MouseOpenY的segment
            for curve in data['Curves']:
                if curve['Id'] == 'ParamMouthOpenY':
                    curve['Segments'] = lipsync_segment
                    break
            
            # 删除id为LipSync的数据组
            data['Curves'] = [curve for curve in data['Curves'] if curve['Id'] != 'LipSync']
            
            # 写回修改后的JSON文件
            with open(file_path, 'w') as file:
                json.dump(data, file, indent=4)
            
            print(f"Segments replaced and LipSync data group deleted successfully in {file_name}.")