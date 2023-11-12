package yuoj.yuojcodesandbox;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.dfa.FoundWord;
import cn.hutool.dfa.WordTree;
import yuoj.yuojcodesandbox.model.ExecuteCodeRequest;
import yuoj.yuojcodesandbox.model.ExecuteCodeResponse;
import yuoj.yuojcodesandbox.model.ExecuteMessage;
import yuoj.yuojcodesandbox.model.JudgeInfo;
import yuoj.yuojcodesandbox.security.MySecurityManager;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static yuoj.yuojcodesandbox.utils.ProcessUtils.runProcessAndGetMessage;

public class JavaNativeCodeSandBox implements CodeSandBox {
    private static final String GLOBAL_CODE_DIR_NAME = "user.dir";
    private static final String GLOBAL_CODE_TEMP_NAME = "tempCode";
    private static final String GLOBAL_JAVA_CLASS_NAME = "Main.java";
    private static final List<String> blackList = Arrays.asList("Files","exec");
    private static final WordTree WORDTEREE = new WordTree();
    private static final String securityManagerPath = "D:\\work\\yuoj-code-sandbox\\src\\main\\java\\yuoj\\yuojcodesandbox\\security\\MySecurityManager.java";



    static {
        // 初始化字典树
        WORDTEREE.addWords(blackList);
    }


    public static void main(String[] args) {
        JavaNativeCodeSandBox javaNativeCodeSandBox = new JavaNativeCodeSandBox();
        String code = ResourceUtil.readStr("testCode/simpleComputeArgs/Main.java", StandardCharsets.UTF_8);
        // String code = ResourceUtil.readStr("testCode/simpleCompute/Main.java", StandardCharsets.UTF_8);
        //String code = ResourceUtil.readStr("testCode/unsafe/RunFileErr.java", StandardCharsets.UTF_8);
        List<String> input = Arrays.asList("1 2", "3 4");
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .inputList(input)
                .code(code)
                .language("java")
                .build();
        ExecuteCodeResponse executeCodeResponse = javaNativeCodeSandBox.executeCode(executeCodeRequest);
        System.out.println(executeCodeResponse);
    }

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
//        // 实际上只需要限制子程序中
       // System.setSecurityManager(new MySecurityManager());

        List<String> inputList = executeCodeRequest.getInputList();
        String code = executeCodeRequest.getCode();
        // 校验是否存在危险程序
//        FoundWord foundWord = WORDTEREE.matchWord(code);
//        if (foundWord!=null) {
//            System.out.println("包含敏感词"+foundWord.getFoundWord());
//            return null;
//        }
        // 1. 将用户的代码保存为文件
        // step 1:项目根目录新建文件夹tempCode，用于存放临时的代码文件
        // 获取当前用户的工作目录（项目根目录 ）
        String userDir = System.getProperty(GLOBAL_CODE_DIR_NAME);
        // File.separator:On UNIX systems the value of this field is '/'; on Microsoft Windows systems it is '\\'.
        String file = userDir + File.separator + GLOBAL_CODE_TEMP_NAME;
        // 判断全局代码目录是否存在
        if (!FileUtil.exist(file)) {
            // 没有则新建一个（父目录）
            FileUtil.mkdir(file);
        }
        // 把用户代码隔离存放（不可能把所有的main放一起）
        String userCodeParentPath = file + File.separator + UUID.randomUUID();
        String userCodePath = userCodeParentPath + File.separator + GLOBAL_JAVA_CLASS_NAME;
        // 将用户代码写入文件：文件内容（用户code），文件路径，字符集utf-8
        File userCodeFile = FileUtil.writeString(code, userCodePath, StandardCharsets.UTF_8);

        // 2. 编译代码，得到class文件
        // 获取编译代码的命令行
        String compileCmd = String.format("javac -encoding utf-8 %s", userCodeFile.getAbsolutePath());
        try {
            Process process = Runtime.getRuntime().exec(compileCmd);
            ExecuteMessage executeMessage = runProcessAndGetMessage(process, "编译");
            System.out.println(executeMessage);
        } catch (Exception e) {
            return getErrResp(e);
        }
        // 3. 执行代码，得到输出结果（命令行）
        // 3-2.获取输出信息列表
        List<ExecuteMessage> executeMessageList = new ArrayList<>();
        for (String inputArgs : inputList) {
            // -Dfile.encoding=UTF-8来解决中文乱码，放在 -cp之前
            // jvm参数 -Xmx256m表示最大的占用256MB，-Xmx256s是最初直接指定空间节约时间
            compileCmd = String.format("java -Xmx256m -Dfile.encoding=UTF-8 -cp %s Main %s", userCodeParentPath, inputArgs);
            //compileCmd = String.format("java -Dfile.encoding=UTF-8 -cp %s;%s -Djava.security.manger=MySecurityManager Main", userCodeParentPath, inputArgs);
            try {
                Process process = Runtime.getRuntime().exec(compileCmd);
                // 超时控制：创建一个线程睡眠120毫秒，如果它睡完这个程序还没执行结束就直接终止该程序
//                new Thread(()->{
//                    try {
//                        Thread.sleep(1500);
//                        process.destroy();
//                        System.out.println("超时中断了");
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
//                }).start();

                ExecuteMessage executeMessage = runProcessAndGetMessage(process, "执行");
                // ExecuteMessage executeMessage = ProcessUtils.runInteractProcessAndGetMessage(exec, "执行", inputArgs);
                System.out.println(executeMessage);
                executeMessageList.add(executeMessage);
            } catch (Exception e) {
                return getErrResp(e);
            }
        }
        // 4. 收集整理执行代码的输出结果
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        List<String> outputList = new ArrayList<>();
        long maxTime = 0;
        for (ExecuteMessage executeMessage : executeMessageList) {
            String errMessage = executeMessage.getErrMessage();
            if (StrUtil.isNotBlank(errMessage)) {
                // 将错误信息设置到响应对象中
                executeCodeResponse.setMessage(errMessage);
                // 用户执行代码中存在错误
                executeCodeResponse.setStatus(3);
                break;
            }
            outputList.add(executeMessage.getMessage());
            // 取最大值目的是判断是否超时
            maxTime = 0;
            Long time = executeMessage.getTime();
            if (time != null) {
                maxTime = Math.max(maxTime, time);
            }
        }
        executeCodeResponse.setOutputList(outputList);
        // 如果每一条信息都正常输出了
        if (outputList.size() == executeMessageList.size()) {
            // 正常完成时状态码为1
            executeCodeResponse.setStatus(1);
        }

        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setTime(maxTime);
        executeCodeResponse.setJudgeInfo(judgeInfo);

        // 5. 文件清理，防止内存不足
        if (userCodeFile.getParentFile() != null) {
            boolean del = FileUtil.del(userCodeParentPath);
            System.out.println("删除"+(del?"成功":"失败"));
        }

        return executeCodeResponse;
    }

    /**
     * 6. 错误处理，提升健壮性
     * 获取错误的响应
     * 封装一个错误处理方法，当程序抛出异常时，直接返回错误响应
     * */
    private ExecuteCodeResponse getErrResp(Throwable e){
        ExecuteCodeResponse executeCodeResponse= new ExecuteCodeResponse();

        // 2 表示代码沙箱错误
        executeCodeResponse.setStatus(2);
        executeCodeResponse.setJudgeInfo(new JudgeInfo());
        executeCodeResponse.setMessage(e.getMessage());
        executeCodeResponse.setOutputList(new ArrayList<>());
        return executeCodeResponse;
    }
}
