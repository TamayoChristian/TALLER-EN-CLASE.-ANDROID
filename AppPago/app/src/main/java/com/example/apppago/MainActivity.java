package com.example.apppago;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edt_total;
    private EditText edt_numero;
    private EditText edt_otros;
    private RadioGroup rgrupo;
    private Button btn_salir;
    private Button btn_calcular;
    private TextView txtPropina;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        edt_total = findViewById(R.id.edt_Total);
        edt_numero = findViewById(R.id.edt_Numero);
        edt_otros = findViewById(R.id.edt_otros);
        rgrupo = findViewById(R.id.rgroup);
        btn_calcular = findViewById(R.id.btn_calcular);
        btn_salir = findViewById(R.id.btn_salir);
        txtPropina = findViewById(R.id.txtPropina);

        btn_calcular.setOnClickListener(this);
        btn_salir.setOnClickListener(this);

        rgrupo.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.r20 || checkedId == R.id.r15) {
                edt_otros.setText("");
                edt_otros.setEnabled(false);
            }
            if (checkedId == R.id.rotros) {
                edt_otros.setText("");
                edt_otros.setEnabled(true);
                edt_otros.requestFocus();
            }
        });
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        if (viewId == R.id.btn_calcular) {
            int radioCheckedId = rgrupo.getCheckedRadioButtonId();
            if (radioCheckedId == R.id.r15) {
                txtPropina.setText(String.valueOf(Double.parseDouble(edt_total.getText().toString()) * 0.15));
            }
            if (radioCheckedId == R.id.r20) {
                txtPropina.setText(String.valueOf(Double.parseDouble(edt_total.getText().toString()) * 0.20));
            }
            if (radioCheckedId == R.id.rotros) {
                double customPercentage = Double.parseDouble(edt_otros.getText().toString()) / 100.0;
                txtPropina.setText(String.valueOf(Double.parseDouble(edt_total.getText().toString()) * customPercentage));
            }
        } else if (viewId == R.id.btn_salir) {
            finish();
        }
    }
}

