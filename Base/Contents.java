package android.lovefantasy.mlscproxy.Base;

/**
 * Created by lovefantasy on 17-3-4.
 */

public class Contents {
    static public String ipts[] = {
            "#!/system/bin/sh\n" +
                    "################参数初始化##################\n" +
                    "cd \"/data/data/android.lovefantasy.CProxy/files/cproxy\"\n" +
                    "./stopipt.sh DROP\n" +
                    "mode_content=\"`cat $(cat current)`\"\n" +
                    "tcp_listen=`echo \"$mode_content\" | grep tcp_listen | grep -o [0-9][0-9]\\*`\n" +
                    "dns_listen=`echo \"$mode_content\" | grep dns_listen | grep -o [0-9][0-9]\\*`\n" +
                    "uid=`echo \"$mode_content\" | grep uid | grep -o [0-9][0-9]\\*`\n\n" +
                    "iptables -t nat -N CP\n" +
                    "iptables -t nat -A CP -o wlan+ -j ACCEPT\n" +
                    "iptables -t nat -A CP -p tcp -j REDIRECT --to $tcp_listen\n" +
                    "iptables -t nat -A CP -p udp --dport 53 -j REDIRECT --to $dns_listen\n" +
                    "iptables -t nat -A CP ! -p tcp -j REDIRECT\n" +
                    "iptables -t nat -A OUTPUT ! -d 192.168/16 ! -o lo -m owner ! --uid $uid -g CP\n" +
                    "iptables -t nat -A PREROUTING -s 192.168/16 ! -d 192.168/16 -g CP\n" +
                    "iptables -t mangle -A OUTPUT -m state --state ESTABLISHED,NEW,RELATED -j ACCEPT\n" +
                    "echo ✄┄ ┄ ┄ ┄ ┄ ┄ ┄ ┄ ┄ ┄ ┄ ┄ ┄ ┄ ┄ ┄ ┄\n" +
                    "echo ❁ nat表 PREROUTING链:\n" +
                    "iptables -tnat -SPREROUTING\n" +
                    "echo ✺ mangle表 FORWARD链:\n" +
                    "iptables -t mangle -S FORWARD\n" +
                    "echo ❁ nat表 OUTPUT链:\n" +
                    "iptables -tnat -SOUTPUT\n" +
                    "echo ✺ mangle表 OUTPUT链:\n" +
                    "iptables -t mangle -S OUTPUT\n" +
                    "echo ❁ nat表 CP链:\n" +
                    "iptables -t nat -S CP 2>&-\n",

            "#!/system/bin/sh\n" +
                    "iptables -t mangle -P OUTPUT ${1:-ACCEPT}\n" +
                    "iptables -t mangle -F OUTPUT\n" +
                    "iptables -t mangle -P FORWARD ${1:-ACCEPT}\n" +
                    "iptables -t mangle -F FORWARD\n" +
                    "iptables -t nat -F OUTPUT\n" +
                    "iptables -t nat -F CP 2>&-\n" +
                    "iptables -t nat -F PREROUTING\n" +
                    "iptables -t nat -X CP 2>&-\n"};


    static public String cproxyconf = "global {\n" +
            "    uid = globaluid;\n" +
            "    mode = globalmode;\n" +
            "    tcp_listen = tcp_listen_port;\n" +
            "    dns_listen = dns_listen_port;\n" +
            "}\n" +
            "\n" +
            "http {\n" +
            "    only_get_post = off;\n" +
            "    addr = httpaddr;\n" +
            "httpdelhdr" +
            "    set_first = httpsetfirst;\n" +
            "}\n" +
            "\n" +
            "https {\n" +
            "    addr = httpsaddr;\n" +
            "httpsdelhdr" +
            "    set_first = httpssetfirst;\n" +
            "}\n" +
            "\n" +
            "\n" +
            "httpdns {\n" +
            "    addr = dnsaddr;\n" +
            "    cachepath = /dev/null;\n" +
            "}";

    public static String conf = "global {\n" +
            "    uid = 3004;\n" +
            "    mode = wap;\n" +
            "    tcp_listen = 10086;\n" +
            "    dns_listen = 10086;\n" +
            "    procs=1;\n" +
            "}\n" +
            "\n" +
            "http {\n" +
            "    only_get_post = off;\n" +
            "    addr = 10.0.0.172:80;\n" +
            "    del_hdr = Host;\n" +
            "    del_hdr = X-Online-Host;\n" +
            "    set_first = \"[M] [U] [V]\\r\\nHost: [H]\\r\\n\";\n" +
            "}\n" +
            "\n" +
            "https {\n" +
            "    addr = 10.0.0.172:80;\n" +
            "    del_hdr = Host;\n" +
            "    del_hdr = X-Online-Host;\n" +
            "    set_first = \"CONNECT [H] HTTP/1.1\\r\\nHost: [H]\\r\\n\";\n" +
            "}\n" +
            "\n" +
            "\n" +
            "httpdns {\n" +
            "    addr = 119.29.29.29;\n" +
            "    cachepath = /dev/null;\n" +
            "}\n";

}
