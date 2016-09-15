package manor9421.com.a15game;

import android.content.Context;
import android.widget.Button;

/**
 * Created by manor on 8/15/16.
 */
public class CellButton extends Button {
    int x;
    int y;
    int num;

    public CellButton(Context context) {
        super(context);
    }


    public CellButton(Context context, int x, int y, int num) {
        super(context);
        this.x = x;
        this.y = y;
        this.num = num;
    }
}
