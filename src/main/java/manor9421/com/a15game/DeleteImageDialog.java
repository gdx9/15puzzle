package manor9421.com.a15game;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;

/**
 * Created by manor on 9/14/16.
 */
public class DeleteImageDialog extends DialogFragment {

    ManageImagesDialog manageImagesDialog;
    AppImage img;
    String pathBig;
    String pathSmall;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawableResource(R.color.black_60);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#e0f1e8df"));
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#e0f1e8df"));
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String progDirectotyPath = getActivity().getApplicationInfo().dataDir;
        pathBig = progDirectotyPath + "/images/images-big";
        pathSmall = progDirectotyPath + "/images/images-small";

        return new AlertDialog.Builder(getActivity())
                .setTitle("Delete")
                .setIcon(img.getDrawable())
                //.setIcon(Drawable.createFromPath(pathSmall + "/" + img.path))
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //меняем настройки, если надо
                        if(img.path == UserPreferences.getPrefSelImage(getActivity())){
                            UserPreferences.setPrefSelImage(getActivity(),"");
                        }

                        File bigFile = new File(pathBig + "/" + img.path);
                        File smallFile = new File(pathSmall + "/" + img.path);
                        boolean deletedBig = bigFile.delete();
                        boolean deletedSmall = smallFile.delete();
                        manageImagesDialog.reloadImages();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //закрываем окно
                    }
                })
                .create();
    }
}
