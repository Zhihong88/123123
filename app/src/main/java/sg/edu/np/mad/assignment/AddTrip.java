package sg.edu.np.mad.assignment;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

public class AddTrip extends AppCompatActivity {
    private ImageView back;
    Button save;
    final Calendar startCalendar= Calendar.getInstance();
    final Calendar endCalendar= Calendar.getInstance();
    final Calendar customCalendarStart= Calendar.getInstance();
    final Calendar customCalendarEnd= Calendar.getInstance();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    EditText name, sd, ed, dest;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trip);
        name = findViewById(R.id.nameEdit);
        sd = findViewById(R.id.startEdit);
        ed = findViewById(R.id.endEdit);
        dest = findViewById(R.id.destEdit);

        TextView header = findViewById(R.id.headerTxt);
        save = findViewById(R.id.saveBtn);

        Trip trip_edit = (Trip)getIntent().getSerializableExtra("EDIT");

        // Edit existing trip
        if(trip_edit != null)
        {
            save.setText("UPDATE");
            header.setText("Update Trip");
            name.setText(trip_edit.getTripName());
            dest.setText(trip_edit.getDestination());
            sd.setText(trip_edit.getStartDate());
            ed.setText(trip_edit.getEndDate());
        }
        else
        {
            save.setText("SAVE");
            header.setText("Add Trip");
        }

        // Send user back to home screen
        back = findViewById(R.id.backBtn);

        back.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = name.getText().toString();
                String destination = dest.getText().toString();
                String sDate = sd.getText().toString();
                String eDate = ed.getText().toString();

                // If user has input something
                if (title.isEmpty() == false || destination.isEmpty() == false || sDate.isEmpty() == false || eDate.isEmpty() == false){
                    new AlertDialog.Builder(AddTrip.this)
                            .setTitle("Discard changes?")
                            .setMessage("Changes made will not be saved.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }

                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                }
                else {
                    Intent Intent = new Intent(AddTrip.this, HomeActivity.class);
                    startActivity(Intent);
                }
            }
        }));
        
        // Country dropdown/searchable spinner
        dest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCountryList();
            }
        });


        // Save user input
        save.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = name.getText().toString();
                String destination = dest.getText().toString();
                String sDate = sd.getText().toString();
                String eDate = ed.getText().toString();

                db.collection("Trip")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (DocumentSnapshot doc : task.getResult()) {
                                        if (trip_edit == null)
                                        {
                                            if (title.isEmpty()){
                                                showError(name, "Missing information");
                                            }
                                            else if (uid.equals(doc.getString("userId")) && title.equals(doc.getString("tripName"))) {
                                                showError(name, "Name already in use");
                                                return;
                                            }
                                            else if (destination.isEmpty()) {
                                                showError(dest, "Missing information");
                                            }
                                            else if (sDate.isEmpty()){
                                                Toast.makeText(AddTrip.this, "Missing Date", Toast.LENGTH_LONG).show();
                                            }
                                            else if (eDate.isEmpty()){
                                                Toast.makeText(AddTrip.this, "Missing Date", Toast.LENGTH_LONG).show();
                                            }
                                            else if (startCalendar.after(endCalendar) || customCalendarStart.after(customCalendarEnd)) {

                                                Toast.makeText(AddTrip.this, "End Date cannot be before Start Date", Toast.LENGTH_LONG).show();
                                            }
                                            else {
                                                DALTrip dalTrip = new DALTrip();
                                                Trip trip = new Trip(dest.getText().toString(), sd.getText().toString(), ed.getText().toString(), name.getText().toString(), name.getText().toString(), uid);
                                                dalTrip.createTrip(trip);
                                                finish();

                                                Intent Intent = new Intent(AddTrip.this, HomeActivity.class);
                                                startActivity(Intent);
                                            }
                                        }
                                        else
                                        {
                                            if (title.isEmpty()){
                                                showError(name, "Missing information");
                                            }
                                            else if (uid.equals(doc.getString("userId")) && title.equals(doc.getString("tripName"))) {
                                                showError(name, "Name already in use");
                                                return;
                                            }
                                            else if (destination.isEmpty()) {
                                                showError(dest, "Missing information");
                                            }
                                            else if (sDate.isEmpty()){
                                                Toast.makeText(AddTrip.this, "Missing Date", Toast.LENGTH_LONG).show();
                                            }
                                            else if (eDate.isEmpty()){
                                                Toast.makeText(AddTrip.this, "Missing Date", Toast.LENGTH_LONG).show();
                                            }
                                            else if (startCalendar.after(endCalendar) || customCalendarStart.after(customCalendarEnd)) {
                                                Toast.makeText(AddTrip.this, "End Date cannot be before Start Date", Toast.LENGTH_LONG).show();
                                            }
                                            else{
                                                db.collection("Trip").document(trip_edit.getId()).update(
                                                        "tripName",title, "destination", destination,
                                                        "startDate", sDate, "endDate", eDate);

                                                Intent Intent = new Intent(AddTrip.this, HomeActivity.class);
                                                startActivity(Intent);
                                            }

                                        }
                                    }
                                }
                                else {
                                    Log.d("checkTripListEmpty()", "Error getting documents: ", task.getException());
                                }
                            }
                        });

            }
        }));

        // Initiate date picker
        DatePickerDialog.OnDateSetListener date = (view, year, month, day) -> {
            startCalendar.set(Calendar.YEAR, year);
            startCalendar.set(Calendar.MONTH,month);
            startCalendar.set(Calendar.DAY_OF_MONTH,day);
            SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MMM/yyyy");
            sd.setText(dateFormat.format(startCalendar.getTime()));
        };

        DatePickerDialog.OnDateSetListener enddate = (view, year, month, day) -> {
            endCalendar.set(Calendar.YEAR, year);
            endCalendar.set(Calendar.MONTH,month);
            endCalendar.set(Calendar.DAY_OF_MONTH,day);
            SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MMM/yyyy");
            ed.setText(dateFormat.format(endCalendar.getTime()));
        };

