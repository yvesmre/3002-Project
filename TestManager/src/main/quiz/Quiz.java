package main.quiz;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Quiz {
    File file;

    String type;
    Question[] questions = new Question[10];

    public Quiz(File path) throws FileNotFoundException, IOException {
        this.file = path;
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            int index = 0;
            while ((line = br.readLine()) != null) {

                if (line.contains("type:")) {
                    type = line.split(":")[1].replace(" ", "");
                }

                if (line.contains("q")) {
                    String attempts = line.split(":")[1];
                    char correct = line.split(":")[2].toCharArray()[0];
                    int cor = Integer.parseInt(correct + "");
                    boolean corr = cor > 0;
                    questions[index] = new Question(Integer.parseInt(attempts), corr);

                    index++;
                }

                if (index > 10)
                    break;
            }
        }
    }

    public void save() throws IOException {
        FileWriter f = new FileWriter(file);

        f.write("type:" + type + "\n");
        for (int i = 0; i < questions.length; i++) {
            f.write("q" + (i + 1) + ":" + questions[i].attemptsLeft + ":" + (questions[i].correct ? 0 : 1) + "\n");
        }
        f.close();
    }

    public String toString() {
        String str = "type: " + type + "\n";
        for (int i = 0; i < questions.length; i++)
            str = str + i + ": " + questions[i].toString() + "\n";
        return str;
    }

    public int getNumberOfAttempts(int question) {
        return questions[question - 1].attemptsLeft;
    }

    public void setNumberOfAttempts(int question, int attempts) {
        questions[question - 1].attemptsLeft = attempts;
    }

    public String getPath() {
        return this.file.getName().replace(".txt", "");
    }

    class Question {
        int attemptsLeft;
        boolean correct;

        public Question(int a, boolean co) {
            attemptsLeft = a;
            correct = co;
        }

        @Override
        public String toString() {
            return "{attempts remaining: " + attemptsLeft + ", correctness: " + correct + "}";
        }
    }

}
