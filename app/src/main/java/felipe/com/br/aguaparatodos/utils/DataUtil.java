package felipe.com.br.aguaparatodos.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

public final class DataUtil {

	@SuppressLint("SimpleDateFormat")
	public static Date formatarData(String data) throws Exception {
		if (data == null || data.equals(""))
			return null;

		Date date = null;
		try {
			DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			date = (Date) formatter.parse(data);
		} catch (ParseException e) {
			throw e;
		}
		return date;
	}

	public static String formatarData(Date data) {
		return new SimpleDateFormat("dd/MM/yyyy").format(data);
	}

	public static boolean validarDataEditText(View pView, String pMessage) {
		if (pView instanceof EditText) {
			EditText edText = (EditText) pView;
			Editable text = edText.getText();
			if (text != null) {
				String strText = text.toString();
				strText = strText.trim();

				// tamanho deve ser sempre 10, ex: 15/02/2014
				if (strText.length() == 10 && isDataValida(strText)) {
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

    @SuppressLint("SimpleDateFormat")
    public static boolean isDataValida(String data) {
        String pattern = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        sdf.setLenient(false);

        try {
            @SuppressWarnings("unused")
            Date date = sdf.parse(data);
            return true;
        } catch (ParseException e) {
            return false;
        }

    }
	
}
