
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * 写入木马文件
 * */
public class Main {
    public static void main(String[] args) throws IOException {
        String userdir = System.getProperty("user.dir");
        String filePath = userdir+ File.separator+"src/main/resources/木马程序.bat";
        String errProgram = "java -version 2>&1";
        Files.write(Paths.get(filePath), Arrays.asList(errProgram));
        System.out.println("写入木马成功");
    }
}
