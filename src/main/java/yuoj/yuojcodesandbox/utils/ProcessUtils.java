package yuoj.yuojcodesandbox.utils;

import cn.hutool.core.util.StrUtil;
import org.springframework.util.StopWatch;
import yuoj.yuojcodesandbox.model.ExecuteMessage;

import java.io.*;

/**
 * 进程工具类
 */
public class ProcessUtils {

    /**
     * 执行进程并获取信息
     * @param opName 操作名
     * @param runProcess
     *
     */
    public static ExecuteMessage runProcessAndGetMessage(Process runProcess,String opName) {

        ExecuteMessage executeMessage = new ExecuteMessage();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            int waitForValue = runProcess.waitFor();
            StringBuilder compileOutputStringBuilder = new StringBuilder();
            // 通过waitForValue判断程序是否正常返回，从 inputStream 和 errStream 获取控制台输出：
            if (waitForValue == 0){
                System.out.println(opName+"成功");
                // 分批获取进程的正常输出
                // process.getInputStream()：进程中获取流 InputStreamReader：输入流读取器 BufferedReader:成块分批的读取输出
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
                String compileOutputLine;
                // 逐行读取
                while ((compileOutputLine = bufferedReader.readLine()) != null){
                    compileOutputStringBuilder.append(compileOutputLine).append("\n");;
                }
                System.out.println(compileOutputStringBuilder);
            }else {
                System.out.println(opName+"失败"+waitForValue);

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
                String compileOutputLine;
                // 逐行读取
                while ((compileOutputLine = bufferedReader.readLine()) != null){
                    compileOutputStringBuilder.append(compileOutputLine).append("\n");;
                }
                System.out.println(compileOutputStringBuilder);
            }

            // 分批获取错误输出
            BufferedReader errorBufferedReader = new BufferedReader(new InputStreamReader(runProcess.getErrorStream()));
            String errorCompileOutputLine;
            StringBuilder errCompileOutputStringBuilder = new StringBuilder();
            // 逐行读取
            while ((errorCompileOutputLine = errorBufferedReader.readLine()) != null){
                errCompileOutputStringBuilder.append(errorCompileOutputLine).append("\n");;
            }
            stopWatch.stop();
            executeMessage.setTime(stopWatch.getLastTaskTimeMillis());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return executeMessage;
    }

    /**
     * 执行进程并获取信息(交互式进程)
     * @param opName 操作名
     * @param runProcess
     *
     */
    public static ExecuteMessage runInteractProcessAndGetMessage(Process runProcess,String opName,String args) {

        ExecuteMessage executeMessage = new ExecuteMessage();
        try {
            // 向控制台输入程序
            OutputStream outputStream = runProcess.getOutputStream();

            // 定义一个写输出流的Writer,相当于是用户在控制台终端里面输入用例input值
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            String[] values = args.split(" ");
            String join = StrUtil.join("\n",values)+"\n";
            outputStreamWriter.write(join);
            // 相当于回车，执行输入
            outputStreamWriter.flush();
            // 分批获取进程的正常输出
            InputStream inputStream = runProcess.getInputStream();
            // process.getInputStream()：进程中获取流 InputStreamReader：输入流读取器 BufferedReader:成块分批的读取输出
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder compileOutputStringBuilder = new StringBuilder();
            String compileOutputLine;
            // 逐行读取
            while ((compileOutputLine = bufferedReader.readLine()) != null){
                compileOutputStringBuilder.append(compileOutputLine).append("\n");
            }
            executeMessage.setErrMessage(compileOutputStringBuilder.toString());
            // 关闭资源
            outputStreamWriter.close();
            outputStream.close();
            inputStream.close();
            runProcess.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return executeMessage;
    }
}

