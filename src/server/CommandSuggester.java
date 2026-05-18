package server;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

public class CommandSuggester {
    private static List<String> COMMANDS;

    private static final List<String> ENTEMPLATES = Arrays.asList(
            "Do you mean '%s'?",
            "I guess you need '%s'",
            "Correct command is '%s'",
            "Maybe you are looking for '%s'?",
            "Did you want to type '%s'?",
            "It looks like you mean '%s'"
    );

    private static final List<String> RUTEMPLATES = Arrays.asList(
            "Вы имели в виду '%s'?",
            "Возможно, вам нужно '%s'?",
            "Скорее всего, правильная команда — '%s'.",
            "Кажется, вы опечатались. Попробуйте '%s'.",
            "Может быть, вы искали '%s'?",
            "Я полагаю, вам требуется команда '%s'.",
            "Вы хотели ввести '%s'?",
            "Похоже, что вы имели в виду '%s'.",
            "Введите '%s', если вы хотите выполнить эту команду.",
            "Система распознала это как '%s'.",
            "Вероятно, имелась в виду команда '%s'.",
            "Попробуйте использовать '%s'."
    );

    public CommandSuggester(CommandManager commandManager) {
         COMMANDS = new ArrayList<>(commandManager.getCommands().keySet());
    }

    //  Levenshtein
    private int getLevenshteinDistance(String s1, String s2) {
        int m = s1.length();
        int n = s2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 0; i <= m; i++) dp[i][0] = i;
        for (int j = 0; j <= n; j++) dp[0][j] = j;

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost);
            }
        }
        return dp[m][n];
    }

    public String correct(String input) {
        if (input == null || input.isEmpty()) return "Command not recognized.";

        String lowerInput = input.toLowerCase();
        String bestCmd = null;
        int minDist = Integer.MAX_VALUE;

        for (String cmd : COMMANDS) {
            if (cmd.toLowerCase().startsWith(lowerInput)) {
                bestCmd = cmd;
                return formatResponse(lowerInput, bestCmd);
            }
        }

        for (String cmd : COMMANDS) {
            int dist = getLevenshteinDistance(lowerInput, cmd);
            if (dist < minDist) {
                minDist = dist;
                bestCmd = cmd;
            }
        }

        // Dynamic threshold (long length => may fail more)
        int threshold = Math.max(2, (int) Math.round(bestCmd.length() * 0.3));

        if (minDist <= threshold) {
            return this.formatResponse(lowerInput, bestCmd);
        } else {
            return "Command not recognized.";
        }
    }

    private String formatResponse(String input, String bestCmd) {
        int templateIndex = Math.abs(input.hashCode()) % RUTEMPLATES.size();
        return String.format(RUTEMPLATES.get(templateIndex), bestCmd);
    }
}
