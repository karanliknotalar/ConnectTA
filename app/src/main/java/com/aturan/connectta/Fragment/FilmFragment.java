package com.aturan.connectta.Fragment;

import static android.os.Looper.getMainLooper;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aturan.connectta.Adapter.FilmDiziListAdapter;
import com.aturan.connectta.ConnectTA;
import com.aturan.connectta.FilmData;
import com.aturan.connectta.R;
import com.aturan.connectta.SaveList;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


public class FilmFragment extends Fragment {


    private RecyclerView recyclerView;
    private FilmDiziListAdapter adapter;
    private ConnectTA connectTA;
    private Handler handler;

    private boolean listLoadOk = false;
    private boolean isWorking = false;

    private TextView topBarText;
    private EditText searcBox;
    private ProgressDialog pDialog;

    private SaveList saveList;
    private ActivityResultLauncher<Intent> activityResultLaunch;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activityResultLaunch = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {

                    @SuppressLint("SimpleDateFormat")
                    String getDate = new SimpleDateFormat("HH.mm dd-MM-yyyy").format(Calendar.getInstance().getTime());

                    if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                        String tag = "<h3>İzlediğim Filmler (" + connectTA.getFilmDataList().size() + " Film) (Eklenme Tarihine Göre Yeniden Eskiye)</h3>";
                        assert result.getData() != null;
                        saveList = new SaveList(getActivity(),
                                connectTA.getFilmDataList(),
                                "FilmListesi " + getDate + ".html", tag, result.getData().getData());
                        saveList.htmlHazirla();
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_film, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.film_fragment_recyclerview);
        topBarText = view.findViewById(R.id.fragment_film_topbar_text);
        searcBox = view.findViewById(R.id.fragment_film_topbar_searchText);
        ImageView btnSave = view.findViewById(R.id.fragment_film_topbar_btnSave);
        pDialog = new ProgressDialog(getActivity());
        handler = new Handler(getMainLooper());

        if (!listLoadOk)
            if (isWorking)
                return;
            else
                getList();
        else
            setAdapter();


        btnSave.setOnClickListener(v -> {

            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            activityResultLaunch.launch(Intent.createChooser(intent, "Dizinini Seç"));

        });

        searcBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (listLoadOk)
                    filtrele(searcBox.getText().toString());
            }
        });
    }

    private int artisMik = 0;

    private void getList() {
        connectTA = new ConnectTA(getActivity());
        handler = new Handler(Looper.getMainLooper());
        if (LoginFragment.LOGIN_IS_OK && LoginFragment.GET_COOKIE != null) {
            pMessage("Veriler alınıyor...");
            new Thread(() -> {

                int i = 1;
                while (!connectTA.isFilmSayfaSonumu()) {
                    connectTA.getFilmListesi(i, LoginFragment.GET_COOKIE);

                    if (!connectTA.isFilmSayfaSonumu()) {
                        artisMik += connectTA.getArtisMik();
                        pMessage("İşleniyor " + artisMik + "/" + connectTA.getTotalSayi());
                    }

                    i++;
                    if (connectTA.isAnError()) {
                        handler.post(() -> {
                            pDialogClose();
                            Toast.makeText(getActivity(), "Bir Sorun var çıkıp yeniden giriş yapmayı deneyin.", Toast.LENGTH_SHORT).show();
                        });
                        break;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (!connectTA.isAnError()) {
                    handler.post(() -> {
                        listLoadOk = true;
                        setAdapter();
                        pDialogClose();
                    });
                }
            }).start();
            isWorking = true;
        }
    }

    @SuppressLint("SetTextI18n")
    private void setAdapter() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        adapter = new FilmDiziListAdapter(getActivity(), connectTA.getFilmDataList());
        recyclerView.setAdapter(adapter);
        topBarText.setText("Film+ : " + adapter.getItemCount());
    }

    @SuppressLint("SetTextI18n")
    private void filtrele(String ara) {
        ArrayList<FilmData> temp = new ArrayList<>();
        for (FilmData list : connectTA.getFilmDataList())
            if (list.getFilmAd().toLowerCase().contains(ara.toLowerCase()))
                temp.add(list);

        adapter.updateList(temp);
        topBarText.setText("Film+ : " + adapter.getItemCount());
    }

    private void pMessage(String message) {

        handler.post(() -> {

            pDialog.setCancelable(false);
            pDialog.setMessage(message);
            pDialog.show();

        });
    }

    private void pDialogClose() {

        handler.post(() -> {
            if (pDialog.isShowing())
                pDialog.dismiss();
        });
    }

}