package com.example.ititop_n;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Scanner;


public class MainActivity extends AppCompatActivity {

    public Button erd;
    public Button nd;
    public Button menu;
    public WebView menuPage;
    public TextView open;
    public TextView more;

    public static final String MENU_CSV = "hours.csv";

    public static final int NAME = 0;
    public static final int DATE = 1;
    public static final int MEAL = 2;
    public static final int OPEN_HOUR = 3;
    public static final int CLOSE_HOUR = 4;
    private static Hashtable<String, String> halls = new Hashtable<String,String>();
    private static Hashtable<String, String> meals = new Hashtable<String,String>();
    private static Hashtable<String, Integer> daysInt= new Hashtable<String, Integer>();
    private static Hashtable<Integer, String> daysToString= new Hashtable<Integer, String>();

    private static Hashtable<String, DiningHall> diningHallInfo = new Hashtable <String, DiningHall>();

    private ArrayList<Hour> hours;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpDictionaries();

        hours = new ArrayList<>();

        try {
            AssetManager mngr = getAssets();
            InputStream is = mngr.open(MENU_CSV);
            Scanner scanner = new Scanner(is);
            while(scanner.hasNextLine()){
                String str = scanner.nextLine();
                String[] info = str.split(",");


                String hallName = info[NAME];
                if(!diningHallInfo.containsKey(hallName)){
                    diningHallInfo.put(hallName, new DiningHall(halls.get(hallName)));
                }
                DiningHall hall = diningHallInfo.get(hallName);
                hall.append(new Hour(info[DATE],meals.get(info[MEAL]),info[OPEN_HOUR],info[CLOSE_HOUR],daysInt,daysToString));

                /*Hours hour = new Hours(info[HALL],info[DATE],info[MEAL],
                        info[OPEN_HOUR],info[CLOSE_HOUR], halls, meals, daysInt, daysToString);
                hours.add(hour);*/
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

        erd = findViewById(R.id.erd);
        nd = findViewById(R.id.nd);
        menu = findViewById(R.id.menu);
        menuPage = findViewById(R.id.menuPage);
        open = findViewById(R.id.open);
        more = findViewById(R.id.more_info);
        erd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                open.setVisibility(View.INVISIBLE);
                more.setVisibility(View.INVISIBLE);
                menu.setVisibility(View.INVISIBLE);
                menuPage.setVisibility(View.INVISIBLE);

                DiningHall erd = diningHallInfo.get("erd");
                boolean isOpen = erd.isOpenNow();

                if(isOpen) {
                    open.setText("Erdman is open!");
                    more.setText(erd.toString());
                    menu.setVisibility(View.VISIBLE);
                    menuPage.loadUrl("https://www.brynmawr.edu/dining/menus-and-hours/erdman-dining-hall-menu");
                }
                else {
                    open.setText("Erdman is closed!");
                    more.setText(erd.toString());
                }

                open.setVisibility(View.VISIBLE);
                more.setVisibility(View.VISIBLE);
            }
        });
        nd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                open.setVisibility(View.INVISIBLE);
                more.setVisibility(View.INVISIBLE);
                menu.setVisibility(View.INVISIBLE);
                menuPage.setVisibility(View.INVISIBLE);

                DiningHall nd = diningHallInfo.get("nd");
                boolean isOpen = nd.isOpenNow();

                if(isOpen) {
                    open.setText("New Dorm is open!");
                    more.setText(nd.toString());
                    menu.setVisibility(View.VISIBLE);
                    menuPage.loadUrl("https://www.brynmawr.edu/dining/menus-and-hours/new-dorm-dining-hall-menu");
                }
                else {
                    open.setText("New Dorm is closed!");
                    more.setText(nd.toString());
                }

                open.setVisibility(View.VISIBLE);
                more.setVisibility(View.VISIBLE);
            }
        });
        menu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                menu.setVisibility(View.INVISIBLE);
                menuPage.setVisibility(View.VISIBLE);
            }
        });
    }

    public static void setUpDictionaries(){
        halls.put("erd","Erdman Dining Hall");
        halls.put("nd", "New Dorm Dining Hall");

        meals.put("b","breakfast");
        meals.put("l", "lunch");
        meals.put("d","dinner");
        meals.put("br","brunch/lunch");
        meals.put("ll","light lunch");

        daysInt.put("Monday",0);
        daysInt.put("Tuesday",1);
        daysInt.put("Wednesday",2);
        daysInt.put("Thursday",3);
        daysInt.put("Friday",4);
        daysInt.put("Saturday",5);
        daysInt.put("Sunday",6);


        daysToString.put(0,"Monday");
        daysToString.put(1,"Tuesday");
        daysToString.put(2,"Wednesday");
        daysToString.put(3,"Thursday");
        daysToString.put(4,"Friday");
        daysToString.put(5,"Saturday");
        daysToString.put(6,"Sunday");
    }
}

