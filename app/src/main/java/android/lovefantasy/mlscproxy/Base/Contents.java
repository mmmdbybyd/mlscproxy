package android.lovefantasy.mlscproxy.Base;

/**
 * Created by lovefantasy on 17-3-4.
 */

public class Contents {
    static public String ipts[] = {"#!/system/bin/sh\n" +
            "#VPN选项，尽量开启VPN后重启防跳，部分机型不支持\n" +
            "card='ccmni0' #数据网卡，该项留空则不处理\n" +
            "proxy_mode='off'  #如果为share则代理共享数据，否则只代理本机\n" +
            "\n" +
            "#放行udp（不包括DNS）\n" +
            "selfudp=''  #填uid，多个空格隔开\n" +
            "shareudp=''  #共享UDP，on放行\n" +
            "\n" +
            "#放行TCP\n" +
            "selftcp=''  #填uid，多个空格隔开\n" +
            "sharetcp=''  #共享TCP，on放行\n" +
            "\n" +
            "#内网IP白名单，填前两段IP，多个空格隔开\n" +
            "ips=''\n" +
            "\n" +
            "\n" +
            "################参数初始化##################\n" +
            "cd \"/data/data/android.lovefantasy.CProxy/files/cproxy\"\n" +
            "mode_content=\"`cat $(cat current)`\"\n" +
            "./stopipt.sh DROP\n" +
            "tcp_listen=`echo \"$mode_content\" | grep tcp_listen | grep -o [0-9][0-9]\\*`\n" +
            "dns_listen=`echo \"$mode_content\" | grep dns_listen | grep -o [0-9][0-9]\\*`\n" +
            "uid=`echo \"$mode_content\" | grep uid | grep -o [0-9][0-9]\\*`\n" +
            "#####移植防跳复制以上代码，并去除启动核心代码#####\n" +
            "\n" +
            "for UID in $selftcp;do no_proxy_uid+=\" -mowner ! --uid $UID\";done\n" +
            "echo \"$no_proxy_uid\"\n" +
            "[ \"$sharetcp\" == 'on' ] && \\\n" +
            "iptables -tmangle -AFORWARD -p6 -jACCEPT || \\\n" +
            "iptables -tnat -APREROUTING -s192.168/16 ! -d192.168/16 -p6 -jREDIRECT --to $tcp_listen\n" +
            "iptables -tnat -AOUTPUT -mowner --uid $uid -p6 -jACCEPT\n" +
            "iptables -tnat -AOUTPUT -olo -jACCEPT\n" +
            "iptables -tnat -AOUTPUT -s192.168/16 -jACCEPT\n" +
            "iptables -tnat -AOUTPUT -owlan+ -jACCEPT #部分wifi不是192.168内网\n" +
            "iptables -tnat -AOUTPUT -ptcp $no_proxy_uid -jREDIRECT --to $tcp_listen\n" +
            "iptables -tnat -AOUTPUT -pudp --dport 53 -jREDIRECT --to $dns_listen\n" +
            "#VPN代理\n" +
            "if [ -n \"$card\" ]\n" +
            "then\n" +
            "    iptables -tnat -IOUTPUT -otun+ -jACCEPT\n" +
            "    iptables -tnat -IPOSTROUTING -j MASQUERADE\n" +
            "    ip rule add uidrange $uid-$uid lookup $card pref 99 || \\\n" +
            "    echo \"可能不支持VPN代理，请自行测试\"\n" +
            "    if [ \"$proxy_mode\" == 'share' ]\n" +
            "    then\n" +
            "        ip rule add from 192.168/16 lookup tun0 pref 99\n" +
            "        iptables -tnat -DPREROUTING -s192.168/16 ! -d192.168/16 -p6 -jREDIRECT --to $tcp_listen\n" +
            "        iptables -tmangle -PFORWARD ACCEPT\n" +
            "        iptables -FFORWARD\n" +
            "    fi\n" +
            "fi\n" +
            "#udp放行\n" +
            "for UID in $selfudp\n" +
            "do\n" +
            "    iptables -tnat -AOUTPUT -p17 -mowner --uid $UID -jACCEPT\n" +
            "done\n" +
            "[ \"$shareudp\" == 'on' ] && iptables -tmangle -AFORWARD -pudp -jACCEPT\n" +
            "#内网白名单\n" +
            "[ -n \"$ips\" ] && iptables -tmangle -IINPUT ! -ilo -j DROP\n" +
            "for ip in $ips\n" +
            "do\n" +
            "    iptables -tmangle -IINPUT -s$ip/16 -jACCEPT\n" +
            "    iptables -tmangle -IINPUT -d$ip/16 -jACCEPT\n" +
            "done\n" +
            "iptables -tnat -AOUTPUT ! -p6 -jREDIRECT\n" +
            "iptables -tmangle -AOUTPUT -mstate --state ESTABLISHED,NEW,RELATED -jACCEPT\n" +
            "echo ✄┄ ┄ ┄ ┄ ┄ ┄ ┄ ┄ ┄ ┄ ┄ ┄ ┄ ┄ ┄ ┄ ┄\n" +
            "echo ❁ nat表 PREROUTING链:\n" +
            "iptables -tnat -SPREROUTING\n" +
            "echo ✺ mangle表 FORWARD链:\n" +
            "iptables -tmangle -SFORWARD\n" +
            "[ -n \"$ips\" ] && echo \"✺ mangle表 INPUT链:\\n`iptables -tmangle -SINPUT`\"\n" +
            "echo ✺ mangle表 OUTPUT链:\n" +
            "iptables -tmangle -SOUTPUT\n" +
            "echo ❁ nat表 OUTPUT链:\n" +
            "iptables -tnat -SOUTPUT\n" +
            "[ -n \"$card\" ] && echo \"❁ nat表 POSTROUTING链:\\n`iptables -tnat -SPOSTROUTING`\\n路由规则:\\n`ip rule ls`\"\n" +
            "\n",

            "#!/system/bin/sh\n" +
                    "iptables -tmangle -PINPUT ACCEPT\n" +
                    "iptables -tmangle -POUTPUT ${1:-ACCEPT}\n" +
                    "iptables -tmangle -FOUTPUT\n" +
                    "iptables -tmangle -PFORWARD ${1:-ACCEPT}\n" +
                    "iptables -tmangle -FFORWARD\n" +
                    "iptables -tnat -FOUTPUT\n" +
                    "iptables -tnat -FPREROUTING\n" +
                    "iptables -tmangle -SINPUT | grep -E '\\-A INPUT ! \\-i lo \\-j DROP|\\-A INPUT \\-[sd] [^ ]* \\-j ACCEPT' | while read rule\n" +
                    "do\n" +
                    "    iptables -tmangle ${rule/-A/-D}\n" +
                    "done\n" +
                    "iptables -t nat -D POSTROUTING -j MASQUERADE 2>/dev/null\n" +
                    "while ip rule list|grep -q ^99:\n" +
                    "do\n" +
                    "    ip rule delete pref 99\n" +
                    "done"};


    static public String cproxyconf = "global {\n" +
            "    uid = globaluid;\n" +
            "    mode = globalmode;\n" +
            "    tcp_listen = 10086;\n" +
            "    dns_listen = 10086;\n" +
            "    stats_uri = /cp;\n" +
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
            "    stats_uri = /cp;\n" +
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
