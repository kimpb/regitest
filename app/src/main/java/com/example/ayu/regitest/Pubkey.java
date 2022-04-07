package com.example.ayu.regitest;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class Pubkey extends StringRequest {

    // 서버 URL 설정 (PHP 파일 연동)
    final static private String URL = "http://13.209.98.41/pubkey.php"; //호스팅 주소 + php
    private Map<String, String> map;



    public Pubkey(String pubkey, Response.Listener<String> listener) { //문자형태로 보낸다는 뜻
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("pubkey", pubkey);

    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}