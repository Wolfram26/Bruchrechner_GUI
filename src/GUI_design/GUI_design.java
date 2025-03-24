package GUI_design;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.Stack;

public class GUI_design {
    private JTextField txt_calculator;
    private JPanel Bruchrechner_GUI;
    private JButton btn_plus, btn_minus, btn_multiply, btn_divide, btn_equals, btn_clear, btn_delete, btn_comma;
    private JButton btn_1, btn_2, btn_3, btn_4, btn_5, btn_6, btn_7, btn_8, btn_9, btn_0;
    private JButton btn_numerator_1, btn_numerator_2, btn_numerator_3, btn_numerator_4, btn_numerator_5;
    private JButton btn_numerator_6, btn_numerator_7, btn_numerator_8, btn_numerator_9, btn_numerator_0, btn_numerator_delete;
    private JButton btn_denominator_1, btn_denominator_2, btn_denominator_3, btn_denominator_4, btn_denominator_5;
    private JButton btn_denominator_6, btn_denominator_7, btn_denominator_8, btn_denominator_9, btn_denominator_0, btn_denominator_delete;
    private JButton btn_plus_minus, btn_decor_1, btn_decor_2, btn_decor_3;

    private String expression = "";
    private String wholeNumber = "";
    private String numeratorInput = "";
    private String denominatorInput = "";
    private boolean isEnteringNumerator = false;
    private boolean isEnteringDenominator = false;
    private boolean fractionLocked = false;

    public GUI_design() {
        setupListeners();
    }

    private void setupListeners() {
        // Number Buttons Listener
        ActionListener numberListener = e -> {
            JButton clickedButton = (JButton) e.getSource();
            String value = clickedButton.getText();

            if (!isEnteringNumerator && !isEnteringDenominator && !fractionLocked) {
                wholeNumber += value;
            } else if (isEnteringNumerator && !fractionLocked) {
                numeratorInput += value;
            } else if (isEnteringDenominator) {
                denominatorInput += value;
                fractionLocked = true;
            }
            updateDisplay();
        };

        JButton[] numberButtons = {btn_0, btn_1, btn_2, btn_3, btn_4, btn_5, btn_6, btn_7, btn_8, btn_9};
        for (JButton btn : numberButtons) btn.addActionListener(numberListener);

        JButton[] numeratorButtons = {btn_numerator_0, btn_numerator_1, btn_numerator_2, btn_numerator_3, btn_numerator_4, btn_numerator_5, btn_numerator_6, btn_numerator_7, btn_numerator_8, btn_numerator_9};
        for (JButton btn : numeratorButtons) btn.addActionListener(numberListener);

        JButton[] denominatorButtons = {btn_denominator_0, btn_denominator_1, btn_denominator_2, btn_denominator_3, btn_denominator_4, btn_denominator_5, btn_denominator_6, btn_denominator_7, btn_denominator_8, btn_denominator_9};
        for (JButton btn : denominatorButtons) btn.addActionListener(numberListener);

        // Switch to numerator input
        btn_comma.addActionListener(e -> {
            if (!fractionLocked) {
                isEnteringNumerator = true;
                isEnteringDenominator = false;
                updateDisplay();
            }
        });

        // Switch to denominator input
        for (JButton btn : numeratorButtons) {
            btn.addActionListener(e -> {
                if (!numeratorInput.isEmpty()) {
                    isEnteringNumerator = false;
                    isEnteringDenominator = true;
                    updateDisplay();
                }
            });
        }

        // Operator buttons
        ActionListener operatorListener = e -> {
            JButton clickedButton = (JButton) e.getSource();
            if (!wholeNumber.isEmpty()) {
                appendFraction();
            }
            expression += " " + clickedButton.getText() + " ";
            fractionLocked = false;
            updateDisplay();
        };

        btn_plus.addActionListener(operatorListener);
        btn_minus.addActionListener(operatorListener);
        btn_multiply.addActionListener(operatorListener);
        btn_divide.addActionListener(operatorListener);

        // Equals Button
        btn_equals.addActionListener(e -> {
            if (!wholeNumber.isEmpty()) {
                appendFraction();
            }
            try {
                String result = evaluateExpression(expression);
                txt_calculator.setText(result);
                expression = result;
            } catch (Exception ex) {
                txt_calculator.setText("Error");
            }
        });

        // Clear Button
        btn_clear.addActionListener(e -> {
            expression = "";
            wholeNumber = "";
            numeratorInput = "";
            denominatorInput = "";
            isEnteringNumerator = false;
            isEnteringDenominator = false;
            fractionLocked = false;
            txt_calculator.setText("");
        });
    }

