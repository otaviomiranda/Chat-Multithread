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

	// private static ArrayList <BufferedWriter> clientes;
	private static ArrayList<Usuario> listaClientes;
	private static ServerSocket server;
	private String nome;
	private Socket con;
	private InputStream in;
	private InputStreamReader inr;
	private BufferedReader bfr;

	/**
	 * Método construtor
	 * 
	 * @param com
	 *            do tipo Socket
	 */
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

	/**
	 * Método run
	 */
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
				System.out.println(str[1]);

				if (str[1].equalsIgnoreCase("TODOS")) {
					System.out.println("PASSOU AQ");
					sendToAll(bfw, str[1]);
					System.out.println(str[1]);
				} else {
					System.out.println("PASSOU NO ELSE");
					sendPrivateMsg(bfw, str[0], str[1]);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	/***
	 * Método usado para enviar mensagem para todos os clients
	 * 
	 * @param bwSaida
	 *            do tipo BufferedWriter
	 * @param msg
	 *            do tipo String
	 * @throws IOException
	 */
	public void sendToAll(BufferedWriter bwSaida, String msg) throws IOException {
		BufferedWriter bwS;
		for (Usuario u : listaClientes) {

			bwS = (BufferedWriter) u.getBw();
			if (!(bwSaida == bwS)) {
				u.getBw().write(nome + " -> " + msg + "\r\n");
				u.getBw().flush();
			}
		}

		/*
		 * BufferedWriter bwS; for (BufferedWriter bw : clientes) {
		 * 
		 * bwS = (BufferedWriter) bw; if (!(bwSaida == bwS)) { bw.write(nome + " -> " +
		 * msg + "\r\n"); bw.flush(); } }
		 */
	}

	public void sendPrivateMsg(BufferedWriter bwSaida, String msg, String destinatario) throws IOException {
		BufferedWriter bwS;

		for (Usuario u : listaClientes) {

			bwS = (BufferedWriter) u.getBw();
			if (!(bwSaida == bwS)) {
				if (u.getNome().equalsIgnoreCase(destinatario)) {
					u.getBw().write(nome + " -> " + msg + "\r\n");
					u.getBw().flush();
				}
			}
		}
		/*
		 * System.out.println("ENTROU NA FUNC"); BufferedWriter bwS; for (int i = 0; i <
		 * clientes.size(); i++) { System.out.println("ENTROU NO FOR"); bwS =
		 * clientes.get(i); if (!(bwSaida == bwS)) { if
		 * (listaNome.get(i).equalsIgnoreCase(destinatario)) {
		 * System.out.println("ENTROU NO IF DO NOME"); clientes.get(i).write(nome +
		 * " -> " + msg + "\r\n"); clientes.get(i).flush(); } } }
		 */
	}

	/***
	 * Método main
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			// Cria os objetos necessário para instânciar o servidor
			JLabel lblMessage = new JLabel("Porta do Servidor:");
			JTextField txtPorta = new JTextField("12345");
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
	}// Fim do método main
} // Fim da classe
