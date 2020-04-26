#!/usr/bin/env bash

#default value for local port
local_port=1080
#default value for cipher method
method="aes-256-cfb"
#default value for server port
server_port=8388
local_address="127.0.0.1"
time_out=600

#parse the parameters
while test $# -gt 0; do
        case "$1" in
            -h|--help)
                echo "A fast tunnel proxy that helps you bypass firewalls."

                echo "You can supply configurations via either config file or command line arguments."

                echo "Proxy options:"
                echo "  -c CONFIG              path to config file"
                echo "  -s SERVER_ADDR         server address"
                echo "  -p SERVER_PORT         server port, default: 8388"
#                echo "  -b LOCAL_ADDR          local binding address, default: 127.0.0.1"
#                echo "  -l LOCAL_PORT          local port, default: 1080"
                echo "  -k PASSWORD            password"
                echo "  -m METHOD              encryption method, default: aes-256-cfb"
                echo "                         Sodium:"
                echo "                             chacha20, chacha20-ietf."
                echo "                         OpenSSL:"
                echo "                             aes-{128|192|256}-cfb,aes-{128|192|256}-ofb,"
                echo "  -t TIMEOUT             timeout in seconds, default: 600"
                exit 0
                ;;
            -c)
                shift
                if test $# -gt 0; then
                                    config=$1
                fi
                shift
                ;;
            -s)
                shift
                if test $# -gt 0; then
                                    server=$1
                fi
                shift
                ;;
            -p)
                shift
                if test $# -gt 0; then
                                    server_port=$1
                fi
                shift
                ;;
            -b)
                shift
                if test $# -gt 0; then
                                    local_address=$1
                fi
                shift
                ;;
            -l)
                shift
                if test $# -gt 0; then
                                    local_port=$1
                fi
                shift
                ;;
            -k)
                shift
                if test $# -gt 0; then
                                    password=$1
                fi
                shift
                ;;
            -m)
                shift
                if test $# -gt 0; then
                                    method=$1
                fi
                shift
                ;;
            -t)
                shift
                if test $# -gt 0; then
                                    time_out=$1
                fi
                shift
                ;;
                    *)
                break
                ;;
        esac
done

main="ServerMain"
#printing some output to the users
echo "config: $config"
if [ -n "$config" ]
then
    echo "Starting shadowsocks ...";
    java -jar ./target/shadowsocks-java-1.0-SNAPSHOT.jar ${main} config=${config}
elif [ -z "$server" ]
then
    echo "please set a server address"
    exit 0
elif [ -z "$password" ]
then
    echo "please set a password"
    exit 0
else
    java -jar ./target/shadowsocks-java-1.0-SNAPSHOT.jar ${main} server=${server} server_port=${server_port}
local_address=${local_address} local_port=${local_port} method=${method} password=${password}
fi


