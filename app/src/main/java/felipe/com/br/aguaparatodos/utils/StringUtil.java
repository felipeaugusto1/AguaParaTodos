package felipe.com.br.aguaparatodos.utils;

import android.text.TextUtils;
import java.text.Normalizer;

public final class StringUtil {

	public static boolean isNumero(String palavra) {
		try {
			Long.parseLong(palavra);
			return true;
		} catch (NumberFormatException ex) {
			return false;
		}
	}

	public final static boolean validarEmail(String txtEmail) {
		if (TextUtils.isEmpty(txtEmail)) {
			return false;
		} else {
			return android.util.Patterns.EMAIL_ADDRESS.matcher(txtEmail)
					.matches();
		}
	}
	
	public static String retirarAcentosDaPalavra(String palavra) {
		palavra = Normalizer.normalize(palavra, Normalizer.Form.NFD);
		palavra = palavra.replaceAll("[^\\p{ASCII}]", "");

		return palavra;
	}

	public static String converterPrimeiraLetraMaiuscula(String palavra) {
        String p = palavra.toLowerCase();
        p = palavra.substring(0, 1).toUpperCase().concat(p.substring(1, p.length()));
        return p;
    }

}
