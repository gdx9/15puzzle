package manor9421.com.a15game;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by manor on 8/12/16.
 */
public class ManageImagesDialog extends DialogFragment {

    static final int GALLERY_REQUEST = 1;

    UserPreferences userPrefs = new UserPreferences();
    AppImage chosenImage;

    View settingsDialog;

    View imagesSettingsCustomView;


    String pathBig;
    String pathSmall;

    GridLayout g;

    //задать цвет фона
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawableResource(R.color.black_60);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#e0f1e8df"));
        ((AlertDialog) getDialog()).getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#e0f1e8df"));
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        imagesSettingsCustomView = getActivity().getLayoutInflater().inflate(R.layout.images_settings_dialog,null);

        g = (GridLayout) imagesSettingsCustomView.findViewById(R.id.imagesGrid);

        String progDirectotyPath = getActivity().getApplicationInfo().dataDir;
        pathBig = progDirectotyPath + "/images/images-big";
        pathSmall = progDirectotyPath + "/images/images-small";
        /*File bigDir = new File(pathBig);
        bigDir.mkdirs();
        File smallDir = new File(pathSmall);
        smallDir.mkdirs();*/

        //добавляем изображения в GridLayout
        reloadImages();

        Button addImage = (Button) imagesSettingsCustomView.findViewById(R.id.addImageButton);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
            }
        });

        return new AlertDialog.Builder(getActivity())
                .setView(imagesSettingsCustomView)
                .setTitle("Manage Images")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //меняем настройки
                        //ImageView iV = (ImageView) customView.findViewById(R.id.imageView);
                        //userPrefs.setPrefSelImage(getActivity(),chosenImage.path);

                        if(chosenImage != null) {
                            Bitmap newImg = ((BitmapDrawable) chosenImage.getDrawable()).getBitmap();
                            ImageView image = (ImageView) settingsDialog.findViewById(R.id.chosenImageView);
                            image.setTag(chosenImage.path);
                            image.setImageBitmap(newImg);
                        }


                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //закрываем окно
                    }
                })
                .create();

    }

    public void chooseNewImage(AppImage clickedView){
        //снимаем цвет для старой картинки
        if(chosenImage != null) {
            //chosenImage.setPadding(0, 0, 0, 0);
            //color
            chosenImage.setBackgroundColor(Color.TRANSPARENT);
        }else{
            //Toast.makeText(getActivity(),"No image",Toast.LENGTH_SHORT).show();
        }
        //обновляем данные для chosenImage
        makeChosen(clickedView);

        chosenImage = clickedView;


    }

    public void makeChosen(AppImage view ){
        chosenImage = view;
        //view.setPadding(2,2,2,2);
        view.setBackgroundColor(Color.RED);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        Bitmap bitmap = null;
        switch(requestCode) {
            case GALLERY_REQUEST:
                if(resultCode == getActivity().RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }catch(NullPointerException e){
                        Toast.makeText(getActivity(),"File error",Toast.LENGTH_SHORT).show();
                    }

                    //проверяем размер картинки
                    if( cropAndSaveImage(bitmap) ){//если картинка подходит и сохранилась
                        Toast.makeText(getActivity(),"Added successfully",Toast.LENGTH_LONG).show();
                        //обновляем окно настроек
                        reloadImages();
                    }else{
                        Toast.makeText(getActivity(), "Image trouble", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }//end of switch
    }

    public boolean cropAndSaveImage(Bitmap bitmap){
        // получаем размер экрана
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int displayWidth = size.x;//ширина экрана
        int displayHeight = size.y;//высота экрана
        //проверяем размер картинки
        //if(bitmap.getWidth() >= displayWidth && bitmap.getHeight() >= displayHeight){
            //имя файла
            UUID newId = UUID.randomUUID();
            String newName = newId.toString()+".jpg";

            FileOutputStream fosBig = null;
            //cоздаем директории
            File bigDir = new File(pathBig);
            bigDir.mkdirs();
            File smallDir = new File(pathSmall);
            smallDir.mkdirs();

            //ширина картинки
            int bitWidth = bitmap.getWidth();
            //высота картинки
            int bitHeight = bitmap.getHeight();


            Bitmap imageForScreen;
                //проверяем соотношение сторон
                //уменьшаем файл до размеров экрана
            try {
                if (displayWidth / displayHeight > bitWidth / bitHeight) {
                    // по ширине
                    int height = (int) Math.ceil(bitHeight / bitWidth * displayWidth);
                    //Toast.makeText(getActivity(), height+"", Toast.LENGTH_LONG).show();

                    imageForScreen = Bitmap.createScaledBitmap(bitmap,
                            displayWidth,
                            (int) Math.ceil(bitHeight / bitWidth * displayWidth),
                            true);
                    //Toast.makeText(getActivity(), bitWidth + "x" + bitHeight + "  " + displayWidth + "x" + displayHeight, Toast.LENGTH_LONG).show();

                } else {
                    // по высоте
                    int width = (int) Math.ceil(bitWidth / bitHeight * displayHeight);
                    //Toast.makeText(getActivity(), width+"", Toast.LENGTH_LONG).show();

                    imageForScreen = Bitmap.createScaledBitmap(bitmap,
                            (int) Math.ceil(bitWidth / bitHeight * displayHeight),
                            displayHeight,
                            true);
                }
            }catch (Exception e){

                imageForScreen = Bitmap.createScaledBitmap(bitmap,
                        displayWidth,
                        displayHeight,
                        true);
                Toast.makeText(getActivity(), "Saved without proportions", Toast.LENGTH_SHORT).show();
            }
            //обрезаем центр
            Bitmap bigBitmap = Bitmap.createBitmap(
                    imageForScreen,
                    imageForScreen.getWidth()/2 - displayWidth/2,
                    imageForScreen.getHeight()/2 - displayHeight/2,
                    displayWidth, //displayHeight
                    displayHeight
            );


            //уменьшаем файл
            Bitmap smallBitmap = Bitmap.createScaledBitmap(bigBitmap, 80,
                    80, false);
            //сохраняем файлы
            try {
                fosBig = new FileOutputStream(pathBig + "/" + newName);
                bigBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fosBig);

                FileOutputStream fosSmall = new FileOutputStream(pathSmall + "/" + newName);
                smallBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fosSmall);

                fosBig.flush();
                fosBig.close();

                fosSmall.flush();
                fosSmall.close();

                return true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

            }
        //}
        return false;
    }

    public void reloadImages(){

        g.removeAllViews();

        try {
            //читаем директорию с изображениями
            File bigDir = new File(pathBig);/////////////////!!!!!!!!!!!
            File file[] = bigDir.listFiles();
            //Toast.makeText(getActivity(), "111", Toast.LENGTH_SHORT).show();
            //если нет изображений
            if (file == null || file.length < 1) {
                Bitmap defaultImg = BitmapFactory.decodeResource(getResources(), R.drawable.def_img);
                //добавляем дефолтное
                cropAndSaveImage(defaultImg);
                this.dismiss();
                //заново запускаем метод
                //reloadImages();

            } else {
                //получаем настройки
                String defaulIma = userPrefs.getPrefSelImage(getActivity());
                //GridLayout imagesGrid = (GridLayout) imagesSettingsCustomView.findViewById(R.id.imagesGrid);
                for (File f : file) {

                    GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
                    layoutParams.setMargins(5, 5, 5, 5);

                    //String path = f.getAbsolutePath();

                    String imgName = f.getName();
                    AppImage pic = new AppImage(getActivity(), imgName);
                    Bitmap newImg = BitmapFactory.decodeFile(pathSmall + "/" + imgName);

                    pic.setImageBitmap(newImg);
                    pic.setLayoutParams(layoutParams);
                    pic.setPadding(2, 2, 2, 2);

                    //если картинка - дефолтная
                    if (imgName.equals(defaulIma)) {

                        makeChosen(pic);
                    }

                    pic.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            chooseNewImage((AppImage) view);
                        }
                    });

                    pic.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            deleteImage((AppImage) view);
                            return true;
                        }
                    });

                    g.addView(pic);
                    //System.out.println(f.getName());
                }

                //если не существует выбранной картинки
                File defImage = new File(pathBig + "/" + defaulIma);
                if (!defImage.exists()) {

                    // если есть дети
                    if (g.getChildCount() > 0) {
                        AppImage newDefIm = (AppImage) g.getChildAt(0);
                        makeChosen(newDefIm);
                        userPrefs.setPrefSelImage(getActivity(), newDefIm.path);
                    } else {
                        Toast.makeText(getActivity(), "Add Image", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        }catch (Exception e){
            Toast.makeText(getActivity(), e+"22", Toast.LENGTH_LONG).show();
        }


    }

    public void deleteImage(final AppImage img){

        DeleteImageDialog deleteImageDialog = new DeleteImageDialog();
        deleteImageDialog.manageImagesDialog = this;
        deleteImageDialog.img = img;
        try {
            FragmentManager manager = ((FragmentActivity) getActivity()).getSupportFragmentManager();
            deleteImageDialog.show(manager,"Delete Image");
        }catch(Exception e){
            Toast.makeText(getContext(),"Fail! "+e,Toast.LENGTH_SHORT).show();
        }

        /*
        new AlertDialog.Builder(getActivity())
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
                        reloadImages();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //закрываем окно
                    }
                })
                .create()
                .show();*/

    }
}
