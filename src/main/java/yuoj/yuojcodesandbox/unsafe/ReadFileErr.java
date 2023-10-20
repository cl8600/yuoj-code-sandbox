package yuoj.yuojcodesandbox.unsafe;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * 读取服务器文件内容
 * */
public class ReadFileErr {
    public static void main(String[] args) throws IOException {
        String userdir = System.getProperty("user.dir");
        String filePath = userdir+ File.separator+"src/main/resources/application.yml";
        List<String> strings = Files.readAllLines(Paths.get(filePath));
        System.out.println(String.join("\n",strings));
    }
}
