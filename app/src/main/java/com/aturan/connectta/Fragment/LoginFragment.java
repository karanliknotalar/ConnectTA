package com.aturan.connectta.Fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aturan.connectta.ConnectTA;
import com.aturan.connectta.R;
import com.aturan.connectta.SaveLoginInfo;

import java.io.IOException;

public class LoginFragment extends Fragment {


    private EditText adText, sifreText;
    private Button girisYapBtn;
    private TextView sonucText, topBarText;
    private CheckBox saveInfo;

    private ConnectTA connectTA;
    private String strAd, strSifre, strUrl;
    private Handler handler;

    public static boolean LOGIN_IS_OK;
    public static String GET_COOKIE;

    private SaveLoginInfo saveLoginInfo;

    private View v;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (v == null)
            v = inflater.inflate(R.layout.fragment_login, container, false);
        return v;
        // return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adText = view.findViewById(R.id.login_fragment_usernameText);
        sifreText = view.findViewById(R.id.login_fragment_passwordText);
        sonucText = view.findViewById(R.id.login_fragment_resultText);
        girisYapBtn = view.findViewById(R.id.login_fragment_btnGiris);
        saveInfo = view.findViewById(R.id.login_fragment_checkSave);
        topBarText = view.findViewById(R.id.fragment_login_topbar_text);

        handler = new Handler(Looper.getMainLooper());

        saveLoginInfo = new SaveLoginInfo(getActivity(), "turkcealtyazi");

        adText.setText(saveLoginInfo.readString("username", ""));
        sifreText.setText(saveLoginInfo.readString("password", ""));
        saveInfo.setChecked(saveLoginInfo.readBoolean("saveInfo", false));

        saveInfo.setOnCheckedChangeListener((buttonView, isChecked) -> saveLoginInfo(isChecked));

        girisYapBtn.setOnClickListener(v -> {
            strAd = adText.getText().toString();
            strSifre = sifreText.getText().toString();
            strUrl = "https://turkcealtyazi.org";

            if (TextUtils.isEmpty(strAd) && TextUtils.isEmpty(strSifre)) {
                MesajGoster("Kullanıc adı, şifre ve Site url boş olamaz");
                return;
            }

            saveLoginInfo(saveInfo.isChecked());

            new Thread(() -> {

                connectTA = new ConnectTA(strAd, strSifre, strUrl, view.getContext());

                try {
                    LOGIN_IS_OK = connectTA.loginTA();
                    GET_COOKIE = connectTA.getCookie();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                handler.post(() -> {
                    if (LOGIN_IS_OK && GET_COOKIE != null) {
                        MesajGoster("Giriş başarılı");
                        sonucText.setVisibility(View.VISIBLE);
                        sonucText.setText("BAŞARILI");
                        girisYapBtn.setEnabled(false);
                        adText.setEnabled(false);
                        sifreText.setEnabled(false);
                        adText.setBackground(getResources().getDrawable(R.drawable.login_ok_bg, null));
                        sifreText.setBackground(getResources().getDrawable(R.drawable.login_ok_bg, null));
                        sonucText.setBackground(getResources().getDrawable(R.drawable.login_ok_bg, null));
                        topBarText.setText("KULLANICI : " + strAd);
                        sonucText.setTextColor(Color.WHITE);

                    } else {
                        sonucText.setVisibility(View.VISIBLE);
                        sonucText.setText("HATA");
                        MesajGoster("Giriş başarısız");
                    }

                });
            }).start();

        });
    }

    private void MesajGoster(String mesaj) {
        Toast.makeText(getActivity(), mesaj, Toast.LENGTH_SHORT).show();
    }

    private void saveLoginInfo(Boolean b) {
        if (b) {
            saveLoginInfo.writeString("username", adText.getText().toString());
            saveLoginInfo.writeString("password", sifreText.getText().toString());
            saveLoginInfo.writeBoolean("saveInfo", true);
        } else {
            saveLoginInfo.Clear();
        }
    }
}