
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("请输入两个整数，以空格分隔: ");
        String input = scanner.nextLine();

        int a = Integer.parseInt(values[0]);
        int b = Integer.parseInt(values[1]);

        int sum = a + b;

        System.out.println("结果: " + sum);

        scanner.close(); // 记得关闭Scanner
    }
}