//         Start date
        sd.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if (trip_edit == null){
                    new DatePickerDialog(AddTrip.this, AlertDialog.THEME_HOLO_LIGHT, date,startCalendar.get(Calendar.YEAR),startCalendar.get(Calendar.MONTH),startCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
                else {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy", Locale.ENGLISH);
                    LocalDate date = LocalDate.parse(sd.getText().toString(), formatter);
                    DatePickerDialog.OnDateSetListener thisdatelistener = (v, year, month, day) -> {
                        customCalendarStart.set(Calendar.YEAR, year);
                        customCalendarStart.set(Calendar.MONTH,month);
                        customCalendarStart.set(Calendar.DAY_OF_MONTH,day);
                        SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MMM/yyyy");
                        sd.setText(dateFormat.format(customCalendarStart.getTime()));
                    };
                    new DatePickerDialog(AddTrip.this, AlertDialog.THEME_HOLO_LIGHT, thisdatelistener,date.getYear(),date.getMonthValue()-1,date.getDayOfMonth()).show();                }
            }
        });

//         End date
        ed.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if (trip_edit == null) {
                    new DatePickerDialog(AddTrip.this, AlertDialog.THEME_HOLO_LIGHT, enddate, endCalendar.get(Calendar.YEAR), endCalendar.get(Calendar.MONTH), endCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
                else {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy", Locale.ENGLISH);
                    LocalDate date = LocalDate.parse(ed.getText().toString(), formatter);
                    DatePickerDialog.OnDateSetListener thisDate = (thisview, year, month, day) -> {
                        customCalendarEnd.set(Calendar.YEAR, year);
                        customCalendarEnd.set(Calendar.MONTH,month);
                        customCalendarEnd.set(Calendar.DAY_OF_MONTH,day);
                        SimpleDateFormat dateFormat=new SimpleDateFormat("dd/MMM/yyyy");
                        ed.setText(dateFormat.format(customCalendarEnd.getTime()));
                    };
                    new DatePickerDialog(AddTrip.this, AlertDialog.THEME_HOLO_LIGHT, thisDate,date.getYear(),date.getMonthValue()-1,date.getDayOfMonth()).show();
                }
            }
        });

    }

    // Input validation
    private void showError(EditText input, String missing_information) {
        input.setError(missing_information);
        input.requestFocus();
    }

    // Alert dialog when user press on their phone's back button
    int counter = 0;
    public void onBackPressed() {
        counter++;

        name = findViewById(R.id.nameEdit);
        sd = findViewById(R.id.startEdit);
        ed = findViewById(R.id.endEdit);
        dest = findViewById(R.id.destEdit);

        String title = name.getText().toString();
        String destination = dest.getText().toString();
        String sDate = sd.getText().toString();
        String eDate = ed.getText().toString();

        // Show dialog if back button is pressed
        if (counter >= 1) {
            // If user has input something
            if (title.isEmpty() == false || destination.isEmpty() == false || sDate.isEmpty() == false || eDate.isEmpty() == false){
                new AlertDialog.Builder(this)
                        .setTitle("Discard changes?")
                        .setMessage("Changes made will not be saved.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }

                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
            else {
                Intent Intent = new Intent(AddTrip.this, HomeActivity.class);
                startActivity(Intent);
            }

        }

    }

    // get list of countries using Locale
    public void getCountryList()
    {
        // Initialise dialog
        Dialog dialog = new Dialog(AddTrip.this);

        // Set customer dialog
        dialog.setContentView(R.layout.dialog_searchable_spinner);

        // Set custom height and width
        dialog.getWindow().setLayout(1000,1200);

        dialog.show();

        // Initialise and assign variable
        EditText editText = dialog.findViewById(R.id.edit_text);
        ListView lv = dialog.findViewById(R.id.listView);

        Locale[] locale = Locale.getAvailableLocales();
        ArrayList<String> countries = new ArrayList<>();
        String country;

        for (Locale loc : locale)
        {
            country = loc.getDisplayCountry();

            if (country.length() > 0 && !countries.contains(country))
            {
                countries.add(country);
            }
        }

        Collections.sort(countries, String.CASE_INSENSITIVE_ORDER);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(AddTrip.this,
                android.R.layout.simple_list_item_1,countries);

        lv.setAdapter(adapter);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // Set selected item on textview
                dest.setText(adapter.getItem(i));

                // Dismiss dialog
                dialog.dismiss();
            }
        });
    }

}