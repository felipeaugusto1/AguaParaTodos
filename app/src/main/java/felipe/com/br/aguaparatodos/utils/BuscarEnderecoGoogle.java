package felipe.com.br.aguaparatodos.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by felipe on 8/29/15.
 */
public class BuscarEnderecoGoogle {

    /**
     * Método responsável por retornar uma lista com os seguintes dados
     * respectivamente, dado um endereço: Rua, Estado, e Nome do
     * Estabelecimento, caso exista.
     *
     * @param endereco
     *            Endereço informado.
     * @return Lista que possui nome da rua e estado.
     */
    public static Map<String, String> buscarEnderecoByNome(String endereco, Context contexto) {
        Geocoder geoCoordenadas = new Geocoder(contexto, (new Locale("pt",
                "BR")));

        Map<String, String> mapaValores = new HashMap<String, String>();

        List<String> dados = new ArrayList<String>();
        List<Address> listaEnderecos = null;

        do {
            try {
                listaEnderecos = geoCoordenadas
                        .getFromLocationName(endereco, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (listaEnderecos == null);

        if (!listaEnderecos.isEmpty()) {
            if (listaEnderecos.size() > 0) {
                mapaValores.put("ENDERECO", listaEnderecos.get(0).getAddressLine(0) + " " + listaEnderecos.get(0).getAddressLine(1));
                mapaValores.put("CEP", listaEnderecos.get(0).getPostalCode());

                String estado = listaEnderecos.get(0).getAdminArea();
                if (estado.length() > 2)
                    mapaValores.put("ESTADO", EstadosBrasileirosUtil.buscarSiglaEstado(estado));
                else
                    mapaValores.put("ESTADO", estado);
                if (listaEnderecos.get(0).getLocality() == null)
                    mapaValores.put("CIDADE", listaEnderecos.get(0).getSubAdminArea());
                else
                    mapaValores.put("CIDADE", listaEnderecos.get(0).getLocality());

                return mapaValores;

            }
        }

        return null;
    }

    /**
     * Método responsável por retornar uma lista de Address.
     *
     * @param latitude  Latitude
     * @param longitude Longitude
     * @return Lista que possui nome da rua, cidade e estado.
     */
    public static List<Address> buscarEnderecoByCoordenadas(Context contexto,
                                                            double latitude, double longitude) {
        Geocoder geoCoordenadas = new Geocoder(contexto,
                (new Locale("pt", "BR")));
        try {

            List<Address> listaEnderecos = geoCoordenadas.getFromLocation(
                    latitude, longitude, 1);

            if (!listaEnderecos.isEmpty()) {
                if (listaEnderecos.size() > 0) {

                    if (!listaEnderecos.isEmpty()) {
                        if (listaEnderecos.size() > 0) {
                            return listaEnderecos;
                        }
                    }
                }
            }

        } catch (IOException e) {
            Log.e("Erro busca endereco",
                    e.getMessage());
        }

        return null;
    }

    /**
     * Método responsável por retornar uma lista com as coordenadas, dado um
     * endereço.
     *
     * @param endereco Endereço informado.
     * @return Lista que possui nome latitude e longitude.
     */
    public static List<Double> buscarCoordenadasPorEndereco(Context contexto, String endereco) {
        Geocoder geoCoordenadas = new Geocoder(contexto, (new Locale("pt",
                "BR")));

        List<Double> coordenadas = new ArrayList<Double>(2);

        try {

            List<Address> listaEnderecos = geoCoordenadas.getFromLocationName(
                    endereco, 1);

            if (!listaEnderecos.isEmpty()) {
                if (listaEnderecos.size() > 0) {
                    coordenadas.add(listaEnderecos.get(0).getLatitude());
                    coordenadas.add(listaEnderecos.get(0).getLongitude());
                    return coordenadas;
                }
            }

        } catch (IOException e) {
            Log.e("Erro buscar coordenada", e.getMessage());
        }

        return new ArrayList<Double>();
    }

}
