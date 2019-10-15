package bugs.demo;

import com.codename1.components.SpanLabel;
import com.codename1.ui.CN;
import com.codename1.ui.Command;
import com.codename1.ui.Dialog;
import com.codename1.ui.TextComponent;
import com.codename1.ui.layouts.BoxLayout;

public class PinDialogs {
	public interface Supplier<R> {
		R get();
	}

	public static <T> T getFromGui(Supplier<T> supplier) {
		if (CN.isEdt()) return supplier.get();
		final Object[] holder = new Object[1];
		CN.callSeriallyOnIdle(() -> {
			synchronized (LOCK) {
				holder[0] = supplier.get();
				LOCK.notifyAll();
			}
		});
		while (true) {
			synchronized (LOCK) {
				if (holder[0] != null) {
					@SuppressWarnings("unchecked") final T result = (T) holder[0];
					return result;
				} else {
					try {
						LOCK.wait(500);
					} catch (final InterruptedException e) {
						// ignoring
					}
				}
			}
		}
	}

	private static final Object LOCK = new Object();

	public static String askPin(String hint) {
		if (!CN.isEdt()) return getFromGui(() -> askPin(hint));
		final String text = "Your PIN for " + hint;
		final SpanLabel label = new SpanLabel(text);
		final TextComponent pin = new TextComponent();
		pin.label("PIN");
		final Command ok = new Command("OK");
		final Command cancel = new Command("Abbrechen");
		final boolean cont = Dialog.show("Your PIN", BoxLayout.encloseY(pin, label), ok, cancel) == ok;
		return cont? pin.getText() : "";
	}
}
