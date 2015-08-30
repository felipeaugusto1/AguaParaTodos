package felipe.com.br.aguaparatodos.utils;

import android.text.TextUtils;

import java.text.Normalizer;
import java.util.Objects;

public final class ValidadorUtil {

	public static boolean isNulo(Object objeto) {
		return (objeto == null) ? true : false;
	}

}
