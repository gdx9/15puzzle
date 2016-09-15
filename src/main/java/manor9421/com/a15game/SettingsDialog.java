package manor9421.com.a15game;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Field;

/**
 * Created by manor on 8/12/16.
 */

public class SettingsDialog extends Dialog {

    Context context;

    int minNumPicker = 3;
    int maxNumPicker = 10;

    public SettingsDialog(Context context) {
        super(context);
        this.context = context;
        final SettingsDialog thisDial = this;


        final UserPreferences userSettings = new UserPreferences();

        boolean useNums = userSettings.getPrefUseNum(context);
        int rows = userSettings.getPrefRowCount(context);
        int cols = userSettings.getPrefColCount(context);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setBackgroundDrawableResource(R.color.black_60);
        this.setContentView(R.layout.settings_dialog);
        //final View settingsCustomView = context.getLayoutInflater().inflate(R.layout.settings_dialog,null);

        final CheckBox useNumCheckBox = (CheckBox) findViewById(R.id.addNumsCheckbox);
        if(useNums){
            useNumCheckBox.setChecked(true);
        }else{
            useNumCheckBox.setChecked(false);
        }

        final ImageView defImage= (ImageView) findViewById(R.id.chosenImageView);
        String imPath = userSettings.getPrefSelImage(context);

        if(imPath.isEmpty()){//если нет такого объекта, то играет на номерах
            ///////////////
            //////////////
            //////////////

        }else {
            try {
                defImage.setTag(imPath);
                String path = getA() + "/" + imPath;
                File file = new File(path);
                if(file.exists()) {
                    Bitmap newImg = BitmapFactory.decodeFile(path);
                    defImage.setImageBitmap(newImg);
                }else {
                    Bitmap defaultImage = BitmapFactory.decodeResource(getContext().getResources(),
                            R.drawable.def_img);
                    Bitmap smallBitmap = Bitmap.createScaledBitmap(defaultImage, 80,
                            80, false);
                    defImage.setImageBitmap(smallBitmap);
                }
            }catch (Exception e){
                Toast.makeText(context,":(",Toast.LENGTH_SHORT).show();
            }
        }

        defImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manageImages();
            }
        });


        final NumberPicker rowsNumPicker = (NumberPicker) findViewById(R.id.defaultRowsNum);
        rowsNumPicker.setMinValue(minNumPicker);
        rowsNumPicker.setMaxValue(maxNumPicker);
        rowsNumPicker.setValue(rows);
        rowsNumPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);// чтобы не было фокуса при открытии диалога

        setNumperPickerTextColor(rowsNumPicker);

        final NumberPicker colsNumPicker = (NumberPicker) findViewById(R.id.defaultColsNum);
        colsNumPicker.setMinValue(minNumPicker);
        colsNumPicker.setMaxValue(maxNumPicker);
        colsNumPicker.setValue(cols);
        colsNumPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        setNumperPickerTextColor(colsNumPicker);

        Button settingsOkButton = (Button) findViewById(R.id.settingsOkButton);
        settingsOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //меняем настройки
                userSettings.setPrefUseNum(getContext(),useNumCheckBox.isChecked());
                userSettings.setPrefRowCount(getContext(),rowsNumPicker.getValue());
                userSettings.setPrefColCount(getContext(),colsNumPicker.getValue());
                if(defImage.getTag() == null) {
                    //нет изображения
                }else {
                    userSettings.setPrefSelImage(getContext(), defImage.getTag().toString());
                }
                thisDial.dismiss();

            }
        });

        Button settingsCancelButton = (Button) findViewById(R.id.settingsCancelButton);
        settingsCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                thisDial.dismiss();
            }
        });


    }

    public boolean setNumperPickerTextColor(NumberPicker numberPicker){
        //set text color
        final int count = numberPicker.getChildCount();
        for(int i = 0; i < count; i++) {
            View child = numberPicker.getChildAt(i);
            if (child instanceof EditText) {
                try {
                    Field selectorWheelPaintField = numberPicker.getClass()
                            .getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    ((Paint) selectorWheelPaintField.get(numberPicker)).setColor(ContextCompat.getColor(context,R.color.numPicker));
                    ((EditText) child).setTextColor(ContextCompat.getColor(context,R.color.numPickerText));
                    numberPicker.invalidate();
                    return true;
                } catch (NoSuchFieldException e) {
                    //Log.w("setNumberPickerTextColor", e);
                } catch (IllegalAccessException e) {
                    //Log.w("setNumberPickerTextColor", e);
                } catch (IllegalArgumentException e) {
                    //Log.w("setNumberPickerTextColor", e);
                }
            }
        }
        return false;
    }

    public String getA(){
        String progDirectotyPath = getContext().getApplicationInfo().dataDir;
        String pathBig = progDirectotyPath + "/images/images-small";
        return pathBig;
    }

   /* @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {



    }*/

    public void manageImages() {

        ManageImagesDialog sd = new ManageImagesDialog();
        sd.settingsDialog = findViewById(R.id.settingsMainLayout);
        try {
            FragmentManager manager = ((FragmentActivity) context).getSupportFragmentManager();
            sd.show(manager,"Images Dialog");
        }catch(Exception e){
            Toast.makeText(getContext(),"Fail! "+e,Toast.LENGTH_SHORT).show();
        }

    }


}

