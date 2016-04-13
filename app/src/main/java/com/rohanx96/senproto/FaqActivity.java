package com.rohanx96.senproto;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FaqActivity extends AppCompatActivity {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);


    }

    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("What is Arbuda Transport Agency?\n");
        listDataHeader.add("What services do we provide?\n");
        listDataHeader.add("What all goods do we transport?\n");
        listDataHeader.add("What is the maximum weight of th goods that can be transported in one order?\n");
        listDataHeader.add("When will my order be dispatched after requesting it?\n");
        listDataHeader.add("How do i use the services of Arbuda Transport Agency?\n");
        listDataHeader.add("How is the safety of the goods assured by the Arbuda Transport Agency?\n");
        listDataHeader.add("What if the goods being transported get damaged during the journey?\n\n");
        listDataHeader.add("How is the price decided for a particular trip?\n");
        listDataHeader.add("When is the payment made ?\n");
        listDataHeader.add("How is the payment made?\n");
        listDataHeader.add("Can an order be placed on the website as well?\n");
        listDataHeader.add("Can a confirmed order be cancelled?\n");

        // Adding child data
        List<String> q1 = new ArrayList<String>();
        q1.add(" Arbuda transport Agency is a logistics company and provides services for the transportation of good via trucks.\n");


        List<String> q2 = new ArrayList<String>();
        q2.add("We transport various types of goods within a circle of radius 500km.");

        List<String> q3 = new ArrayList<String>();
        q3.add("Tiles,grains,cement,well-packed fluids,cotton etc.");

        List<String> q4 = new ArrayList<String>();
        q4.add("37.2 tonnes.");

        List<String> q5 = new ArrayList<String>();
        q5.add("Once physical verification is done, the order will be dispatched the same day.\n");

        List<String> q6 = new ArrayList<String>();
        q6.add("Services of Arbuda Transport Agency can be used by placing your order on the Arbuda Transport Agency app available on Google Play Store. You can also download the app by going to Arbuda Transport Agency’s website and clicking on the download link.\n");

        List<String> q7 = new ArrayList<String>();
        q7.add("To assure our users that their goods are safe and are taken care of, we provide our users the facility to track the exact location of the truck carrying their goods. Also there will always be a guard travelling with the truck driver in every journey.\n");

        List<String> q8 = new ArrayList<String>();
        q8.add(" We are very cautious about the goods but still if the goods are damaged during transportation, appropriate refund will be provided to the user.");

        List<String> q9 = new ArrayList<String>();
        q9.add(" The price will depend on the weight of the goods being transported, rental charges for the truck required for the trip and distance to be covered in the trip.\n");

        List<String> q10 = new ArrayList<String>();
        q10.add("The payment is made in two parts. A portion of it is to be paid before the start of the trip and the remain after the goods are delivered.");

        List<String> q11 = new ArrayList<String>();
        q11.add("The payment is done either through cash or Cheque. If the amount to be paid is under Rs.50,000/- it is done in cash otherwise Cheques are used as the mode of payment.\n");

        List<String> q12 = new ArrayList<String>();
        q12.add("No, to place a order it is necessary to use the app.");

        List<String> q13 = new ArrayList<String>();
        q13.add("Yes, it can be cancelled if the cancel request is made before the goods are dispatched from the warehouse.\n");

        listDataChild.put(listDataHeader.get(0), q1); // Header, Child data
        listDataChild.put(listDataHeader.get(1), q2);
        listDataChild.put(listDataHeader.get(2), q3);
        listDataChild.put(listDataHeader.get(3), q4);
        listDataChild.put(listDataHeader.get(4), q5);
        listDataChild.put(listDataHeader.get(5), q6);
        listDataChild.put(listDataHeader.get(6), q7);
        listDataChild.put(listDataHeader.get(7), q8);
        listDataChild.put(listDataHeader.get(8), q9);
        listDataChild.put(listDataHeader.get(9), q10);
        listDataChild.put(listDataHeader.get(10), q11);
        listDataChild.put(listDataHeader.get(11), q12);
        listDataChild.put(listDataHeader.get(12), q13);
    }


}
