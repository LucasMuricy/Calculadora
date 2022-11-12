package br.com.calc.modelo;

@FunctionalInterface
public interface MemoriaObservadora {
	
	 public void valorAlterado(String novoValor);

}
