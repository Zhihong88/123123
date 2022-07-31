package sg.edu.np.mad.assignment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TripsFragment extends Fragment {
    private View mview;
    RecyclerView ongoingRV, upcomingRV;
    List<Trip> dataHolder = new ArrayList<>();
    List<Trip> dataHolder2 = new ArrayList<>();
    List<Trip> dataHolder3 = new ArrayList<>();

    TextView pastTxt, upcomingTxt;
    ImageView addBtn;

    private Calendar calendar;
    private SimpleDateFormat dateFormat;
    private String todaydate;

    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    // Firestore instance
    FirebaseFirestore db;

    public TripsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mview = inflater.inflate(R.layout.fragment_trips, container, false);

        pastTxt = mview.findViewById(R.id.pastTxt);
        upcomingTxt = mview.findViewById(R.id.upcomingTxt);
        addBtn = mview.findViewById(R.id.addBtn);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mview.getContext(), AddTrip.class);
                startActivity(i);
            }
        });

        // init firestore
        db = FirebaseFirestore.getInstance();

        showData();

        return mview;
    }

    private void showData()
    {
        // get current date
        calendar = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("dd/MMM/yyyy");
        todaydate = dateFormat.format(calendar.getTime());

        // get & write firestore data
        db.collection("Trip")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        TripAdapter adapter = new TripAdapter(getContext(), dataHolder);
                        TripAdapter adapter2 = new TripAdapter(getContext(), dataHolder2);
                        TripAdapter adapter3 = new TripAdapter(getContext(), dataHolder3);

                        // Call when data is retrieved
                        boolean hasTrips = false;
                        for (DocumentSnapshot doc: task.getResult()) {
                            // If the trip is created by the user
                            if (uid.equals(doc.getString("userId"))) {
                                hasTrips = true;
                                Trip trip = new Trip(
                                        doc.getString("destination"),
                                        doc.getString("startDate"),
                                        doc.getString("endDate"),
                                        doc.getString("tripName"),
                                        doc.getString("id"),
                                        doc.getString("userId"));

                                try {
                                    Date today = dateFormat.parse(todaydate);
                                    Date startdate = dateFormat.parse(doc.getString("startDate"));
                                    Date enddate = dateFormat.parse(doc.getString("endDate"));

                                    // Display ongoing trips - today's date AFTER start date & BEFORE end date
                                    if (today.after(startdate) && today.before(enddate) || today.equals(startdate)) {
                                        dataHolder.add(trip);
                                        ongoingRV = mview.findViewById(R.id.ongoingRV);

                                        LinearLayoutManager firstManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                                        ongoingRV.setLayoutManager(firstManager);
                                        ongoingRV.setAdapter(adapter);

                                        addBtn.setVisibility(View.GONE);
                                    }
                                    //Display upcoming trips - today's date BEFORE start date & BEFORE end date
                                    else if (today.before(startdate) && today.before(enddate)) {
                                        dataHolder2.add(trip);
                                        upcomingRV = mview.findViewById(R.id.upcomingRV);

                                        LinearLayoutManager secondManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                                        upcomingRV.setLayoutManager(secondManager);
                                        upcomingRV.setAdapter(adapter2);
                                    }
                                    // Display past trips - after end date
                                    else if (today.after(enddate)) {
                                        dataHolder3.add(trip);
                                        upcomingRV = mview.findViewById(R.id.upcomingRV);

                                    }

                                    pastTxt.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            pastTxt.setTextColor(Color.parseColor("#112D4E"));
                                            upcomingTxt.setTextColor(Color.parseColor("#808080"));

                                            if (dataHolder3.isEmpty() == false) {
                                                LinearLayoutManager secondManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                                                upcomingRV.setLayoutManager(secondManager);
                                                upcomingRV.setAdapter(adapter3);
                                                upcomingRV.setVisibility(View.VISIBLE);
                                            }
                                            else if (dataHolder2.isEmpty() == false)
                                            {
                                                upcomingRV.setVisibility(View.GONE);
                                            }
                                        }
                                    });

                                    upcomingTxt.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            pastTxt.setTextColor(Color.parseColor("#808080"));
                                            upcomingTxt.setTextColor(Color.parseColor("#112D4E"));
                                            Log.v("hello", String.valueOf(dataHolder2.isEmpty()));

                                            if (dataHolder2.isEmpty() == false) {
                                                LinearLayoutManager secondManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                                                upcomingRV.setLayoutManager(secondManager);
                                                upcomingRV.setAdapter(adapter2);
                                                upcomingRV.setVisibility(View.VISIBLE);
                                            }
                                            else if (dataHolder3.isEmpty() == false)
                                            {
                                                upcomingRV.setVisibility(View.GONE);
                                            }

                                        }
                                    });

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
//                        }
                    }
                });
    }
}