package com.example.vadim.calc;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.math.BigDecimal;

public class MainActivity extends AppCompatActivity {

    TextView screen;
    TextView result;
    private static final String SCREEN = "screen";
    private static final String RESULT = "result";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        screen = (TextView) findViewById(R.id.screen);
        result = (TextView) findViewById(R.id.result);
        if (savedInstanceState != null) {
            screen.setText(savedInstanceState.getString(SCREEN, ""));
            result.setText(savedInstanceState.getString(RESULT, ""));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle data) {
        super.onSaveInstanceState(data);
        data.putString(SCREEN, screen.getText().toString());
        data.putString(RESULT, result.getText().toString());
    }

    protected void checkField() {
        if (result.getText().length() != 0) {
            screen.setText("");
            result.setText("");
        }
    }

    public void clickText(View view) {
        checkField();
        Button current = (Button) view;
        screen.append(current.getText());
    }

    public void clickC(View view) {
        checkField();
        String text = screen.getText().toString();
        if (!text.isEmpty()) {
            text = text.substring(0, text.length() - 1);
            screen.setText(text);
        }
    }

    public void clickDel(View view) {
        screen.setText("");
        result.setText("");
    }

    public void clickEqv(View view) {
        if (result.getText().length() != 0) {
            return;
        }
        try {
            BigDecimal ret = Parser.getResult(screen.getText().toString());
            result.setText("=" + String.valueOf(ret.doubleValue()));
        } catch(ArithmeticException e) {
            result.setText("=" + e.getMessage());
        }
    }
    private static final String TAG = "Calc";
}