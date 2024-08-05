import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

// Classe para representar um arquivo serializado
//Classe para representar um arquivo serializado
class ArquivoSerializado implements Serializable {
 private static final long serialVersionUID = 1L;
 private String caminhoRelativo;
 private long tamanho;
 private byte[] conteudo;

 public ArquivoSerializado(String caminhoRelativo, long tamanho) {
     this.caminhoRelativo = caminhoRelativo;
     this.tamanho = tamanho;
 }

 public String getCaminhoRelativo() {
     return caminhoRelativo;
 }

 public long getTamanho() {
     return tamanho;
 }

 public byte[] getConteudo() {
     return conteudo;
 }

 public void setConteudo(byte[] conteudo) {
     this.conteudo = conteudo;
 }
 

 public ArquivoSerializado(String caminhoRelativo, byte[] conteudo) {
     this.caminhoRelativo = caminhoRelativo;
     this.conteudo = conteudo;
 }
 
 private String nome;

 public String getNome() {
     return nome;
 }


 public ArquivoSerializado(String caminhoRelativo, long tamanho, byte[] conteudo) {
     this.caminhoRelativo = caminhoRelativo;
     this.tamanho = tamanho;
     this.conteudo = conteudo;
 }


 public void setCaminhoRelativo(String caminhoRelativo) {
     this.caminhoRelativo = caminhoRelativo;
 }

 public void setTamanho(long tamanho) {
     this.tamanho = tamanho;
 }


}



public class Encriptacao {
	
	static String diretorioTemporario;

