package dean.weather;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by DeanF on 11/25/2016.
 */

public class slantView extends View {
    private Context context;
    Paint paint;
    Path path;

    public slantView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        setWillNotDraw(false);
        init();
    }

    private void init(){
        int w = getWidth(), h = getHeight();
        paint.setStrokeWidth(2);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAntiAlias(true);

        path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(0,0);
        path.lineTo(0,h);
        path.lineTo(w,h);
        path.close();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(path, paint);
    }
}