/*
public class SettingsDialog extends DialogFragment {

    public String getA(){
        String progDirectotyPath = getActivity().getApplicationInfo().dataDir;
        String pathBig = progDirectotyPath + "/images/images-small";
        return pathBig;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final UserPreferences userSettings = new UserPreferences();

        boolean useNums = userSettings.getPrefUseNum(getActivity());
        int rows = userSettings.getPrefRowCount(getActivity());
        int cols = userSettings.getPrefColCount(getActivity());

        final View settingsCustomView = getActivity().getLayoutInflater().inflate(R.layout.settings_dialog,null);

        final CheckBox useNumCheckBox = (CheckBox) settingsCustomView.findViewById(R.id.addNumsCheckbox);
        if(useNums){
            useNumCheckBox.setChecked(true);
        }else{
            useNumCheckBox.setChecked(false);
        }

        final ImageView defImage= (ImageView) settingsCustomView.findViewById(R.id.chosenImageView);
        String imPath = userSettings.getPrefSelImage(getActivity());

        if(imPath.isEmpty()){//если нет такого объекта, то играет на номерах
            ///////////////
            //////////////
            //////////////

        }else {
            try {
                defImage.setTag(imPath);
                Bitmap newImg = BitmapFactory.decodeFile(getA() + "/" + imPath);
                defImage.setImageBitmap(newImg);
            }catch (Exception e){
                Toast.makeText(getActivity(),":(",Toast.LENGTH_SHORT).show();
            }
        }

        final ImageView b = (ImageView) settingsCustomView.findViewById(R.id.chosenImageView);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manageImages(view,settingsCustomView);
            }
        });


        final NumberPicker rowsNumPicker = (NumberPicker) settingsCustomView.findViewById(R.id.defaultRowsNum);
        rowsNumPicker.setMinValue(2);
        rowsNumPicker.setMaxValue(11);
        rowsNumPicker.setValue(rows);

        final NumberPicker colsNumPicker = (NumberPicker) settingsCustomView.findViewById(R.id.defaultColsNum);
        colsNumPicker.setMinValue(2);
        colsNumPicker.setMaxValue(11);
        colsNumPicker.setValue(cols);


        return new AlertDialog.Builder(getActivity())
                .setView(settingsCustomView)
                .setTitle("Settings")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //меняем настройки

                        userSettings.setPrefUseNum(getActivity(),useNumCheckBox.isChecked());
                        userSettings.setPrefRowCount(getActivity(),rowsNumPicker.getValue());
                        userSettings.setPrefColCount(getActivity(),colsNumPicker.getValue());
                        userSettings.setPrefSelImage(getActivity(),defImage.getTag().toString());
//
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //закрываем окно
                    }
                })
                .create();//.show()


    }

    public void manageImages(View view,View settings) {

        ManageImagesDialog sd = new ManageImagesDialog();
        sd.settingsDialog = settings;
        FragmentManager manager = getActivity().getSupportFragmentManager();
        sd.show(manager,"Images Dialog");

        Toast.makeText(getActivity().getApplicationContext(),"OK",Toast.LENGTH_SHORT).show();
    }


}
*/