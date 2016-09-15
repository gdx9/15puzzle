package manor9421.com.a15game;

import android.content.Context;
import android.widget.ImageView;

/**
 * Created by manor on 8/14/16.
 */
public class AppImage extends ImageView{
    public String path;

    public AppImage(Context context) {
        super(context);
        this.path = null;
    }

    public AppImage(Context context, String path) {
        super(context);
        this.path = path;
    }
}
