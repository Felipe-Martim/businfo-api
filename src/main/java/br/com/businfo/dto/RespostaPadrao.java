package br.com.businfo.dto;

/** Envelope { status, message, data } (Seção 5 da Especificação). */
public record RespostaPadrao<T>(int status, String message, T data) {
}
