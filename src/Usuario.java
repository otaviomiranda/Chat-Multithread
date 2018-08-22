import java.io.BufferedWriter;

public class Usuario {

	private BufferedWriter bw;
	private String nome;

	public Usuario(BufferedWriter bw, String nome) {
		this.bw = bw;
		this.nome = nome;
	}

	public BufferedWriter getBw() {
		return bw;
	}

	public String getNome() {
		return nome;
	}
}