    // Método para serializar toda a estrutura de diretório
    @SuppressWarnings("deprecation")
	public static void serializarDiretorioCompleto(File diretorio, String arquivoSerializado) throws IOException {
        List<ArquivoSerializado> arquivos = listarArquivos(diretorio, diretorio.getPath().length() + 1);
        DiretorioSerializado diretorioSerializado = new DiretorioSerializado(arquivos);

        try (ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(arquivoSerializado)))) {
            out.writeObject(diretorioSerializado);
            for (ArquivoSerializado arquivo : arquivos) {
            	if(!arquivo.toString().contains(".fseventsd") && !arquivo.toString().contains(".DS_Store") && !arquivo.toString().contains("._") && !arquivo.toString().contains(".Spotlight-V100") && !arquivo.toString().contains(".Trashes")) {
	                try (InputStream fis = new FileInputStream(new File(diretorio.getPath(), arquivo.getCaminhoRelativo()))) {
	                    byte[] buffer = new byte[8192]; // 8 KB buffer
	                    int bytesRead;
	                    while ((bytesRead = fis.read(buffer)) != -1) {
	                        out.write(buffer, 0, bytesRead);
	                    }
	                }
            	}
            }
        }
        
        if (isFileCorrupted(arquivoSerializado)) {
            System.out.println("O ficheiro está corrompido, tente novamente.");
            JOptionPane.showMessageDialog(null, "O ficheiro está corrompido, tente novamente.\nClique OK para reverter as alterações.",
                    "Erro", JOptionPane.ERROR_MESSAGE);
            excluirDiretorioTemporario(new File(diretorioTemporario));
            JOptionPane.showMessageDialog(null, "Processo revertido com sucesso.",
                    "Information", JOptionPane.INFORMATION_MESSAGE);
            Thread.currentThread().stop();
        } else {
            System.out.println("O ficheiro não está corrompido.");
            
            // Após serializar, excluir os arquivos
            excluirArquivos(diretorio);
            
            System.out.println("Diretório completo serializado com sucesso: " + arquivoSerializado);            
        }

        
    }

    // Método para listar todos os arquivos dentro de um diretório e subdiretórios
    public static List<ArquivoSerializado> listarArquivos(File diretorio, int caminhoBaseLength) {
        List<ArquivoSerializado> arquivos = new ArrayList<>();
        File[] lista = diretorio.listFiles();
        if (lista != null) {
            for (File arquivo : lista) {
            	//System.out.println(arquivo);
            	if(!arquivo.toString().contains("System Volume Information") && !arquivo.toString().contains(".fseventsd") && !arquivo.toString().contains(".DS_Store") && !arquivo.toString().contains("._") && !arquivo.toString().contains(".Spotlight-V100") && !arquivo.toString().contains(".Trashes")) {
                if (arquivo.isDirectory()) {
                    arquivos.addAll(listarArquivos(arquivo, caminhoBaseLength));
                } else {
                    String caminhoRelativo = arquivo.getPath().substring(caminhoBaseLength);
                    arquivos.add(new ArquivoSerializado(caminhoRelativo, arquivo.length()));
                }
            	}
            }
        }
        return arquivos;
    }

    // Método para excluir todos os arquivos e subdiretórios dentro de um diretório
    @SuppressWarnings("unused")
    public static void excluirArquivos(File diretorio) throws IOException {
        File[] arquivos = diretorio.listFiles();
        if (arquivos != null) {
            for (File arquivo : arquivos) {
                if (arquivo.isDirectory()) {
                    excluirArquivos(arquivo);
                }
                try {
                    String osname = System.getProperty("os.name");

                    if(osname.startsWith("Mac")) {
                        ProcessBuilder builder = new ProcessBuilder("chflags", "-R", "nouchg", ""+arquivo+"");
                        builder.redirectErrorStream(true);
                        Process p = builder.start();
                        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
                        String line;
                        line = r.readLine();
                        arquivo.toString().replaceAll("[^\\w\\s.,+-]", " ");
                        if(arquivo.delete()) {  // Delete the file after reading its content
                        	//System.out.println(arquivo+" apagado.");
                        }else {
                        	if(!arquivo.toString().contains(".fseventsd") && !arquivo.toString().contains(".DS_Store") && !arquivo.toString().contains("._") && !arquivo.toString().contains(".Spotlight-V100") && !arquivo.toString().contains(".Trashes")) {
	                        	Files.delete(arquivo.toPath());
	                        	//System.out.println(arquivo+" apagado.");
                        	}
                        }
                    } else if(osname.startsWith("Windows")) {
                        ProcessBuilder builder = new ProcessBuilder("attrib", "-r", ""+arquivo+"");
                        builder.redirectErrorStream(true);
                        Process p = builder.start();
                        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
                        String line;
                        line = r.readLine();
                        if(arquivo.delete()) {  // Delete the file after reading its content
                        	//System.out.println(arquivo+" apagado.");
                        }else {
                        	if(!arquivo.toString().contains("._.DS_Store") && !arquivo.toString().contains("._") && !arquivo.toString().contains(".Spotlight-V100") && !arquivo.toString().contains(".Trashes")) {
	                        	Files.delete(arquivo.toPath());
	                        	//System.out.println(arquivo+" apagado.");
                        	}
                        }
                    }
                } catch(Exception e) {
                    System.out.println(e);
                }
            }
        }
    }

    // Método para criar um diretório temporário no computador
    public static String criarDiretorioTemporario() throws IOException {
        String tempDir = System.getProperty("java.io.tmpdir");
        File dir = new File(tempDir + File.separator + "diretorio_serializado_temp");
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                System.out.println("Diretório temporário criado: " + dir.getAbsolutePath());
            } else {
                throw new IOException("Não foi possível criar o diretório temporário: " + dir.getAbsolutePath());
            }
        }
        return dir.getAbsolutePath();
    }

    public static void encryptFolder(String pastaNaPenUSB) throws IOException {
    	try {
        	DeleteHiddenFiles.DeleteFiles();
        }catch(Exception e) {
        	e.printStackTrace();
        }
    	
        // Diretório que será serializado por completo na Pen USB
        File diretorio = new File(pastaNaPenUSB);

        // Criar um diretório temporário no computador
        try {
            diretorioTemporario = criarDiretorioTemporario();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        
        
        

        // Nome do arquivo serializado que será criado no diretório temporário
        String arquivoSerializado = diretorioTemporario + File.separator + "lockedData.ser";
        String outputarquivoencriptado = diretorioTemporario + File.separator + "lockedData.enc";

        try {
            serializarDiretorioCompleto(diretorio, arquivoSerializado);
            
            try {
	            AES256.encrypt(arquivoSerializado, outputarquivoencriptado);
            }catch(Exception e1) {
            	System.out.println(e1);
            }
            
            // Mover o arquivo serializado da pasta temporária para a Pen USB
            moverArquivoParaPenUSB(outputarquivoencriptado, pastaNaPenUSB);
            
            // Apagar o diretório temporário e seu conteúdo
            excluirDiretorioTemporario(new File(diretorioTemporario));
            
            JOptionPane.showMessageDialog(null, "Processo de encriptação concluído",
            		"Information", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para mover o arquivo serializado para a Pen USB
    public static void moverArquivoParaPenUSB(String outputarquivoencriptado, String pastaNaPenUSB) {
        File arquivoOrigem = new File(outputarquivoencriptado);
        File diretorioDestino = new File(pastaNaPenUSB);

        try {
            Files.move(arquivoOrigem.toPath(), new File(diretorioDestino, arquivoOrigem.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);   
        } catch (IOException e) {
        	JOptionPane.showMessageDialog(null, "Ocorreu um problema durante a encriptação",
                    "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        
        System.out.println("Arquivo movido para Pen USB com sucesso.");
    }
    
    
    //Verificar se o ficheiro .ser está corrompido
    @SuppressWarnings("unused")
	public static boolean isFileCorrupted(String filePath) {
        try (FileInputStream fis = new FileInputStream(filePath);
             ObjectInputStream ois = new ObjectInputStream(fis)) {

            // Tente ler o objeto do ficheiro
            Object obj = ois.readObject();
            // Se a leitura for bem-sucedida, o ficheiro não está corrompido
            return false;

        } catch (InvalidClassException e) {
            System.err.println("Incompatibilidade de versão de classe: " + e.getMessage());
        } catch (StreamCorruptedException e) {
            System.err.println("Fluxo de entrada está corrompido: " + e.getMessage());
        } catch (OptionalDataException e) {
            System.err.println("Dados primitivos no fluxo: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Classe não encontrada: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Erro de E/S: " + e.getMessage());
        }
        // Se alguma exceção for lançada, o ficheiro está corrompido ou incompatível
        return true;
    }
    

    // Método para excluir o diretório temporário e seu conteúdo
    public static void excluirDiretorioTemporario(File diretorio) {
        File[] arquivos = diretorio.listFiles();
        if (arquivos != null) {
            for (File arquivo : arquivos) {
                if (arquivo.isDirectory()) {
                    excluirDiretorioTemporario(arquivo);
                }
                arquivo.delete();
            }
        }
        diretorio.delete();
    }
    

}
