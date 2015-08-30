package felipe.com.br.aguaparatodos.utils;

import java.text.Normalizer;
import java.util.Locale;

public final class EstadosBrasileirosUtil {

	public static String buscarSiglaEstado(String estado) {
		if (estado.equalsIgnoreCase("Acre"))
			return "AC";
		else if (estado.equalsIgnoreCase("Alagoas"))
			return "AL";
		else if (estado.equalsIgnoreCase("Amapá"))
			return "AP";
		else if (estado.equalsIgnoreCase("Amazonas"))
			return "AM";
		else if (estado.equalsIgnoreCase("Bahia"))
			return "BA";
		else if (estado.equalsIgnoreCase("Ceará"))
			return "CE";
		else if (estado.equalsIgnoreCase("Distrito Federal"))
			return "DF";
		else if (estado.equalsIgnoreCase("Espírito Santo"))
			return "ES";
		else if (estado.equalsIgnoreCase("Goiás"))
			return "GO";
		else if (estado.equalsIgnoreCase("Maranhão"))
			return "MA";
		else if (estado.equalsIgnoreCase("Mato Grosso"))
			return "MT";
		else if (estado.equalsIgnoreCase("Mato Grosso do Sul"))
			return "MS";
		else if (estado.equalsIgnoreCase("Minas Gerais"))
			return "MG";
		else if (estado.equalsIgnoreCase("Pará"))
			return "PA";
		else if (estado.equalsIgnoreCase("Paraíba"))
			return "PB";
		else if (estado.equalsIgnoreCase("Paraná"))
			return "PR";
		else if (estado.equalsIgnoreCase("Pernambuco"))
			return "PE";
		else if (estado.equalsIgnoreCase("Piauí"))
			return "PI";
		else if (estado.equalsIgnoreCase("Rio de Janeiro"))
			return "RJ";
		else if (estado.equalsIgnoreCase("Rio Grande do Norte"))
			return "RN";
		else if (estado.equalsIgnoreCase("Rio Grande do Sul"))
			return "RS";
		else if (estado.equalsIgnoreCase("Rondônia"))
			return "RO";
		else if (estado.equalsIgnoreCase("Roraima"))
			return "RR";
		else if (estado.equalsIgnoreCase("Santa Catarina"))
			return "SC";
		else if (estado.equalsIgnoreCase("São Paulo"))
			return "SP";
		else if (estado.equalsIgnoreCase("Sergipe"))
			return "SE";
		else if (estado.equalsIgnoreCase("Tocantins"))
			return "TO";

		return "";
	}

	public static String formatarCidade(String cidade) {
		cidade = cidade.toLowerCase(new Locale("pt", "BR"));
		cidade = cidade.replace(" ", "-");
		cidade = StringUtil.retirarAcentosDaPalavra(cidade);

		return cidade;
	}

	public static String formatarRua(String rua) {
		rua = rua.toLowerCase(new Locale("pt", "BR"));
		// Por padrao, todas os nomes de rua vindo do Google iniciam com "R."
		rua = rua.replace("r.", "");
		rua = rua.trim();
		rua = rua.replace(" ", "-");

		rua = StringUtil.retirarAcentosDaPalavra(rua);

		return rua;
	}

}
