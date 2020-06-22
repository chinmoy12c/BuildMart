package com.example.buildmart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class PostUnknownService extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText postText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_unknown_service);

        toolbar = findViewById(R.id.unknownServiceToolbar);
        postText = findViewById(R.id.postTextUnknown);

        toolbar.setTitle("Post your requirement");
        setSupportActionBar(toolbar);
    }

    public void cancelPost(View view) {
        finish();
    }

    public void confirmPost(View view) {
        new FireStoreHandler(this).postUnknownRequirement(postText.getText().toString(), this);
    }
}
