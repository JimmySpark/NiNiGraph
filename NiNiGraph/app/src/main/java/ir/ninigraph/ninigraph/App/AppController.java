package ir.ninigraph.ninigraph.App;

import android.app.Application;

import ir.ninigraph.ninigraph.R;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;


public class AppController extends Application
{

	private static AppController mInstance;

	@Override
	public void onCreate()
	{
		super.onCreate();

		CalligraphyConfig.initDefault(new CalligraphyConfig
				.Builder()
				.setDefaultFontPath("font/Vazir-FD-WOL.ttf")
				.setFontAttrId(R.attr.fontPath)
				.build());

		mInstance = this;
	}

	public static String setText(String s) {
		s = s.replace("1", "۱");
		s = s.replace("2", "۲");
		s = s.replace("3", "۳");
		s = s.replace("4", "۴");
		s = s.replace("5", "۵");
		s = s.replace("6", "۶");
		s = s.replace("7", "۷");
		s = s.replace("8", "۸");
		s = s.replace("9", "۹");
		s = s.replace("0", "۰");

		return s;
	}
}