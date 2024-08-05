import java.io.Serializable;
import java.util.List;

public class DiretorioSerializado implements Serializable {
    private static final long serialVersionUID = 1L;
    
    //Encriptacao e Desencriptacao
    private List<ArquivoSerializado> arquivos;

    public DiretorioSerializado(List<ArquivoSerializado> arquivos) {
        this.arquivos = arquivos;
    }

    public List<ArquivoSerializado> getArquivos() {
        return arquivos;
    }
    
    
    //LerEncriptacao
    private String path;
    private List<Object> children; // Pode conter tanto DiretorioSerializado quanto FicheiroSerializado

    public DiretorioSerializado(String path, List<Object> children) {
        this.path = path;
        this.children = children;
    }

    public String getPath() {
        return path;
    }

    public List<Object> getChildren() {
        return children;
    }
    
    
    //ExtractFile

    public void setArquivos(List<ArquivoSerializado> arquivos) {
        this.arquivos = arquivos;
    }

}
