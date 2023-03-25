package top.trumandu.util;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * @author Truman.P.Du
 * @date 2023/03/25
 * @description
 */
public class ResourcesFileUtil {

    private static final String FILE_SPLIT = "/";

    public static boolean isExistFile(String path) {
        Path truePath = Paths.get("src/main/resources" + path);
        File file = truePath.toFile();
        return file.exists();
    }


    public static byte[] getResource(String path) throws IOException {
        if (path.startsWith(FILE_SPLIT)) {
            path = path.substring(1, path.length());
        }
        URL resourceUrl = ResourcesFileUtil.class.getClassLoader().getResource(path);
        RandomAccessFile accessFile = new RandomAccessFile(resourceUrl.getFile(), "r");
        FileChannel channel = accessFile.getChannel();
        ByteBuffer buffer = ByteBuffer.allocate((int) channel.size());
        channel.read(buffer);
        buffer.flip();
        byte[] result = buffer.array();
        channel.close();
        accessFile.close();
        return result;
    }

    public static void main(String[] args) throws IOException {
        System.out.println(Arrays.toString(ResourcesFileUtil.getResource("/index.html")));
        System.out.println(ResourcesFileUtil.isExistFile("/index.html"));
    }
}
