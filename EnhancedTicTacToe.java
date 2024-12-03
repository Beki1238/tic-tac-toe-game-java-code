import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class EnhancedTicTacToe extends JFrame {
    private JButton[][] buttons = new JButton[3][3];
    private char currentPlayer = 'X';
    private JLabel statusLabel = new JLabel("Player X's Turn");
    private int scoreX = 0, scoreO = 0;
    private JLabel scoreLabel = new JLabel("Score - X: 0, O: 0");
    private JLabel timerLabel = new JLabel("Time: 0s");
    private int secondsElapsed = 0;
    private Timer gameTimer;
    private ArrayList<String> gameHistory = new ArrayList<>();
    private JTextArea historyArea = new JTextArea(5, 20);

    public EnhancedTicTacToe() {
        setTitle("Enhanced Tic-Tac-Toe");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 600);
        setLayout(new BorderLayout());

        // Game Board
        JPanel board = new JPanel();
        board.setLayout(new GridLayout(3, 3));
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new JButton("");
                buttons[i][j].setFont(new Font("Arial", Font.BOLD, 60));
                buttons[i][j].addActionListener(new ButtonClickListener(i, j));
                board.add(buttons[i][j]);
            }
        }
        add(board, BorderLayout.CENTER);

        // Status Panel
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new GridLayout(3, 1));
        statusPanel.add(statusLabel);
        statusPanel.add(scoreLabel);
        statusPanel.add(timerLabel);
        add(statusPanel, BorderLayout.NORTH);

        // Control Panel
        JPanel controlPanel = new JPanel();
        JButton restartButton = new JButton("Restart Game");
        restartButton.addActionListener(e -> resetGame(false));
        JButton resetAllButton = new JButton("Reset All");
        resetAllButton.addActionListener(e -> resetGame(true));
        JButton toggleThemeButton = new JButton("Toggle Theme");
        toggleThemeButton.addActionListener(e -> toggleTheme());

        controlPanel.add(restartButton);
        controlPanel.add(resetAllButton);
        controlPanel.add(toggleThemeButton);
        add(controlPanel, BorderLayout.SOUTH);

        // Game History
        JPanel historyPanel = new JPanel();
        historyPanel.setBorder(BorderFactory.createTitledBorder("Game History"));
        historyArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(historyArea);
        historyPanel.add(scrollPane);
        add(historyPanel, BorderLayout.EAST);

        // Timer
        gameTimer = new Timer(1000, e -> {
            secondsElapsed++;
            timerLabel.setText("Time: " + secondsElapsed + "s");
        });
        gameTimer.start();

        setFocusable(true);
        setVisible(true);
    }

    private class ButtonClickListener implements ActionListener {
        private int row, col;

        public ButtonClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (buttons[row][col].getText().equals("") && !checkWinner()) {
                playSound("click.wav");
                buttons[row][col].setText(String.valueOf(currentPlayer));
                buttons[row][col].setEnabled(false);
                if (checkWinner()) {
                    playSound("win.wav");
                    statusLabel.setText("Player " + currentPlayer + " Wins!");
                    updateScore();
                    gameHistory.add("Player " + currentPlayer + " won in " + secondsElapsed + "s");
                    updateHistory();
                    return;
                } else if (isBoardFull()) {
                    playSound("draw.wav");
                    statusLabel.setText("It's a Draw!");
                    gameHistory.add("Draw in " + secondsElapsed + "s");
                    updateHistory();
                    return;
                }
                currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
                statusLabel.setText("Player " + currentPlayer + "'s Turn");
            }
        }
    }

    private boolean checkWinner() {
        for (int i = 0; i < 3; i++) {
            if (buttons[i][0].getText().equals(String.valueOf(currentPlayer)) &&
                buttons[i][1].getText().equals(String.valueOf(currentPlayer)) &&
                buttons[i][2].getText().equals(String.valueOf(currentPlayer))) {
                return true;
            }
            if (buttons[0][i].getText().equals(String.valueOf(currentPlayer)) &&
                buttons[1][i].getText().equals(String.valueOf(currentPlayer)) &&
                buttons[2][i].getText().equals(String.valueOf(currentPlayer))) {
                return true;
            }
        }
        if (buttons[0][0].getText().equals(String.valueOf(currentPlayer)) &&
            buttons[1][1].getText().equals(String.valueOf(currentPlayer)) &&
            buttons[2][2].getText().equals(String.valueOf(currentPlayer))) {
            return true;
        }
        if (buttons[0][2].getText().equals(String.valueOf(currentPlayer)) &&
            buttons[1][1].getText().equals(String.valueOf(currentPlayer)) &&
            buttons[2][0].getText().equals(String.valueOf(currentPlayer))) {
            return true;
        }
        return false;
    }

    private boolean isBoardFull() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (buttons[i][j].getText().equals("")) {
                    return false;
                }
            }
        }
        return true;
    }

    private void updateScore() {
        if (currentPlayer == 'X') {
            scoreX++;
        } else {
            scoreO++;
        }
        scoreLabel.setText("Score - X: " + scoreX + ", O: " + scoreO);
    }

    private void resetGame(boolean resetScores) {
        currentPlayer = 'X';
        statusLabel.setText("Player X's Turn");
        secondsElapsed = 0;
        timerLabel.setText("Time: 0s");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
                buttons[i][j].setEnabled(true);
            }
        }
        if (resetScores) {
            scoreX = 0;
            scoreO = 0;
            scoreLabel.setText("Score - X: 0, O: 0");
            gameHistory.clear();
            updateHistory();
        }
    }

    private void updateHistory() {
        historyArea.setText("");
        for (String record : gameHistory) {
            historyArea.append(record + "\n");
        }
    }

    private void toggleTheme() {
        Color background = getContentPane().getBackground();
        if (background.equals(Color.WHITE)) {
            getContentPane().setBackground(Color.DARK_GRAY);
            for (JButton[] row : buttons) {
                for (JButton button : row) {
                    button.setBackground(Color.LIGHT_GRAY);
                    button.setForeground(Color.WHITE);
                }
            }
            statusLabel.setForeground(Color.WHITE);
            scoreLabel.setForeground(Color.WHITE);
            timerLabel.setForeground(Color.WHITE);
        } else {
            getContentPane().setBackground(Color.WHITE);
            for (JButton[] row : buttons) {
                for (JButton button : row) {
                    button.setBackground(null);
                    button.setForeground(Color.BLACK);
                }
            }
            statusLabel.setForeground(Color.BLACK);
            scoreLabel.setForeground(Color.BLACK);
            timerLabel.setForeground(Color.BLACK);
        }
    }

    private void playSound(String fileName) {
        try {
            File soundFile = new File(fileName);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new EnhancedTicTacToe();
    }
}
