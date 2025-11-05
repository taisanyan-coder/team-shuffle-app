import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String[] OMIKUJI_RESULTS = {
            "大吉 - 最高の一日になりそう！",
            "中吉 - 小さな幸せが訪れるでしょう。",
            "小吉 - 穏やかな一日になりそうです。",
            "吉 - 前向きな気持ちで進んでみましょう。",
            "末吉 - 些細なことで笑顔になれるかも。",
            "凶 - 慌てず落ち着いて行動しましょう。",
            "大凶 - リセットして再スタートするチャンス！"
    };
    private static final Random RANDOM = new Random();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TaskManager manager = new TaskManager(scanner);

        System.out.println("🗒️  タスク管理アプリへようこそ  🗒️");
        System.out.println("---------------------------------");

        boolean running = true;
        while (running) {
            printMenu();
            System.out.print("メニュー番号を選択してください: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    manager.addTask();
                    break;
                case "2":
                    manager.listTasks();
                    break;
                case "3":
                    manager.completeTask();
                    break;
                case "4":
                    manager.deleteTask();
                    break;
                case "5":
                    runOmikuji(scanner);
                    break;
                case "6":
                    running = false;
                    break;
                default:
                    System.out.println("⚠️ 無効な入力です。1〜6の数字を入力してください。\n");
            }
        }

        System.out.println("ご利用ありがとうございました！");
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("1. タスクを追加");
        System.out.println("2. タスク一覧を表示");
        System.out.println("3. タスクを完了にする");
        System.out.println("4. タスクを削除");
        System.out.println("5. おみくじを引く");
        System.out.println("6. アプリを終了");
    }

    private static void runOmikuji(Scanner scanner) {
        System.out.println("\n🎍 Javaおみくじ 🎍");
        System.out.println("-----------------------");
        System.out.print("お名前を入力してください: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            name = "ゲスト";
        }

        String result = OMIKUJI_RESULTS[RANDOM.nextInt(OMIKUJI_RESULTS.length)];
        System.out.println();
        System.out.println(name + "さんの本日の運勢は...");
        System.out.println("🎯 " + result + " 🎯");
        System.out.println("\nEnterキーでメニューに戻ります。");
        scanner.nextLine();
    }

    private static class TaskManager {
        private final List<Task> tasks = new ArrayList<>();
        private final Scanner scanner;
        private int nextId = 1;

        TaskManager(Scanner scanner) {
            this.scanner = scanner;
        }

        void addTask() {
            System.out.println("\n--- タスク追加 ---");
            System.out.print("タイトル: ");
            String title = scanner.nextLine().trim();
            if (title.isEmpty()) {
                System.out.println("⚠️ タイトルは必須です。タスクの追加をキャンセルしました。\n");
                return;
            }

            System.out.print("詳細（任意）: ");
            String description = scanner.nextLine().trim();

            System.out.print("期限 (yyyy-MM-dd, 任意): ");
            String dueDateInput = scanner.nextLine().trim();
            LocalDate dueDate = null;
            if (!dueDateInput.isEmpty()) {
                try {
                    dueDate = LocalDate.parse(dueDateInput, DATE_FORMAT);
                } catch (DateTimeParseException e) {
                    System.out.println("⚠️ 日付の形式が正しくありません。期限は未設定にします。\n");
                }
            }

            Task task = new Task(nextId++, title, description, dueDate);
            tasks.add(task);
            System.out.println("✅ タスクを追加しました: " + task + "\n");
        }

        void listTasks() {
            printTasksWithHeader("\n--- タスク一覧 ---", "登録されているタスクはありません。\n");
        }

        void completeTask() {
            if (!printTasksWithHeader("\n--- 完了にするタスクを選択 ---", "⚠️ タスクがないため完了にできません。\n")) {
                return;
            }

            int id = promptForTaskId("完了にしたいタスクIDを入力してください（Enterでキャンセル）: ");
            if (id == -1) {
                System.out.println("操作をキャンセルしました。\n");
                return;
            }
            Task task = findTaskById(id);
            if (task == null) {
                System.out.println("⚠️ 指定されたIDのタスクは存在しません。\n");
                return;
            }

            if (task.isCompleted()) {
                System.out.println("⚠️ このタスクは既に完了済みです。\n");
                return;
            }

            task.markCompleted();
            System.out.println("✨ タスクを完了にしました: " + task + "\n");
        }

        void deleteTask() {
            if (!printTasksWithHeader("\n--- 削除するタスクを選択 ---", "⚠️ タスクがないため削除できません。\n")) {
                return;
            }

            int id = promptForTaskId("削除したいタスクIDを入力してください（Enterでキャンセル）: ");
            if (id == -1) {
                System.out.println("操作をキャンセルしました。\n");
                return;
            }
            Task task = findTaskById(id);
            if (task == null) {
                System.out.println("⚠️ 指定されたIDのタスクは存在しません。\n");
                return;
            }

            tasks.remove(task);
            System.out.println("🗑️ タスクを削除しました: " + task + "\n");
        }

        private boolean printTasksWithHeader(String header, String emptyMessage) {
            System.out.println(header);
            if (tasks.isEmpty()) {
                System.out.println(emptyMessage);
                return false;
            }

            tasks.stream()
                    .sorted(Comparator
                            .comparing(Task::isCompleted)
                            .thenComparing(Task::getDueDateForSorting)
                            .thenComparing(Task::getId))
                    .forEach(task -> System.out.println(task.toDisplayString()));
            System.out.println();
            return true;
        }

        private int promptForTaskId(String message) {
            while (true) {
                System.out.print(message);
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) {
                    return -1;
                }
                try {
                    return Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.println("⚠️ 数字で入力してください。");
                }
            }
        }

        private Task findTaskById(int id) {
            return tasks.stream()
                    .filter(task -> task.getId() == id)
                    .findFirst()
                    .orElse(null);
        }
    }

    private static class Task {
        private final int id;
        private final String title;
        private final String description;
        private final LocalDate dueDate;
        private boolean completed;

        Task(int id, String title, String description, LocalDate dueDate) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.dueDate = dueDate;
        }

        int getId() {
            return id;
        }

        LocalDate getDueDateForSorting() {
            return dueDate != null ? dueDate : LocalDate.MAX;
        }

        boolean isCompleted() {
            return completed;
        }

        void markCompleted() {
            this.completed = true;
        }

        String toDisplayString() {
            String status = completed ? "[完了]" : "[未完了]";
            String due = dueDate != null ? dueDate.format(DATE_FORMAT) : "期限なし";
            if (isOverdue()) {
                due += " ⚠️期限切れ";
            }
            String detail = description.isEmpty() ? "(詳細なし)" : description;
            return String.format("ID:%d %s %s | 期限: %s | %s", id, status, title, due, detail);
        }

        private boolean isOverdue() {
            return !completed && dueDate != null && dueDate.isBefore(LocalDate.now());
        }

        @Override
        public String toString() {
            return String.format("ID:%d %s", id, title);
        }
    }
}
