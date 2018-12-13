package api.utils;

import org.apache.commons.io.FileUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class PicUtil {
    public static String baseurl(String path) throws IOException {
        if (path==null||path.equals(""))
            return "";
        File file=new File(path);
        if (!file.exists())
            return "";
        byte[] data = null;
        FileInputStream fileInputStream = FileUtils.openInputStream(new File(path));
        data=new byte[fileInputStream.available()];
        String filename=file.getName();
        String last=filename.substring(filename.lastIndexOf(".")+1);
        fileInputStream.read(data);
        BASE64Encoder encoder=new BASE64Encoder();
        String encode=encoder.encode(data).replaceAll("\n","");
        return "data:image/"+last+";base64,"+encode;
    }

    public static String decode64(String pic) throws IOException {
        String path="pic/";
        String filename= UUID.randomUUID().toString();
        if (pic==null||pic.equals(""))
            return null;
        File file = new File(path+filename);
        BASE64Decoder decoder=new BASE64Decoder();
        byte[] data=decoder.decodeBuffer(pic);
        OutputStream os=FileUtils.openOutputStream(file);
        os.write(data);
        os.close();
        return path+filename;
    }
}
