
package com.example.ayu.regitest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import javax.crypto.KeyGenerator;


public class Login extends AppCompatActivity {

    private EditText textID, textPW;
    private Button login_button, join_button;
    private String saltHash;
    private String hashPw;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        textID = findViewById(R.id.edittext3);
        textPW = findViewById(R.id.edittext4);


        join_button = findViewById( R.id.join_button );
        join_button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( Login.this, Register.class );//
                startActivity( intent );
            }
        });

        login_button = findViewById( R.id.login_button );
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String Lid = textID.getText().toString();
                String Lpw = textPW.getText().toString();
                saltHash = "KAUCapstonedesign"+ Lpw +"SmartLock";
                SHA516_Hash_InCode hash_inCode = new SHA516_Hash_InCode();
                hashPw = hash_inCode.SHA516_Hash_InCode(saltHash); //솔팅한 값 해시화한 값

                Response.Listener<String> responseListener = new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            if (success) {//성공시

                                String textID = jsonObject.getString( "id" );
                                String textPW = jsonObject.getString( "pw" );
                                String UserName = jsonObject.getString( "name" );

                                Toast.makeText( getApplicationContext(), String.format("%s님 환영합니다.", UserName), Toast.LENGTH_SHORT ).show();
                                Intent intent = new Intent( Login.this, MainActivity.class );

                                intent.putExtra( "UserEmail", textID );
                                intent.putExtra( "UserPwd", textPW );
                                intent.putExtra( "UserName", UserName );

                                startActivity( intent );
                            } else {//실패시

                                Toast.makeText(getApplicationContext(), "로그인 실패", Toast.LENGTH_SHORT).show();
                                return;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "예외 1", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                };

                LoginRequest loginRequest = new LoginRequest(Lid, hashPw, responseListener);
                RequestQueue queue = Volley.newRequestQueue(Login.this);
                queue.add(loginRequest);

            }
        });
    }
}