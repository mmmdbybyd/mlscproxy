package android.lovefantasy.mlscproxy.Base;



import android.lovefantasy.mlscproxy.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lovefantasy on 2017/5/19.
 */

public class CProxy {
    private Process process = null;
    private ProcessBuilder builder = null;
    private String mPath=null;
    private InputStream processIputStream=null;
    private OutputStream processOutputStream=null;
    private InputStream processErrorStream=null;

//******
    public CProxy(String path) {
       mPath=path;
    }



    private void config() {
        builder = new ProcessBuilder();
        builder.directory(new File(App.getContext().getFilesDir()+"/CProxy"));
        Map<String, String> environment = builder.environment();
        environment.put("PATH", App.getContext().getFilesDir()+"/CProxy");

    }
    public boolean start() {
        try {
            if (mPath==null)return false;
        //    Utils.prepareCProxyConfig(mPath);
            config();
            builder.command("CProxy", App.getContext().getString(R.string.app_name)+".conf");
            process = builder.start();
            if (process != null) {
                processErrorStream = process.getErrorStream();
                processOutputStream=process.getOutputStream();
                processIputStream = process.getInputStream();
            }else {
                config();
                builder.command("./CProxy", App.getContext().getString(R.string.app_name)+".conf");
                process = builder.start();
                if (process != null) {
                    processErrorStream = process.getErrorStream();
                    processOutputStream=process.getOutputStream();
                    processIputStream = process.getInputStream();
                }else {
                       return false;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }



    public boolean stop() {

            config();
            builder.command("./CProxy","stop");
            try {
               process= builder.start();
                if (process != null) {
                    processErrorStream = process.getErrorStream();
                    //processOutputStream=process.getOutputStream();
                    processIputStream = process.getInputStream();
                }else {
                    config();
                    builder.command("./CProxy","stop");
                    process= builder.start();
                    if (process != null) {
                        processErrorStream = process.getErrorStream();
                        processOutputStream=process.getOutputStream();
                        processIputStream = process.getInputStream();
                    }else {
                        return false;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
            return true;

    }
    public InputStream getStardOutput() {
        if (process != null&&processIputStream!=null)
            return processIputStream;
        return null;
    }
    public InputStream getStardError(){
        if (process != null&&processErrorStream!=null) {
            return processErrorStream;
        }
        return null;
    }
    public OutputStream getCommandStream(){
        if (process != null&&processOutputStream!=null) {
            return processOutputStream;
        }
        return null;
    }

    public int exitValue() {
        if (process != null) {
            return process.exitValue();
        }
        return -2;
    }


    public void setInheritIO(boolean b) {
        if (b) {
            builder.redirectErrorStream(true);
        }
    }

    static public void prepareCProxyConfig(String path) throws IOException {
        String tmp =null;//= readFile(new File(path));
        File config = new File(App.getContext().getFilesDir() + "/CProxy/" + App.getContext().getString(R.string.app_name) + ".conf");
        if (config.exists()) {
            if (!config.delete()) {
                throw new IOException("删除失败");
            }
        }

        tmp= tmp.replaceAll("uid\\s*=\\s*\\d*\\s*;","");
        Pattern p = Pattern.compile("tcp_listen\\s*=\\s*(\\d+)\\s*;");
        Pattern p1 = Pattern.compile("dns_listen\\s*=\\s*(\\d+)\\s*;");
        Matcher matcher = p.matcher(tmp);
        // LogTools.e("m","startingma");
        if (matcher.find()) {
            // ProxyConfig.TCPLISTEN=Integer.parseInt( matcher.group(1));
            // LogTools.e("m",matcher.group(1));
        }else {
            // ProxyConfig.TCPLISTEN=10086;
        }
        matcher = p1.matcher(tmp);
        if (matcher.find()) {
            //  ProxyConfig.UDPLISTEN=Integer.parseInt( matcher.group(1));

        }else {
            // ProxyConfig.UDPLISTEN=10086;
        }
        FileOutputStream fileOutputStream = new FileOutputStream(config);
        fileOutputStream.write(tmp.getBytes(), 0, tmp.getBytes().length);
        fileOutputStream.flush();
        fileOutputStream.close();


        config.setReadable(true, false);
        config.setWritable(true, false);


    }
}
