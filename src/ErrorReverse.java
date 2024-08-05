import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javax.swing.JOptionPane;
import org.apache.commons.io.FileUtils;

public class ErrorReverse {
	
	
		public static void ReverseDecrypt(String pastaTemporaria) {
			String arquivoSerializado = pastaTemporaria+"/lockedData.ser";
	        String outputarquivoencriptado = pastaTemporaria+"/lockedData.enc";
	        File Pastaapagar = new File(MainWindow.textField.getText());
			
	        try {
	        	excluirArquivos(Pastaapagar);
	        }catch(Exception e1) {
	        	e1.printStackTrace();
            }
	        
			try {
	            AES256.encrypt(arquivoSerializado, outputarquivoencriptado);
            }catch(Exception e1) {
            	e1.printStackTrace();
            }
			
			try {
				//Mover o arquivo encriptado
				Files.move(Paths.get(pastaTemporaria+"/lockedData.enc"), Paths.get(MainWindow.textField.getText()+"/lockedData.enc"), StandardCopyOption.REPLACE_EXISTING);
				
				
			}catch(Exception e) {
				e.printStackTrace();
			}
			
			try {
				FileUtils.deleteDirectory(new File(pastaTemporaria));
			}catch(Exception e) {
				e.printStackTrace();
			}
			
			MainWindow.lblNewLabel_1.setText("");
			JOptionPane.showMessageDialog(null, "Reversão concluída.",
                    "Information", JOptionPane.INFORMATION_MESSAGE);
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

	                    if(osname.startsWith("Mac")) {
	                        ProcessBuilder builder = new ProcessBuilder("chflags", "-R", "nouchg", ""+arquivo+"");
	                        builder.redirectErrorStream(true);
	                        Process p = builder.start();
	                        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
	                        String line;
	                        line = r.readLine();
	                        arquivo.delete();  // Delete the file after reading its content
	                        System.out.println(arquivo+" apagado.");
	                    } else if(osname.startsWith("Windows")) {
	                        ProcessBuilder builder = new ProcessBuilder("attrib", "-r", ""+arquivo+"");
	                        builder.redirectErrorStream(true);
	                        Process p = builder.start();
	                        BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
	                        String line;
	                        line = r.readLine();
	                        arquivo.delete();  // Delete the file after reading its content
	                        System.out.println(arquivo+" apagado.");
	                    }
	                } catch(Exception e) {
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
