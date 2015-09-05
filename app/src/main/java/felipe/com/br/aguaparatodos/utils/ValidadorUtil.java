package felipe.com.br.aguaparatodos.utils;

import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import java.text.Normalizer;
import java.util.Objects;

public final class ValidadorUtil {

	public static boolean isNuloOuVazio(Object objeto) {
		if (objeto == null)
            return true;

        if (objeto instanceof String)
            return (((String) objeto).isEmpty());

        if (objeto instanceof Number)
            return (((Number) objeto).intValue() == 0);

        return false;
	}

	public static boolean validarCampoEmBranco(View pView, String pMessage) {
		if (pView instanceof EditText) {
			EditText edText = (EditText) pView;
			Editable text = edText.getText();
			if (text != null) {
				String strText = text.toString();
				strText = strText.trim();
				if (!TextUtils.isEmpty(strText)) {
					return true;
				}
			}
			// em qualquer outra condição é gerado um erro
			edText.setError(pMessage);
			edText.setFocusable(true);
			edText.requestFocus();
			return false;
		}
		return false;
	}

}
