package yuoj.yuojcodesandbox.security;

import cn.hutool.core.io.FileUtil;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TestSecurityManager {
    public static void main(String[] args) {
        // 开启安全管理器
        System.setSecurityManager(new SecurityManager());
        List<String> strings = FileUtil.readLines("D:\\work\\yuoj-code-sandbox\\src\\main\\resources\\application.yml", StandardCharsets.UTF_8);
        System.out.println(strings);
    }
}