class DiningHall{
    private String name;
    private ArrayList<Hour> hours;
    private String mealString;

    public DiningHall(String name){
        this.name = name;
        hours = new ArrayList<Hour>();

    }

    public Boolean isOpenNow(){
        for(Hour meal:hours){
            if(meal.isOpenNow()){
                mealString = meal.toString();
                return true;
            }
        }
        return false;
    }

    public void append(Hour hour){
        hours.add(hour);

    }

    @Override
    public String toString(){
        System.out.println(hours);
        if(isOpenNow()){
            return name + mealString;
        }
        return name + " is not open now.";

    }

}


class Hour{
    private String range;
    private String meal;
    private LocalTime start;
    private LocalTime end;

    private Boolean[] days = new Boolean[7];
    private Hashtable<String, Integer> daysInt;
    private Hashtable<Integer, String> daysToString;

    public Hour(String range, String meal, String start, String end,
                Hashtable<String, Integer> daysInt, Hashtable<Integer, String> daysToString){
        //dictionaries

        this.daysInt = daysInt;
        this.daysToString = daysToString;

        //instance variable
        this.meal = meal;

        String[] startSplit = start.split(":");
        this.start = LocalTime.of(Integer.parseInt(startSplit[0]),Integer.parseInt(startSplit[1]));

        String[] endSplit = end.split(":");
        this.end = LocalTime.of(Integer.parseInt(endSplit[0]),Integer.parseInt(endSplit[1]));

        days[daysInt.get("Monday")] = range.indexOf("m") != -1;
        days[daysInt.get("Tuesday")] = range.indexOf("t") != -1;
        days[daysInt.get("Wednesday")] = range.indexOf("w") != -1;
        days[daysInt.get("Thursday")] = range.indexOf("r") != -1;
        days[daysInt.get("Friday")] = range.indexOf("f") != -1;
        days[daysInt.get("Saturday")] = range.indexOf("s") != -1;
        days[daysInt.get("Sunday")] = range.indexOf("u") != -1;
    }


    public String getMeal(){
        return meal;
    }

    public Boolean isOpenDay(String dayOfWeek){
        return days[daysInt.get(dayOfWeek)];
    }

    public Boolean isAfterStartTime(){
        return start.isBefore(LocalTime.now());
    }

    public Boolean isBeforeEndTime(){
        return end.isAfter(LocalTime.now());
    }

    public Boolean isOpenNow(){
        return isBeforeEndTime() && isAfterStartTime()
                && isOpenDay(LocalDate.now().getDayOfWeek().getDisplayName(TextStyle.FULL,new Locale("en")));
    }

    public String getDays(){
        String str = "";
        for(int i = 0; i<days.length;i++){
            if(days[i]){
                str += daysToString.get(i)+", ";
            }
        }
        return str.substring(0, str.lastIndexOf(", "));
    }

    @Override
    public String toString(){
        return " serves "+ meal + " from "+ start.toString()+ " until " + end.toString()+ " on "+ getDays()+ ".";
    }
}

