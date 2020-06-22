package com.example.buildmart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class PostRequirementScreen extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText postText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_requirement_screen);

        toolbar = findViewById(R.id.postRequirementToolbar);
        postText = findViewById(R.id.postText);

        toolbar.setTitle("Post your requirement");
        setSupportActionBar(toolbar);
    }

    public void cancelPost(View view) {
        finish();
    }

    public void confirmPost(View view) {
        new FireStoreHandler(this).postRequirement(postText.getText().toString(), this);
    }
}
