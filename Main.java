import java.util.Random;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String[] omikuji = {"大吉", "中吉", "小吉", "吉", "末吉", "凶"};
        Scanner sc = new Scanner(System.in);

        System.out.println("🎍 Javaおみくじ 🎍");
        System.out.println("-------------------");
        System.out.print("あなたの名前を入力してください：");

        String name = sc.nextLine();
        String result = omikuji[new Random().nextInt(omikuji.length)];

        System.out.println("\n" + name + "さんの今日の運勢は【" + result + "】です！");
        if (result.equals("大吉")) {
            System.out.println("✨ 最高の1日になりそうです！");
        } else if (result.equals("凶")) {
            System.out.println("😅 注意深く行動しましょう。");
        } else {
            System.out.println("😊 穏やかな日になりそうです。");
        }

        sc.close();
    }
}
