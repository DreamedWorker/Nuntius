# Nuntius
Nuntius is a messenger existing between you and NTQQ

## 配置 （Configuration）

在你的 `Nuntius` 的工作目录上创建一个空的名为 `nuntius.xml` 的文件，然后写入下面的内容

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<Nuntius>
    <BotNetwork>
        <BaseUrl>服务器域名或者ip地址，如example.com</BaseUrl>
        <WebSocketPort>ws服务器端口</WebSocketPort>
        <HttpPort useHttps="false">http服务器端口（如果使用https协议，将前面的false改为true）</HttpPort>
        <AccessToken enabled="true">AccessToken，如果不使用则留空并将前面的true改为false</AccessToken>
    </BotNetwork>
    <BotService>
        <CallingToken>命令前缀，只能填写一个字符！</CallingToken>
        <Master>你的QQ</Master>
        <ServerGroups>
            <Code>需要监听的群号，如有多个则另起一行以相同样式再写。</Code>
        </ServerGroups>
        <BlockedPersons>
            <!--            <Code>需要忽略的人的QQ</Code>-->
        </BlockedPersons>
    </BotService>
    <StorageConfiguration>
<!--    这里保持默认即可    -->
        <DataPath>/</DataPath>
    </StorageConfiguration>
</Nuntius>
```

目前正在开发中
