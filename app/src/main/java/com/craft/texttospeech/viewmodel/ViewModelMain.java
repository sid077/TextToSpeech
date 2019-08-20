package com.craft.texttospeech.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.craft.texttospeech.models.LanguageStringFormat;
import com.craft.texttospeech.views.SpeechToTextActivity;
import com.craft.texttospeech.views.TextToSpeechActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewModelMain extends ViewModel {
    public MutableLiveData<Map<String, String>> getStoredTTSData() {
        return storedTTSData;
    }

    private ArrayList<String> fileContentsStt;

    public Map<String, String> getFileContentsTts() {
        return fileContentsTts;
    }

    public void setFileContentsTts(Map<String, String> fileContentsTts) {
        this.fileContentsTts = fileContentsTts;
    }

    private Map<String,String> fileContentsTts;
    MutableLiveData<Map<String,String>> storedTTSData = new MutableLiveData<>();
    MutableLiveData<List<String>> storedSTTData= new MutableLiveData<>();


    public MutableLiveData<List<String>> getStoredSTTData() {
        return storedSTTData;
    }

    public void setStoredSTTData(MutableLiveData<List<String>> storedSTTData) {
        this.storedSTTData = storedSTTData;
    }

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }

    private Map<String, String> map;

    public MutableLiveData<String> getLanguageLiveData() {
        return languageLiveData;
    }

    MutableLiveData<String> languageLiveData = new MutableLiveData<>();
    MutableLiveData<ArrayList<LanguageStringFormat>> languageAndCodeliveData = new MutableLiveData<>();

    public MutableLiveData<ArrayList<LanguageStringFormat>> getLanguageAndCodeliveData() {
        return languageAndCodeliveData;
    }

    public void fetchLanguages() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        final ArrayList<LanguageStringFormat> arrayListLangFormat = new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot d : dataSnapshot.getChildren()){

                    arrayListLangFormat.add(d.getValue(LanguageStringFormat.class));

                }
                languageAndCodeliveData.setValue(arrayListLangFormat);
                map = new HashMap<>();
                for(int i=0;i<arrayListLangFormat.size();i++){
                    map.put(arrayListLangFormat.get(i).getName(),arrayListLangFormat.get(i).getCode());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void getSTTStoredFile(final SpeechToTextActivity activity) {
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                File root = android.os.Environment.getExternalStorageDirectory();
                File dir = new File(root.getAbsolutePath()+"/downloads/TTS/speech to text");
                fileContentsStt = new ArrayList<>();
                if(dir.exists()){
                    File [] files = dir.listFiles();
                    if (files == null)
                        return;
                    for(int i=0;i<files.length;i++){
                        try {
                            FileInputStream inputStream = new FileInputStream(files[i]);
                            int length = (int) files[i].length();
                            byte[] bytes = new byte[length];
                            inputStream.read(bytes);
                            Log.i("data",new String(bytes));
                            fileContentsStt.add(new String(bytes));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            storedSTTData.setValue(fileContentsStt);

                        }
                    });



                }
            }
        });
        th.start();
    }
    public void fetchTTSStoredFile(final TextToSpeechActivity activity) {
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {
                File root = android.os.Environment.getExternalStorageDirectory();
                File dir = new File(root.getAbsolutePath()+"/downloads/TTS/text to speech");

                if(dir.exists()){
                    fileContentsTts = new HashMap<>();
                    File [] files = dir.listFiles();

                    for(int i=0;i<files.length;i++){
                        fileContentsTts.put(files[i].getName(),files[i].getAbsolutePath());
//                        try {
//                            FileInputStream inputStream = new FileInputStream(files[i]);
//                            int length = (int) files[i].length();
//                            byte[] bytes = new byte[length];
//                            inputStream.read(bytes);
//                            Log.i("data",new String(bytes));
//                            fileContentsStt.add(new String(bytes));
//                        } catch (FileNotFoundException e) {
//                            e.printStackTrace();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//

                    }
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            storedTTSData.setValue(fileContentsTts);
                        }
                    });



                }
            }
        });
        th.start();
    }


    public void deleteTTSFile(String s) {
        File root = android.os.Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath() + "/downloads/TTS/text to speech");

        if (dir.exists()) {
            fileContentsTts = new HashMap<>();
            File[] files = dir.listFiles();
            for(int i=0;i<files.length;i++){
                if(files[i].getName().equals(s))
                    files[i].delete();
            }

        }
    }

    public void setFileContentsStt(ArrayList<String> data) {
        this.fileContentsStt=data;
    }

    public void deleteSTTFile(String s) {
        File root = android.os.Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath() + "/downloads/TTS/speech to text");

        if (dir.exists()) {
            fileContentsStt = new ArrayList<>();
            File[] files = dir.listFiles();
            for(int i=0;i<files.length;i++){
                try {
                    FileInputStream inputStream = new FileInputStream(files[i]);
                    int length = (int) files[i].length();
                    byte[] bytes = new byte[length];
                    inputStream.read(bytes);
                    Log.i("data",new String(bytes));
                   if(s.equals(new String(bytes)))
                       files[i].delete();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


        }


    }
}
