package yuoj.yuojcodesandbox.unsafe;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 运行木马文件
 * */
public class RunFileErr {
    public static void main(String[] args) throws IOException, InterruptedException {
        String userdir = System.getProperty("user.dir");
        String filePath = userdir+ File.separator+"src/main/resources/木马程序.bat";
        Process process = Runtime.getRuntime().exec(filePath);
        process.waitFor();

        // 分批获取进程的正常输出
        // process.getInputStream()：进程中获取流 InputStreamReader：输入流读取器 BufferedReader:成块分批的读取输出
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String compileOutputLine;
        // 逐行读取
        while ((compileOutputLine = bufferedReader.readLine()) != null){
            System.out.println(compileOutputLine);
        }
        System.out.println("执行木马成功");
    }
}
