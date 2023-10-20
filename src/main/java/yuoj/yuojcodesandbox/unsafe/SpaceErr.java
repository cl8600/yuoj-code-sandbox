package yuoj.yuojcodesandbox.unsafe;

import java.util.ArrayList;
import java.util.List;

/**
 * 无限占用空间，浪费系统内存（空间）
 */
public class SpaceErr {
    public static void main(String[] args) throws InterruptedException {
        List<byte[]> list = new ArrayList<>();
        while (true){
            list.add(new byte[1000000]);
        }
    }
}
