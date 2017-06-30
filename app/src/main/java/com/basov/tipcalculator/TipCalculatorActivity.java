package com.basov.tipcalculator;

import java.text.NumberFormat;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.Toast;

public class TipCalculatorActivity extends Activity 
implements OnEditorActionListener, OnClickListener, RadioGroup.OnCheckedChangeListener, AdapterView.OnItemSelectedListener, SeekBar.OnSeekBarChangeListener {

    // define variables for the widgets
    private EditText billAmountEditText;
    private TextView percentTextView;   
    private Button   percentUpButton;
    private Button   percentDownButton;
    private TextView tipTextView;
    private TextView totalTextView;
    private RadioGroup roundRadioGroup;
    private Spinner splitBillSpinner;
    private TextView splitAmountTextView;
    private SeekBar persentSeekBar;


    // define the SharedPreferences object
    private SharedPreferences savedValues;
    
    // define instance variables that should be saved
    private String billAmountString = "";
    private float tipPercent = .15f;
    private int checkedRoundRadioButtonId = 0;
    private int splitBillIndex = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip_calculator);
        
        // get references to the widgets
        billAmountEditText = (EditText) findViewById(R.id.billAmountEditText);
        percentTextView = (TextView) findViewById(R.id.percentTextView);
        percentUpButton = (Button) findViewById(R.id.percentUpButton);
        percentDownButton = (Button) findViewById(R.id.percentDownButton);
        tipTextView = (TextView) findViewById(R.id.tipTextView);
        totalTextView = (TextView) findViewById(R.id.totalTextView);
        roundRadioGroup = (RadioGroup) findViewById(R.id.roundRadioGroup);
        splitBillSpinner = (Spinner) findViewById(R.id.splitBillSpinner);
        splitAmountTextView = (TextView) findViewById(R.id.splitAmountTextView);
        persentSeekBar = (SeekBar) findViewById(R.id.persentSeekBar);


                // set the listeners
        billAmountEditText.setOnEditorActionListener(this);
        percentUpButton.setOnClickListener(this);
        percentDownButton.setOnClickListener(this);
        roundRadioGroup.setOnCheckedChangeListener(this);
        splitBillSpinner.setOnItemSelectedListener(this);
        persentSeekBar.setOnSeekBarChangeListener(this);


        // get SharedPreferences object
        savedValues = getSharedPreferences("SavedValues", MODE_PRIVATE);        
    }
    
    @Override
    public void onPause() {
        // save the instance variables       
        Editor editor = savedValues.edit();        
        editor.putString("billAmountString", billAmountString);
        editor.putFloat("tipPercent", tipPercent);
        editor.putFloat("splitBillIndex", splitBillIndex);

        editor.commit();        

        super.onPause();      
    }
    
    @Override
    public void onResume() {
        super.onResume();
        
        // get the instance variables
        billAmountString = savedValues.getString("billAmountString", "");
        tipPercent = savedValues.getFloat("tipPercent", 0.15f);

        // set the bill amount on its widget
        billAmountEditText.setText(billAmountString);

        // calculate and display
        calculateAndDisplay();
    }    
    
    public void calculateAndDisplay() {        

        // get the bill amount
        billAmountString = billAmountEditText.getText().toString();
        float billAmount;
        if (billAmountString.equals("")) {
            billAmount = 0;
        } else {
            billAmount = Float.parseFloat(billAmountString);
        }


        // calculate tip and total 
        float tipAmount = billAmount * tipPercent;
        float totalAmount = billAmount + tipAmount;


        switch(checkedRoundRadioButtonId){
            case 0:
                break;
            case 1:
                tipAmount = StrictMath.round(tipAmount);
                totalAmount = billAmount + tipAmount;
                break;
            case 2:
                totalAmount = StrictMath.round(totalAmount);
                tipAmount = totalAmount - billAmount;
                break;
        }

        // display the other results with formatting
        NumberFormat currency = NumberFormat.getCurrencyInstance();
        tipTextView.setText(currency.format(tipAmount));
        totalTextView.setText(currency.format(totalAmount));
        
        NumberFormat percent = NumberFormat.getPercentInstance();
        percentTextView.setText(percent.format(tipPercent));


        Float splitFloat = totalAmount / (splitBillIndex +1);
        splitAmountTextView.setText(currency.format(splitFloat));
    }
    
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE ||
    		actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
            calculateAndDisplay();
        }        
        return false;
    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.percentDownButton:
            resetRoundGroup();
            tipPercent = tipPercent - .01f;
            break;
        case R.id.percentUpButton:
            resetRoundGroup();
            tipPercent = tipPercent + .01f;
            break;
        }

        calculateAndDisplay();
    }

    public void resetRoundGroup()
    {
        RadioButton rb = (RadioButton) findViewById(R.id.noneRadioButton);
        rb.setChecked(true);
    }
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {

        checkedRoundRadioButtonId = 0;
        if(i == R.id.tipRadioButton){
            checkedRoundRadioButtonId = 1;
        }

        else if(i == R.id.totalRadioButton){
            checkedRoundRadioButtonId = 2;
        }
        calculateAndDisplay();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        splitBillIndex = i;
        calculateAndDisplay();
//        Toast.makeText(adapterView.getContext(),
//                "OnItemSelectedListener : " + adapterView.getItemAtPosition(i).toString(),
//                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        tipPercent = progress * 0.01f;
        calculateAndDisplay();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}