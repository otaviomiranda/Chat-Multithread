import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class Servidor extends Thread {

	private static ArrayList<Usuario> listaClientes;
	private static ServerSocket server;
	private String nome;
	private Socket con;
	private InputStream in;
	private InputStreamReader inr;
	private BufferedReader bfr;


	public Servidor(Socket con) {
		this.con = con;
		try {
			in = con.getInputStream();
			inr = new InputStreamReader(in);
			bfr = new BufferedReader(inr);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {

		try {

			String msg;
			OutputStream ou = this.con.getOutputStream();
			Writer ouw = new OutputStreamWriter(ou);
			BufferedWriter bfw = new BufferedWriter(ouw);
			nome = msg = bfr.readLine();

			Usuario user = new Usuario(bfw, nome);
			listaClientes.add(user);

			while (!"Sair".equalsIgnoreCase(msg) && msg != null) {
				msg = bfr.readLine();

				System.out.println(msg);

				String[] str = new String[2];
				str = msg.split(";");

				if (str[1].equalsIgnoreCase("TODOS")) {
					sendToAll(bfw, str[0]);
				} else if (str[1].equalsIgnoreCase("LISTA")) {
					getListaClientes(bfw);
				}
				else if (str[1].equalsIgnoreCase("SAIR")){
					System.out.println("Saiu");
				}
				else {
					sendPrivateMsg(bfw, str[0], str[1]);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	public void sendToAll(BufferedWriter bwSaida, String msg) throws IOException {
		BufferedWriter bwS;
		for (Usuario u : listaClientes) {

			bwS = (BufferedWriter) u.getBw();
			if (!(bwSaida == bwS)) {
				u.getBw().write(nome + " -> " + msg + "\r\n");
				u.getBw().flush();
			}
		}

	}

	public void sendPrivateMsg(BufferedWriter bwSaida, String msg, String destinatario) throws IOException {
		BufferedWriter bwS;

		for (Usuario u : listaClientes) {

			bwS = (BufferedWriter) u.getBw();
			if (!(bwSaida == bwS)) {
				if (u.getNome().equalsIgnoreCase(destinatario)) {
					u.getBw().write("MENSAGEM PRIVADA DE  " + nome + " -> " + msg + "\r\n");
					u.getBw().flush();
				}
			}
		}
	}

	public void getListaClientes(BufferedWriter bwSaida) throws IOException {
		String str = "";
		String[] vetorNomes = new String[3];

		for (Usuario u : listaClientes) {
			str += ";" + u.getNome();
		}

		vetorNomes = str.split(";");
		bwSaida.write("\n\nLISTA DE USUÁRIOS CONECTADOS" + "\r\n");
		bwSaida.flush();

		for (int i = 0; i < vetorNomes.length; i++) {
			bwSaida.write("     " + vetorNomes[i] + "\r\n");
			bwSaida.flush();
		}

		bwSaida.write("\n\n Observação: Para mandar uma \n mensagem privada, digite a \n mensagem  seguida por \n   ;nomeDoUsuário" + "\r\n\n\n");
		bwSaida.flush();
	}

	public static void main(String[] args) {

		try {
			// Cria os objetos necessários para instânciar o servidor
			JLabel lblMessage = new JLabel("Porta do Servidor:");
			JTextField txtPorta = new JTextField("8000");
			Object[] texts = { lblMessage, txtPorta };
			JOptionPane.showMessageDialog(null, texts);
			server = new ServerSocket(Integer.parseInt(txtPorta.getText()));
			listaClientes = new ArrayList<Usuario>();
			JOptionPane.showMessageDialog(null, "Servidor ativo na porta: " + txtPorta.getText());

			while (true) {
				System.out.println("Aguardando conexão...");
				Socket con = server.accept();
				System.out.println("Cliente conectado...");
				Thread t = new Servidor(con);
				t.start();
			}

		} catch (Exception e) {

			e.printStackTrace();
		}
	}
}