    private void appendFraction() {
        if (!wholeNumber.isEmpty()) {
            if (!numeratorInput.isEmpty() && !denominatorInput.isEmpty()) {
                expression += wholeNumber + "_" + numeratorInput + "/" + denominatorInput + " ";
            } else {
                expression += wholeNumber + " ";
            }
        }
        wholeNumber = "";
        numeratorInput = "";
        denominatorInput = "";
        isEnteringNumerator = false;
        isEnteringDenominator = false;
        fractionLocked = false;
    }

    private void updateDisplay() {
        String displayText = expression;
        if (!wholeNumber.isEmpty()) {
            displayText += wholeNumber;
            if (!numeratorInput.isEmpty()) {
                displayText += "_" + numeratorInput;
            }
            if (!denominatorInput.isEmpty()) {
                displayText += "/" + denominatorInput;
            }
        }
        txt_calculator.setText(displayText);
    }

    private double parseFraction(String fraction) {
        if (fraction.contains("_")) { // Mixed fraction (e.g., "3_1/2")
            String[] parts = fraction.split("_");
            int whole = Integer.parseInt(parts[0]);
            String[] frac = parts[1].split("/");
            int num = Integer.parseInt(frac[0]);
            int den = Integer.parseInt(frac[1]);
            return whole + (double) num / den;
        } else if (fraction.contains("/")) { // Proper fraction (e.g., "1/2")
            String[] frac = fraction.split("/");
            int num = Integer.parseInt(frac[0]);
            int den = Integer.parseInt(frac[1]);
            return (double) num / den;
        }
        return Double.parseDouble(fraction); // Whole number
    }

    private String convertToFraction(double value) {
        int whole = (int) value;
        double decimalPart = value - whole;

        if (decimalPart == 0) {
            return String.valueOf(whole); // Just a whole number
        }

        int denominator = 100000; // Large denominator for precision
        int numerator = (int) Math.round(decimalPart * denominator);
        int gcd = gcd(numerator, denominator);

        numerator /= gcd;
        denominator /= gcd;

        if (whole == 0) {
            return numerator + "/" + denominator;
        }
        return whole + "_" + numerator + "/" + denominator;
    }

    // Helper method to find GCD
    private int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }

    private String evaluateExpression(String expr) {
        String[] tokens = expr.split(" ");
        Stack<Double> values = new Stack<>();
        Stack<String> operators = new Stack<>();

        for (String token : tokens) {
            if (token.matches("-?\\d+(_\\d+/\\d+)?|\\d+/\\d+")) {
                values.push(parseFraction(token));
            } else if (token.matches("[+\\-*/]")) {
                operators.push(token);
            }
        }

        while (!operators.isEmpty()) {
            double b = values.pop();
            double a = values.pop();
            String op = operators.pop();

            double result = switch (op) {
                case "+" -> a + b;
                case "-" -> a - b;
                case "*" -> a * b;
                case "/" -> a / b;
                default -> 0;
            };

            values.push(result);
        }

        return convertToFraction(values.pop()); // Convert the result back to a fraction
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Fraction Calculator");
            GUI_design calculator = new GUI_design();
            frame.setContentPane(calculator.Bruchrechner_GUI);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        });
    }
}
