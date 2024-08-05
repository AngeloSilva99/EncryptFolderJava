import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.security.spec.KeySpec;
import java.util.List;

public class LerEncriptacao {
    @SuppressWarnings("deprecation")
    public static void MainClass() {
        try {
            // Caminho para os arquivos
            File encryptedFile = new File(MainWindow.textField.getText() + "/lockedData.enc");
            File ivFile = new File(MainWindow.textField.getText() + "/iv.enc");
            File saltFile = new File(MainWindow.textField.getText() + "/salt.enc");

            // Verifica se os arquivos existem
            if (!encryptedFile.exists() || !ivFile.exists() || !saltFile.exists()) {
                System.err.println("Um ou mais arquivos necessários não foram encontrados.");
                return;
            }

            // Lê o IV e o salt dos arquivos
            byte[] ivBytes = Files.readAllBytes(ivFile.toPath());
            byte[] saltBytes = Files.readAllBytes(saltFile.toPath());

            // Verifica se IV e salt foram lidos corretamente
            if (ivBytes == null || ivBytes.length == 0) {
                System.err.println("IV é nulo ou vazio.");
                return;
            }
            if (saltBytes == null || saltBytes.length == 0) {
                System.err.println("Salt é nulo ou vazio.");
                return;
            }

            // Deriva a chave de criptografia a partir de uma senha e o salt
            String password = MainWindow.textField_1.getText(); // Use uma senha segura
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec spec = new PBEKeySpec(password.toCharArray(), saltBytes, 65536, 256); // 65536 iterações e chave de 256 bits
            SecretKey tmp = factory.generateSecret(spec);
            SecretKey secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            // Inicializa o IV
            IvParameterSpec iv = new IvParameterSpec(ivBytes);

            // Configura o Cipher para descriptografar
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

            // Lê e descriptografa o ficheiro criptografado em blocos
            FileInputStream fileInputStream = new FileInputStream(encryptedFile);
            CipherInputStream cipherInputStream = new CipherInputStream(fileInputStream, cipher);
            ObjectInputStream objectInputStream = new ObjectInputStream(cipherInputStream);

            // Desserializa o objeto
            DiretorioSerializado root = (DiretorioSerializado) objectInputStream.readObject();

            // Fecha os streams
            objectInputStream.close();
            cipherInputStream.close();
            fileInputStream.close();

            // Visualiza a estrutura de diretórios e arquivos
            printFileStructure(root);

        } catch (Exception e) {
            e.printStackTrace();
            MainWindow.lblNewLabel_1.setText("Password de encriptação incorreta ou erro ao descriptografar");
            MainWindow.lblNewLabel_1.setForeground(Color.RED);
            Timer timer = new Timer(4000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent event) {
                    MainWindow.lblNewLabel_1.setText("");
                    MainWindow.lblNewLabel_1.setForeground(Color.BLACK);
                }
            });
            timer.setRepeats(false);
            timer.start();
        }
    }

    private static void printFileStructure(DiretorioSerializado root) {
        List<ArquivoSerializado> arquivos = root.getArquivos();
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (ArquivoSerializado arquivo : arquivos) {
            listModel.addElement(arquivo.getCaminhoRelativo() + " (tamanho: " + arquivo.getTamanho() + " bytes)");
        }
        JLabel newLabel = new JLabel("");
        JList<String> fileList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(fileList);
        

        JButton extractButton = new JButton("Extrair Arquivo");
        extractButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = fileList.getSelectedIndex();
                if (selectedIndex != -1) {
                	newLabel.setText("A extrair...");
                    ArquivoSerializado selectedFile = arquivos.get(selectedIndex);
                    try {
                        String pastaNaPenUSB = MainWindow.textField.getText() + "/lockedData.enc"; // Substitua com o caminho correto da sua Pen USB
                        String diretorioDestino = MainWindow.textField.getText(); // Substitua com o caminho desejado para o diretório de destino
                        String arquivounico = selectedFile.getCaminhoRelativo();
                        System.out.println("A extrair: " + arquivounico);
                        
                        DesencriptacaoFicheiro.decryptFolder(arquivounico, pastaNaPenUSB, diretorioDestino);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Selecione um arquivo para extrair", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        
        

        JPanel panel = new JPanel();
        panel.add(scrollPane);
        panel.add(extractButton);
        panel.add(newLabel);

        JOptionPane.showMessageDialog(null, panel, "Conteúdo do ficheiro", JOptionPane.INFORMATION_MESSAGE);
    }
}
