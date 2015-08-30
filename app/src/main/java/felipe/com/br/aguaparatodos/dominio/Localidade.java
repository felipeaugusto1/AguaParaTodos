package felipe.com.br.aguaparatodos.dominio;

/**
 * Created by felipe on 8/29/15.
 */
public class Localidade {

    private int id;
    private double latitude;
    private double longitude;

    public Localidade() {
        super();
    }

    public Localidade(double latitude, double longitude) {
        super();
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Localidade [latitude=" + latitude + ", longitude=" + longitude
                + "]";
    }
}
