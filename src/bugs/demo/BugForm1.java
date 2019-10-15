package bugs.demo;

import com.codename1.ui.Button;
import com.codename1.ui.Form;
import com.codename1.ui.layouts.BoxLayout;

public class BugForm1 extends Form {
	BugForm1() {
		super("Modal dialog demo", BoxLayout.y());
		{
			final String s = "From EDT";
			final Button b = new Button(s);
			b.addActionListener(a -> PinDialogs.askPin(s));
			add(b);
		}
		{
			final String s = "From a different thread";
			final Button b = new Button(s);
			b.addActionListener(a -> startNCuriousThreads(1, s, 0));
			add(b);
		}
		{
			final String s = "From five ordered threads";
			final Button b = new Button(s);
			b.addActionListener(a -> startNCuriousThreads(5, s, 1000));
			add(b);
		}
		{
			final String s = "From ten unordered threads";
			final Button b = new Button(s);
			b.addActionListener(a -> startNCuriousThreads(10, s, 0));
			add(b);
		}
	}

	private void startNCuriousThreads(int n, String s, int stepMillis) {
		for (int i=0; i<n; ++i) {
			final String hint = s + " " + i;
			final int delayMillis = i * stepMillis;
			final Thread thread = new Thread(() -> sleepAndAskPin(hint, delayMillis));
			thread.start();
		}
	}

	private String sleepAndAskPin(String hint, int delayMillis) {
		try {
			if (delayMillis > 0) Thread.sleep(delayMillis);
		} catch (final InterruptedException e) {
			throw new RuntimeException(e);
		}
		return PinDialogs.askPin(hint);
	}
}
