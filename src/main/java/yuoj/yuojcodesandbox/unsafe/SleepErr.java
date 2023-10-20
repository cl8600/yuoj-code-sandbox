package yuoj.yuojcodesandbox.unsafe;

/**
 * 无限睡眠（时间）
 */
public class SleepErr {
    public static void main(String[] args) throws InterruptedException {
        Thread.sleep(1000*60*60);
        System.out.println("睡完了");
    }
}
