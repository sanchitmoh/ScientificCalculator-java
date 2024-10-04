
import java.awt.*;
import java.awt.event.*;
import java.util.Stack;
import javax.swing.*;

public class ScientificCalculator extends JFrame implements ActionListener {

    private JTextField display;
    private StringBuilder input;

    public ScientificCalculator() {
        input = new StringBuilder();

        // Frame settings
        setTitle("Scientific Calculator");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Display field at the top
        display = new JTextField();
        display.setFont(new Font("Arial", Font.BOLD, 30));
        display.setEditable(false);
        display.setHorizontalAlignment(SwingConstants.RIGHT);
        add(display, BorderLayout.NORTH);

        // Button panel for the calculator
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(8, 4, 10, 10));

        String[] buttonLabels = {
                "7", "8", "9", "/",
                "4", "5", "6", "*",
                "1", "2", "3", "-",
                "0", ".", "=", "+",
                "(", ")", "C", "Back",
                "sin", "cos", "tan", "log",
                "sqrt", "exp", "^", "neg"
        };

        // Add buttons to the panel
        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.setFont(new Font("Arial", Font.BOLD, 20));
            button.addActionListener(this);
            panel.add(button);
        }

        add(panel, BorderLayout.CENTER);

        // Show the frame
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        switch (command) {
            case "=":
                try {
                    double result = evaluate(input.toString());
                    display.setText(String.valueOf(result));
                    input.setLength(0);
                } catch (ArithmeticException ex) {
                    display.setText("Math Error");
                    input.setLength(0);
                } catch (Exception ex) {
                    display.setText("Error");
                    input.setLength(0);
                }
                break;
            case "C":
                input.setLength(0);
                display.setText("");
                break;
            case "Back":
                if (input.length() > 0) {
                    input.deleteCharAt(input.length() - 1);
                    display.setText(input.toString());
                }
                break;
            case "neg":
                if (input.length() == 0 || isOperator(input.charAt(input.length() - 1))) {
                    input.append("-");
                } else if (input.charAt(0) != '-') {
                    input.insert(0, '-');
                }
                display.setText(input.toString());
                break;
            case "sin":
            case "cos":
            case "tan":
            case "log":
            case "sqrt":
            case "exp":
                handleAdvancedOperations(command);
                break;
            default:
                input.append(command);
                display.setText(input.toString());
                break;
        }
    }

    private void handleAdvancedOperations(String operation) {
        try {
            double value = Double.parseDouble(display.getText());
            double result = 0;

            switch (operation) {
                case "sin":
                    result = Math.sin(Math.toRadians(value));
                    break;
                case "cos":
                    result = Math.cos(Math.toRadians(value));
                    break;
                case "tan":
                    result = Math.tan(Math.toRadians(value));
                    break;
                case "log":
                    result = Math.log(value);
                    break;
                case "sqrt":
                    result = Math.sqrt(value);
                    break;
                case "exp":
                    result = Math.exp(value);
                    break;
            }

            display.setText(String.valueOf(result));
            input.setLength(0);

        } catch (Exception e) {
            display.setText("Error");
        }
    }

    // Evaluate the input string for basic arithmetic operations
    private double evaluate(String expression) throws Exception {
        Stack<Double> numbers = new Stack<>();
        Stack<Character> operators = new Stack<>();
        boolean expectNumber = true;  // To handle negative numbers properly

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);

            if (Character.isDigit(c) || c == '.') {
                StringBuilder num = new StringBuilder();
                while (i < expression.length() && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    num.append(expression.charAt(i++));
                }
                i--;
                numbers.push(Double.parseDouble(num.toString()));
                expectNumber = false;
            } else if (c == '(') {
                operators.push(c);
                expectNumber = true;
            } else if (c == ')') {
                while (operators.peek() != '(') {
                    numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop()));
                }
                operators.pop();
                expectNumber = false;
            } else if (isOperator(c)) {
                // Handle negative numbers
                if (c == '-' && expectNumber) {
                    numbers.push(0.0);
                }
                while (!operators.isEmpty() && precedence(c) <= precedence(operators.peek())) {
                    numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop()));
                }
                operators.push(c);
                expectNumber = true;
            }
        }

        while (!operators.isEmpty()) {
            numbers.push(applyOperation(operators.pop(), numbers.pop(), numbers.pop()));
        }

        return numbers.pop();
    }

    private boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '^';
    }

    private int precedence(char operator) {
        switch (operator) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
            case '^':
                return 3;
            default:
                return -1;
        }
    }

    private double applyOperation(char operator, double b, double a) {
        switch (operator) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0) throw new ArithmeticException("Cannot divide by zero");
                return a / b;
            case '^':
                return Math.pow(a, b);
            default:
                return 0;
        }
    }

    public static void main(String[] args) {
        new ScientificCalculator();
    }
}


    

