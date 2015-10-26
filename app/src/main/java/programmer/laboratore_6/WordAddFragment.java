package programmer.laboratore_6;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeoutException;

import programmer.laboratore_6.Database.MyDbHandler;
import programmer.laboratore_6.Model.Word;


public class WordAddFragment extends Fragment {
    private static View rootView;
    TextView wordTextView;
    TextView descriptionTextView;
    Button saveButton;
    MyDbHandler myDbHandler;
    AlertDialogManager alert;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_word_add, container, false);
        init();
        return rootView;
    }

    public void init(){
        alert = new AlertDialogManager();
        myDbHandler = new MyDbHandler(getActivity());
        wordTextView = (TextView)rootView.findViewById(R.id.wordText);
        descriptionTextView = (TextView)rootView.findViewById(R.id.descriptionText);
        saveButton = (Button)rootView.findViewById(R.id.save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String word = wordTextView.getText().toString();
                String description = descriptionTextView.getText().toString();
                Cursor words = myDbHandler.checkUser(word, description);

                if (words == null) {
                    alert.showAlertDialog(getActivity(), "Error", "Database query error", false);
                } else {
                    getActivity().startManagingCursor(words);

                    if (words.getCount() > 0) {
                        alert.showAlertDialog(getActivity(), "Алдаа", "Бүртгэлтэй үг байна", false);
                        getActivity().stopManagingCursor(words);
                        words.close();
                    } else {
                        myDbHandler.addWord(new Word(word, description));
                        Toast.makeText(getActivity(), "Амжилттай нэмлээ", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout)getActivity().findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.getActivity().onBackPressed();
        }
    }

}
