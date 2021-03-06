package com.comp354.teami.icycle;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.moomeen.endo2java.EndomondoSession;
import com.moomeen.endo2java.error.LoginException;
import com.moomeen.endo2java.error.InvocationException;
import com.moomeen.endo2java.model.Workout;


import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.text.method.ScrollingMovementMethod;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View.OnClickListener;

import android.os.AsyncTask;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class iCycle extends AppCompatActivity {

    TextView textView;
    EditText username, password;
    Button button;
    String str0, str1;
    EndomondoSession session;
    public static  List<Workout> workouts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_i_cycle);

        textView = findViewById(R.id.textView);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        button = findViewById(R.id.button);
        textView.setMovementMethod(new ScrollingMovementMethod());

        button.setOnClickListener(
                new View.OnClickListener(){
                    public void onClick(View view)
                    {
                        str0 = username.getText().toString();
                        str1 = password.getText().toString();


                        session = new EndomondoSession(str0, str1);

                        try {
                            int loginResult = new LoginTask().execute().get();
                            if (loginResult == 0) {
                                workouts = new GetWorkoutsTask().execute().get();
                                List<Workout> shallowCopy = workouts.subList(0, workouts.size());
                                Collections.reverse(shallowCopy);
                                workouts = shallowCopy;
                                goToMainMenu(view);
                            } else {
                                textView.setText("unable to login");
                            }
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                            textView.setText("unhandled exception");
                        }



                    }
                });
    }


    public void goToMainMenu(View view) {
        Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);
    }

    private class LoginTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... params) {
            try {
                session.login();
            } catch (LoginException e) {
                e.printStackTrace();
                return -1;
            }
            return 0;
        }
    }

    private class GetWorkoutsTask extends AsyncTask<Void, Void, List<Workout>> {
        @Override
        protected List<Workout> doInBackground(Void... params) {
            try {
                return session.getWorkouts();
            } catch (InvocationException e) {
                e.printStackTrace();
                return null; //return "unable to get workouts";
            }
        }
    }
}
