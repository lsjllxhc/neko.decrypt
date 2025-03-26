import json

# 读取JSON文件
with open('motion.json', 'r') as file:
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
with open('motion.json', 'w') as file:
    json.dump(data, file, indent=4)

print("Segments replaced and LipSync data group deleted successfully.")