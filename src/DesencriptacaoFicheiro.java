import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.swing.JOptionPane;

public class DesencriptacaoFicheiro {

    // Método para desserializar a estrutura de diretório
    public static DiretorioSerializado desserializarDiretorioCompleto(Path arquivoSerializado) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(Files.newInputStream(arquivoSerializado)))) {
            return (DiretorioSerializado) in.readObject();
        }
    }

    // Método para reconstruir a estrutura de diretório e seus arquivos
    public static void extrairArquivoSelecionado(String arquivounico, DiretorioSerializado diretorioSerializado, Path arquivoSerializado, String diretorioDestino) throws IOException, ClassNotFoundException {
        File diretorio = new File(diretorioDestino);
        if (!diretorio.exists()) {
            diretorio.mkdirs();
        }

        try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(Files.newInputStream(arquivoSerializado)))) {
            in.readObject(); // Lê o objeto DiretorioSerializado

            for (ArquivoSerializado arquivoSerializado1 : diretorioSerializado.getArquivos()) {
                if (arquivoSerializado1.getCaminhoRelativo().equals(arquivounico)) {
                    File destino = new File(diretorio, arquivoSerializado1.getCaminhoRelativo());
                    File parentDir = destino.getParentFile();
                    if (!parentDir.exists()) {
                        parentDir.mkdirs();
                    }

                    try (OutputStream destinoStream = new BufferedOutputStream(new FileOutputStream(destino))) {
                        byte[] buffer = new byte[8192]; // 8 KB buffer
                        long tamanhoRestante = arquivoSerializado1.getTamanho();
                        while (tamanhoRestante > 0) {
                            int bytesLidos = in.read(buffer, 0, (int) Math.min(buffer.length, tamanhoRestante));
                            if (bytesLidos == -1) {
                                break; // Se não há mais bytes para ler, sai do loop
                            }
                            destinoStream.write(buffer, 0, bytesLidos);
                            tamanhoRestante -= bytesLidos;
                        }
                    }

                    System.out.println(arquivoSerializado1.getCaminhoRelativo());
                    JOptionPane.showMessageDialog(null, "Ficheiro extraído com sucesso.",
                            "Information", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
            }

            JOptionPane.showMessageDialog(null, "Arquivo não encontrado.",
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }

        System.out.println("Extração concluída com sucesso em: " + diretorio.getPath());
    }

    @SuppressWarnings("deprecation")
    public static void decryptFolder(String arquivounico, String arquivoSerializado, String diretorioDestino) {
        Path pastaTemporaria = null;
        try {
            // Criar uma pasta temporária
            pastaTemporaria = Files.createTempDirectory("diretorio_serializado_temp");

            // Mover o arquivo .ser para a pasta temporária
            Path arquivoTemporariocopiar = pastaTemporaria.resolve("lockedData.enc");
            Files.copy(Paths.get(arquivoSerializado), arquivoTemporariocopiar, StandardCopyOption.REPLACE_EXISTING);

            // Descriptografar o arquivo
            try {
                AES256.decrypt(pastaTemporaria.toString());
            } catch (Exception e1) {
                System.out.println("Erro durante a descriptografia: " + e1.getMessage());
                e1.printStackTrace();
                return;
            }

            // Caminho para o arquivo descriptografado
            Path arquivoTemporario = pastaTemporaria.resolve("lockedData.ser");

            // Verificar se o arquivo descriptografado é um arquivo válido de objeto Java
            if (!Files.exists(arquivoTemporario) || Files.size(arquivoTemporario) == 0) {
                System.out.println("Arquivo descriptografado inválido ou vazio.");
                MainWindow.lblNewLabel_1.setText("A reverter alterações...");
                JOptionPane.showMessageDialog(null, "Ocorreu um problema durante a desencriptação\nClique em OK para reverter as alerações.",
                        "Erro", JOptionPane.ERROR_MESSAGE);

                // Excluir o arquivo .ser da pasta temporária
                Files.delete(arquivoTemporario);
                // Excluir a pasta temporária
                Files.delete(pastaTemporaria);
                MainWindow.lblNewLabel_1.setText("");
                JOptionPane.showMessageDialog(null, "Processo revertido com sucesso.",
                        "Information", JOptionPane.INFORMATION_MESSAGE);
                Thread.currentThread().stop();
            } else if (!arquivounico.isEmpty()) {
                // Desserializar e extrair o arquivo selecionado
                DiretorioSerializado diretorioSerializado = desserializarDiretorioCompleto(arquivoTemporario);
                extrairArquivoSelecionado(arquivounico, diretorioSerializado, arquivoTemporario, diretorioDestino);

                try {
                    // Mover o arquivo encriptado
                    Files.move(Paths.get(pastaTemporaria + "/lockedData.enc"), Paths.get(MainWindow.textField.getText() + "/lockedData.enc"), StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                // Excluir o arquivo .ser da pasta temporária
                Files.delete(arquivoTemporario);
                // Excluir a pasta temporária
                Files.delete(pastaTemporaria);
                MainWindow.lblNewLabel_1.setText("");
            } else {
                File ficheiroapagar1 = new File(pastaTemporaria + "/lockedData.enc");
                File ficheiroapagar2 = new File(MainWindow.textField.getText() + "/salt.enc");
                File ficheiroapagar3 = new File(MainWindow.textField.getText() + "/iv.enc");
                Files.delete(ficheiroapagar1.toPath());
                Files.delete(ficheiroapagar2.toPath());
                Files.delete(ficheiroapagar3.toPath());

                // Desserializar e extrair o arquivo selecionado
                DiretorioSerializado diretorioSerializado = desserializarDiretorioCompleto(arquivoTemporario);
                extrairArquivoSelecionado(arquivounico, diretorioSerializado, arquivoTemporario, diretorioDestino);

                // Excluir o arquivo .ser da pasta temporária
                Files.delete(arquivoTemporario);
                // Excluir a pasta temporária
                Files.delete(pastaTemporaria);

                JOptionPane.showMessageDialog(null, "Processo de desencriptação concluído",
                        "Information", JOptionPane.INFORMATION_MESSAGE);
                System.out.println("Arquivo temporário e pasta temporária excluídos com sucesso.");
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            MainWindow.lblNewLabel_1.setText("A reverter alterações...");
            JOptionPane.showMessageDialog(null, "Ocorreu um problema durante a desencriptação\nClique em OK para reverter as alerações.",
                    "Erro", JOptionPane.ERROR_MESSAGE);
            try {
                ErrorReverse.ReverseDecrypt(pastaTemporaria.toString());
            } catch (Exception e1) {
                e.printStackTrace();
            }

        } finally {
            if (pastaTemporaria != null && Files.exists(pastaTemporaria)) {
                try {
                    Files.delete(pastaTemporaria);
                } catch (IOException e) {
                    System.out.println("Erro ao excluir a pasta temporária: " + e.getMessage());
                }
            }
        }
    }

    @SuppressWarnings("unused")
    public static void excluirArquivos(File diretorio) {
        File[] arquivos = diretorio.listFiles();
        if (arquivos != null) {
            for (File arquivo : arquivos) {
                if (arquivo.isDirectory()) {
                    excluirArquivos(arquivo);
                }
                try {
                    String osname = System.getProperty("os.name");

                    if (osname.startsWith("Mac")) {
                        ProcessBuilder builder = new ProcessBuilder("chflags", "-R", "nouchg", "" + arquivo + "");
                        builder.redirectErrorStream(true);
                        Process p = builder.start();
                        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
                        String line;
                        line = r.readLine();
                        arquivo.delete();  // Delete the file after reading its content
                        System.out.println(arquivo + " apagado.");
                    } else if (osname.startsWith("Windows")) {
                        ProcessBuilder builder = new ProcessBuilder("attrib", "-r", "" + arquivo + "");
                        builder.redirectErrorStream(true);
                        Process p = builder.start();
                        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
                        String line;
                        line = r.readLine();
                        arquivo.delete();  // Delete the file after reading its content
                        System.out.println(arquivo + " apagado.");
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
            }
        }
    }

    public static void excluirDiretorio(File diretorio) {
        File[] arquivos = diretorio.listFiles();
        if (arquivos != null) {
            for (File arquivo : arquivos) {
                if (arquivo.isDirectory()) {
                    excluirDiretorio(arquivo);
                }
                arquivo.delete();
            }
        }
        diretorio.delete();
    }
}
