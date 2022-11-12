package br.com.calc.modelo;

import java.util.ArrayList;
import java.util.List;

public class Memoria {

	private enum TipoComando {
		ZERAR, SINAL,  NUMERO, DIV, MULT, SUB, SOMA, IGUAL, VIRGULA;
	};

	private static final Memoria instancia = new Memoria();

	private final List<MemoriaObservadora> observadores = new ArrayList<>();

	private TipoComando ultimaOperacao = null;
	private boolean substituir = false;
	private String TextoAtual = "";
	private String TextoBuffer = "";

	private Memoria() {

	}

	public String getTextoAtual() {
		return TextoAtual.isEmpty() ? "0" : TextoAtual;
	}

	public static Memoria getInstancia() {
		return instancia;
	}

	public void adicionarObservador(MemoriaObservadora observador) {
		observadores.add(observador);

	}

	public void processarComando(String texto) {

		TipoComando tipoComando = detectarTipoComando(texto);

		if (tipoComando == null) {
			return;
		} else if (tipoComando == TipoComando.ZERAR) {
			TextoAtual = "";
			TextoBuffer = "";
			substituir = false;
			ultimaOperacao = null;
		} else if (tipoComando == TipoComando.SINAL && TextoAtual.contains("-")) {
			TextoAtual =  TextoAtual.substring(1);
		} else if (tipoComando == TipoComando.SINAL && !TextoAtual.contains("-")) {
			TextoAtual = "-" +  TextoAtual;
		} else if (tipoComando == TipoComando.NUMERO || tipoComando == TipoComando.VIRGULA) {
			TextoAtual = substituir ? texto : TextoAtual + texto;
			substituir = false;
		} else {
			substituir = true;
			TextoAtual = obterResultadoOperacao();
			TextoBuffer = TextoAtual;
			ultimaOperacao = tipoComando;
		}

		observadores.forEach(o -> o.valorAlterado(getTextoAtual()));

	}

	private String obterResultadoOperacao() {
		if (ultimaOperacao == null || ultimaOperacao == TipoComando.IGUAL) {
			return TextoAtual;
		}

		double numeroBuffer = Double.parseDouble(TextoBuffer.replace(",", "."));
		double numeroAtual = Double.parseDouble(TextoAtual.replace(",", "."));

		double resultado = 0;

		if (ultimaOperacao == TipoComando.SOMA) {
			resultado = numeroBuffer + numeroAtual;
		} else if (ultimaOperacao == TipoComando.SUB) {
			resultado = numeroBuffer - numeroAtual;
		} else if (ultimaOperacao == TipoComando.MULT) {
			resultado = numeroBuffer * numeroAtual;
		} else if (ultimaOperacao == TipoComando.DIV) {
			resultado = numeroBuffer / numeroAtual;
		}

		String texto = Double.toString(resultado).replace(".", ",");
		boolean inteiro = texto.endsWith(",0");

		return inteiro ? texto.replace(",0", "") : texto;
	}

	private TipoComando detectarTipoComando(String texto) {

		if (TextoAtual.isEmpty() && texto == "0") {
			return null;
		}

		try {
			Integer.parseInt(texto);
			return TipoComando.NUMERO;
		} catch (NumberFormatException e) {
			// Quando Não For Número...

			if ("AC".equals(texto)) {
				return TipoComando.ZERAR;
			} else if ("/".equals(texto)) {
				return TipoComando.DIV;
			} else if ("*".equals(texto)) {
				return TipoComando.MULT;
			} else if ("+".equals(texto)) {
				return TipoComando.SOMA;
			} else if ("-".equals(texto)) {
				return TipoComando.SUB;
			} else if ("=".equals(texto)) {
				return TipoComando.IGUAL;
			} else if ("±".equals(texto)) {
				return TipoComando.SINAL;
			} else if (",".equals(texto) && !TextoAtual.contains(",")) {
				return TipoComando.VIRGULA;
			}

		}

		return null;
	}
}
