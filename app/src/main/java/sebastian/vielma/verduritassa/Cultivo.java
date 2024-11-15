package sebastian.vielma.verduritassa;

public class Cultivo {
    private String idDocumento;
    private String alias;
    private String fechaCosecha;

    public Cultivo(String idDocumento, String alias, String fechaCosecha) {
        this.idDocumento = idDocumento;
        this.alias = alias;
        this.fechaCosecha = fechaCosecha;
    }

    public String getIdDocumento() {
        return idDocumento;
    }

    public String getAlias() {
        return alias;
    }

    public String getFechaCosecha() {
        return fechaCosecha;
    }
}
