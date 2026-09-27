package com.controlefinanceiro.backend.exception;

// RuntimeException (exceção "não verificada") em vez de Exception comum:
// isso significa que o compilador não obriga quem chama o método a colocar
// um try/catch ou declarar "throws" — é a convenção do Spring para esse tipo
// de erro. Na próxima etapa (Controllers) vamos criar um
// @RestControllerAdvice que intercepta esta exceção em qualquer lugar do
// código e transforma automaticamente numa resposta HTTP 404.
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
