# Nuntius
Nuntius is a messenger existing between you and NTQQ

## 配置 （Configuration）

在你的 `Nuntius` 的工作目录上创建一个空的名为 `nuntius.yaml` 的文件，然后写入下面的内容

```yaml
# 机器人配置
bot:
  server_url: "" # 直接输入服务器的地址，如 0.0.0.0 或者 example.com
  http_port: 1234 # http 服务器对外暴露的端口
  websocket_port: 4321 # websocket 服务器对外暴露的端口
  access_token: "" # AccessToken 如果没有则不要填写任何内容 但要留下双引号
  use_https: 0 # 如果你的服务器使用 https，则将这里改为 1

# 服务前配置
service:
  master_qq: "" # 在引号内填写你自己的qq
  allowed_groups: [""] # 填写允许的群 如 "123", "321"
  blocked_person: [] # 填写需要屏蔽的人 格式同上

# 服务配置
operation:
  data_storage_path: "/" # 文件存储目录 如果要就在工作目录下创建 则保持不动 否则填写你需要的绝对路径
  calling_token: "%" # 唤起 Nuntius 的前缀 只能填写一个字符！
```

目前正在开发中
