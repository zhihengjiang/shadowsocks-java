# shadowsocks-java

Shadowsocks-java 是一个基于SOCKS5代理的使用java开发的[shadowsocks](https://github.com/shadowsocks/shadowsocks)
代理软件。可以同时作为客户端和服务端使用目前只支持TCP协议及流加密，后续会增加UDP协议和AEAD的支持。

 


## Build & Install

```bash
git clone https://github.com/ThalesOfChengDu/shasowsocks-server
cd shadowsocks-libev
mvn install
```

## Getting Started

创建配置文件

```json
{
    "server": "my_server_ip",
    "server_port": 8388,
    "local_address": "127.0.0.1",
    "local_port": 1080,
    "password": "mypassword",
    "timeout": 300,
    "method": "aes-256-cfb"
}
```

详细的参数解释可以参考 [shadowsocks](https://github.com/shadowsocks/shadowsocks/wiki) 的文档.
## 支持的加密方式

### 流加密

* `aes-128-cfb`, `aes-192-cfb`, `aes-256-cfb`
* `aes-128-ofb`, `aes-192-ofb`, `aes-256-ofb`
* `chacha20`,  `chacha20-ietf`

## TODO

- [ ] Documentation
- [ ] 支持UDP协议
- [ ] 支持AEAD加密
- [ ] 编写使用脚本
