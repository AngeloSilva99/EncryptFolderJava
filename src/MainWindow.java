import java.awt.Color;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.Stack;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.UIManager;
import org.apache.commons.io.FileSystemUtils;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Point;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRootPane;

import java.io.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.spec.*;

@SuppressWarnings("deprecation")
public class MainWindow {

	private JFrame frame;
	private static JButton btnNewButton;
	private static JButton btnNewButton_2;
	private JButton btnNewButton_1;
	private JButton btnClose;
	public static JTextField textField;
	public static JLabel lblNewLabel_1;
	public static JPasswordField textField_1;
	public static String diretorioTemporario;
	boolean formatodisco = false;
	boolean pastasuportada = false;
	boolean espacolivre = false;
	long maxSize = 4294967295L;
	long finalsize;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow window = new MainWindow();
					window.frame.setVisible(true);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		});
		
	}
	
	

	/**
	 * Create the application.
	 * @throws IOException 
	 */
	public MainWindow() throws IOException {
		initialize();
		InserirTextFields();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	
	public static class FrameDragListener extends MouseAdapter {

        private final JFrame frame;
        private Point mouseDownCompCoords = null;

        public FrameDragListener(JFrame frame) {
            this.frame = frame;
        }

        public void mouseReleased(MouseEvent e) {
            mouseDownCompCoords = null;
        }

        public void mousePressed(MouseEvent e) {
            mouseDownCompCoords = e.getPoint();
        }

        public void mouseDragged(MouseEvent e) {
            Point currCoords = e.getLocationOnScreen();
            frame.setLocation(currCoords.x - mouseDownCompCoords.x, currCoords.y - mouseDownCompCoords.y);
        }
    }
	
	
	
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setTitle("ENCRIPTÇÃO USB DRIVE");
		frame.setBounds(100, 100, 450, 260);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setUndecorated(true);
		frame.getRootPane().setWindowDecorationStyle(JRootPane.NONE);
		frame.setLocationRelativeTo(null);
		frame.getContentPane().setLayout(null);
		 try { 
		        UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel"); 
		 } catch(Exception ignored){}
		
		JPanel panel = new JPanel();
		panel.setBounds(13, 204, 411, 39);
		frame.getContentPane().add(panel);
		panel.setLayout(null);
		FrameDragListener frameDragListener = new FrameDragListener(frame);
        frame.addMouseListener(frameDragListener);
        frame.addMouseMotionListener(frameDragListener);
		
				
				btnNewButton = new JButton("ENCRYPT");
				btnNewButton.setBounds(0, 0, 128, 39);
				panel.add(btnNewButton);
				btnNewButton.setIcon(new ImageIcon(MainWindow.class.getResource("/images/lock.png")));
				btnNewButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						
						
						Path formato = Paths.get(MainWindow.textField.getText());
						
						
						//Verificar se o formato da Pen USB é FAT32
						try {
							
			                long size = sizeOfDirectory(Paths.get(MainWindow.textField.getText()));
			                if(size >= maxSize) {
			                	System.out.println("Não suportado pelo formato Fat32.");
			                	pastasuportada = false;
			                }else {
			                	System.out.println("É suportado pelo formato Fat32.");
			                	pastasuportada = true;
			                }
			                System.out.println("Tamanho do diretório: " + size + " bytes");
			            } catch (IOException ex) {
			                System.err.println("Erro ao calcular o tamanho do diretório: " + ex.getMessage());
			            }
						
						
						//Verifica se há espaço no disco do computador
						try {
							
			                long size = sizeOfDirectory(Paths.get(MainWindow.textField.getText()));
			                long HardDriveLeftSpace = FileSystemUtils.freeSpaceKb("/"); 
			                finalsize = size / 1024;
			                finalsize = finalsize * 2;
			                
			                if(finalsize >= HardDriveLeftSpace) {
			                	System.out.println("O computador não tem espaço suficiente."+finalsize);
			                	espacolivre = false;
			                }else {
			                	System.out.println("O computador tem espaço suficiente.");
			                	espacolivre = true;
			                }
			            } catch (IOException ex) {
			                System.err.println("Erro ao calcular o tamanho do diretório: " + ex.getMessage());
			            }
						
						
						try {
				            // Obtém o FileStore onde a pasta está localizada
				            FileStore fileStore = Files.getFileStore(formato);

				            // Obtém o tipo de sistema de arquivos
				            String fileSystemType = fileStore.type();

				            // Verifica se é FAT32
				            if ("msdos".equalsIgnoreCase(fileSystemType) || "vfat".equalsIgnoreCase(fileSystemType) || "fat32".equalsIgnoreCase(fileSystemType)) {
				                System.out.println("O sistema de arquivos é FAT32.");
				                formatodisco = true;
				            } else {
				                System.out.println("O sistema de arquivos não é FAT32, é: " + fileSystemType);
				                formatodisco = false;
				            }
				        } catch (IOException e1) {
				            System.err.println("Ocorreu um erro ao tentar verificar o sistema de arquivos: " + e1.getMessage());
				        }
						
				File f = new File(MainWindow.textField.getText()+"/lockedData.enc");
				if(f.exists() && !f.isDirectory()) { 
					lblNewLabel_1.setText("Esta pasta já foi encriptada");
					lblNewLabel_1.setForeground(Color.RED);
					btnNewButton.setEnabled(false);
					btnNewButton_2.setEnabled(false);
					Timer timer = new Timer(4000, event -> {
						lblNewLabel_1.setText("");
			            lblNewLabel_1.setForeground(Color.BLACK);
			            btnNewButton.setEnabled(true);
						btnNewButton_2.setEnabled(true);
				    });
				    timer.setRepeats(false);
				    timer.start();
				}else {
				if(espacolivre == false) {
					lblNewLabel_1.setText("Precisa de "+finalsize+" KB livres no computador.");
					lblNewLabel_1.setForeground(Color.RED);
					btnNewButton.setEnabled(false);
					btnNewButton_2.setEnabled(false);
					Timer timer = new Timer(4000, event -> {
						lblNewLabel_1.setText("");
			            lblNewLabel_1.setForeground(Color.BLACK);
			            btnNewButton.setEnabled(true);
						btnNewButton_2.setEnabled(true);
				    });
				    timer.setRepeats(false);
				    timer.start();
				}else {
				if(formatodisco == true && pastasuportada == false) {
					lblNewLabel_1.setText("A pasta é muito grande para o formato do disco.");
					lblNewLabel_1.setForeground(Color.RED);
					btnNewButton.setEnabled(false);
					btnNewButton_2.setEnabled(false);
					Timer timer = new Timer(4000, event -> {
						lblNewLabel_1.setText("");
			            lblNewLabel_1.setForeground(Color.BLACK);
			            btnNewButton.setEnabled(true);
						btnNewButton_2.setEnabled(true);
				    });
				    timer.setRepeats(false);
				    timer.start();
				}else {
				if(textField.getText().isEmpty() || textField_1.getText().isEmpty()) {
					lblNewLabel_1.setText("Password ou caminho em falta");
					lblNewLabel_1.setForeground(Color.RED);
					btnNewButton.setEnabled(false);
					btnNewButton_2.setEnabled(false);
					Timer timer = new Timer(4000, event -> {
	            	lblNewLabel_1.setText("");
	            	lblNewLabel_1.setForeground(Color.BLACK);
	            	btnNewButton.setEnabled(true);
					btnNewButton_2.setEnabled(true);
		            });
		            timer.setRepeats(false);
		            timer.start();
				}else {
					System.out.println("A encriptar ficheiros...");
					
					lblNewLabel_1.setText("A encriptar, por favor aguarde...");
					btnNewButton.setEnabled(false);
					btnNewButton_2.setEnabled(false);
					btnClose.setEnabled(false);
					btnNewButton_1.setEnabled(false);
					Timer timer = new Timer(2000, event -> {
	            	lblNewLabel_1.setText("");
	            	btnNewButton.setEnabled(true);
					btnNewButton_2.setEnabled(true);
					btnClose.setEnabled(true);
					btnNewButton_1.setEnabled(true);
	            	try {
		            	String pastaNaPenUSB = textField.getText();
		                Encriptacao.encryptFolder(pastaNaPenUSB);
	            	}catch(Exception e1) {
	            		e1.printStackTrace();
	            	}
		            });
		            timer.setRepeats(false);
		            timer.start();
				}
				}	
				}
				}
					}
				});
				
				btnClose = new JButton("EXIT");
				btnClose.setBounds(280, 0, 128, 39);
				panel.add(btnClose);
				btnClose.setIcon(new ImageIcon(MainWindow.class.getResource("/images/exit.png")));
				
				btnNewButton_2 = new JButton("DECRYPT");
				btnNewButton_2.setBounds(140, 0, 128, 39);
				panel.add(btnNewButton_2);
				btnNewButton_2.setIcon(new ImageIcon(MainWindow.class.getResource("/images/unlock.png")));
				btnNewButton_2.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						//Verifica se há espaço no disco do computador
						try {
							
			                long size = sizeOfDirectory(Paths.get(MainWindow.textField.getText()));
			                long HardDriveLeftSpace = FileSystemUtils.freeSpaceKb("/"); 
			                finalsize = size / 1024;
			                finalsize = finalsize * 2;
			                
			                if(finalsize >= HardDriveLeftSpace) {
			                	System.out.println("O computador não tem espaço suficiente."+finalsize);
			                	espacolivre = false;
			                }else {
			                	System.out.println("O computador tem espaço suficiente.");
			                	espacolivre = true;
			                }
			            } catch (IOException ex) {
			                System.err.println("Erro ao calcular o tamanho do diretório: " + ex.getMessage());
			            }
						
						
						File f = new File(MainWindow.textField.getText()+"/lockedData.enc");
						if(!f.exists() && !f.isDirectory()) { 
							lblNewLabel_1.setText("Ficheiro encriptado não encontrado");
							lblNewLabel_1.setForeground(Color.RED);
							btnNewButton.setEnabled(false);
							btnNewButton_2.setEnabled(false);
							Timer timer = new Timer(4000, event -> {
			            	lblNewLabel_1.setText("");
			            	lblNewLabel_1.setForeground(Color.BLACK);
			            	btnNewButton.setEnabled(true);
							btnNewButton_2.setEnabled(true);
				            });
				            timer.setRepeats(false);
				            timer.start();
						}else {
						if(espacolivre == false) {
								lblNewLabel_1.setText("Precisa de "+finalsize+" KB livres no computador.");
								lblNewLabel_1.setForeground(Color.RED);
								btnNewButton.setEnabled(false);
								btnNewButton_2.setEnabled(false);
								Timer timer = new Timer(4000, event -> {
									lblNewLabel_1.setText("");
						            lblNewLabel_1.setForeground(Color.BLACK);
						            btnNewButton.setEnabled(true);
									btnNewButton_2.setEnabled(true);
							    });
							    timer.setRepeats(false);
							    timer.start();
						}else {
						if (!isPasswordCorrect(MainWindow.textField.getText()+"/lockedData.enc", MainWindow.textField.getText()+"/salt.enc", MainWindow.textField.getText()+"/iv.enc", textField_1.getText())) {
							MainWindow.lblNewLabel_1.setText("Password de encriptação incorreta");
					        MainWindow.lblNewLabel_1.setForeground(Color.RED);
					        MainWindow.btnNewButton.setEnabled(false);
					        MainWindow.btnNewButton_2.setEnabled(false);
							Timer timer = new Timer(4000, event -> {
								MainWindow.lblNewLabel_1.setText("");
								MainWindow.lblNewLabel_1.setForeground(Color.BLACK);
								MainWindow.btnNewButton.setEnabled(true);
								MainWindow.btnNewButton_2.setEnabled(true);
								restartApplication(textField.getText(), textField_1.getText());
					        });
					        timer.setRepeats(false);
					        timer.start();
					        
					    } else {
						if(textField.getText().isEmpty() || textField_1.getText().isEmpty()) {
							lblNewLabel_1.setText("Password ou caminho em falta");
							lblNewLabel_1.setForeground(Color.RED);
							btnNewButton.setEnabled(false);
							btnNewButton_2.setEnabled(false);
							Timer timer = new Timer(4000, event -> {
			            	lblNewLabel_1.setText("");
			            	lblNewLabel_1.setForeground(Color.BLACK);
			            	btnNewButton.setEnabled(true);
							btnNewButton_2.setEnabled(true);
				            });
				            timer.setRepeats(false);
				            timer.start();
						}else {
							System.out.println("A desencriptar ficheiros...");
	
							lblNewLabel_1.setText("A desencriptar, por favor aguarde...");
							btnNewButton.setEnabled(false);
							btnNewButton_2.setEnabled(false);
							btnClose.setEnabled(false);
							btnNewButton_1.setEnabled(false);
				            Timer timer = new Timer(2000, event -> {
				            	lblNewLabel_1.setText("");
				            	btnNewButton.setEnabled(true);
								btnNewButton_2.setEnabled(true);
								btnClose.setEnabled(true);
								btnNewButton_1.setEnabled(true);
				            	try {
				            		String pastaNaPenUSB = MainWindow.textField.getText()+"/lockedData.enc"; // Substitua com o caminho correto da sua Pen USB
				            		String diretorioDestino = MainWindow.textField.getText(); // Substitua com o caminho desejado para o diretório de destino
				            		String arquivounico = "";
									Desencriptacao.decryptFolder(arquivounico, pastaNaPenUSB, diretorioDestino);
								} catch (Exception e1) {
									e1.printStackTrace();
								}
				            });
				            timer.setRepeats(false);
				            timer.start();
						}
					    }
					    }
						}
						
						
				       
						
						
					
						/*String path = textField.getText();
						String pwd = "MyPASSWORD";
						System.out.println("A desencriptar ficheiros...");
						try {
						Encriptacao.decrypt(path, pwd);
						}catch(Exception e1) {
							
						}
						 System.out.println("Desencriptação concluída");*/
						
					}
				});
				btnClose.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						try {
						 	String tempDir = System.getProperty("java.io.tmpdir");
				            File dir = new File(tempDir + File.separator + "AplicacaoTextField");
					        File myObj = new File(dir + File.separator + "TextFieldOldText.txt"); 
					        File dir2 = new File(myObj.toString());
					        File[] currList;
					        Stack<File> stack = new Stack<File>();
					        stack.push(dir2);
					        while (! stack.isEmpty()) {
					            if (stack.lastElement().isDirectory()) {
					                currList = stack.lastElement().listFiles();
					                if (currList.length > 0) {
					                    for (File curr: currList) {
					                        stack.push(curr);
					                    }
					                } else {
					                    stack.pop().delete();
					                }
					            } else {
					                stack.pop().delete();
					            }
					        }
					        System.exit(0);
				        }catch(Exception e2) {
				        	System.out.println(e2);
				        }
						
					}
				});
				
		
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(17, 42, 407, 115);
		frame.getContentPane().add(panel_1);
		panel_1.setLayout(null);
		
		textField = new JTextField();
		textField.setBounds(0, 14, 285, 34);
		panel_1.add(textField);
		textField.setColumns(10);
		
		
		
		JLabel lblNewLabel = new JLabel("FOLDER");
		lblNewLabel.setBounds(0, 0, 170, 16);
		panel_1.add(lblNewLabel);
		
		btnNewButton_1 = new JButton("CHOOSE");
		btnNewButton_1.setBounds(290, 14, 117, 34);
		panel_1.add(btnNewButton_1);
		btnNewButton_1.setIcon(new ImageIcon(MainWindow.class.getResource("/images/select.png")));
		
		textField_1 = new JPasswordField();
		textField_1.setColumns(10);
		textField_1.setBounds(0, 73, 401, 34);
		panel_1.add(textField_1);
		
		JLabel lblInsiraAPassword = new JLabel("INSERT YOUR ENCRIPTION PASSWORD");
		lblInsiraAPassword.setBounds(0, 56, 389, 16);
		panel_1.add(lblInsiraAPassword);
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//Ínicio da escolha a pasta de origem
				try {
					JFileChooser f = new JFileChooser();
			        f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); 
			        f.showSaveDialog(null);
			        String osname = System.getProperty("os.name");

                    if(osname.startsWith("Mac")) {
                    	File PickedFolder = f.getCurrentDirectory();
                    	textField.setText(PickedFolder.toString());
                    }else if(osname.startsWith("Windows")) {
                    	File PickedFolder = f.getSelectedFile();
                    	textField.setText(PickedFolder.toString());
                    }
			        
			        //System.out.println(f.getCurrentDirectory());
			        //System.out.println(f.getSelectedFile());
				}catch(Exception exceptionfile) {
					System.out.println(exceptionfile);
				}
				//Fim da escolha a pasta de origem
			}
		});
		
		lblNewLabel_1 = new JLabel("");
		lblNewLabel_1.setFont(new Font("Arial", Font.BOLD, 16));
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setBounds(6, 169, 438, 34);
		frame.getContentPane().add(lblNewLabel_1);
		
		JButton btnNewButton_3 = new JButton("-");
		btnNewButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				frame.setState(Frame.ICONIFIED);
			}
		});
		btnNewButton_3.setBounds(404, 6, 40, 29);
		frame.getContentPane().add(btnNewButton_3);
		
		JButton btnNewButton_4 = new JButton("See content");
		btnNewButton_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				File f = new File(MainWindow.textField.getText()+"/lockedData.enc");
				if(!f.exists() && !f.isDirectory()) { 
					lblNewLabel_1.setText("Ficheiro encriptado não encontrado");
					lblNewLabel_1.setForeground(Color.RED);
					btnNewButton.setEnabled(false);
					btnNewButton_2.setEnabled(false);
					Timer timer = new Timer(4000, event -> {
	            	lblNewLabel_1.setText("");
	            	lblNewLabel_1.setForeground(Color.BLACK);
	            	btnNewButton.setEnabled(true);
					btnNewButton_2.setEnabled(true);
		            });
		            timer.setRepeats(false);
		            timer.start();
				}else {
					LerEncriptacao.MainClass();
				}
			}
		});
		btnNewButton_4.setBounds(13, 6, 117, 29);
		frame.getContentPane().add(btnNewButton_4);
	}
	
	
	@SuppressWarnings("unused")
	public static boolean isPasswordCorrect(String encryptedFilePath, String saltFilePath, String ivFilePath, String password) {
        try {
            // Read the salt
            FileInputStream saltFis = new FileInputStream(saltFilePath);
            byte[] salt = new byte[8];
            saltFis.read(salt);
            saltFis.close();

            // Read the iv
            FileInputStream ivFis = new FileInputStream(ivFilePath);
            byte[] iv = new byte[16];
            ivFis.read(iv);
            ivFis.close();

            // Generate the key from the password and salt
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
            SecretKey secretKey = factory.generateSecret(keySpec);
            SecretKey secret = new SecretKeySpec(secretKey.getEncoded(), "AES");

            // Initialize the cipher for decryption
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv));

            // Decrypt the file
            FileInputStream fis = new FileInputStream(encryptedFilePath);
            CipherInputStream cis = new CipherInputStream(fis, cipher);
            ObjectInputStream ois = new ObjectInputStream(cis);

            try {
                // Try to read the object from the decrypted stream
                Object obj = ois.readObject();
                ois.close();
                fis.close();
                return true; // Password is correct
            } catch (ClassNotFoundException | IOException e) {
                System.err.println("Erro ao desserializar o objeto: " + e.getMessage());
                return false; // Password is incorrect
            }
        } catch (Exception e) {
            System.err.println("Erro ao desencriptar o ficheiro: " + e.getMessage());
            return false; // Password is incorrect or other decryption error
        }
    }
	
	public static long sizeOfDirectory(Path path) throws IOException {
        final long[] size = {0};

        Files.walkFileTree(path, EnumSet.noneOf(FileVisitOption.class), Integer.MAX_VALUE, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                size[0] += attrs.size();
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                // Ignorar diretórios/arquivos aos quais não temos permissão de acesso
                return FileVisitResult.CONTINUE;
            }
        });

        return size[0];
    }
	
	
	public static void restartApplication(String textFieldValue, String textField1Value) {
        // Criar um novo processo para executar a aplicação novamente
        try {
            // Construir o comando para executar a aplicação novamente
        	String javaBin = System.getProperty("java.home") + "/bin/java";
            String classPath = System.getProperty("java.class.path");
            String className = MainWindow.class.getName();
            
            try {
            	criarDiretorioTemporario();
            }catch(Exception e) {
            	System.out.println(e);
            }

            // Nome do arquivo serializado que será criado no diretório temporário
            String tempDir = System.getProperty("java.io.tmpdir");
            File dir = new File(tempDir + File.separator + "AplicacaoTextField");
            String arquivoSerializado = dir + File.separator + "TextFieldOldText.txt";
            
            FileOutputStream f = new FileOutputStream(arquivoSerializado);
            PrintStream ps = new PrintStream(f);
            ps.print(textField.getText());
            ps.println(); //this writes your new line
            ps.print(textField_1.getText());
            ps.close();
            
            
            
            
            ProcessBuilder builder = new ProcessBuilder(javaBin, "-cp", classPath, className);
            builder.start();
            
            
            // Fechar a aplicação atual
            System.exit(0);
            
            
        } catch (Exception e) {
            e.printStackTrace();
            // Tratar exceções, se necessário
        }
        
        
    }
	
	public static String criarDiretorioTemporario() throws IOException {
        String tempDir = System.getProperty("java.io.tmpdir");
        File dir = new File(tempDir + File.separator + "AplicacaoTextField");
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                System.out.println("Diretório temporário criado: " + dir.getAbsolutePath());
            } else {
                throw new IOException("Não foi possível criar o diretório temporário: " + dir.getAbsolutePath());
            }
        }
        return dir.getAbsolutePath();
    }
	
	public static void InserirTextFields() {
					String tempDir = System.getProperty("java.io.tmpdir");
		            File dir = new File(tempDir + File.separator + "AplicacaoTextField");
		            if(dir.exists()) {
		            try (BufferedReader br = new BufferedReader(new FileReader(dir + File.separator + "TextFieldOldText.txt"))) {
		                String linha1 = br.readLine();
		                String linha2 = br.readLine();

		                if (linha1 != null) {
		                    textField.setText(linha1);
		                }

		                if (linha2 != null) {
		                    textField_1.setText(linha2);
		                }
		            } catch (IOException e) {
		                e.printStackTrace();
		            }
		
		 		try {
			        File myObj = new File(dir + File.separator + "TextFieldOldText.txt"); 
			        File dir2 = new File(myObj.toString());
			        File[] currList;
			        Stack<File> stack = new Stack<File>();
			        stack.push(dir2);
			        while (! stack.isEmpty()) {
			            if (stack.lastElement().isDirectory()) {
			                currList = stack.lastElement().listFiles();
			                if (currList.length > 0) {
			                    for (File curr: currList) {
			                        stack.push(curr);
			                    }
			                } else {
			                    stack.pop().delete();
			                }
			            } else {
			                stack.pop().delete();
			            }
			        }
		        }catch(Exception e2) {
		        	System.out.println(e2);
		        }
		        
		        try {
			        File myObj2 = new File(dir.toString()); 
			        if (myObj2.delete()) { 
			          System.out.println("Deleted the folder: " + myObj2.getName());
			        } else {
			          System.out.println("Failed to delete the folder.");
			        } 
		        }catch(Exception e2) {
		        	System.out.println(e2);
		        }
		        }
	}
}
