package com.example.projecr7;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.projecr7.database.Couple;
import com.example.projecr7.database.DatabaseClient;
import com.example.projecr7.database.Dinner;
import com.example.projecr7.database.Family;
import com.example.projecr7.database.Table;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class AddDinnerActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = "AddDinnerActivity";

    private List<Dinner> allDinner;
    private RecyclerView recyclerView;
    private LinearLayout linearLayout;
    private RecyclerView.Adapter mAdapter;

    private DatePicker datePicker;
    private Calendar calendar;
    private TextView dateView;
    private DialogFragment newFragment;
    private int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dinner);

        dateView = findViewById(R.id.dinner_date_display_Add);

        linearLayout = findViewById(R.id.dinner_date_linearlayout);
        newFragment = new DatePickerFragment();
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newFragment.show(getFragmentManager(), "datePicker");
            }
        });

        Button addDinnerWithNamebtn = (Button) findViewById(R.id.button_add_dinner_with_name);
        addDinnerWithNamebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText nameTextE = (EditText) findViewById(R.id.dinner_text_input);
                String givenName = nameTextE.getText().toString();
                if(givenName.matches("") || year == 0)
                    return;
                Dinner newDinner = new Dinner(givenName);
                newDinner.setDinnerDate(year, month, day);
                DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().dinnerDao().insert(newDinner);
                Intent intent = new Intent(AddDinnerActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        dateView.setText("" + day + "/" + (month+1) + "/" + year);
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
            return new DatePickerDialog(getActivity(), (AddDinnerActivity)getActivity(), year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
        }
    }

}
