package yuoj.yuojcodesandbox.model;

import lombok.Data;

/**
 * 进程执行信息
 * 定义一个对象来返回封装的信息
 */
@Data
public class ExecuteMessage {
    private int waitForValue;
    private String message;
    private String errMessage;
    private long time;
}
