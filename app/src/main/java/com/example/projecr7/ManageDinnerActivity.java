package com.example.projecr7;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.projecr7.database.DatabaseClient;
import com.example.projecr7.database.Dinner;
import com.example.projecr7.database.Person;

import java.util.Calendar;
import java.util.List;

public class ManageDinnerActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private Spinner mSpinner;
    private Dinner[] arraySpinner;
    private Dinner currentDinner;
    private TextView dateTextview;
    private EditText dinnerNameEdittext;
    private DialogFragment newFragment;
    private int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_dinner);

        mSpinner = findViewById(R.id.deleteDinnerSpinner);
        dateTextview = findViewById(R.id.dinner_date_display_manage);
        dinnerNameEdittext = findViewById(R.id.dinner_text_input_manage);

        List<Dinner> allDinner = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().dinnerDao().getAll();

        arraySpinner = new Dinner[allDinner.size() - 1];
        int j = 0;
        for(int i = 0; i < allDinner.size(); i++){
            if(allDinner.get(i).getUid() != 4) {
                arraySpinner[j] = allDinner.get(i);
                j++;
            }
        }
        ArrayAdapter<Dinner> adapter = new ArrayAdapter<Dinner>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentDinner = arraySpinner[position];
                dinnerNameEdittext.setText(currentDinner.getDinnerName());
                dateTextview.setText(currentDinner.getDinnerDate() + "/" + currentDinner.getDinnerMonth() + "/" + currentDinner.getDinnerYear());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        Button mButton = findViewById(R.id.button_delete_dinner_with_name);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDinner();
            }
        });
        Button mButtonSave = findViewById(R.id.button_save_dinner);
        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String givenName = dinnerNameEdittext.getText().toString();
                if(givenName.matches("") || year == 0)
                    return;
                Dinner newDinner = new Dinner(givenName);
                newDinner.setDinnerDate(year, month, day);
                newDinner.setUid(currentDinner.getUid());
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().dinnerDao().updateUsers(newDinner);
                NavUtils.navigateUpFromSameTask(ManageDinnerActivity.this);
            }
        });

        newFragment = new ManageDinnerActivity.DatePickerFragment();
        LinearLayout linearLayout = findViewById(R.id.dinner_date_linearlayout_manage);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newFragment.show(getFragmentManager(), "datePicker");
            }
        });

    }

    public void deleteDinner(){
        Dinner selectDinner = (Dinner) mSpinner.getSelectedItem();
        deletePeople(selectDinner.getUid());
        DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().dinnerDao().delete(selectDinner);
        NavUtils.navigateUpFromSameTask(ManageDinnerActivity.this);
    }

    private void deletePeople(int dinnerId){
        List<Person> personToDelete = DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().personDao().loadAllByDinner(dinnerId);
        for(int i = 0; i < personToDelete.size(); i++){
            DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().personDao().delete(personToDelete.get(i));
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        dateTextview.setText("" + day + "/" + (month+1) + "/" + year);
        this.year = year;
        this.month = month+1;
        this.day = day;
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        public int year, month, day;
        private TextView dateView;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), (ManageDinnerActivity)getActivity(), year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
        }
    }
}
