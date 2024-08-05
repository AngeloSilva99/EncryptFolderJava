package br.com.triadworks.rponte.util;

import java.text.Normalizer;

public class StringUtils {

	/**
	 * Remove toda a acentuação da string substituindo por caracteres simples sem acento.
	 */
	public static String unaccent(String src) {
		return Normalizer
				.normalize(src, Normalizer.Form.NFD)
				.replaceAll("[^\\p{ASCII}]", "");
	}
	
}
