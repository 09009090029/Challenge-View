package co.chatsdk.android.app.myapplication;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;

public class ChellangeImageView extends AppCompatImageView {

	private String LOGGING_TAG = "ChellangeImageView";
	private int borderWidth;
	private int canvasSize;
	private Bitmap image;
	private Paint paint;
	private Paint paintBorder;
	int borderColor;
	int selectedColor;
	int unselectedColor;
	float shadowHight = 0;

	public ChellangeImageView(final Context context) {
		this(context, null);
		selectedColor = context.getResources().getColor(R.color.orange);
		unselectedColor = context.getResources().getColor(R.color.shadow_gray);

	}

	public ChellangeImageView(Context context, AttributeSet attrs) {
		this(context, attrs, R.attr.circularImageViewStyle);
		selectedColor = context.getResources().getColor(R.color.orange);
		unselectedColor = context.getResources().getColor(R.color.shadow_gray);

	}

	public ChellangeImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		selectedColor = context.getResources().getColor(R.color.orange);
		unselectedColor = context.getResources().getColor(R.color.shadow_gray);

		// init paint
		paint = new Paint();
		paint.setAntiAlias(true);

		paintBorder = new Paint();
		paintBorder.setAntiAlias(true);

		// load the styled attributes and set their properties
		TypedArray attributes = context.obtainStyledAttributes(attrs,
				R.styleable.CustomView, defStyle, 0);

		if (attributes.getBoolean(R.styleable.CustomView_border, true)) {

			int defaultBorderSize = (int) (4 * getContext().getResources()
					.getDisplayMetrics().density + 0.5f);
			setBorderWidth(attributes.getDimensionPixelOffset(
					R.styleable.CustomView_border_width, defaultBorderSize));
			setBorderColor(attributes.getColor(
					R.styleable.CustomView_border_color, Color.WHITE));
		}

		if (attributes.getBoolean(R.styleable.CustomView_shadow, false)) {
			shadowHight = 4.0f;
			addShadow();
		}

	}

	public void setBorderWidth(int borderWidth) {
		this.borderWidth = borderWidth;
		this.requestLayout();
		this.invalidate();
	}

	public void setBorderColor(int borderColor) {
		if (paintBorder != null)
			paintBorder.setColor(borderColor);
		this.invalidate();
	}

	public void addShadow() {
		setLayerType(LAYER_TYPE_SOFTWARE, paintBorder);
		paintBorder.setShadowLayer(4.0f, 0.0f, 2.0f, Color.BLACK);
	}

	@Override
	public void onDraw(Canvas canvas) {
		// load the bitmap
		image = drawableToBitmap(getDrawable());

		// init shader
		if (image != null) {
			canvasSize = canvas.getWidth();
			Log.d(LOGGING_TAG, "Width is " + canvas.getWidth());
			Log.d(LOGGING_TAG, "Height is " + canvas.getHeight());
			if (canvas.getHeight() < canvasSize)
				canvasSize = canvas.getHeight();
			Log.d(LOGGING_TAG, "Size is " + canvasSize);
			BitmapShader shader = new BitmapShader(Bitmap.createScaledBitmap(
					image, canvasSize, canvasSize, false),
					Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
			paint.setShader(shader);

			// circleCenter is the x or y of the view's center
			// radius is the radius in pixels of the cirle to be drawn
			// paint contains the shader that will texture the shape

			int circleX = getMeasuredWidth() / 2;
			int circleY = getMeasuredHeight() / 2;
            Log.d(LOGGING_TAG,"Width " + circleX +" Height " + circleY);
			int circleCenter = (canvasSize - (borderWidth * 2)) / 2;
			canvas.drawCircle(circleX, circleY,
					((canvasSize - (borderWidth * 2)) / 2) + borderWidth
							- shadowHight, paintBorder);
			canvas.drawCircle(circleX, circleY,
					((canvasSize - (borderWidth * 2)) / 2) - shadowHight, paint);
		}

	}

	 @Override
	 protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		 int width = measureWidth(widthMeasureSpec);
		 int height = measureHeight(heightMeasureSpec);
//         int width = getMeasuredWidth();
//		 int height = getMeasuredHeight();
		 Log.d(LOGGING_TAG,"Width " + width +" Height " + height);
		 int maxDimension = Math.min(width,height);
		 Log.d(LOGGING_TAG,"Width " + maxDimension);
         if(maxDimension > 0 )
		 setMeasuredDimension(maxDimension, maxDimension);
         Log.d(LOGGING_TAG,"After change Width " + getMeasuredWidth() +" Height " + getMeasuredHeight());
	 }

	private int measureWidth(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);
        Log.d(LOGGING_TAG,"MeasureSpec " + specMode +" specSize " + specSize);
		if (specMode == MeasureSpec.EXACTLY) {
			// The parent has determined an exact size for the child.
			result = specSize;
		} else if (specMode == MeasureSpec.AT_MOST) {
			// The child can be as large as it wants up to the specified size.
			result = specSize;
		} else {
			// The parent has not imposed any constraint on the child.
			result = canvasSize;
		}

		return result;
	}

	private int measureHeight(int measureSpecHeight) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpecHeight);
		int specSize = MeasureSpec.getSize(measureSpecHeight);

		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be
			result = specSize;
		} else if (specMode == MeasureSpec.AT_MOST) {
			// The child can be as large as it wants up to the specified size.
			result = specSize;
		} else {
			// Measure the text (beware: ascent is a negative number)
			result = canvasSize;
		}

		return (result + 2);
	}

	public Bitmap drawableToBitmap(Drawable drawable) {
		if (drawable == null) {
			return null;
		} else if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		}

		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);

		return bitmap;
	}

	public void setStatus(Boolean status) {
		if (status) {
			borderColor = selectedColor;
		} else {
			borderColor = unselectedColor;
		}
		setBorderColor(borderColor);
		this.invalidate();
	}
}
