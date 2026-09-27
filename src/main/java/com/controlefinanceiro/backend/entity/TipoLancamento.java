package com.controlefinanceiro.backend.entity;

// Um enum em Java é um tipo com um conjunto fixo e conhecido de valores.
// Aqui ele espelha o CHECK (tipo IN ('A_PAGAR', 'A_RECEBER')) do banco:
// o valor só pode ser um desses dois, nunca uma string qualquer.
public enum TipoLancamento {
    A_PAGAR,
    A_RECEBER
}
